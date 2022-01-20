package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.definition.Variable;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.util.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class JoinModule extends Module implements Listener
{
	// CLASS SPECIFIC OBJECTS
	private final Config enabled = Config.MODULE_JOIN_ENABLED;
	private final Config message = Config.MODULE_JOIN_MSG;
	
	/**
	 * An event handler used to handle our join event when triggered and execute the required tasks
	 *
	 * @param event Event triggered
	 * @since 1.0
	 */
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		Placeholder.addCustom(Variable.DEFAULT, event.getJoinMessage());
		printToFile(player,  Placeholder.set(player, getMessage()));
	}
	
	/**
	 * A method is called once the module is registered, and initializes the assigned arithmetic.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize()      { Bukkit.getServer().getPluginManager().registerEvents(this, plugin); }
	
	/**
	 * A method used to return the message assigned to a module
	 *
	 * @return Module message
	 * @since 1.2
	 */
	@Override
	public String getMessage()    { return manager.getString(message);                                  }
	
	/**
	 * A method used to return whether a module is enabled
	 *
	 * @return Module status
	 * @since 1.2
	 */
	@Override
	public boolean isEnabled()    { return manager.getBoolean(enabled);                                 }
	
	/**
	 * A method used to determine whether a module is registered.
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	@Override
	public boolean isRegistered() { return  manager.getRegisteredList().contains(this);                 }
	
	/**
	 * A method used to return the identifier for a module. The identifier serves as the name of
	 * the module and additionally can be used to track its timer using the cache manager.
	 *
	 * @return Module identifier
	 * @since 1.0
	 */
	public @NotNull String getIdentifier()
	{
		String rawModuleName = this.getClass().getSimpleName().toLowerCase();
		return rawModuleName.replace("module", "");
	}
	
	/**
	 * A method used to return the full identifier for a module.
	 *
	 * @return Full module identifier
	 * @since 1.2
	 */
	@Override
	public @NotNull String getFullIdentifier()
	{
		String rawModuleName = this.getClass().getSimpleName().toLowerCase();
		return rawModuleName.replace("module", "-module");
	}
	
	/**
	 * A method used to return the active handler list for a module.
	 *
	 * @since 1.2
	 */
	@Override
	public void removeListener()  { PlayerQuitEvent.getHandlerList().unregister(this);                  }
}
