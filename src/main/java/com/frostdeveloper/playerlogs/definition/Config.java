package com.frostdeveloper.playerlogs.definition;

import com.frostdeveloper.api.exceptions.ResourceNotFound;
import com.frostdeveloper.playerlogs.PlayerLogs;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public enum Config
{
	VERSION("version"),
	LOCALE("locale"),
	AUTO_UPDATE("auto-update"),
	USE_METRICS("use-metrics"),
	USE_PREFIX("use-prefix"),
	PREFIX("prefix"),
	CUSTOM_MESSAGE("custom-message"),
	DEBUG_MODE("debug-log"),
	
	MODULARIZE("modularize"),
	USE_UUID("use-uuid"),
	
	MODULE_JOIN("modules.join"),
	MODULE_QUIT("modules.quit"),
	MODULE_CHAT("modules.chat"),
	MODULE_CMD("modules.cmd"),
	MODULE_DEATH("modules.death"),
	MODULE_WORLD("modules.world-change"),
	MODULE_BREAK("modules.block-break"),
	MODULE_PLACE("modules.block-place"),
	MODULE_TELEPORT("modules.teleport"),
	MODULE_ENCHANT("modules.enchant"),
	MODULE_TEMPLATE(null);
	
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final String key;
	
	Config(String key)
	{
		this.key = key;
	}
	
	public String getKey()     { return key;                 }
	
	public @Nullable String getDefault()
	{
		if (plugin.getResource("config.yml") != null) {
			InputStream input = Objects.requireNonNull(plugin.getResource("config.yml"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			return YamlConfiguration.loadConfiguration(reader).getString(key);
		}
		return null;
	}
}
