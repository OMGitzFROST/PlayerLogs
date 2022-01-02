package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
import com.frostdeveloper.playerlogs.util.Placeholder;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class RamModule extends ModuleManager implements Module, Scheduler
{
	// CLASS SPECIFIC OBJECTS
	private final String identifier = "ram-module";
	public final File moduleFile = Util.toFile(moduleDir, "{0}.log", identifier);
	private final Config permission = Config.MODULE_RAM_ENABLED;
	private final Config message    = Config.MODULE_RAM_MSG;
	private final Config cooldown   = Config.MODULE_RAM_COOLDOWN;
	private static BukkitTask task;
	
	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize()               { start();                                   }
	
	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	@Override
	public void start()
	{
		task = new BukkitRunnable() {
			int counter = 0;
			
			@Override
			public void run() {
				int interval = api.convertToTime(yaml.getString(cooldown.getPath()));
				
				// STOP TASK IN-CASE THE DATA FOLDER IS DELETED
				if (!plugin.getDataFolder().exists()) {
					this.cancel();
					return;
				}
				
				// IF TASK IS NO LONGER REGISTERED, ATTEMPT SHUTDOWN.
				if (!isRegistered()) {
					shutdown();
				}
				
				// IF COUNTER IS GREATER THAN INTERVAL, DELETE CACHE.
				if (counter > interval) {
					counter = 0;
				}
				
				// IF COUNTER IS LESS THAN INTERVAL, ADD TO COUNTER AND SET CACHE
				if (counter < interval) {
					counter++;
				}
				else {
					String rawMsg = "Used: %server_ram_used% MB | Free: %server_ram_free% MB | Total: %server_ram_total% MB | Max: %server_ram_max% MB";
					
					if (getConfig().isList(Config.MODULE_RAM_MSG.getPath())) {
						List<String> msg = api.stripColor(getConfig().getStringList(message.getPath()));
						
						for (String string : msg) {
							if (string.equalsIgnoreCase("DEFAULT")) {
								printToFile(moduleFile, Placeholder.set(rawMsg));
							}
							else {
								printToFile(moduleFile, Placeholder.set(string));
							}
						}
					}
					
					if (getConfig().isString(Config.MODULE_RAM_MSG.getPath())) {
						String msg = api.stripColor(getConfig().getString(message.getPath()));
						printToFile(moduleFile, Placeholder.set(msg));
					}
					
					counter = 0;
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	@Override
	public void registerModule()
	{
		addToMaster(this);
		
		if (getConfig().getBoolean(permission.getPath())) {
			addToRegistry(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
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
	public boolean isRegistered()          { return getRegisteredList().contains(this); }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	public @NotNull String getIdentifier() { return identifier;                         }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public int getTaskId()                 { return getRegisteredList().indexOf(this);  }
	
	/**
	 * {@inheritDoc}
	 * @since 1.2
	 */
	@Override
	public boolean isCancelled()           { return task == null || task.isCancelled(); }
	
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
