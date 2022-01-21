package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.util.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This module class houses all required methods inorder for this module to work, Each module is
 * nested under our {@link Module} class that defines all required methods needed for a module to
 * work.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class CMDModule extends Module implements Listener
{
	// CLASS SPECIFIC OBJECTS
	private final Config enabled = Config.MODULE_CMD_ENABLED;
	private final Config message = Config.MODULE_CMD_MSG;
	
	/**
	 * A method used to handle our event trigger and complete a task when triggered.
	 *
	 * @param event Target event
	 * @since 1.0
	 */
	@EventHandler
	public void onEventTrigger(@NotNull PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		
		// SET CUSTOM PLACEHOLDERS
		Placeholder.addCustom("%issued_command%", event.getMessage());
		
		String defaultMessage = api.format("%player_name% issued %issued_command%");
		
		if (manager.isList(message)) {
			printToFile(player, Placeholder.set(player, getMessageList()), Placeholder.set(player, defaultMessage));
		}
		else {
			printToFile(player, Placeholder.set(player, getMessage()), Placeholder.set(player, defaultMessage));
		}
	}
	
	/**
	 * A method used to return the message assigned to a module
	 *
	 * @return Module message
	 * @since 1.2
	 */
	@Override
	public String getMessage()    { return manager.getString(message);                                  }
	
	/**
	 * A method used to return the message list assigned to the module.
	 *
	 * @return Message List
	 * @since 1.2
	 */
	@Override
	public List<String> getMessageList() { return manager.getStringList(message);                       }
	
	/**
	 * A method used to return whether a module is enabled
	 *
	 * @return Module status
	 * @since 1.2
	 */
	@Override
	public boolean isEnabled()    { return manager.getBoolean(enabled);                                 }
	
	/**
	 * A method used to return the active handler list for a module.
	 *
	 * @since 1.2
	 */
	@Override
	public void removeListener() { PlayerCommandPreprocessEvent.getHandlerList().unregister(this); }
}
