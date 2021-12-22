package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exceptions.EventInvalidException;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.manager.ReportManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * A class used to define a module, a modules requirements are defined in the {@link Module} interface.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class TeleportModule extends ModuleManager implements Module, Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final String moduleIdentifier = "teleport-activity";
	private final Config modulePermission = Config.MODULE_TELEPORT;
	
	/**
	 * This method is used to call and handle an event and log the activity to its designated log file.
	 *
	 * @param event Event Type
	 */
	@EventHandler
	public void onTeleportEvent(PlayerTeleportEvent event) throws IOException
	{
		try {
			if (isRegistered()) {
				User user = new User(event.getPlayer());
				api.createFile(getModuleFile(user, moduleIdentifier));
				
				switch (event.getCause()) {
					case COMMAND:
					case ENDER_PEARL:
					case SPECTATE:
					case CHORUS_FRUIT:
					case PLUGIN:
						logActivity(getModuleFile(user, moduleIdentifier), user.getDisplayName() + " teleported from (" + api.format(event.getFrom()) + ") to (" + api.format(Objects.requireNonNull(event.getTo())) + ")");
				}
			}
		}
		catch (EventInvalidException ex) {
			ReportManager.createReport(getClass(), ex, true);
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
