package com.frostdeveloper.playerlogs.model;

import com.frostdeveloper.playerlogs.PlayerLogs;

public interface Manager
{
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should
	 * be used to create a configuration file and potentially include patch updates.
	 *
	 * @since 1.2
	 */
	void initialize();
}
