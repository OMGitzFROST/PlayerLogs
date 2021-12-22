package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class QuitModule extends ModuleManager implements Module, Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final String moduleIdentifier = "quit-activity";
	private final Config modulePermission = Config.MODULE_QUIT;
	
	@EventHandler
	public void onPlayerQuit(@NotNull PlayerQuitEvent event) throws IOException
	{
		if (isRegistered()) {
			User user = new User(event.getPlayer());
			api.createFile(getModuleFile(user, moduleIdentifier));
			logActivity(getModuleFile(user, moduleIdentifier), event.getQuitMessage());
		}
	}
	
	@Override
	public void registerModule() throws IOException
	{
		addToRegistry(this, moduleIdentifier, modulePermission);
	}
	
	@Override
	public void removeModule()
	{
		getRegistryList().remove(this);
		plugin.log("module.unload.success", moduleIdentifier);
	}
	
	@Override
	public boolean isRegistered() { return getRegistryList().contains(this); }
}
