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
	
	/**
	 * A method used to apply any patch updates for newer versions of our plugin. If there are no
	 * patches that need to be made for existing versions, this method will remain empty.
	 *
	 * @implNote Remember to empty method if no updates are needed for existing features.
	 * @since 1.2
	 */
	void applyPatch();
}
