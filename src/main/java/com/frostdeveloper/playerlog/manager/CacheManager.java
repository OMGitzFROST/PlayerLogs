package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CacheManager
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final FrostAPI api = plugin.getFrostApi();
	
	private final File cacheFile = api.toFile( ".cache.yml");
	
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
	
	public void deleteCache(String path)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(cacheFile);
		config.set(path, null);
	}
	
	public void delete()
	{
		if (cacheFile.delete()) {
			plugin.log("cache.delete.success");
		}
		else {
			plugin.log("cache.delete.failed");
		}
	}
}
