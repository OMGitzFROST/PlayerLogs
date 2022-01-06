package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.util.Util;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

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
	
	// CLASS SPECIFIC OBJECTS
	private final File configFile = Util.toFile("config.yml");
	
	/**
	 * A method used to create our configuration file if one does not exist.
	 *
	 * @since 1.0
	 */
	public void runTask()
	{
		if (!configFile.exists()) {
			plugin.saveResource(configFile.getName(), true);
			plugin.log("index.create.success", configFile.getName());
		}
		verifyConfig();
	}
	
	/**
	 * A method used to verify if the config file is up-to-date, if it is not, we will update to the latest file.
	 *
	 * @since 1.1
	 */
	public void verifyConfig()
	{
		try {
			ConfigUpdater.update(plugin, configFile.getName(), configFile);
		}
		catch (IOException ex) {
			plugin.getReport().create(getClass(), ex, false);
		}
	}
	
	/**
	 * A method used to reload our configurations, this method will create a config if one does not exist.
	 *
	 * @since 1.2
	 */
	public void reloadConfig()                      { runTask();                              }
	
	/**
	 * A method used to return a string from our configuration
	 *
	 * @param path Configuration path
	 * @return Configuration string
	 * @since 1.0
	 */
	public String getString(@NotNull Config path)   { return getConfig().getString(path.getPath());  }
	
	/**
	 * A method used to return a boolean value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration path
	 * @return Parsed boolean
	 * @since 1.0
	 */
	public boolean getBoolean(@NotNull Config path) { return getConfig().getBoolean(path.getPath()); }
	
	/**
	 * A method used to return a double value from our configuration, it takes a string and parses it into
	 * a valid double.
	 *
	 * @param path Configuration path
	 * @return Parsed double
	 * @since 1.1
	 */
	public double getDouble(@NotNull Config path)   { return getConfig().getDouble(path.getPath());  }
	
	/**
	 * A method used to return our configuration as an object.
	 *
	 * @return Configuration as object.
	 * @since 1.2
	 */
	@Contract (" -> new")
	private @NotNull FileConfiguration getConfig() { return YamlConfiguration.loadConfiguration(configFile); }
}