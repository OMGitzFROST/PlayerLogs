package com.frostdeveloper.playerlogs.event;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.util.Placeholder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when the server is requesting its ram information
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class RamEvent extends Event
{
	// CLASS INSTANCES
	private final FrostAPI api = FrostAPI.getInstance();
	
	// CLASS SPECIFIC OBJECTS
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * A method used to return the message given when this event is triggered.
	 *
	 * @return Event message
	 * @since 1.2
	 */
	public String getMessage()
	{
		String message = api.format("TOTAL/MAX: %server_ram_total% MB / %server_ram_max% MB| FREE/USED: %server_ram_free% MB| %server_ram_used% MB");
		return Placeholder.set(message);
	}
	
	/**
	 * A method used to return our event handlers
	 *
	 * @return Event handlers
	 * @since 1.2
	 */
	@Override
	public @NotNull HandlerList getHandlers() { return handlers; }
	
	/**
	 * A method used to return a list of our event handlers
	 *
	 * @return Event handler list
	 * @since 1.2
	 */
	public static HandlerList getHandlerList() { return handlers; }
}
