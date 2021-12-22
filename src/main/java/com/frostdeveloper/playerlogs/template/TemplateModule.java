package com.frostdeveloper.playerlogs.template;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

/**
 * A class used to define a module, a modules requirements are defined in the {@link Module} interface.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class TemplateModule extends ModuleManager implements Module, Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final String moduleIdentifier = "template-activity";                                         /* EDIT */
	private final Config modulePermission = Config.MODULE_TEMPLATE;                                      /* EDIT */
	
	/**
	 * This method is used to call and handle an event and log the activity to its designated log file.
	 *
	 * @param event Event Type
	 */
	@EventHandler
	public void onTemplateEvent(TemplateEvent event) throws IOException                                  /* EDIT */
	{
		if (isRegistered()) {
			User user = new User(event.getPlayer());
			api.createFile(getModuleFile(user, moduleIdentifier));
			logActivity(getModuleFile(user, moduleIdentifier), "");                                /* EDIT */
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
