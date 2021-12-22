package com.frostdeveloper.playerlog.util;

import com.frostdeveloper.playerlog.PlayerLog;
import com.frostdeveloper.playerlog.manager.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * A class is used to handle our log filtering
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class LogFilter implements Filter
{
	// CLASS SPECIFIC OBJECTS
	private final boolean debug;
	
	/**
	 * A class constructor used to initialize our class objects and create
	 * an instance of the LogFilter class.
	 *
	 * @since 1.0
	 */
	public LogFilter()
	{
		PlayerLog plugin = PlayerLog.getInstance();
		this.debug = plugin.getConfigManager().getBoolean(ConfigManager.Config.DEBUG_MODE);
	}
	
	/**
	 * A method used to return whether a message is loggable through our
	 * filter, it will search for the debug string in a message, and if enabled,
	 * log our debug message accordingly.
	 *
	 * @param record The log record
	 * @return Whether the message is loggable.
	 * @since 1.0
	 */
	public boolean isLoggable(@NotNull LogRecord record)
	{
		return !record.getMessage().contains("[DEBUG]") || this.debug;
	}
}