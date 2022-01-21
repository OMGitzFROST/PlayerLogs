package com.frostdeveloper.playerlogs.command;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.LocaleManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.service.UpdateService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class used to implement necessary methods required to make our base command work as intended
 *
 * @author OMGitzFROST
 * @since 1.1
 */
public class BaseCommand implements CommandExecutor, TabCompleter
{
	// CLASS INSTANCES
	private final PlayerLogs plugin     = PlayerLogs.getInstance();
	private final ConfigManager config  = plugin.getConfigManager();
	private final ModuleManager module  = plugin.getModuleManager();
	private final LocaleManager locale  = plugin.getLocaleManager();
	private final UpdateService updater = plugin.getUpdateManager();
	private final FrostAPI api          = plugin.getFrostAPI();
	
	/**
	 * Executes the given command, returning its success.
	 * <br>
	 * If false is returned, then the "usage" plugin.yml entry for this command (if defined) will be sent to the
	 * player.
	 *
	 * @param sender  Source of the command
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    Passed command arguments
	 * @return true if a valid command, otherwise false
	 * @since 1.1
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		if (command.getAliases().contains(label)) {
			
			if (args.length != 1) {
				executeInvalid(sender, command, label);
				return true;
			}
			
			switch (args[0]) {
				case "update":
					executeUpdate(sender);
					break;
				case "reload":
					executeReload(sender);
					break;
				case "help":
				case "purge":
					executeUnsupported(sender);
					break;
				case "modules":
				case "module":
					executeModule(sender);
					break;
				default:
					executeInvalid(sender, command, label);
					break;
			}
		}
		return true;
	}
	
	/**
	 * A method used to execute our update task.
	 *
	 * @param sender Entity that executed the command
	 * @since 1.1
	 */
	private void executeUpdate(CommandSender sender)
	{
		if (Permission.isPermitted(sender, Permission.CMD_UPDATE)) {
			updater.initialize();
			
			if (sender instanceof Player) {
				sender.sendMessage(updater.getMessage());
			}
		}
		else {
			sendMessage(sender, "plugin.command.denied");
		}
	}
	
	/**
	 * A method used to execute our reload task.
	 *
	 * @param sender Entity that executed the command
	 * @since 1.1
	 */
	private void executeReload(CommandSender sender)
	{
		if (Permission.isPermitted(sender, Permission.CMD_RELOAD)) {
			// CONFIGURATION RELOAD
			config.reload();
			locale.reload();
			module.reload();
			
			// PREFORM AUDITS
			module.initializeAudit();
			
			if (sender instanceof Player) {
				sendMessage(sender, "plugin.reload.success");
			}
			plugin.log("plugin.reload.success");
		}
	}
	
	/**
	 * A method used to execute our module task.
	 *
	 * @param sender Entity that executed the command.
	 * @since 1.2
	 */
	public void executeModule(@NotNull CommandSender sender)
	{
		if (Permission.isPermitted(sender, Permission.CMD_MODULE)) {
			sender.sendMessage("Registered Modules: (" + module.getCount(module.getRegisteredList()) + ") " + Arrays.toString(module.toList()));
		}
	}
	
	/*
	 * INVALID COMMAND HANDLERS
	 */
	
	/**
	 * A method used to notify the CommandSender is a command is available but is currently
	 * unsupported
	 *
	 * @param sender The entity that executed the command
	 * @since 1.2
	 */
	private void executeUnsupported(CommandSender sender)
	{
		if (sender instanceof Player) {
			sendMessage(sender, "plugin.command.unsupported");
		}
		else {
			plugin.log("plugin.command.unsupported");
		}
	}
	
	/**
	 * A method used to notify the CommandSender if a command is invalid
	 *
	 * @param sender The entity that executed the command
	 * @param command The command executed
	 * @param label The label used to execute command
	 * @since 1.2
	 */
	private void executeInvalid(CommandSender sender, Command command, String label)
	{
		if (sender instanceof Player) {
			sendMessage(sender, "plugin.command.invalid", api.format(command.getUsage(), label));
		}
		else {
			plugin.log("plugin.command.invalid", api.format(command.getUsage(), label));
		}
	}
	
	/*
	 * COMMAND SENDER MESSAGING
	 */
	
	/**
	 * A method used to send a command sender a localized message
	 *
	 * @param sender The entity that executed a command
	 * @param message The property key
	 * @param param Optional parameters
	 * @since 1.2
	 */
	private void sendMessage(@NotNull CommandSender sender, String message, Object... param)
	{
		String prefix = config.getString(Config.PREFIX);
		boolean usePrefix = config.getBoolean(Config.USE_PREFIX);
		String msg = usePrefix ? prefix + " " +  locale.getMessage(message) : locale.getMessage(message);
		
		sender.sendMessage(api.format(msg, param));
	}
	
	/*
	 * TAB COMPLETER
	 */
	
	/**
	 * Requests a list of possible completions for a command argument.
	 *
	 * @param sender  Source of the command.  For players tab-completing a command inside of a command block, this
	 *                will be the player, not the command block.
	 * @param command Command which was executed
	 * @param alias   The alias used
	 * @param args    The arguments passed to the command, including final partial argument to be completed and
	 *                command label
	 * @return A List of possible completions for the final argument, or null to default to the command executor
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
	{
		if (command.getAliases().contains(alias)) {
			if (args.length == 1) {
				List<String> options = new ArrayList<>();
				api.addToList(options,"reload", Permission.isPermitted(sender, Permission.CMD_RELOAD));
				api.addToList(options,"update", Permission.isPermitted(sender, Permission.CMD_UPDATE));
				api.addToList(options, "module", Permission.isPermitted(sender, Permission.CMD_MODULE));
				return options;
			}
		}
		return null;
	}
}