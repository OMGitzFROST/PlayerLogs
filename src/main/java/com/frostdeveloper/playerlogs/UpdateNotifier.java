package com.frostdeveloper.playerlogs;

import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.definition.UpdateResult;
import com.frostdeveloper.playerlogs.manager.UpdateManager;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to notify players of updates when they become available
 *
 * @since 1.1
 */
public class UpdateNotifier implements Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final UpdateManager updater = plugin.getUpdateManager();
	
	/**
	 * A listener used to announce available updates if one is available
	 *
	 * @param event Triggered event
	 * @since 1.1
	 */
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		
		Player player = event.getPlayer();
		
		if (Permission.isPermitted(player, Permission.CMD_UPDATE)) {

			if (updater.getResult() == UpdateResult.AVAILABLE) {
				player.sendMessage("update.result.available");
			}
		}
	}
}