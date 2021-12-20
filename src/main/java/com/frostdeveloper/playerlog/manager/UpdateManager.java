package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
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
public class UpdateManager
{
	// CLASS INSTANCES
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final LocaleManager locale = plugin.getLocaleManager();
	private final CacheManager cache = new CacheManager();
	private final FrostAPI api = plugin.getFrostApi();
	
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
	private static Result result;
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
	 * A method used to run our updater task, this method uses a scheduler in order to periodically
	 * check for new updates.
	 *
	 * @since 1.0
	 */
	public void runTask()
	{
		String cachedTimer = cache.getCache("update-timer");
		
		task  = new BukkitRunnable() {
			int counter = cachedTimer != null ? Integer.parseInt(cachedTimer) : 0;
			final int interval = api.toMinute(30);
			
			@Override
			public void run() {
				// STOP TASK INCAS THE DATA FOLDER IS DELETED
				if (!plugin.getDataFolder().exists()) {
					this.cancel();
					return;
				}
				
				// IF COUNTER IS GREATER THAN INTERVAL, DELETE CACHE.
				if (counter > interval) {
					cache.deleteCache("update-timer");
				}
				
				// IF COUNTER IS LESS THAN INTERVAL, ADD TO COUNTER AND SET CACHE
				if (counter < interval) {
					counter++;
					cache.setCache("update-timer", counter);
				}
				else {
					// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
					
					attemptDownload();
					plugin.log(getMessage());
					
					counter = 0;
					cache.setCache("update-timer", counter);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
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
			if (!config.getBoolean(ConfigManager.Config.AUTO_UPDATE)) {
				result = Result.DISABLED;
				return;
			}
			
			fetchReleaseDetails();
			
			if (result == Result.UNKNOWN || result == Result.ERROR) {
				return;
			}
			
			if (ASSET_NAME == null || DOWNLOAD_URL == null) {
				result = Result.UNKNOWN;
				return;
			}
			
			if (!shouldUpdate()) {
				result = Result.CURRENT;
				return;
			}
			
			File downloadFile = new File(UPDATE_FOLDER, ASSET_NAME);
			
			if (downloadFile.exists()) {
				result = Result.AVAILABLE;
				return;
			}
			
			api.createParent(downloadFile);
			BufferedInputStream in = new BufferedInputStream(new URL(DOWNLOAD_URL).openStream());
			File updateFile = new File(UPDATE_FOLDER, ASSET_NAME);
			
			Files.copy(in, updateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			if (downloadFile.exists()) {
				result = Result.DOWNLOADED;
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
				result = Result.UNKNOWN;
				return;
			}
			
			// GITHUB IS DOWN
			if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR || connection.getResponseCode() == HttpURLConnection.HTTP_BAD_GATEWAY) {
				plugin.debug("update.fetch.error");
				result = Result.ERROR;
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
					result = Result.UNKNOWN;
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
		if (result == Result.DOWNLOADED) {// The latest version was downloaded.
			return api.format(locale.getMessage("update.result.downloaded"), REMOTE_VERSION);
		}
		else if (result == Result.CURRENT) {// The latest version is currently installed
			return api.format(locale.getMessage("update.result.current"));
		}
		else if (result == Result.DISABLED) {// The updater is disabled in the config file.
			return api.format(locale.getMessage("update.result.disabled"));
		}
		else if (result == Result.AVAILABLE) {// An update is available for download.
			return api.format(locale.getMessage("update.result.available"), REMOTE_VERSION);
		}
		else if (result == Result.ERROR) {// Either GitHub is down or the rate limit was reached.
			return api.format(locale.getMessage("update.result.error"));
		}
		else if (result == Result.UNKNOWN) {// The status of the updater is unknown
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
	public Result getResult() { return result; }
	
	/**
	 * A method  used to return the project repository identifier
	 *
	 * @return Repository identifier
	 * @since 1.1
	 */
	public String getRepo() { return REPO; }
	
	/**
	 * A method used to return an instance of our updater task. This allows us to modify our task.
	 *
	 * @return Our updater task.
	 * @since 1.0
	 */
	public BukkitTask getTask() { return task; }
	
	/**
	 * An enum used to define the available result types for our auto updater.
	 *
	 * @since 1.0
	 */
	public enum Result
	{
		/**
		 * This enum value is called when the status of the download is unknown, in other words, a valid url was
		 * provided, but we could not access information about the latest build it will return this result.,
		 * Possible outcomes include
		 * <br><br/>
		 * <p>
		 * 1. The jar file is built locally <br> 2. GitHub's rate limit was reached <br> 3. No asset file was
		 * found
		 *
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		UNKNOWN,
		/**
		 * This enum value is called during these circumstances.
		 * <br><br/>
		 * 1. GitHub is down <br> 2. A Broken url was provided
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		ERROR,
		/**
		 * This enum value is called when our plugin determines that a newer version of our plugin is available for
		 * download, but is prevented from downloading the update. Additionally, this result is returned if the
		 * update is located in the update folder
		 *
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		AVAILABLE,
		/**
		 * This enum value is called when our plugin determines that the latest version is currently installed,
		 * thus no changes have taken place.
		 *
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		CURRENT,
		/**
		 * This enum value is called when our plugin successfully downloads the latest version.
		 *
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		DOWNLOADED,
		/**
		 * This enum value is called when our auto-updater is disabled inside our configuration file.
		 *
		 * <br><br/>
		 *
		 * @since 1.0
		 */
		DISABLED
	}
}