package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.event.RamEvent;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
import com.frostdeveloper.playerlogs.util.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
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
public class RamModule extends Module implements Listener, Scheduler
{
	// CLASS SPECIFIC OBJECTS
	private final Config message  = Config.MODULE_RAM_MSG;
	private final Config enabled  = Config.MODULE_RAM_ENABLED;
	private final Config cooldown = Config.MODULE_RAM_COOLDOWN;
	
	private BukkitTask task;
	
	/**
	 * A method used to handle our event trigger and complete a task when triggered.
	 *
	 * @param event Target event
	 * @since 1.0
	 */
	@EventHandler
	public void onEventTrigger(@NotNull RamEvent event)
	{
		if (manager.isList(message)) {
			printToFile(Placeholder.set(getMessageList()), Placeholder.set(event.getMessage()));
		}
		else {
			printToFile(Placeholder.set(getMessage()), Placeholder.set(event.getMessage()));
		}
	}
	
	/**
	 * A method is called once the module is registered, and initializes the assigned arithmetic.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
		task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			manager.reload();
			Bukkit.getServer().getPluginManager().callEvent(new RamEvent());
		}, 0, api.toTime(manager.getString(cooldown)) * 20L);
	}
	
	/**
	 * A method used to return the message assigned to a module
	 *
	 * @return Module message
	 * @since 1.2
	 */
	@Override
	public String getMessage()           { return manager.getString(message);                }
	
	/**
	 * A method used to return the message list assigned to the module.
	 *
	 * @return Message List
	 * @since 1.2
	 */
	@Override
	public List<String> getMessageList() { return manager.getStringList(message);            }
	
	/**
	 * A method used to return whether a module is enabled
	 *
	 * @return Module status
	 * @since 1.2
	 */
	@Override
	public boolean isEnabled()           { return manager.getBoolean(enabled);               }
	
	/**
	 * A method used to return the active handler list for a module.
	 *
	 * @since 1.2
	 */
	@Override
	public void removeListener()         { RamEvent.getHandlerList().unregister(this);       }
	
	/**
	 * Returns the taskId for the task.
	 *
	 * @return Task id number
	 * @since 1.2
	 */
	@Override
	public int getTaskId()               { return manager.getRegisteredList().indexOf(this); }
	
	/**
	 * Returns true if this task has been cancelled.
	 *
	 * @return true if the task has been cancelled
	 * @since 1.2
	 */
	@Override
	public boolean isCancelled()         { return task == null || task.isCancelled();        }
	
	/**
	 * Will attempt to cancel this task.
	 *
	 * @since 1.2
	 */
	@Override
	public void cancel()                 { task.cancel();                                    }
}
