package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.core.Yaml;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.util.Util;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This class is designed to handle and register all current and new modules. It also houses methods
 * to get module counts and provides access to required methods used in our module classes.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class ModuleManager
{
	protected final PlayerLogs plugin = PlayerLogs.getInstance();
	protected final FrostAPI api = plugin.getFrostApi();
	
	private static final ArrayList<Module> registered = new ArrayList<>();
	private static final ArrayList<Module> master = new ArrayList<>();
	
	protected File moduleDir = Util.toFile("log-files");
	protected final Yaml yaml = new Yaml(Util.toFile("modules.yml"));
	
	public void runTask()
	{
		try {
			if (!yaml.getFile().exists()) {
				yaml.createFile();
				plugin.log("index.create.success", yaml.getName());
			}
			else {
				ConfigUpdater.update(plugin, yaml.getName(), Util.toFile(yaml.getName()));
				plugin.log("index.search.success", yaml.getName());
			}
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for (Module module : getRegisteredList()) {
						module.initialize();
					}
				}
				
			}.runTaskLater(plugin, 0);
			
			plugin.log("module.register.total", getCount());
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	public void shutdown()
	{
		for (Module module : getRegisteredList()) {
			module.shutdown();
		}
		getRegisteredList().clear();
		getMasterList().clear();
	}
	
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
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	protected void printToFile(File @NotNull [] targetFiles, String message)
	{
		try {
			for (File currentFile : targetFiles) {
				api.createParent(currentFile);
				
				FileWriter writer = new FileWriter(currentFile, true);
				PrintWriter printer = new PrintWriter(writer);
				printer.println(message);
				writer.close();
			}
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
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
	public ArrayList<Module> getRegisteredList() { return registered;                                                }
	
	/**
	 * A method used to return the master list of modules, the master list is defined as
	 * a list that contains ALL Available modules, indiscriminately of operation status.
	 *
	 * @return The master list
	 * @since 1.2
	 */
	public ArrayList<Module> getMasterList()     { return master;                                                    }
	
	/**
	 * A method used to return the current count of modules running.
	 *
	 * @return Operation count.
	 * @since 1.2
	 */
	public String getCount()                     { return getRegisteredList().size() + "/" + getMasterList().size(); }
}