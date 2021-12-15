package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.playerlog.PlayerLog;
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
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	
	/**
	 * A method used to initialize our metrics collection, it will gather various metrics
	 * used to better develop our plugin.
	 *
	 * @since 1.0
	 */
	public void runTask()
	{
		if (config.getBoolean(ConfigManager.Config.USE_METRICS)) {
			int id = 13598;
			Metrics metrics = new Metrics(plugin, id);
			
			metrics.addCustomChart(new SimplePie("locale", () -> config.getString(ConfigManager.Config.LOCALE)));
			metrics.addCustomChart(new SimplePie("auto_updater", () -> config.getString(ConfigManager.Config.AUTO_UPDATE)));
			metrics.addCustomChart(new SimplePie("use_prefix", () -> config.getString(ConfigManager.Config.USE_PREFIX)));
			metrics.addCustomChart(new SimplePie("use_custom_message", () -> config.getString(ConfigManager.Config.CUSTOM_MESSAGE)));
			
			plugin.log(getClass(), "metrics.collect.success");
		}
		else {
			plugin.log(getClass(), "metrics.collect.disabled");
		}
	}
}