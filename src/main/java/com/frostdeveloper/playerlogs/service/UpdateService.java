package com.frostdeveloper.playerlogs.service;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.definition.UpdateResult;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.LocaleManager;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A class used to manage our auto-updater
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class UpdateService implements Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin    = PlayerLogs.getInstance();
	private final FrostAPI api         = plugin.getFrostAPI();
	private final ConfigManager config = plugin.getConfigManager();
	private final LocaleManager locale = plugin.getLocaleManager();
	
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
	private UpdateResult result;
	
	/**
	 * A constructor for our UpdateManager class, this constructor is used to instantiate
	 * objects required for our updater to work properly.
	 *
	 * @since 1.1
	 */
	public UpdateService()
	{
		REPO = api.format("OMGitzFROST/{0}", plugin.getDescription().getName());
		RELEASE_URL = api.format("https://api.github.com/repos/{0}/releases/latest", getRepo());
		UPDATE_FOLDER = plugin.getServer().getUpdateFolderFile();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * A method used to run our updater task, this tasks will be run after all other tasks are complete.
	 *
	 * @since 1.0
	 */
	public void initialize()
	{
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			attemptDownload();
			initializeLogger();
		}, 0);
	}
	
	/**
	 * A listener used to announce available updates if one is available
	 *
	 * @param event Triggered event
	 * @since 1.1
	 */
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		if (Permission.isPermitted(player, Permission.CMD_UPDATE)) {
			if (getResult() == UpdateResult.AVAILABLE) {
				player.sendMessage("update.result.available");
			}
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
				this.result = UpdateResult.DISABLED;
				return;
			}
			
			fetchReleaseDetails();
			
			if (this.result == UpdateResult.UNKNOWN || this.result == UpdateResult.ERROR) {
				return;
			}
			
			if (ASSET_NAME == null || DOWNLOAD_URL == null) {
				this.result = UpdateResult.UNKNOWN;
				return;
			}
			
			if (!shouldUpdate()) {
				this.result = UpdateResult.CURRENT;
				return;
			}
			
			File downloadFile = new File(UPDATE_FOLDER, ASSET_NAME);
			
			if (downloadFile.exists()) {
				this.result = UpdateResult.AVAILABLE;
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
				
				CHANGELOG = CHANGELOG.replaceAll("\\*", "    ???");
				CHANGELOG = CHANGELOG.replaceAll("#", "");
				
				pw.println(CHANGELOG);
				pw.close();
			}
			in.close();
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
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
			
			if (connection == null) {
				result = UpdateResult.UNKNOWN;
				return;
			}
			
			connection.connect();
			
			// COULDN'T ACCESS URL, BUILT LOCALLY? OR RATE LIMIT REACHED
			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
				plugin.debug("update.result.unknown");
				this.result = UpdateResult.UNKNOWN;
				return;
			}
			
			// GITHUB IS DOWN
			if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR || connection.getResponseCode() == HttpURLConnection.HTTP_BAD_GATEWAY) {
				plugin.debug("update.result.error");
				this.result = UpdateResult.ERROR;
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
					this.result = UpdateResult.UNKNOWN;
				}
				connection.disconnect();
			}
			catch (ParseException | NumberFormatException ex) {
				plugin.getReport().create(ex);
			}
		}
		catch (IOException ex) {
			if (ex instanceof UnknownHostException) {
				result = UpdateResult.UNKNOWN;
			}
			else {
				plugin.getReport().create(ex);
			}
		}
	}
	
	/**
	 * A method used to gather and print our messages in regards the result type.
	 *
	 * @since 1.1
	 */
	public String getMessage()
	{
		switch (result) {
			case DOWNLOADED:
				return api.format(locale.getMessage("update.result.downloaded"), REMOTE_VERSION);
			case CURRENT:
				return api.format(locale.getMessage("update.result.current"));
			case DISABLED:
				return api.format(locale.getMessage("update.result.disabled"));
			case AVAILABLE:
				return api.format(locale.getMessage("update.result.available"), REMOTE_VERSION);
			case ERROR:
				return api.format(locale.getMessage("update.result.error"));
			default:
				return api.format(locale.getMessage("update.result.unknown"));
		}
	}
	
	/**
	 * A method used in conjunction the the {@link #initialize()} class to log the update status
	 * that this updater returned;
	 *
	 * @since 1.2
	 */
	public void initializeLogger()
	{
		switch (result) {
			case AVAILABLE:
			case DOWNLOADED:
				plugin.log(getMessage());
				break;
			default:
				plugin.debug(getMessage());
		}
	}
	
	/**
	 * A method used to determine whether an update is required. It compares the remote version to the local version
	 * to return its requested outcome.
	 *
	 * @return Update status
	 * @since 1.0
	 */
	private boolean shouldUpdate()
	{
		double remote = api.toDouble(REMOTE_VERSION.replace("v", ""));
		double local  = api.toDouble(plugin.getDescription().getVersion());
		return remote > local;
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