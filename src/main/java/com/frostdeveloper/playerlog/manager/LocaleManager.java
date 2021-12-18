package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class used to handle all tasks related to our localization.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class LocaleManager
{
	// CLASS INSTANCES
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final File messageFile = new File(plugin.getDataFolder(), api.format("message_{0}.properties", getLocale()));
	private final Properties customMap = new Properties();
	private final Properties defaultMap = new Properties();
	
	/*
	 * TASK HANDLER METHODS
	 */
	
	/**
	 * A method used to create a message file if one does not exist.
	 *
	 * @since 1.0
	 */
	public void createFile()
	{
		if (!messageFile.exists() && config.getBoolean(ConfigManager.Config.CUSTOM_MESSAGE)) {
			InputStream inputStream = plugin.getResource(messageFile.getName());
			
			if (inputStream != null) {
				plugin.saveResource(messageFile.getName(), true);
				
				if (messageFile.exists()) {
					plugin.log("index.create.success", messageFile.getName());
				}
				else {
					plugin.log("index.create.failed", messageFile.getName());
				}
			}
		}
		else {
			plugin.log("index.search.success", messageFile.getName());
		}
	}
	
	/*
	 * GETTER METHODS
	 */
	
	/**
	 * A method used to return a localized message.
	 *
	 * @param key Message key
	 * @return Localized message
	 * @since 1.0
	 */
	public String getMessage(String key)
	{
		try {
			if (getCustomMap().getProperty(key) != null) {
				return api.format(getCustomMap().getProperty(key));
			}
			return key;
		}
		catch (IOException ex) {
			return null;
		}
	}
	
	/**
	 * A method used to return our custom messages. These messages are located in the plugin's data folder. it will
	 * use the locale defined to return the messages for the specific locale but if they don't exist, we will
	 * return our default map.
	 *
	 * @return Our custom map.
	 * @throws IOException Thrown if our custom map could not be loaded. Will return default map.
	 * @since 1.0
	 */
	private Properties getCustomMap() throws IOException
	{
		try {
			FileInputStream inputStream = new FileInputStream(messageFile);
			customMap.load(inputStream);
			return customMap;
		}
		catch (IOException ex) {
			return getDefaultMap();
		}
	}
	
	/**
	 * A method used to return our default messages. These messages are located within the .jar file it will use
	 * the locale defined to return the messages for the specific locale but if they don't exist, we will default
	 * to 'en'.
	 *
	 * @return Default Messages
	 * @throws IOException Thrown if default map failed to load.
	 * @since 1.0
	 */
	private Properties getDefaultMap() throws IOException
	{
		InputStream inputStream = plugin.getResource(messageFile.getName());
		defaultMap.load(inputStream);
		return defaultMap;
	}
	
	/**
	 * A method used to return the defined locale, definition can be found in the server's config.yml for this
	 * plugin. If the locale cannot be verified, it will default to 'en'.
	 *
	 * @return Server Locale
	 * @since 1.0
	 */
	private String getLocale()
	{
		if (verifyLocale(config.getString(ConfigManager.Config.LOCALE))) {
			return config.getString(ConfigManager.Config.LOCALE);
		}
		return ConfigManager.Config.LOCALE.getDefault();
	}
	
	/*
	 * UTIL METHOD
	 */
	
	/**
	 * A method used to verify that the included locale exists within our available locales.
	 *
	 * @param locale Target Locale
	 * @return Verification status
	 * @since 1.0
	 */
	public boolean verifyLocale(String locale)
	{
		if (locale != null) {
			String[] splitLocale = locale.split("[-_]");
			
			for (String currentSplit : splitLocale) {
				String fileName = api.format("message_{0}.properties", currentSplit);
				if (plugin.getResource(fileName) != null) {
					return true;
				}
			}
		}
		return false;
	}
}