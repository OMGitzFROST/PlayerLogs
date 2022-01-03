package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class JoinModule extends ModuleManager implements Module, Listener
{
	// CLASS SPECIFIC OBJECTS
	private final String identifier = "join-module";
	private final Config permission = Config.MODULE_JOIN_ENABLED;
	private final Config message    = Config.MODULE_JOIN_MSG;
	
	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize() { Bukkit.getServer().getPluginManager().registerEvents(this, plugin); }
	
	/**
	 * An event handler used to listen for when a player joins the server and log the event
	 * to a file.
	 *
	 * @param event Triggered event
	 * @since 1.1
	 */
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		Player player   = event.getPlayer();
		File playerFile = Util.toFile(getPlayerDir(player), "{0}.log", identifier);
		File[] logFiles = {globalFile, playerFile};
		
		boolean requireDefault = Objects.requireNonNull(getConfig().getString(message.getPath())).equalsIgnoreCase("DEFAULT");
		String msg = requireDefault ? event.getJoinMessage() : Objects.requireNonNull(getConfig().getString(message.getPath()));
		
		if (getConfig().getBoolean(Config.MODULARIZE.getPath())) {
			printToFile(logFiles, api.stripColor(msg));
		}
		else {
			printToFile(globalFile, api.stripColor(msg));
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2
	 */
	@Override
	public void registerModule()
	{
		addToMaster(this);
		
		if (getConfig().getBoolean(permission.getPath())) {
			addToRegistry(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	@Override
	public boolean isRegistered()          { return getRegisteredList().contains(this); }
	
	/**
	 * {@inheritDoc}
	 *
	 * @return Module identifier
	 * @since 1.0
	 */
	@Override
	public @NotNull String getIdentifier() { return identifier;                         }
}
