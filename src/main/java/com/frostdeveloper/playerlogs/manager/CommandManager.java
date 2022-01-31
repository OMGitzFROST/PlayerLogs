package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.model.Manager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

/**
 * A class used to handle all tasks related to our commands and its functions.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class CommandManager implements Manager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should be used to
	 * create a configuration file and potentially include patch updates.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize() {}
	
	/**
	 * A method used to register our commands and initiate the defined class as
	 * an executor
	 *
	 * @param cmd Target command
	 * @param executor Command Executor
	 * @since 1.2
	 */
	public void register(String cmd, CommandExecutor executor)
	{
		Objects.requireNonNull(plugin.getCommand(cmd)).setExecutor(executor);
	}
	
	/**
	 * A method used to register our commands and initiate the defined class as
	 * both an executor and a tab completer.
	 *
	 * @param cmd Target command
	 * @param executor Command Executor
	 * @param tabComplete Whether the class is a tab completer.
	 * @since 1.2
	 */
	public void register(String cmd, CommandExecutor executor, boolean tabComplete)
	{
		Objects.requireNonNull(plugin.getCommand(cmd)).setExecutor(executor);
		
		if (tabComplete) {
			Objects.requireNonNull(plugin.getCommand(cmd)).setTabCompleter((TabCompleter) executor);
		}
	}
}
