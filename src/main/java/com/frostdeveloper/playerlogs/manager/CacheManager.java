package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.core.Yaml;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.util.Util;

import java.io.File;

/**
 * A class used to manage our cache. This mananger includes methods to set, get, and delete cache values.
 *
 * @author OMGitzFROST
 * @since 1.1
 */
public class CacheManager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	
	// CLASS SPECIFIC OBJECTS
	private final File cacheFile = Util.toFile( ".cache.yml");
	private final Yaml cache = new Yaml(cacheFile, true);

	/**
	 * A method used to set a cache to a value
	 *
	 * @param path Target cache
	 * @param value Target value
	 * @since 1.1
	 */
	public void setCache(String path, Object value) { cache.setDefault(path, value, true);      }

	/**
	 * A method used to get a cache value/
	 *
	 * @param path Target path
	 * @return Cache value
	 * @since 1.1
	 */
	public String getCache(String path)             { return cache.getConfig().getString(path); }

	/**
	 * A method used to delete a cache path, in other words, used to reset its value.
	 *
	 * @param path Target path
	 * @since 1.1
	 */
	public void deleteCache(String path)            { cache.removeDefault(path);                }
}