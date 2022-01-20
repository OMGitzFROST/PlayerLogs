package com.frostdeveloper.playerlogs.model;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	 * A method is called once the module is registered, and initializes the assigned arithmetic.
	 *
	 * @since 1.2
	 */
	public abstract void initialize();
	
	/**
	 * A method used to return the message assigned to a module
	 *
	 * @return Module message
	 * @since 1.2
	 */
	public abstract String getMessage();
	
	/**
	 * A method used to return whether a module is enabled
	 *
	 * @return Module status
	 * @since 1.2
	 */
	public abstract boolean isEnabled();
	
	/**
	 * A method used to determine whether a module is registered.
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	public abstract boolean isRegistered();
	
	/**
	 * A method used to return the identifier for a module. The identifier serves as the name of
	 * the module and additionally can be used to track its timer using the cache manager.
	 *
	 * @return Module identifier
	 * @since 1.0
	 */
	public abstract @NotNull String getIdentifier();
	
	/**
	 * A method used to return the full identifier for a module.
	 *
	 * @return Full module identifier
	 * @since 1.2
	 */
	public abstract @NotNull String getFullIdentifier();
	
	/**
	 * A method used to return the active handler list for a module.
	 *
	 * @since 1.2
	 */
	public abstract void removeListener();
	
	/**
	 * A method used to print a modules message to its log file.
	 *
	 * @param player Target player
	 * @param message Target message
	 * @since 1.2
	 */
	public void printToFile(Player player, String message)
	{
		File playerFile = Util.toFile(manager.getUserDirectory(player), getFullIdentifier() + ".log");
		
		try {
			FileWriter writer = new FileWriter(playerFile, true);
			PrintWriter printer = new PrintWriter(writer);
			printer.println("[" + api.getTodayAsString() + "]: " + api.stripColor(message));
			printer.close();
		}
		catch (IOException ex) {
			plugin.getReport().create(ex);
		}
	}
}
