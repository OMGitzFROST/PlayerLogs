package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Properties;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.util.Util;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

/**
 * A class used to handle all tasks related to our localization.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class LocaleManager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostAPI();
	
	// CLASS SPECIFIC OBJECTS
	private final File messageFile = Util.toFile("message_{0}.properties", getLocale());
	private final Properties prop = new Properties(true);
	private final Properties defaultProp = new Properties();
	
	/**
	 * This method is used to configure and update our messages if an update is available.
	 *
	 * @since 1.2
	 */
	public void runTask()
	{
		defaultProp.load(plugin.getResource(messageFile.getName()));
		
		if (config.getBoolean(Config.CUSTOM_MESSAGE)) {
			
			// IF OTHER MESSAGE FILES EXIST IN DATA FOLDER, MOVE TO BACKUP FOLDER.
			if (plugin.getDataFolder().exists()) {
				for (File currentFile : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
					if (currentFile.getName().contains("message") && !currentFile.getName().equals(messageFile.getName())) {
						
						File backupFile = Util.toFile("backup/{0}", currentFile.getName());
						
						api.createParent(backupFile);
						api.renameFile(currentFile, backupFile);
					}
				}
			}
			
			// IF MESSAGE FILE EXISTS IN BACK UP FOLDER MOVE TO DATA FOLDER
			if (Util.toFile("backup").exists()) {
				for (File currentFile : Objects.requireNonNull(Util.toFile("backup").listFiles())) {
					if (currentFile.getName().contains("message") && currentFile.getName().equals(messageFile.getName())) {
						api.renameFile(currentFile, messageFile);
					}
				}
			}
			
			// IF MESSAGE FILE EXISTS, CHECK FOR UPDATES TO MESSAGE FILE
			if (messageFile.exists()) {
				prop.load(messageFile);
				Properties mergedProp = new Properties(prop.isOrdered());
				
				for (String propKey : prop.stringPropertyNames()) {
					for (String defKey : defaultProp.stringPropertyNames()) {
						
						// IF DEFAULT CONTAINS CUSTOM KEY ADD TO MERGED MAP
						if (defaultProp.containsKey(propKey)) {
							mergedProp.setProperty(propKey, prop.getProperty(propKey));
						}
						
						// IF CUSTOM MAP DOES NOT CONTAIN DEFAULT ADD TO MERGED (UPDATE)
						if (!prop.containsKey(defKey)) {
							mergedProp.setProperty(defKey, defaultProp.getProperty(defKey));
							plugin.debug(getClass(), "plugin.translation.added", defKey);
						}
					}
				}
				if (!mergedProp.isEmpty()) {
					mergedProp.store(messageFile);
					mergedProp.clear();
					prop.load(messageFile);
				}
			}
			
			// IF ENABLED, CREATE MISSING MESSAGE FILE
			if (!messageFile.exists()) {
				if (defaultProp.isEmpty()) {
					throw new IllegalArgumentException(api.format(getMessage("plugin.locale.invalid"), getLocale()));
				}
				plugin.saveResource(messageFile.getName(), true);
			}
		}
	}
	
	/**
	 * A method used to return a localized message.
	 *
	 * @param key Message key
	 * @return Localized message
	 * @since 1.0
	 */
	public String getMessage(String key)
	{
		defaultProp.load(plugin.getResource(messageFile.getName()));
		
		if (defaultProp.isEmpty()) {
			defaultProp.load(plugin.getResource("message_en.properties"));
		}
		
		if (messageFile.exists()) {
			prop.load(messageFile);
		}
		return prop.getProperty(key, defaultProp.getProperty(key));
	}
	
	/**
	 * A method used to return the defined locale, definition can be found in the server's config.yml for this
	 * plugin. If the locale cannot be verified, it will default to 'en'.
	 *
	 * @return Server Locale
	 * @since 1.0
	 */
	public Locale getLocale() { return api.toLocale(config.getString(Config.LOCALE)); }
}