package com.frostdeveloper.playerlogs.core;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.handler.Validate;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.util.Util;
import com.google.common.base.Charsets;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A class used to handle our default methods for any new configuration. This method provides the developer
 * with methods to make getting values and creating files that much easier.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class Configuration
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final FrostAPI api      = plugin.getFrostAPI();
	
	// CLASS SPECIFIC OBJECTS
	private final File configFile;
	private final boolean reload;
	private FileConfiguration config;
	
	/**
	 * A super constructor used to define the variables needed to determine how this class works.
	 *
	 * @param target This parameter is used to define the path in which the desired
	 *               configuration will be located.
	 * @param reload This parameter will define if the configuration should always automatically reload
	 *               its values, if set to false, the target will only update on a complete reload or shutdown.
	 * @since 1.2
	 */
	public Configuration(@NotNull String target, boolean reload)
	{
		Validate.notNull(api.getResource(target));
		
		this.configFile = Util.toFile(target);
		this.reload     = reload;
		this.config     = YamlConfiguration.loadConfiguration(api.toFile(target));
	}
	
	/**
	 * A method used to save the resource that corresponds with the defined target.
	 *
	 * @since 1.2
	 */
	public void saveDefaultConfig()
	{
		if (!getFile().exists()) {
			plugin.saveResource(getName(), false);
		}
	}
	
	/**
	 * A method used to save the resource that corresponds with the defined target, Additionally, you can
	 * define whether this method should ignore an existing file and save a new copy anyway.
	 *
	 * @param replace Whether to replace an existing configuration
	 * @since 1.2
	 */
	public void saveDefaultConfig(boolean replace)
	{
		if (!getFile().exists()) {
			plugin.saveResource(getName(), replace);
		}
	}
	
	/**
	 * Loads this {@link FileConfiguration} from the specified location.
	 * <p>
	 * All the values contained within this configuration will be removed,
	 * leaving only settings and defaults, and the new values will be loaded
	 * from the given file.
	 * <p>
	 * If the file cannot be loaded for any reason, an exception will be
	 * thrown.
	 *
	 * @since 1.2
	 */
	public void loadConfiguration()
	{
		try {
			config.load(getFile());
		}
		catch (IOException | InvalidConfigurationException ex) {
			plugin.getReport().create(ex);
		}
	}
	
	/**
	 * This method is used to reload our configurations from disk, If the resource does not exist inside
	 * the jar file, this method will return no changes.
	 *
	 * @since 1.2
	 */
	public void reload()
	{
		config = YamlConfiguration.loadConfiguration(getFile());
		
		final InputStream defConfigStream = api.getResource(getName());
		if (defConfigStream == null) {
			return;
		}
		
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}
	
	/**
	 * A method used to verify and attempt an update to the yaml file if an update is required. This method
	 * required the {@link ConfigUpdater} class inorder to work.
	 *
	 * @since 1.2
	 */
	public void attemptUpdate()
	{
		try {
			ConfigUpdater.update(plugin, getName(), getFile());
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
	
	/**
	 * A method used to return the configuration and its values. If the auto reload parameter was set to true,
	 * this method will take care of automatically reloading the configuration and returning the reloaded values.
	 *
	 * @return The configuration map.
	 * @since 1.2
	 */
	public FileConfiguration getConfig()
	{
		if (reload) {
			reload();
		}
		return config;
	}
	
	/**
	 * A method used to return an object of the configuration file.
	 *
	 * @return An object of the configuration file.
	 * @since 1.2
	 */
	public File getFile()                                    { return configFile;                                 }
	
	/**
	 * A method used to return whether the configuration file exists inside the location defined.
	 *
	 * @return Whether the configuration file exists.
	 * @since 1.2
	 */
	public boolean exists()                                  { return getFile().exists();                         }
	
	/**
	 * A method used to return the name of the configuration file in use.
	 *
	 * @return The configuration's name
	 * @since 1.2
	 */
	private @NotNull String getName()                        { return getFile().getName();                        }
	
	/*
	 * CONFIGURATION GETTERS
	 */
	
	/**
	 * A method used to return a string from our configuration
	 *
	 * @param path Configuration path
	 * @return Configuration string
	 * @since 1.0
	 */
	public String getString(@NotNull Config path)            { return getConfig().getString(path.getPath());      }
	
	/**
	 * A method used to return a boolean value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration path
	 * @return Parsed boolean
	 * @since 1.0
	 */
	public boolean getBoolean(@NotNull Config path)          { return getConfig().getBoolean(path.getPath());     }
	
	/**
	 * A method used to return a double value from our configuration, it takes a string and parses it into
	 * a valid double.
	 *
	 * @param path Configuration path
	 * @return Parsed double
	 * @since 1.1
	 */
	public double getDouble(@NotNull Config path)            { return getConfig().getDouble(path.getPath());      }
	
	/**
	 * A method used to return an integer value from our configuration, it takes a string and parses it into
	 * a valid double.
	 *
	 * @param path Configuration path
	 * @return Parsed double
	 * @since 1.2
	 */
	public int getInt(@NotNull Config path)                  { return getConfig().getInt(path.getPath());         }
	
	
	/**
	 * A method used to return a list of string from our configuration.
	 *
	 * @param path Configuration path
	 * @return A list of strings
	 * @since 1.2
	 */
	public List<String> getStringList(@NotNull Config path)  { return getConfig().getStringList(path.getPath());  }
	
	/*
	 * BOOLEAN CHECKS
	 */
	
	/**
	 * A method used to return whether a path represents a list.
	 *
	 * @param path Target path
	 * @return Whether path is a list
	 * @since 1.2
	 */
	public boolean isList(@NotNull Config path)              { return config.isList(path.getPath());              }
}
