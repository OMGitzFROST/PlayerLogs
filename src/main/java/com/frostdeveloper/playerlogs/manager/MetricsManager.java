package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
	private final CacheManager cache = plugin.getCacheManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private static BukkitTask task;
	int id = 13598;
	
	/**
	 * A method used to initialize our metrics collection, it will gather various metrics
	 * used to better develop our plugin.
	 *
	 * @since 1.0
	 */
	public void runTask()
	{
		String cachedTimer = cache.getCache("update-timer");
		
		task = new BukkitRunnable() {
			int counter = cachedTimer != null ? Integer.parseInt(cachedTimer) : 0;
			final int interval = api.toMinute(30);
			
			@Override
			public void run() {
				// STOP TASK IN-CASE THE DATA FOLDER IS DELETED
				if (!plugin.getDataFolder().exists()) {
					this.cancel();
					return;
				}
				
				// IF COUNTER IS GREATER THAN INTERVAL, DELETE CACHE.
				if (counter > interval) {
					cache.deleteCache("metrics-timer");
				}
				
				// IF COUNTER IS LESS THAN INTERVAL, ADD TO COUNTER AND SET CACHE
				if (counter < interval) {
					counter++;
					cache.setCache("metrics-timer", counter);
				}
				else {
					// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
					
					if (config.getBoolean(Config.USE_METRICS)) {
						Metrics metrics = new Metrics(plugin, id);
						
						metrics.addCustomChart(new SimplePie("locale", () -> config.getString(Config.LOCALE)));
						metrics.addCustomChart(new SimplePie("version", () -> config.getString(Config.VERSION)));
						metrics.addCustomChart(new SimplePie("auto_updater", () -> config.getString(Config.AUTO_UPDATE)));
						metrics.addCustomChart(new SimplePie("use_prefix", () -> config.getString(Config.USE_PREFIX)));
						metrics.addCustomChart(new SimplePie("use_custom_message", () -> config.getString(Config.CUSTOM_MESSAGE)));
						
						plugin.log(MetricsManager.class, "metrics.collect.success");
					}
					else {
						plugin.log(MetricsManager.class, "metrics.collect.disabled");
					}
					
					counter = 0;
					cache.setCache("metrics-timer", counter);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	/**
	 * A method used to stop our updater task, this method will stop the current scheduler, setting it up for a successful
	 * server state change.
	 *
	 * @since 1.1
	 */
	public void stopTask()
	{
		if (getTask().isCancelled()) {
			plugin.log("metrics.disable.unchanged");
		}
		else {
			getTask().cancel();
			plugin.debug("metrics.task.disabled");
		}
	}
	
	/**
	 * A method used to return an instance of our updater task. This allows us to modify our task.
	 *
	 * @return Our updater task.
	 * @since 1.0
	 */
	public BukkitTask getTask() { return task; }
}