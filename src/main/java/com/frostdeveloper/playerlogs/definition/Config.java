package com.frostdeveloper.playerlogs.definition;

import com.frostdeveloper.playerlogs.PlayerLogs;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An enum used to define our configuration paths as well as their defaults values
 *
 * @since 1.0
 */
public enum Config
{
	/* CONFIGURATION FILE */
	
	/**
	 * A path used to determine the desired locale
	 *
	 * @since 1.0
	 */
	LOCALE("locale"),
	/**
	 * A path used to determine if auto updating should be enabled.
	 *
	 * @since 1.0
	 */
	AUTO_UPDATE("auto-update"),
	/**
	 * A path used to determine if metrics should be collected
	 *
	 * @since 1.0
	 */
	USE_METRICS("use-metrics"),
	/**
	 * A path used to determine if the prefix should be used when messaging.
	 *
	 * @since 1.0
	 */
	USE_PREFIX("use-prefix"),
	/**
	 * A path used to determine define this plugin's prefix in messaging
	 *
	 * @since 1.0
	 */
	PREFIX("prefix"),
	/**
	 * A path used to determine if custom messages will be used
	 *
	 * @since 1.0
	 */
	CUSTOM_MESSAGE("custom-message"),
	/**
	 * A path used to determine if debug messages should be printed
	 *
	 * @since 1.0
	 */
	DEBUG_MODE("debug-log"),
	/**
	 * A path used to determine if we should modularize loggers
	 *
	 * @since 1.1
	 */
	MODULARIZE("modularize"),
	/**
	 * A path used to determine if uuid's should be used when creating player files.
	 *
	 * @since 1.1
	 */
	USE_UUID("use-uuid"),
	
	/* MODULE CONFIGURATION FILE */
	
	/**
	 * A path to determine if the join module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_JOIN_ENABLED("join.module.enabled"),
	/**
	 * A path to the join modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_JOIN_MSG("join.module.message"),
	/**
	 * A path to determine if the quit module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_QUIT_ENABLED("quit.module.enabled"),
	/**
	 * A path to the quit modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_QUIT_MSG("quit.module.message"),
	/**
	 * A path to determine if the chat module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_CHAT_ENABLED("chat-module.enabled"),
	/**
	 * A path to the chat modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_CHAT_MSG("chat-module.message"),
	/**
	 * A path to determine if the command module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_CMD_ENABLED("command-module.enabled"),
	/**
	 * A path to the command modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_CMD_MSG("command-module.message"),
	/**
	 * A path to determine if the death module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_DEATH_ENABLED("death-module.enabled"),
	/**
	 * A path to the death modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_DEATH_MSG("death-module.message"),
	/**
	 * A path to determine if the world-change module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_WORLD_ENABLED("world-change-module.enabled"),
	/**
	 * A path to the change-world modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_WORLD_MSG("world-change-module.message"),
	/**
	 * A path to determine if the break module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_BREAK_ENABLED("block-break-module.enabled"),
	/**
	 * A path to the break modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_BREAK_MSG("block-break-module.message"),
	/**
	 * A path to determine if the place module is enabled.
	 *
	 * @since 1.1
	 */
	MODULE_PLACE_ENABLED("block-place-module.enabled"),
	/**
	 * A path to the place modules logged message.
	 *
	 * @since 1.1
	 */
	MODULE_PLACE_MSG("block-place-module.message"),
	/**
	 * A path to determine if the ram module is enabled.
	 *
	 * @since 1.2
	 */
	MODULE_RAM_ENABLED("ram-module.enabled"),
	/**
	 * A path to the ram modules logged message.
	 *
	 * @since 1.2
	 */
	MODULE_RAM_MSG("ram-module.message"),
	/**
	 * A path to the ram module's cooldown
	 *
	 * @since 1.2
	 */
	MODULE_RAM_COOLDOWN("ram-module.cooldown");
	
	// CLASS SPECIFIC OBJECTS
	private final String path;
	
	/**
	 * A class constructor used to define the requirements when defining
	 * a new/current enum value.
	 *
	 * @param path Target path
	 * @since 1.0
	 */
	Config(String path)     { this.path = path; }
	
	/**
	 * This method is used to return the path for a designated enum value
	 *
	 * @return Desired path.
	 * @since 1.0
	 */
	public String getPath() { return path;     }
	
	/**
	 * This message is designed to search inside our configuration files and retrieve the default
	 * value defined in the file
	 *
	 * @return Default value for a path
	 * @since 1.2
	 */
	public String getDefault()
	{
		InputStream input = PlayerLogs.getInstance().getResource("config.yml");
		Validate.notNull(input);
		FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
		return config.getString(path);
	}
}
