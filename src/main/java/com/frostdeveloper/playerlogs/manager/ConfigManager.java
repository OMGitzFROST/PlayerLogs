package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Yaml;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.util.Util;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.jetbrains.annotations.NotNull;

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
	private static final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final Yaml yaml = new Yaml(Util.toFile("config.yml"));
	
	/**
	 * A method used to create our configuration file if one does not exist.
	 *
	 * @since 1.0
	 */
	public void createFile()
	{
		if (yaml.getFile().exists()) {
			verifyConfig();
		}
		yaml.createFile();
	}
	
	/**
	 * A method used to verify if the config file is up-to-date, if it is not, we will update to the latest file.
	 *
	 * @since 1.1
	 */
	public void verifyConfig()
	{
		try {
			ConfigUpdater.update(plugin, yaml.getName(), yaml.getFile());
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/**
	 * A method used to reload our configuration file
	 *
	 * @since 1.2
	 */
	public void reload()                            { yaml.reload();                                            }
	
	/**
	 * A method used to return a string from our configuration
	 *
	 * @param path Configuration path
	 * @return Configuration string
	 * @since 1.0
	 */
	public String getString(@NotNull Config path)   { return yaml.getString(path.getPath(), path.getDefault());  }
	
	/**
	 * A method used to return a boolean value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration path
	 * @return Parsed boolean
	 * @since 1.0
	 */
	public boolean getBoolean(@NotNull Config path) { return yaml.getBoolean(path.getPath(), path.getDefault()); }
	
	/**
	 * A method used to return a double value from our configuration, it takes a string and parses it into
	 * a valid double.
	 *
	 * @param path Configuration path
	 * @return Parsed double
	 * @since 1.1
	 */
	public double getDouble(@NotNull Config path)   { return yaml.getDouble(path.getPath(), path.getDefault());  }
}