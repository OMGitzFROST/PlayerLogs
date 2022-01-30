package com.frostdeveloper.playerlogs;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exception.FailedMethodException;
import com.frostdeveloper.api.handler.Report;
import com.frostdeveloper.playerlogs.command.BaseCommand;
import com.frostdeveloper.playerlogs.manager.CommandManager;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.LocaleManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.service.MetricsService;
import com.frostdeveloper.playerlogs.service.UpdateService;
import com.frostdeveloper.playerlogs.util.Util;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public class PlayerLogs extends JavaPlugin
{
	// CLASS INSTANCES
	private static PlayerLogs instance;
	private FrostAPI api;
	
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
			api      = FrostAPI.getInstance();
			getLogger().setFilter(new LogFilter());
			
			getLocaleManager().initialize();
			getConfigManager().initialize();
			getModuleManager().initialize();
			
			getUpdateManager().initialize();
			getMetricsService().initialize();
			
			if (isPAPIHooked()) {
				Plugin papi = getPlugin(PlaceholderAPIPlugin.class);
				File serverExpansion = api.toFile(papi.getDataFolder(), "expansions/server-expansion.jar");
				File playerExpansion = api.toFile(papi.getDataFolder(), "expansions/player-expansion.jar");
				
				if (!serverExpansion.exists()) {
					log(Level.WARNING, "papi.expansion.missing", "server");
				}
				
				if (!playerExpansion.exists()) {
					log(Level.WARNING, "papi.expansion.missing", "player");
				}
				log("plugin.dependency.hooked", papi.getName());
			}
			
			// REGISTER COMMANDS
			getCommandManager().register("playerlog", new BaseCommand(), true);
			
			if (isDeveloperMode()) {
				log(Level.WARNING, "WARNING YOU ARE USING A UNSTABLE VERSION OF THIS PLUGIN");
				log(Level.WARNING, "We recommend you use a stable version since this version");
				log(Level.WARNING, "may contain a lot of bugs or issues. Thank you.");
			}
			
			log("plugin.enable.success", getDescription().getVersion());
		}
		catch (Exception ex) {
			log("plugin.enable.failed", getDescription().getVersion());
			getReport().create(ex);
		}
	}
	
	/**
	 * A method used to verify that all requirements are met for the plugin to work.
	 *
	 * @since 1.2
	 */
	public void initializeAudit()
	{
		if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
			throw new FailedMethodException("Failed to create our data-folder!");
		}
		
		if (!getConfigManager().getFile().exists()) {
			getConfigManager().initialize();
		}
		
		if (!getLocaleManager().getFile().exists()) {
			getLocaleManager().initialize();
		}
		
		if (!getModuleManager().getFile().exists()) {
			getModuleManager().initialize(false);
		}
	}
	
	/**
	 * A method used to return whether this plugin is in developer mode.
	 *
	 * @return Developer mode
	 */
	public boolean isDeveloperMode() { return false;                                                         }
	
	/**
	 * A method used to identify if we successfully hooked into PlaceholderAPI, this
	 * method will return true or false depending on its status.
	 *
	 * @return Dependency hook status.
	 * @since 1.2
	 */
	public boolean isPAPIHooked()    { return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null; }
	
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
		getLogger().log(Level.INFO, getFrostAPI().format(true, getLocaleManager().getMessage(key)), param);
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
		getLogger().log(level, getFrostAPI().format(true, getLocaleManager().getMessage(key)), param);
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
		String preMSG = getFrostAPI().format("[{0}] ", cl.getSimpleName());
		getLogger().log(Level.INFO, preMSG + getFrostAPI().format(true, getLocaleManager().getMessage(key), param));
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
		String preMSG = getFrostAPI().format("[{0}] ", cl.getSimpleName());
		getLogger().log(level, preMSG + getFrostAPI().format(true, getLocaleManager().getMessage(key), param));
	}
	
	/*
	 * DEBUG LOGGERS
	 */
	
	/**
	 * A method used to log a debug message to the console.
	 *
	 * @param key Object instance of a message
	 * @since 1.1
	 */
	public void debug(@NotNull Object key)
	{
		getLogger().log(Level.INFO, "[DEBUG] " + getFrostAPI().format(true, getLocaleManager().getMessage(key.toString())));
	}
	
	/**
	 * A method used to log debug messages to the console.
	 *
	 * @param key   Message key
	 * @param param Optional parameters
	 * @since 1.0
	 */
	public void debug(String key, Object... param)
	{
		getLogger().log(Level.INFO, "[DEBUG] " + getFrostAPI().format(true, getLocaleManager().getMessage(key)), param);
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
		getLogger().log(level, "[DEBUG] " + getFrostAPI().format(true, getLocaleManager().getMessage(key)), param);
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
		String preMSG = getFrostAPI().format(true, "[{0}] [DEBUG] ", cl.getSimpleName());
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
		String preMSG = getFrostAPI().format(true, "[{0}] [DEBUG] ", cl.getSimpleName());
		getLogger().log(level, preMSG + getLocaleManager().getMessage(key), param);
	}
	
	/*
	 * CLASS INSTANCE GETTERS
	 */
	
	/**
	 * A method used to return an instance of the main class.
	 *
	 * @return An instance of the main class
	 * @since 1.0
	 */
	public static PlayerLogs getInstance()
	{
		if (instance == null) {
			instance = new PlayerLogs();
		}
		return instance;
	}
	
	/**
	 * A method used to return an instance of the FrostAPI class
	 *
	 * @return FrostAPI class
	 * @since 1.0
	 */
	public FrostAPI getFrostAPI()                      { return api;                         }
	
	/**
	 * A method used to return an instance of our reporting handler.
	 *
	 * @return Report handler
	 * @since 1.2
	 */
	public Report getReport() { return new Report(Util.toFile("crash-report/report.log")); }

	/**
	 * A method used to return an instance of the MetricsService class
	 *
	 * @return MetricsService class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull MetricsService getMetricsService() { return new MetricsService();        }

	/**
	 * A method used to return an instance of the UpdateService class
	 *
	 * @return UpdateService class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull UpdateService getUpdateManager()   { return new UpdateService();         }
	
	/**
	 * A method used to get an instance of the ModuleManager class
	 *
	 * @return ModuleManager instance;
	 * @since 1.1
	 */
	@Contract (" -> new")
	public @NotNull ModuleManager getModuleManager()   { return new ModuleManager("modules.yml", true); }

	/**
	 * A method used to return an instance of the ConfigManager class
	 *
	 * @return ConfigManager class
	 * @since 1.0
	 */
	@Contract (" -> new")
	public @NotNull ConfigManager getConfigManager() { return new ConfigManager("config.yml", true); }

	/**
	 * A method used to return an instance of the LocaleManager class
	 *
	 * @return LocaleManager class
	 * @since 1.0
	 * */
	@Contract (" -> new")
	public @NotNull LocaleManager getLocaleManager()   { return new LocaleManager();         }
	
	/**
	 * A method used to return an instance of the CommandManager class
	 *
	 * @return CommandManager class
	 * @since 1.2
	 */
	public @NotNull CommandManager getCommandManager() { return new CommandManager();        }
}
