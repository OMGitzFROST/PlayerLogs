package com.frostdeveloper.playerlog;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.command.BaseCommand;
import com.frostdeveloper.playerlog.listener.ActivityListener;
import com.frostdeveloper.playerlog.listener.JoinListener;
import com.frostdeveloper.playerlog.manager.*;
import com.frostdeveloper.playerlog.util.LogFilter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;

/**
 * A class defined as this plugin's main class, It's used to handle and execute all
 * methods and features found within all classes.
 *
 * @author OMGitzFROSt
 * @since 1.0
 */
public final class PlayerLog extends JavaPlugin
{
	// CLASS INSTANCE
	private static PlayerLog instance;
	
	/**
	 * A method invoked on plugin enable.
	 *
	 * @since 1.0
	 */
	@Override
	public void onEnable()
	{
		try {
			instance = this;
			getLogger().setFilter(new LogFilter());
			getUpdateManager().runTask();
			getMetricsManager().runTask();

			// CREATE FILES
			getConfigManager().createFile();
			getLocaleManager().createFile();

			// REGISTER LISTENERS
			getServer().getPluginManager().registerEvents(new ActivityListener(), this);
			getServer().getPluginManager().registerEvents(new JoinListener(), this);

			// REGISTER COMMANDS
			Objects.requireNonNull(getCommand("playerlog")).setExecutor(new BaseCommand());
			Objects.requireNonNull(getCommand("playerlog")).setTabCompleter(new BaseCommand());

			log("plugin.enable.success", getDescription().getVersion());
		}
		catch (Exception ex) {
			log("plugin.enable.failed", getDescription().getVersion());
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/**
	 * A method invoked on plugin disable.
	 *
	 * @since 1.1
	 */
	@Override
	public void onDisable()
	{
		try {
			getUpdateManager().getTask().cancel();
			
			if (getUpdateManager().getTask().isCancelled()) {
				log("update.task.disabled");
			}
			log("plugin.disable.success", getFrostApi().getVersion());
		}
		catch (Exception ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/*
	 * STANDARD LOGGERS
	 */
	
	/**
	 * A method used to send logging messages to the console.
	 *
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void log(String key, Object... param)
	{
		getLogger().log(Level.INFO, getFrostApi().format(true, getLocaleManager().getMessage(key)), param);
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
		getLogger().log(level, getFrostApi().format(true, getLocaleManager().getMessage(key)), param);
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
		getLogger().log(Level.INFO, preMSG + getFrostApi().format(true, getLocaleManager().getMessage(key), param));
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
		getLogger().log(level, preMSG + getFrostApi().format(true, getLocaleManager().getMessage(key), param));
	}
	
	/*
	 * DEBUG LOGGERS
	 */
	
	/**
	 * A method used to log a debug message to the console.
	 *
	 * @param msg Object instance of a message
	 * @since 1.1
	 */
	public void debug(Object msg) { getLogger().log(Level.INFO, "[DEBUG] " + msg); }
	
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
	
	/**
	 * A method used to return an instance of the main class
	 *
	 * @return Main class
	 * @since 1.0
	 */
	public static PlayerLog getInstance()              { return instance;                    }
	
	/**
	 * A method used to return an instance of the FrostAPI class
	 *
	 * @return FrostAPI class
	 * @since 1.0
	 */
	@Contract (value = " -> new", pure = true)
	public @NotNull FrostAPI getFrostApi()             { return new FrostAPI(this);          }
	
	/**
	 * A method used to return an instance of the ActivityManager class
	 *
	 * @return ActivityManager class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull ActivityManager getLogManager()         { return new ActivityManager();            }
	
	/**
	 * A method used to return an instance of the ConfigManager class
	 *
	 * @return ConfigManager class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull ConfigManager getConfigManager()   { return new ConfigManager();         }
	
	/**
	 * A method used to return an instance of the LocaleManager class
	 *
	 * @return LocaleManager class
	 * @since 1.0
	 * */
	@Contract (" -> new")
	public @NotNull LocaleManager getLocaleManager()   { return new LocaleManager();         }
	
	/**
	 * A method used to return an instance of the MetricsManager class
	 *
	 * @return MetricsManager class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull MetricsManager getMetricsManager() { return new MetricsManager();        }
	
	/**
	 * A method used to return an instance of the UpdateManager class
	 *
	 * @return UpdateManager class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull UpdateManager getUpdateManager()   { return new UpdateManager();        }
	
	@Contract (" -> new")
	public @NotNull ActivityManager getActivityManager()        { return new ActivityManager(); }
}
