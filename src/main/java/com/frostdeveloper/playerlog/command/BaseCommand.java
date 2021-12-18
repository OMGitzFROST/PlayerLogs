package com.frostdeveloper.playerlog.command;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.PlayerLog;
import com.frostdeveloper.playerlog.manager.ConfigManager;
import com.frostdeveloper.playerlog.manager.UpdateManager;
import com.frostdeveloper.playerlog.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BaseCommand implements CommandExecutor
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final UpdateManager updater = plugin.getUpdateManager();
	private final ConfigManager config = plugin.getConfigManager();
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
		if (label.equalsIgnoreCase("playerlog") || command.getAliases().contains(label)) {
			
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
		return true;
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
	}
	
	private @NotNull String getLabelUsage(@NotNull Command cmd, String label)
	{
		return api.format(cmd.getUsage(), label);
	}
	
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
}
