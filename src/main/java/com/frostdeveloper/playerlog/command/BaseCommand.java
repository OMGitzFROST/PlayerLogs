package com.frostdeveloper.playerlog.command;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import com.frostdeveloper.playerlog.manager.ActivityManager;
import com.frostdeveloper.playerlog.manager.ConfigManager;
import com.frostdeveloper.playerlog.manager.ReportManager;
import com.frostdeveloper.playerlog.manager.UpdateManager;
import com.frostdeveloper.playerlog.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseCommand implements CommandExecutor, TabCompleter
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final UpdateManager updater = plugin.getUpdateManager();
	private final ConfigManager config = plugin.getConfigManager();
	private final ActivityManager activity = plugin.getLogManager();
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
			if (label.equalsIgnoreCase("playerlog") || command.getAliases().contains(label)) {
				if (api.hasPermission(sender, Permission.CMD_RELOAD, Permission.CMD_UPDATE)) {
					if (args.length == 1) {
						switch (args[0]) {
							case "update":
								executeUpdate(sender);
								break;
							case "reload":
								executeReload(sender);
								break;
							default:
								sendMessage(sender, "plugin.command.invalid", getLabelUsage(command, label));
						}
					}
					else {
						sendMessage(sender, "plugin.command.invalid", getLabelUsage(command, label));
					}
				}
				else {
					sendMessage(sender, "plugin.command.denied");
				}
			}
			return true;
		}
		catch (Exception ex) {
			ReportManager.createReport(ex, true);
			return true;
		}
	}
	
	private void executeUpdate(CommandSender sender)
	{
		if (api.hasPermission(sender, Permission.CMD_UPDATE)) {
			updater.attemptDownload();
			
			if (sender instanceof Player) {
				sendMessage(sender, updater.getMessage());
				plugin.log(updater.getMessage());
				return;
			}
			plugin.log(updater.getMessage());
		}
		else {
			sendMessage(sender, "plugin.command.denied");
		}
	}
	
	private void executeReload(CommandSender sender)
	{
		if (api.hasPermission(sender, Permission.CMD_RELOAD)) {
			updater.reload();
			
			if (sender instanceof Player) {
				sendMessage(sender, "plugin.reload.success");
			}
			plugin.log("plugin.reload.success");
		}
		else {
			sendMessage(sender, "plugin.command.denied");
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
	 * A method used to send a player a localized message.
	 *
	 * @param sender Command sender
	 * @param message Target Message
	 * @param param Optional parameters
	 * @since 1.1
	 */
	private void sendMessage(@NotNull CommandSender sender, String message, Object... param)
	{
		boolean usePrefix = config.getBoolean(ConfigManager.Config.USE_PREFIX);
		String prefix = config.getString(ConfigManager.Config.PREFIX);
		
		if (usePrefix) {
			sender.sendMessage(api.format(prefix + " " + plugin.getLocaleManager().getMessage(message), param));
		}
		else {
			sender.sendMessage(api.format(plugin.getLocaleManager().getMessage(message), param));
		}
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
		if (label.equalsIgnoreCase("playerlog") || command.getAliases().contains(label)) {
			List<String> options = new ArrayList<>();
			api.addToList(options,"reload", api.hasPermission(sender, Permission.CMD_RELOAD));
			api.addToList(options,"update", api.hasPermission(sender, Permission.CMD_UPDATE));
			return options;
		}
		return null;
	}
}
