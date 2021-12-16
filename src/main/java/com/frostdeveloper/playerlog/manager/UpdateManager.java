package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import com.google.common.base.Charsets;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateManager
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final CacheManager cache = new CacheManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	@SuppressWarnings ("FieldCanBeLocal")
	private final String REPO = "OMGitzFROST/PlayerLogs";
	private File updateFolder;
	
	private String REMOTE_VERSION;
	private String ASSET_NAME;
	private URL DOWNLOAD_URL;
	
	private static Result result;
	
	public void runTask()
	{
		updateFolder = plugin.getServer().getUpdateFolderFile();
		fetchLatestRelease();
		
		switch(result) {
			case INSTALLED:
				plugin.log("update.result.updated", REMOTE_VERSION);
				return;
			case EXISTS:
				plugin.log(Level.WARNING, "update.result.exists", REMOTE_VERSION);
				return;
			case CURRENT:
				plugin.log("update.result.current");
				return;
			case DISABLED:
				plugin.log(Level.WARNING, "update.result.disabled");
				return;
			case AVAILABLE:
				plugin.log(Level.WARNING, "update.result.available", REMOTE_VERSION);
			case ERROR:
				plugin.debug(Level.WARNING, "update.result.error");
			case NOFILE:
				plugin.debug(Level.WARNING, "update.result.nofile");
			case UNKNOWN:
				plugin.debug(Level.WARNING, "update.result.unknown");
		}
	}
	
	private void fetchLatestRelease()
	{
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + REPO + "/releases/latest").openConnection();
			connection.connect();
			
			if (!config.getBoolean(ConfigManager.Config.AUTO_UPDATE)) {
				// UPDATER IS DISABLED AS DEFINED IN CONFIG
				result = Result.DISABLED;
				return;
			}
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				// COULDN'T ACCESS URL, BUILT LOCALLY?
				result = Result.UNKNOWN;
				return;
			}
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR || connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
				// GITHUB IS DOWN OR RATE LIMIT REACHED
				result = Result.ERROR;
				return;
			}
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
				setReleaseInfo(reader);
				
				if (shouldUpdate(REMOTE_VERSION, plugin.getDescription().getVersion())) {
					result = Result.AVAILABLE;
					download(DOWNLOAD_URL, updateFolder);
				}
				else {
					result = Result.CURRENT;
				}
			}
			catch (ParseException | NumberFormatException ex) {
				ReportManager.createReport(ex, true);
			}
		}
		catch (IOException ex) {
			ReportManager.createReport(ex, true);
		}
	}
	
	private boolean shouldUpdate(@NotNull String removeVersion, String localVersion)
	{
		double remote = Double.parseDouble(removeVersion.replace("v", ""));
		double local  = Double.parseDouble(localVersion);
		
		return remote > local;
	}
	
	private void setReleaseInfo(Reader reader) throws IOException, ParseException
	{
		JSONParser jsonParser = new JSONParser();
		JSONObject releaseInfo = (JSONObject) jsonParser.parse(reader);
		JSONArray jsonArray = (JSONArray) releaseInfo.get("assets");
		
		REMOTE_VERSION = (String) releaseInfo.get("tag_name");
		
		if (!jsonArray.isEmpty()) {
			JSONObject assetInfo = (JSONObject) jsonArray.get(0);
			ASSET_NAME     = (String) assetInfo.get("name");
			DOWNLOAD_URL   = new URL((String) assetInfo.get("browser_download_url"));
		}
		else {
			result = Result.NOFILE;
		}
	}
	
	private void download(@NotNull URL url, @NotNull File location)
	{
		try {
			if (ASSET_NAME != null) {
				File downloadFile = new File(location, ASSET_NAME);
				
				if (!downloadFile.exists()) {
					api.createParent(downloadFile);
					BufferedInputStream in;
					FileOutputStream fout;
					
					final int fileLength = url.openConnection().getContentLength();
					in = new BufferedInputStream(url.openStream());
					fout = new FileOutputStream(new File(updateFolder, ASSET_NAME));
					
					final byte[] data = new byte[1024];
					int count;
					
					long downloaded = 0;
					while ((count = in.read(data, 0, 1024)) != -1) {
						downloaded += count;
						fout.write(data, 0, count);
						final int percent = (int) ((downloaded * 100) / fileLength);
						if ((percent % 10) == 0) {
							plugin.log("Downloading update: " + percent + "% of " + fileLength + " bytes.");
						}
					}
					
					if (downloadFile.exists()) {
						result = Result.INSTALLED;
					}
				}
				else {
					result = Result.EXISTS;
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to download update", ex);
		}
	}
	
	public Result getResult() { return result != null ? result : Result.UNKNOWN; }
	
	public enum Result
	{
		UNKNOWN,
		DISABLED,
		ERROR,
		CURRENT,
		AVAILABLE,
		NOFILE,
		INSTALLED,
		EXISTS
	}
}