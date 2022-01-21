package com.frostdeveloper.playerlogs.model;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.definition.Variable;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * An interface used to define the required classes needed in-order for a module to work.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public abstract class Module
{
	// CLASS INSTANCES
	protected final PlayerLogs plugin = PlayerLogs.getInstance();
	protected final FrostAPI api = plugin.getFrostAPI();
	protected final ModuleManager manager = plugin.getModuleManager();
	
	/**
	 * A method used to return the message assigned to a module
	 *
	 * @return Module message
	 * @since 1.2
	 */
	public abstract String getMessage();
	
	/**
	 * A method used to return the message list assigned to the module.
	 *
	 * @return Message List
	 * @since 1.2
	 */
	public abstract List<String> getMessageList();
	
	/**
	 * A method used to return whether a module is enabled
	 *
	 * @return Module status
	 * @since 1.2
	 */
	public abstract boolean isEnabled();
	
	/**
	 * A method used to return the active handler list for a module.
	 *
	 * @since 1.2
	 */
	public abstract void removeListener();
	
	/**
	 * A method is called once the module is registered, and initializes the assigned arithmetic.
	 *
	 * @since 1.2
	 */
	public void initialize() { Bukkit.getServer().getPluginManager().registerEvents((Listener) this, plugin); }
	
	/**
	 * A method used to return the modules file,
	 *
	 * @param player Target player
	 * @return A valid module file
	 */
	public File getModuleFile(Player player)
	{
		return Util.toFile(manager.getUserDirectory(player), getFullIdentifier() + ".log");
	}
	
	/**
	 * A method used to return the modules file,
	 *
	 * @param player Target offline player
	 * @return A valid module file
	 */
	public File getModuleFile(OfflinePlayer player)
	{
		return Util.toFile(manager.getUserDirectory(player), getFullIdentifier() + ".log");
	}
	
	/**
	 * A method used to determine whether a module is registered.
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	public boolean isRegistered() { return  manager.getRegisteredList().contains(this); }
	
	/**
	 * A method used to return the identifier for a module. The identifier serves as the name of
	 * the module and additionally can be used to track its timer using the cache manager.
	 *
	 * @return Module identifier
	 * @since 1.0
	 */
	public @NotNull String getIdentifier()
	{
		String rawModuleName = this.getClass().getSimpleName().toLowerCase();
		return rawModuleName.replace("module", "");
	}
	
	/**
	 * A method used to return the full identifier for a module.
	 *
	 * @return Full module identifier
	 * @since 1.2
	 */
	public @NotNull String getFullIdentifier()
	{
		String rawModuleName = this.getClass().getSimpleName().toLowerCase();
		return rawModuleName.replace("module", "-module");
	}
	
	/**
	 * A method used to print a modules message to its log file.
	 *
	 * @param player Target player
	 * @param message Target message
	 * @param alternate An alternative message
	 * @since 1.2
	 */
	protected void printToFile(Player player, @NotNull String message, String alternate)
	{
		File playerFile = getModuleFile(player);
		manager.initializeCorrection();
		api.createParent(playerFile);
		
		try {
			if (manager.getBoolean(Config.MODULARIZE)) {
				FileWriter writer = new FileWriter(playerFile, true);
				PrintWriter printer = new PrintWriter(writer);
				
				if (message.contains(Variable.DEFAULT.toVar())) {
					printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(alternate));
				}
				else {
					printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(message));
				}
				printer.close();
			}
			printToFile(message, alternate);
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
	
	/**
	 * A method used to print a modules message list to its log file.
	 *
	 * @param player Target player
	 * @param message Target messages
	 * @param alternate An alternative message
	 * @since 1.2
	 */
	protected void printToFile(Player player, @NotNull List<String> message, String alternate)
	{
		File playerFile = getModuleFile(player);
		manager.initializeCorrection();
		api.createParent(playerFile);
		
		try {
			if (manager.getBoolean(Config.MODULARIZE)) {
				FileWriter writer = new FileWriter(playerFile, true);
				PrintWriter printer = new PrintWriter(writer);
				
				for (String current : message) {
					if (manager.getBoolean(Config.MODULARIZE)) {
						if (message.contains(Variable.DEFAULT.toVar())) {
							printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(alternate));
						}
						else {
							printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(current));
						}
					}
				}
				printer.close();
			}
			printToFile(message, alternate);
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
	
	/**
	 * A method used to print a modules message to its log file. Additionally, if the message contains
	 * the default placeholder, we will default to the alternate message. The alternate message can is
	 * typically the default message when an event is triggered or a custom message created by us.
	 *
	 * @param message Target message
	 * @param alternate An alternative message
	 * @since 1.2
	 */
	protected void printToFile(@NotNull String message, String alternate)
	{
		File logFile = Util.toFile(manager.getLogDirectory(), "global.log");
		api.createParent(logFile);
		
		try {
			FileWriter writer = new FileWriter(logFile, true);
			PrintWriter printer = new PrintWriter(writer);
			
			if (message.contains(Variable.DEFAULT.toVar())) {
				printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(alternate));
			}
			else {
				printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(message));
			}
			printer.close();
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
	
	/**
	 * A method used to print a modules message to its log file. Additionally, if the message list contains
	 * the default placeholder, we will default to the alternate message. The alternate message can is
	 * typically the default message when an event is triggered or a custom message created by us.
	 *
	 * @param message Target messages
	 * @param alternate An alternative message
	 * @since 1.2
	 */
	protected void printToFile(@NotNull List<String> message, String alternate)
	{
		File logFile = Util.toFile(manager.getLogDirectory(), "global.log");
		api.createParent(logFile);
		
		try {
			FileWriter writer = new FileWriter(logFile, true);
			PrintWriter printer = new PrintWriter(writer);
			
			for (String current : message) {
				if (current.contains(Variable.DEFAULT.toVar())) {
					printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(alternate));
				}
				else {
					printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(current));
				}
			}
			printer.close();
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
}
