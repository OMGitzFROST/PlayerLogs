package com.frostdeveloper.playerlogs.command;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.manager.ReportManager;
import com.frostdeveloper.playerlogs.manager.UpdateManager;
import com.frostdeveloper.playerlogs.model.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final UpdateManager updater = plugin.getUpdateManager();
	private final ConfigManager config = plugin.getConfigManager();
	private final ModuleManager module = plugin.getModuleManager();
	private final FrostAPI api = plugin.getFrostApi();
	
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
		try {
			User user = new User((OfflinePlayer) sender);
			
			if (label.equalsIgnoreCase("playerlog") || command.getAliases().contains(label)) {
				if (user.hasPermission(Permission.CMD_RELOAD, Permission.CMD_UPDATE)) {
					if (args.length == 1) {
						switch (args[0]) {
							case "update":
								executeUpdate(user);
								break;
							case "reload":
								executeReload(user);
								break;
							default:
								user.sendMessage("plugin.command.invalid", getLabelUsage(command, label));
						}
					}
					else {
						user.sendMessage("plugin.command.invalid", getLabelUsage(command, label));
					}
				}
				else {
					user.sendMessage("plugin.command.denied");
				}
			}
			return true;
		}
		catch (Exception ex) {
			ReportManager.createReport(getClass(), ex, true);
			return true;
		}
	}
	
	/**
	 * A method used to execute our update command
	 *
	 * @since 1.1
	 */
	private void executeUpdate(@NotNull User user)
	{
		if (user.hasPermission(Permission.CMD_UPDATE)) {
			updater.attemptDownload();
			
			if (user.getPlayer().isOnline()) {
				user.sendMessage(updater.getMessage());
				plugin.log(updater.getMessage());
				return;
			}
			plugin.log(updater.getMessage());
		}
		else {
			user.sendMessage("plugin.command.denied");
		}
	}
	
	/**
	 * A method used to execute our reload command
	 *
	 * @since 1.1
	 */
	private void executeReload(@NotNull User user)
	{
		if (user.hasPermission(Permission.CMD_RELOAD)) {
			updater.reload();
			module.correctUserFiles();
			config.attemptUpdate();
			
			if (user.getPlayer().isOnline()) {
				user.sendMessage("plugin.reload.success");
			}
			plugin.log("plugin.reload.success");
		}
		else {
			user.sendMessage("plugin.command.denied");
		}
	}
	
	/*
	 * MISC METHODS
	 */
	
	/**
	 * A method used to get a command usage from a label.
	 *
	 * @param cmd Executed command
	 * @param label References label
	 * @return Command ussage.
	 * @since 1.1
	 */
	private @NotNull String getLabelUsage(@NotNull Command cmd, String label)
	{
		return api.format(cmd.getUsage(), label);
	}
	
	/**
	 * Requests a list of possible completions for a command argument.
	 *
	 * @param sender  Source of the command.  For players tab-completing a command inside of a command block, this
	 *                will be the player, not the command block.
	 * @param command Command which was executed
	 * @param label   The alias used
	 * @param args    The arguments passed to the command, including final partial argument to be completed and
	 *                command label
	 * @return A List of possible completions for the final argument, or null to default to the command executor
	 */
	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		User user = new User((OfflinePlayer) sender);
		
		if (label.equalsIgnoreCase("playerlog") || command.getAliases().contains(label)) {
			List<String> options = new ArrayList<>();
			api.addToList(options,"reload", user.hasPermission(Permission.CMD_RELOAD));
			api.addToList(options,"update", user.hasPermission(Permission.CMD_UPDATE));
			return options;
		}
		return null;
	}
}