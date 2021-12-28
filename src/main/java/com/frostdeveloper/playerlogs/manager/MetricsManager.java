package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
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
public class MetricsManager extends ModuleManager implements Module, Scheduler
{
	// CLASS INSTANCES
	private final ConfigManager config = plugin.getConfigManager();
	private final CacheManager cache = plugin.getCacheManager();
	
	private final String identifier = "metric-module";
	private final String cacheIdentifier = "metric-timer";
	private final Config permission = Config.USE_METRICS;
	private static BukkitTask task;
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void initialize() { start(); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void start()
	{
		String cachedTimer = cache.getCache(cacheIdentifier) != null ? cache.getCache(cacheIdentifier) : "0";
		
		task  = new BukkitRunnable() {
			int counter = Integer.parseInt(cachedTimer);
			
			@Override
			public void run() {
				final int interval = api.toMinute(30);
				
				// STOP TASK IN-CASE THE DATA FOLDER IS DELETED
				if (!plugin.getDataFolder().exists() || !isRegistered()) {
					shutdown();
					return;
				}
				
				// IF COUNTER IS GREATER THAN INTERVAL, DELETE CACHE.
				if (counter > interval) {
					cache.deleteCache(cacheIdentifier);
				}
				
				// IF COUNTER IS LESS THAN INTERVAL, ADD TO COUNTER AND SET CACHE
				if (counter < interval) {
					counter++;
					cache.setCache(cacheIdentifier, counter);
				}
				else {
					// IF ALL CHECKS PASS, ATTEMPT UPDATE AND RESET CACHE
					int id = 13598;
					Metrics metrics = new Metrics(plugin, id);
					
					metrics.addCustomChart(new SimplePie("locale", () -> config.getString(Config.LOCALE)));
					metrics.addCustomChart(new SimplePie("auto_updater", () -> config.getString(Config.AUTO_UPDATE)));
					metrics.addCustomChart(new SimplePie("use_prefix", () -> config.getString(Config.USE_PREFIX)));
					metrics.addCustomChart(new SimplePie("use_custom_message", () -> config.getString(Config.CUSTOM_MESSAGE)));
					
					plugin.log(getClass(), "metrics.collect.success");
					
					counter = 0;
					cache.setCache(cacheIdentifier, counter);
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void registerModule()
	{
		addToMaster(this);
		
		if (config.getBoolean(permission)) {
			addToRegistry(this);
		}
		else {
			plugin.debug(getClass(), "metrics.collect.disabled");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void shutdown()
	{
		cancel();
		
		if (isCancelled()) {
			plugin.debug(getClass(), "module.unload.success", identifier);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	@Override
	public boolean isRegistered() { return getRegisteredList().contains(this); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	public String getIdentifier() { return identifier;                         }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public int getTaskId() { return getRegisteredList().indexOf(this); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public boolean isCancelled() { return task == null || task.isCancelled(); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public void cancel()
	{
		if (task != null) {
			task.cancel();
		}
	}
}