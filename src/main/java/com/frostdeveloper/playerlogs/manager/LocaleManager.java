package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Properties;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Manager;
import com.frostdeveloper.playerlogs.util.Util;

import java.io.File;
import java.util.Locale;

/**
 * A class used to handle all tasks related to our localization.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class LocaleManager implements Manager
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
	@Override
	public void initialize()
	{
		if (config.getBoolean(Config.CUSTOM_MESSAGE) && !messageFile.exists()) {
			getDefaultMap().store(messageFile);
			plugin.log("index.create.success", messageFile.getName());
		}
	}
	
	/**
	 * A method used to apply any patch updates for newer versions of our plugin. If there are no patches that need
	 * to be made for existing versions, this method will remain empty.
	 *
	 * @implNote Remember to empty method if no updates are needed for existing features.
	 * @since 1.2
	 */
	@Override
	public void applyPatch() {}
	
	/**
	 * A method used to verify our an existing message file and make sure that all required properties exist
	 * inside the message file, if the message file is missing default keys, this method will automatically add them.
	 * If keys that are no longer supported exist, this method will remove them.
	 *
	 * @since 1.2
	 */
	public void initializeAudit()
	{
		// UPDATE MESSAGE FILE
		if (messageFile.exists()) {
			prop.load(messageFile);
			boolean changeMade = false;
			
			for (String currentProp : getPropMap().stringPropertyNames()) {
				for (String currentDef : getDefaultMap().stringPropertyNames()) {
					// REMOVE OLD PROPERTY KEYS
					if (!getDefaultMap().containsKey(currentProp)) {
						prop.remove(currentProp);
						plugin.debug("plugin.translation.removed", currentProp);
						changeMade = true;
					}
					
					// ADD MISSING DEFAULTS TO MAP
					if (!prop.containsKey(currentDef)) {
						prop.setProperty(currentDef, getDefaultMap().getProperty(currentDef));
						plugin.debug("plugin.translation.added", currentDef);
						changeMade = true;
					}
				}
			}
			
			// IF THE CUSTOM PROPERTY MAP IS EMPTY, STORE DEFAULTS.
			if (prop.isEmpty()) {
				getDefaultMap().store(messageFile);
			}
			
			// STORE ANY CHANGES MADE TO THE MESSAGE FILE
			if (changeMade) {
				prop.store(messageFile);
			}
		}
	}
	
	/**
	 * A method used to reload our custom property map, the default map by default will always reload
	 * when using the {@link #getDefaultMap()} method.
	 *
	 * @see #getDefaultMap()
	 * @since 1.2
	 */
	public void reload()
	{
		prop.clear();
		
		if (messageFile.exists()) {
			prop.load(messageFile);
		}
	}
	
	/**
	 * A method used to return our default property map
	 *
	 * @return Default property map
	 * @since 1.2
	 */
	public Properties getDefaultMap()
	{
		defaultProp.load(plugin.getResource(messageFile.getName()));
		return defaultProp;
	}
	
	/**
	 * A method used to return our custom property map
	 *
	 * @return Custom property map
	 * @since 1.2
	 */
	public Properties getPropMap()
	{
		if (messageFile.exists()) {
			prop.load(messageFile);
		}
		return prop;
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
		if (getPropMap().getProperty(key) != null || getDefaultMap().getProperty(key) != null) {
			return getPropMap().getProperty(key, getDefaultMap().getProperty(key));
		}
		return key;
	}
	
	/**
	 * A method used to return the defined locale, definition can be found in the server's config.yml for this
	 * plugin. If the locale cannot be verified, it will default to 'en'.
	 *
	 * @return Server Locale
	 * @since 1.0
	 */
	public Locale getLocale()
	{
		String locale = config.getString(Config.LOCALE);
		
		if (plugin.getResource(api.format("message_{0}.properties", locale)) != null) {
			return api.toLocale(locale);
		}
		return api.toLocale("en");
	}
	
	/**
	 * A method used to return an instance of our message file.
	 *
	 * @return Message file instance
	 * @since 1.2
	 */
	public File getFile() { return messageFile; }
}