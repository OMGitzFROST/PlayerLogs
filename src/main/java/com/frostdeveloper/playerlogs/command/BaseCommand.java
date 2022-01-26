package com.frostdeveloper.playerlogs.command;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.LocaleManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.service.UpdateService;
import com.frostdeveloper.playerlogs.util.Util;
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
	private final FrostAPI api          = plugin.getFrostAPI();
	private final ConfigManager config  = plugin.getConfigManager();
	private final ModuleManager module  = plugin.getModuleManager();
	private final LocaleManager locale  = plugin.getLocaleManager();
	private final UpdateService updater = plugin.getUpdateManager();
	
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
		if (command.getAliases().contains(label) || command.getName().equalsIgnoreCase("playerlog")) {
			
			/* /<you are here> */
			if (args.length == 0) {
				executeInvalid(sender, command, label);
				return true;
			}
			
			/* /playerlogs <you are here> */
			switch (args[0]) {
				case "update":
					executeUpdate(sender);
					return true;
				case "reload":
					executeReload(sender);
					return true;
				case "help":
				case "purge":
					executeUnsupported(sender);
					return true;
				case "modules":
				case "module":
					executeModule(sender, command, label, args);
					return true;
				default:
					executeInvalid(sender, command, label);
					return true;
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
			updater.attemptDownload();
			
			if (sender instanceof Player) {
				sendMessage(sender, updater.getMessage());
				updater.initializeLogger();
			}
			else {
				plugin.log(updater.getMessage());
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
			
			// VERIFY MANAGERS
			plugin.initializeAudit();
			locale.initializeAudit();
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
	private void executeModule(@NotNull CommandSender sender, Command cmd, String label,  String[] args)
	{
		if (Permission.isPermitted(sender, Permission.CMD_MODULE)) {
			if (args.length == 1) {
				sendMessage(sender, "module.list.registered",module.getCount(module.getRegisteredList()), Arrays.toString(module.toList()));
			}
			
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("info")) {
					String usage = api.format("/{0} module info <identifier>", label);
					executeInvalid(sender, label, usage);
				}
				else {
					executeInvalid(sender, cmd, label);
				}
			}
			
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("info")) {
					if (module.getModuleByPartial(args[2]) != null) {
						Module target = module.getModuleByPartial(args[2]);
						
						String header = Util.buildHeader(24, '-');
						String body;
						
						if (sender instanceof Player) {
							body   = Util.buildBody(header.length() + 6, api.format("{0}", target.getName()), '+');
						}
						else {
							body   = Util.buildBody(header.length(), api.format("{0}", target.getName()), '+');
						}
						
						sendMessage(sender, header);
						sendMessage(sender, body);
						sendMessage(sender, header);
						
						for (String current : target.getInformation()) {
							sendMessage(sender, current);
						}
						sendMessage(sender, header);
					}
					else {
						String usage = api.format("/{0} module info <identifier>", label);
						executeInvalid(sender, label, usage);
					}
				}
				else {
					executeInvalid(sender, cmd, label);
				}
			}
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
	
	/**
	 * A method used to notify the CommandSender if a command is invalid with a custom usage
	 *
	 * @param sender The entity that executed the command
	 * @param usage Custom usage
	 * @param label The label used to execute command
	 * @since 1.2
	 */
	private void executeInvalid(CommandSender sender, String label, String usage)
	{
		if (sender instanceof Player) {
			sendMessage(sender, "plugin.command.invalid", api.format(usage, label));
		}
		else {
			plugin.log("plugin.command.invalid", api.format(usage, label));
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
		sender.sendMessage(api.format(Util.getPrefix() + Util.format(locale.getMessage(message)), param));
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
		if (command.getAliases().contains(alias) || command.getName().equalsIgnoreCase("playerlog")) {
			// /playerlog <you are here>
			if (args.length == 1) {
				List<String> options = new ArrayList<>();
				api.addToList(options,"reload", Permission.isPermitted(sender, Permission.CMD_RELOAD));
				api.addToList(options,"update", Permission.isPermitted(sender, Permission.CMD_UPDATE));
				api.addToList(options, "module", Permission.isPermitted(sender, Permission.CMD_MODULE));
				return options;
			}
			
			// /playerlog arg1 <you are here>
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("module")) {
					List<String> options = new ArrayList<>();
					api.addToList(options, "info", Permission.isPermitted(sender, Permission.CMD_MODULE_INFO));
					return options;
				}
			}
			
			// /playerlog arg1 arg2 <you are here>
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("info")) {
					List<String> options = new ArrayList<>();
					for (Module current : module.getMasterList()) {
						api.addToList(options, current.getIdentifier(), true /* TODO: ADD NEW METHOD THAT DOES NOT REQUIRE CONDITION */);
					}
					return options;
				}
			}
		}
		return null;
	}
}