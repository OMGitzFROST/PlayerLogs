package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.definition.UpdateResult;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
import com.google.common.base.Charsets;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A class used to manage our auto-updater
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class UpdateManager extends ModuleManager implements Module, Scheduler
{
	// CLASS INSTANCES
	private final ConfigManager config = plugin.getConfigManager();
	private final LocaleManager locale = plugin.getLocaleManager();
	private final CacheManager cache = plugin.getCacheManager();
	
	// REQUIRED OBJECTS
	private final File UPDATE_FOLDER;
	private final String REPO;
	
	// RELEASE INFO
	private final String RELEASE_URL;
	private String REMOTE_VERSION;
	private String DOWNLOAD_URL;
	private String ASSET_NAME;
	private String CHANGELOG;
	
	// UPDATER OBJECTS
	private static UpdateResult result;
	
	// CLASS SPECIFIC OBJECTS
	private final String identifier = "update-module";
	private final String cacheIdentifier = "update-timer";
	private final Config permission = Config.AUTO_UPDATE;
	private static BukkitTask task;
	
	/**
	 * A constructor for our UpdateManager class, this constructor is used to instantiate
	 * objects required for our updater to work properly.
	 *
	 * @since 1.1
	 */
	public UpdateManager()
	{
		REPO = api.format("OMGitzFROST/{0}", plugin.getDescription().getName());
		RELEASE_URL = api.format("https://api.github.com/repos/{0}/releases/latest", getRepo());
		UPDATE_FOLDER = plugin.getServer().getUpdateFolderFile();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void initialize() { start(); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void start()
	{
		String cachedTimer = cache.getCache(cacheIdentifier) != null ? cache.getCache(cacheIdentifier) : "0";
		
		task  = new BukkitRunnable() {
			int counter = Integer.parseInt(cachedTimer);
			
			@Override
			public void run() {
				final int interval = api.toMinute(30);
				
				// STOP TASK IN-CASE THE DATA FOLDER IS DELETED
				if (!plugin.getDataFolder().exists() || !isRegistered()) {
					shutdown();
					return;
				}
				
				// IF COUNTER IS GREATER THAN INTERVAL, DELETE CACHE.
				if (counter > interval) {
					cache.deleteCache(cacheIdentifier);
				}
				
				// IF COUNTER IS LESS THAN INTERVAL, ADD TO COUNTER AND SET CACHE
				if (counter < interval) {
					counter++;
					cache.setCache(cacheIdentifier, counter);
				}
				else {
					// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
					attemptDownload();
					plugin.log(getMessage());
					
					counter = 0;
					cache.setCache(cacheIdentifier, counter);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void registerModule()
	{
		addToMaster(this);
		
		if (config.getBoolean(permission)) {
			addToRegistry(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void shutdown()
	{
		cancel();
		
		if (isCancelled()) {
			plugin.debug(getClass(), "module.unload.success", identifier);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	@Override
	public boolean isRegistered() { return getRegisteredList().contains(this); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	public String getIdentifier() { return identifier;                         }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public int getTaskId() { return getRegisteredList().indexOf(this); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public boolean isCancelled() { return task == null || task.isCancelled(); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void cancel()
	{
		if (task != null) {
			task.cancel();
		}
	}
	
	/**
	 * A method used to verify and download an update if this updater determines an update is required.
	 *
	 * @since 1.1
	 */
	public void attemptDownload()
	{
		try {
			/* PRE-CHECKS */
			if (!config.getBoolean(Config.AUTO_UPDATE)) {
				result = UpdateResult.DISABLED;
				return;
			}
			
			fetchReleaseDetails();
			
			if (result == UpdateResult.UNKNOWN || result == UpdateResult.ERROR) {
				return;
			}
			
			if (ASSET_NAME == null || DOWNLOAD_URL == null) {
				result = UpdateResult.UNKNOWN;
				return;
			}
			
			if (!shouldUpdate()) {
				result = UpdateResult.CURRENT;
				return;
			}
			
			File downloadFile = new File(UPDATE_FOLDER, ASSET_NAME);
			
			if (downloadFile.exists()) {
				result = UpdateResult.AVAILABLE;
				return;
			}
			
			api.createParent(downloadFile);
			BufferedInputStream in = new BufferedInputStream(new URL(DOWNLOAD_URL).openStream());
			File updateFile = new File(UPDATE_FOLDER, ASSET_NAME);
			
			Files.copy(in, updateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			if (downloadFile.exists()) {
				result = UpdateResult.DOWNLOADED;
			}
			
			if (CHANGELOG != null) {
				FileWriter fw = new FileWriter(new File(plugin.getDataFolder(),"CHANGELOG- " + REMOTE_VERSION + ".txt"));
				PrintWriter pw = new PrintWriter(fw);
				
				CHANGELOG = CHANGELOG.replaceAll("\\*", "    ∙");
				CHANGELOG = CHANGELOG.replaceAll("#", "");
				
				pw.println(CHANGELOG);
				pw.close();
			}
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/**
	 * A method designed to only set the release info. It is not designed for anything else.
	 *
	 * @since 1.1
	 */
	private void fetchReleaseDetails()
	{
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(RELEASE_URL).openConnection();
			connection.connect();
			
			// COULDN'T ACCESS URL, BUILT LOCALLY? OR RATE LIMIT REACHED
			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
				plugin.debug("update.fetch.unknown");
				result = UpdateResult.UNKNOWN;
				return;
			}
			
			// GITHUB IS DOWN
			if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR || connection.getResponseCode() == HttpURLConnection.HTTP_BAD_GATEWAY) {
				plugin.debug("update.fetch.error");
				result = UpdateResult.ERROR;
				return;
			}
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
				JSONObject releaseInfo = (JSONObject) new JSONParser().parse(reader);
				JSONArray jsonArray = (JSONArray) releaseInfo.get("assets");
				
				REMOTE_VERSION = (String) releaseInfo.get("tag_name");
				CHANGELOG = (String) releaseInfo.get("body");
				
				if (!jsonArray.isEmpty()) {
					JSONObject assetInfo = (JSONObject) jsonArray.get(0);
					ASSET_NAME = (String) assetInfo.get("name");
					DOWNLOAD_URL = (String) assetInfo.get("browser_download_url");
				}
				else {
					plugin.debug("update.result.nofile");
					result = UpdateResult.UNKNOWN;
				}
			}
			catch (ParseException | NumberFormatException ex) {
				ReportManager.createReport(getClass(), ex, true);
			}
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/**
	 * A method used to gather and print our messages in regards the result type.
	 *
	 * @since 1.1
	 */
	public String getMessage()
	{
		if (result == UpdateResult.DOWNLOADED) {// The latest version was downloaded.
			return api.format(locale.getMessage("update.result.downloaded"), REMOTE_VERSION);
		}
		else if (result == UpdateResult.CURRENT) {// The latest version is currently installed
			return api.format(locale.getMessage("update.result.current"));
		}
		else if (result == UpdateResult.DISABLED) {// The updater is disabled in the config file.
			return api.format(locale.getMessage("update.result.disabled"));
		}
		else if (result == UpdateResult.AVAILABLE) {// An update is available for download.
			return api.format(locale.getMessage("update.result.available"), REMOTE_VERSION);
		}
		else if (result == UpdateResult.ERROR) {// Either GitHub is down or the rate limit was reached.
			return api.format(locale.getMessage("update.result.error"));
		}
		else if (result == UpdateResult.UNKNOWN) {// The status of the updater is unknown
			return api.format(locale.getMessage("update.result.unknown"));
		}
		return "";
	}
	
	/**
	 * A method used to determine whether an update is required. It compares the remote version to the local vesion
	 * to return its requested outcome.
	 *
	 * @return Update status
	 * @since 1.0
	 */
	private boolean shouldUpdate()
	{
		double remote = Double.parseDouble(REMOTE_VERSION.replace("v", ""));
		double local  = Double.parseDouble(plugin.getDescription().getVersion());
		return remote > local;
	}
	
	/**
	 * A method used to reload our updater, this method cancels all existing schedulers and reloads our updater.
	 *
	 * @since 1.1
	 */
	public void reload()
	{
		task.cancel();
		runTask();
	}
	
	/**
	 * A method used to return the outcome updater result.
	 *
	 * @return Update result.
	 * @since 1.1
	 */
	public UpdateResult getResult() { return result; }
	
	/**
	 * A method  used to return the project repository identifier
	 *
	 * @return Repository identifier
	 * @since 1.1
	 */
	public String getRepo() { return REPO; }
}