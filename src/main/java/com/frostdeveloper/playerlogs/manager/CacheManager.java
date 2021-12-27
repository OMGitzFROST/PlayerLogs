package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Properties;
import com.frostdeveloper.api.core.Yaml;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * A class used to manage our cache. This mananger includes methods to set, get, and delete cache values.
 *
 * @author OMGitzFROST
 * @since 1.1
 */
public class CacheManager
{
	// CLASS SPECIFIC OBJECTS
	private final Properties cache = new Properties(true);
	private final File cacheFile   = Util.toFile(".cache.properties");

	/**
	 * A method used to set a cache to a value
	 *
	 * @param path Target cache
	 * @param value Target value
	 * @since 1.1
	 */
	public void setCache(String path, Object value)
	{
		getCache().setProperty(path, value, true);
		cache.store(cacheFile);
	}
	
	/**
	 * This method is used to load and return our cache properties
	 *
	 * @return Cache map
	 * @since 1.2
	 */
	public Properties getCache()
	{
		if (cacheFile.exists()) {
			cache.load(cacheFile);
		}
		return cache;
	}
	
	/**
	 * A method used to get a cache value/
	 *
	 * @param path Target path
	 * @return Cache value
	 * @since 1.1
	 */
	public String getCache(String path, Object def) { return getCache().getProperty(path, def); }
	
	/**
	 * A method used to return a value from a cached path
	 *
	 * @param path Target path
	 * @return Cached value
	 * @since 1.2
	 */
	public String getCache(String path)             { return getCache().getProperty(path); }

	/**
	 * A method used to delete a cache path, in other words, used to reset its value.
	 *
	 * @param path Target path
	 * @since 1.1
	 */
	public void deleteCache(String path)
	{
		getCache().removeProperty(path);
		cache.store(cacheFile);
	}
}