package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A class used to handle our config tasks, this manager will create our file if one
 * does not exist and houses our getters for our configurations.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ConfigManager
{
	private static final PlayerLog plugin = PlayerLog.getInstance();
	private static final FrostAPI api = plugin.getFrostApi();
	
	private final File configFile = new File(plugin.getDataFolder(), "config.yml");
	
	/**
	 * A method used to create our configuration file if one does not exist.
	 *
	 * @since 1.0
	 */
	public void createFile()
	{
		if (!configFile.exists()) {
			plugin.saveResource(configFile.getName(), true);
			plugin.log("index.create.success", configFile.getName());
		}
		else {
			plugin.log("index.search.success", configFile.getName());
		}
	}
	
	/**
	 * A method used to return a string from our configuration
	 *
	 * @param path Configuration path
	 * @return Configuration string
	 * @since 1.0
	 */
	public String getString(@NotNull Config path)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		return config.getString(path.getKey()) != null ? config.getString(path.getKey()) : config.getString(path.getDefault());
	}
	
	/**
	 * A method used to return a boolean value from our configuration, it takes a string and parses it into
	 * a valid boolean.
	 *
	 * @param path Configuration path
	 * @return Parsed boolean
	 * @since 1.0
	 */
	public boolean getBoolean(Config path)
	{
		return Boolean.parseBoolean(getString(path));
	}
	
	/**
	 * A method used to return an instance of our config file.
	 *
	 * @return Our config file
	 * @since 1.0
	 */
	public File getFile() { return configFile; }
	
	/**
	 * An enum used to define our configuration paths as well as their defaults values
	 *
	 * @since 1.0
	 */
	public enum Config {
		VERSION("version", 1.0),
		LOCALE("locale", "en"),
		AUTO_UPDATE("auto-update", true),
		USE_METRICS("use-metrics", true),
		USE_PREFIX("use-prefix", true),
		PREFIX("prefix", "&7[&6" + api.getPrefix() +"&7]"),
		CUSTOM_MESSAGE("custom-message", false),
		DEBUG_MODE("debug-log", false),
		
		MODULARIZE("modularize", false),
		
		MODULE_JOIN("modules.join", true),
		MODULE_QUIT("modules.quit", true),
		MODULE_CHAT("modules.chat", true),
		MODULE_CMD("modules.cmd", true),
		MODULE_DEATH("modules.death", true),
		MODULE_WORLD("modules.world-change", true),
		MODULE_BREAK("modules.block-break", true),
		MODULE_PLACE("modules.block-place", true);
		
		private final String key;
		private final Object def;
		
		Config(String key, Object def)
		{
			this.key = key;
			this.def = def;
		}
		
		public String getKey() { return key; }
		
		public String getDefault() { return String.valueOf(def); }
	}
}