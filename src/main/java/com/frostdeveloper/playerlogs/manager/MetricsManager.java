package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

/**
 * A class used to handle collection of metrics and server demographics.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class MetricsManager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	
	
	public void runTask()
	{
		if (config.getBoolean(Config.USE_METRICS)) {
			// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
			int id = 13598;
			Metrics metrics = new Metrics(plugin, id);
			
			metrics.addCustomChart(new SimplePie("locale", () -> config.getString(Config.LOCALE)));
			metrics.addCustomChart(new SimplePie("auto_updater", () -> config.getString(Config.AUTO_UPDATE)));
			metrics.addCustomChart(new SimplePie("use_prefix", () -> config.getString(Config.USE_PREFIX)));
			metrics.addCustomChart(new SimplePie("use_custom_message", () -> config.getString(Config.CUSTOM_MESSAGE)));
			
			plugin.debug(getClass(), "metrics.collect.success");
		}
	}
}