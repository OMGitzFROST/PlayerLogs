package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * A class used to handle our config tasks, this manager will create our file if one
 * does not exist and houses our getters for our configurations.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ConfigManager
{
	// CLASS INSTANCES
	private static final PlayerLogs plugin = PlayerLogs.getInstance();
	private static final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final File configFile = api.toFile("config.yml");
	
	/**
	 * A method used to create our configuration file if one does not exist.
	 *
	 * @since 1.0
	 */
	public void createFile()
	{
		attemptUpdate();
		
		if (!configFile.exists()) {
			plugin.saveResource(configFile.getName(), true);
			plugin.log("index.create.success", configFile.getName());
		}
		else {
			plugin.log("index.search.success", configFile.getName());
		}
	}
	
	/**
	 * A method used to verify if the config file is up-to-date, if it is not, we will update to the latest file.
	 *
	 * @since 1.1
	 */
	public void attemptUpdate()
	{
		if (configFile.exists()) {
			float currentVersion = getFloat(Config.VERSION);
			float latestVersion  = api.toFloat(Objects.requireNonNull(Config.VERSION.getDefault()));
			
			if (latestVersion > currentVersion) {
				File backupFile = api.toFile("backup/old-config.yml", currentVersion);
				
				api.createParent(backupFile);
				
				if (configFile.renameTo(backupFile)) {
					createFile();
					plugin.log("index.update.success", configFile.getName());
				}
			}
		}
	}
	
	/**
	 * A method used to return a string from our configuration
	 *
	 * @param path Configuration path
	 * @return Configuration string
	 * @since 1.0
	 */
	public String getString(@NotNull Config path)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		
		if (config.getString(path.getKey()) != null) {
			return config.getString(path.getKey());
		}
		return config.getString(Objects.requireNonNull(path.getDefault()));
	}
	
	/**
	 * A method used to return a boolean value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration path
	 * @return Parsed boolean
	 * @since 1.0
	 */
	public boolean getBoolean(Config path) { return Boolean.parseBoolean(getString(path)); }
	
	/**
	 * A method used to return a double value from our configuration, it takes a string and parses it into
	 * a valid double.
	 *
	 * @param path Configuration path
	 * @return Parsed double
	 * @since 1.1
	 */
	public double getDouble(Config path)   { return Double.parseDouble(getString(path));   }
	
	/**
	 * A method used to return a Float value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration Float
	 * @return Parsed Float
	 * @since 1.1
	 */
	public float getFloat(Config path)     { return Float.parseFloat(getString(path));     }
}