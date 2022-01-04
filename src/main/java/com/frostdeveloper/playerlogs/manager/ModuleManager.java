package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Yaml;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
import com.frostdeveloper.playerlogs.util.Util;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is designed to handle and register all current and new modules. It also houses methods
 * to get module counts and provides access to required methods used in our module classes.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class ModuleManager
{
	// CLASS INSTANCES
	protected final PlayerLogs plugin = PlayerLogs.getInstance();
	protected final FrostAPI api = plugin.getFrostAPI();
	
	// MODULE LISTS
	private static final ArrayList<Object> registered = new ArrayList<>();
	private static final ArrayList<Object> master = new ArrayList<>();
	
	// CLASS SPECIFIC OBJECTS
	protected File moduleDir  = Util.toFile("log-files");
	protected File globalFile = Util.toFile(moduleDir, "global.log");
	protected final Yaml yaml = new Yaml(Util.toFile("modules.yml"));
	
	/**
	 * A method used to initialize our startup task.
	 *
	 * @since 1.2
	 */
	public void runTask()
	{
		try {
			if (!yaml.getFile().exists()) {
				plugin.saveResource(yaml.getName(), true);
				plugin.debug(getClass(), "index.create.success", yaml.getName());
			}
			else {
				ConfigUpdater.update(plugin, yaml.getName(), Util.toFile(yaml.getName()));
			}
			
			Bukkit.getScheduler().runTaskLater(plugin, () ->
			{
				for (Object module : getRegisteredList()) {
					((Module) module).initialize();
				}
				plugin.log("module.register.total", getCount());
				
			}, 0);
		}
		catch (IOException ex) {
			plugin.getReport().create(getClass(), ex, false);
		}
	}
	
	/**
	 * A method used to reload our configurations, this method will create a config if one does not exist.
	 *
	 * @since 1.2
	 */
	public void reloadConfig()
	{
		shutdown();
		plugin.registerModules();
		runTask();
	}
	
	/**
	 * A method used to initiate our shutdown sequence.
	 *
	 * @since 1.2
	 */
	public void shutdown()
	{
		for (Object module : getRegisteredList()) {
			if (module instanceof Scheduler) {
				((Scheduler) module).shutdown();
			}
		}
		
		getRegisteredList().clear();
		getMasterList().clear();
	}
	
	/**
	 * A method used to print a message to a modules file.
	 *
	 * @param targetFile Target file
	 * @param message Target message
	 * @since 1.2
	 */
	protected void printToFile(File targetFile, String message)
	{
		try {
			api.createParent(targetFile);
			
			FileWriter writer = new FileWriter(targetFile, true);
			PrintWriter printer = new PrintWriter(writer);
			printer.println(api.format("[{0}] {1}", api.getTimeNow(), message));
			writer.close();
		}
		catch (IOException ex) {
			plugin.getReport().create(getClass(), ex, false);
		}
	}
	
	/**
	 * A method used to print a message to a modules file.
	 *
	 * @param targetFiles Target file array
	 * @param message Target message
	 * @since 1.2
	 */
	protected void printToFile(File @NotNull [] targetFiles, String message)
	{
		try {
			for (File currentFile : targetFiles) {
				api.createParent(currentFile);
				
				FileWriter writer = new FileWriter(currentFile, true);
				PrintWriter printer = new PrintWriter(writer);
				printer.println(api.format("[{0}] {1}", api.getTimeNow(), message));
				writer.close();
			}
		}
		catch (IOException ex) {
			plugin.getReport().create(getClass(), ex, false);
		}
	}
	
	/**
	 * A method used to add a module into our registry.
	 *
	 * @param module Target module
	 * @since 1.2
	 */
	protected void addToRegistry(Module module)
	{
		if (!getRegisteredList().contains(module)) {
			getRegisteredList().add(module);
			
			Validate.isTrue(getRegisteredList().contains(module));
			
			if (getRegisteredList().contains(module)) {
				plugin.debug(getClass(), "module.register.success", module.getIdentifier());
			}
		}
	}
	
	/**
	 * A method used to add a module into our master list.
	 *
	 * @param module Target module
	 * @since 1.2
	 */
	protected void addToMaster(Module module)
	{
		if (!getMasterList().contains(module)) {
			getMasterList().add(module);
		}
		Validate.isTrue(getMasterList().contains(module));
	}
	
	/**
	 * A method used to return an instance of this class' required configuration file
	 *
	 * @return Configuration map
	 * @since 1.2
	 */
	protected FileConfiguration getConfig()      { return yaml.getConfig();                                          }
	
	/**
	 * A method used to return the registered list of modules, the registered list is defined as
	 * a list that contains only operational modules.
	 *
	 * @return The registered list
	 * @since 1.2
	 */
	public ArrayList<Object> getRegisteredList() { return registered;                                                }
	
	/**
	 * A method used to return the master list of modules, the master list is defined as
	 * a list that contains ALL Available modules, indiscriminately of operation status.
	 *
	 * @return The master list
	 * @since 1.2
	 */
	public ArrayList<Object> getMasterList()     { return master;                                                    }
	
	/**
	 * A method used to return a player directory for a module file.
	 *
	 * @param player Target player
	 * @return Player directory
	 * @since 1.2
	 */
	public File getPlayerDir(OfflinePlayer player)
	{
		if (getConfig().getBoolean(Config.USE_UUID.getPath())) {
			//TO UUID
			if (Util.toFile(moduleDir, player.getName()).exists()) {
				api.renameFile(Util.toFile(moduleDir, player.getName()), player.getUniqueId().toString());
			}
		}
		else {
			// TO NAME
			if (Util.toFile(moduleDir, player.getUniqueId().toString()).exists()) {
				api.renameFile(Util.toFile(moduleDir, player.getUniqueId().toString()), player.getName());
			}
		}
		
		if (getConfig().getBoolean(Config.USE_UUID.getPath())) {
			return Util.toFile(moduleDir, player.getUniqueId().toString());
		}
		return Util.toFile(moduleDir, Objects.requireNonNull(player.getName()));
	}
	
	/**
	 * A method used to return the current count of modules running.
	 *
	 * @return Operation count.
	 * @since 1.2
	 */
	public String getCount()                     { return getRegisteredList().size() + "/" + getMasterList().size(); }
}