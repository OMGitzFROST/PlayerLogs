package com.frostdeveloper.playerlog;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.listener.ActivityListener;
import com.frostdeveloper.playerlog.manager.*;
import com.frostdeveloper.playerlog.util.LogFilter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class PlayerLog extends JavaPlugin
{
	private static PlayerLog instance;
	
	@Override
	public void onEnable()
	{
		try {
			instance = this;
			getLogger().setFilter(new LogFilter());
			getUpdateManager().runTask();
			getMetricsManager().runTask();
			
			getConfigManager().createFile();
			getLocaleManager().createFile();
			
			getServer().getPluginManager().registerEvents(new ActivityListener(), this);
			
			log("plugin.enable.success", getDescription().getVersion());
		}
		catch (Exception ex) {
			ReportManager.createReport(ex, true);
		}
	}
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void log(String key, Object... param)
	{
		getLogger().log(Level.INFO, getFrostApi().format(getLocaleManager().getMessage(key)), param);
	}
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param level Level of severity
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void log(Level level, String key, Object... param)
	{
		getLogger().log(level, getFrostApi().format(getLocaleManager().getMessage(key)), param);
	}
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param cl    Class executor
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void log(@NotNull Class<?> cl, String key, Object... param)
	{
		String preMSG = getFrostApi().format("[{0}] ", cl.getSimpleName());
		getLogger().log(Level.INFO, preMSG + getLocaleManager().getMessage(key), param);
	}
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param cl    Class executor
	 * @param level Level of severity
	 * @param key   Message key
	 * @param param Optional Parameters
	 * @since 1.0
	 */
	public void log(@NotNull Class<?> cl, Level level, String key, Object... param)
	{
		String preMSG = getFrostApi().format("[{0}] ", cl.getSimpleName());
		getLogger().log(level, preMSG + getLocaleManager().getMessage(key), param);
	}
	
	/*
	 * DEBUG LOGGERS
	 */
	
	/**
	 * A method used to log debug messages to the console.
	 *
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void debug(String key, Object... param)
	{
		getLogger().log(Level.INFO, "[DEBUG] " + getFrostApi().format(getLocaleManager().getMessage(key)), param);
	}
	
	/**
	 * A method used to log debug messages to the console.
	 *
	 * @param level Level of severity
	 * @param key   Message key
	 * @param param Optional Parameters
	 * @since 1.0
	 */
	public void debug(Level level, String key, Object... param)
	{
		getLogger().log(level, "[DEBUG] " + getFrostApi().format(getLocaleManager().getMessage(key)), param);
	}
	
	/**
	 * A method used to log debug messages to the console.
	 *
	 * @param cl    Class executor
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void debug(@NotNull Class<?> cl, String key, Object... param)
	{
		String preMSG = getFrostApi().format("[{0}] [DEBUG] ", cl.getSimpleName());
		getLogger().log(Level.INFO, preMSG + getLocaleManager().getMessage(key), param);
	}
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param cl    Class executor
	 * @param level Level of severity
	 * @param key   Message key
	 * @param param Optional Parameters
	 * @since 1.0
	 */
	public void debug(@NotNull Class<?> cl, Level level, String key, Object... param)
	{
		String preMSG = getFrostApi().format("[{0}] [DEBUG] ", cl.getSimpleName());
		getLogger().log(level, preMSG + getLocaleManager().getMessage(key), param);
	}
	
	/*
	 * CLASS INSTANCE GETTERS
	 */
	
	public static PlayerLog getInstance()            { return instance; }
	
	@Contract (value = " -> new", pure = true)
	public @NotNull FrostAPI getFrostApi()           { return new FrostAPI(this); }
	
	@Contract (" -> new")
	public @NotNull LogManager getLogManager()       { return new LogManager();            }
	
	@Contract (" -> new")
	public @NotNull ConfigManager getConfigManager()   { return new ConfigManager();         }
	
	@Contract (" -> new")
	public @NotNull LocaleManager getLocaleManager()   { return new LocaleManager();         }
	
	@Contract (" -> new")
	public @NotNull UpdateManager getUpdateManager()   { return new UpdateManager();         }
	
	
	public @NotNull MetricsManager getMetricsManager() { return new MetricsManager();        }
	
	@Contract (" -> new")
	public @NotNull CacheManager getCacheManager()     { return new CacheManager();          }
}
