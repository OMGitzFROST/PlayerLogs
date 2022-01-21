package com.frostdeveloper.playerlogs.service;

import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * A class used to handle collection of metrics and server demographics.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class MetricsService
{
	// CLASS INSTANCES
	private final PlayerLogs plugin    = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final ModuleManager module = plugin.getModuleManager();
	
	/**
	 * A method used to start collect metrics
	 *
	 * @since 1.0
	 */
	public void initialize()
	{
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (config.getBoolean(Config.USE_METRICS)) {
				// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
				int id = 13598;
				Metrics metrics = new Metrics(plugin, id);
				
				metrics.addCustomChart(new SimplePie("locale", () -> config.getString(Config.LOCALE)));
				metrics.addCustomChart(new SimplePie("auto_updater", () -> config.getString(Config.AUTO_UPDATE)));
				metrics.addCustomChart(new SimplePie("use_prefix", () -> config.getString(Config.USE_PREFIX)));
				metrics.addCustomChart(new SimplePie("use_custom_message", () -> config.getString(Config.CUSTOM_MESSAGE)));
				metrics.addCustomChart(new AdvancedPie("modules_in_use", () -> {
					Map<String, Integer> map = new HashMap<>();
					
					for (Module current : module.getRegisteredList()) {
						map.put(current.getFullIdentifier(), 1);
					}
					return map;
				}));
				
				plugin.debug(getClass(), "metrics.collect.success");
			}
		}, 0);
	}
}
