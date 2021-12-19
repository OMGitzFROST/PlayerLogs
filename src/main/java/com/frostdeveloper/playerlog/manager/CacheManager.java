package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * A class used to manage our cache. This mananger includes methods to set, get, and delete cache values.
 *
 * @author OMGitzFROST
 * @since 1.1
 */
public class CacheManager
{
	// CLASS INSTANCES
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final File cacheFile = api.toFile( ".cache.yml");
	
	/**
	 * A method used to set a cache to a value
	 *
	 * @param path Target cache
	 * @param value Target value
	 * @since 1.1
	 */
	public void setCache(String path, Object value)
	{
		try {
			FileConfiguration config = YamlConfiguration.loadConfiguration(cacheFile);
			config.set(path, value);
			config.save(cacheFile);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to get a cache value/
	 *
	 * @param path Target path
	 * @return Cache value
	 * @since 1.1
	 */
	public String getCache(String path)
	{
		if (cacheFile.exists()) {
			plugin.debug("index.search.success", cacheFile.getName());
			FileConfiguration config = YamlConfiguration.loadConfiguration(cacheFile);
			return config.getString(path);
		}
		else {
			plugin.debug("index.search.failed", cacheFile.getName());
			return null;
		}
	}
	
	/**
	 * A method used to delete a cache path, in other words, used to reset its value.
	 *
	 * @param path Target path
	 * @since 1.1
	 */
	public void deleteCache(String path)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(cacheFile);
		config.set(path, null);
	}
}
