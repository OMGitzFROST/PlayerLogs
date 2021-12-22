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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DeathModule extends ModuleManager implements Module, Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final String moduleIdentifier = "death-activity";
	private final Config modulePermission = Config.MODULE_DEATH;
	
	@EventHandler
	public void onPlayerDeath(@NotNull PlayerDeathEvent event) throws IOException
	{
		if (isRegistered()) {
			User user = new User(event.getEntity());
			api.createFile(getModuleFile(user, moduleIdentifier));
			logActivity(getModuleFile(user, moduleIdentifier), event.getDeathMessage());
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
