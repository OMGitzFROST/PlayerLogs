package com.frostdeveloper.playerlog.listener;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.util.Permission;
import com.frostdeveloper.playerlog.PlayerLog;
import com.frostdeveloper.playerlog.manager.UpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class JoinListener implements Listener
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final UpdateManager updater = plugin.getUpdateManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		if (api.hasPermission(player, Permission.UPDATE)){
			
			if (updater.getResult() == UpdateManager.Result.AVAILABLE) {
				player.sendMessage("update.result.available");
			}
		}
	}
}
