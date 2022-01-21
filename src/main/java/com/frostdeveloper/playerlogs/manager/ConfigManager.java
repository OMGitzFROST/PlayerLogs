package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.core.Configuration;
import com.frostdeveloper.playerlogs.model.Manager;
import org.jetbrains.annotations.NotNull;

/**
 * A manager tasked with handling all tasks related to our main configuration, with this class you can retrieve
 * configuration values, create, load, etc.
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ConfigManager extends Configuration implements Manager
{
	/**
	 * A super constructor used to define the variables needed to determine how this class works.
	 *
	 * @param target This parameter is used to define the path in which the desired
	 *               configuration will be located.
	 * @param reload This parameter will define if the configuration should always automatically reload
	 *               its values, if set to false, the target will only update on a complete reload or shutdown.
	 * @since 1.2
	 */
	public ConfigManager(@NotNull String target, boolean reload) { super(target, reload); }
	
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should be used to
	 * create a configuration file and potentially include patch updates.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize()
	{
		saveDefaultConfig();
		
		if (exists()) {
			attemptUpdate();
		}
	}
}
