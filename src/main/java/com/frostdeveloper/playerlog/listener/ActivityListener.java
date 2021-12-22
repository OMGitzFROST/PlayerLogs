package com.frostdeveloper.playerlog.listener;

import com.frostdeveloper.playerlog.util.Activity;
import com.frostdeveloper.playerlog.PlayerLog;
import com.frostdeveloper.playerlog.manager.ConfigManager;
import com.frostdeveloper.playerlog.manager.ActivityManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to handle our activity listeners
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ActivityListener implements Listener
{
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final ActivityManager log = plugin.getLogManager();
	private final ConfigManager config = plugin.getConfigManager();
	
	/**
	 * An event handler used to listen for when a player sends a chat message
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerChat(@NotNull AsyncPlayerChatEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_CHAT)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.PLAYER_CHAT, event.getPlayer(), event.getPlayer().getDisplayName() + ": " + event.getMessage());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getPlayer().getDisplayName() + ": " + event.getMessage());
		}
	}
	
	/**
	 * An event handler used to listen for when a player executes a command
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_CMD)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.PLAYER_COMMAND, event.getPlayer(), event.getPlayer().getDisplayName() + " issued server command: " + event.getMessage());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getPlayer().getDisplayName() + " issued server command: " + event.getMessage());
		}
	}
	
	/**
	 * An event handler used to listen for when a player joins the server
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_JOIN)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.PLAYER_JOIN, event.getPlayer(), event.getJoinMessage());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getJoinMessage());
		}
	}
	
	/**
	 * An event handler used to listen for when a player leaves the server
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerQuit(@NotNull PlayerQuitEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_QUIT)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.PLAYER_QUIT, event.getPlayer(), event.getQuitMessage());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getQuitMessage());
		}
	}
	
	/**
	 * An event handler used to listen for when a player dies
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerDeath(@NotNull PlayerDeathEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_DEATH)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.PLAYER_DEATH, event.getEntity(), event.getDeathMessage());
			}
			log.logActivity(Activity.ALL, event.getEntity(), event.getDeathMessage());
		}
	}
	
	/**
	 * An event handler used to listen for when a player places a block
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onBlockPlace(@NotNull BlockPlaceEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_PLACE)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.BLOCK_PLACE, event.getPlayer(),event.getPlayer().getDisplayName() + "placed " + event.getBlock().getType());
			}
			log.logActivity(Activity.ALL, event.getPlayer(),event.getPlayer().getDisplayName() + " placed " + event.getBlock().getType());
		}
	}
	
	/**
	 * An event handler used to listen for when a player breaks a block
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onBlockBreak(@NotNull BlockBreakEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_BREAK)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.BLOCK_BREAK, event.getPlayer(), event.getPlayer().getDisplayName() + "broke " + event.getBlock().getType());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getPlayer().getDisplayName()+ " broke " + event.getBlock().getType());
		}
	}
	
	/**
	 * An event handler used to listen for when a player changes worlds
	 *
	 * @param event Event Type
	 * @since 1.0
	 */
	@EventHandler
	public void onWorldChange(@NotNull PlayerChangedWorldEvent event)
	{
		if (config.getBoolean(ConfigManager.Config.MODULE_WORLD)) {
			if (config.getBoolean(ConfigManager.Config.MODULARIZE)) {
				log.logActivity(Activity.WORLD_CHANGE, event.getPlayer(), event.getPlayer().getDisplayName() + " changed worlds to " + event.getPlayer().getWorld().getName());
			}
			log.logActivity(Activity.ALL, event.getPlayer(), event.getPlayer().getDisplayName() + " changed worlds to " + event.getPlayer().getWorld().getName());
		}
	}
}