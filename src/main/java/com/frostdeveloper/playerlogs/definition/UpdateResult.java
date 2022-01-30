package com.frostdeveloper.playerlogs.definition;

/**
 * An enum used to define the available result types for our auto updater.
 *
 * @since 1.2
 */
public enum UpdateResult
{
	/**
	 * This enum value is called when the status of the download is unknown, in other words, a valid url was
	 * provided, but we could not access information about the latest build it will return this result.,
	 * Possible outcomes include
	 * <br><br/>
	 * <p>
	 * 1. The jar file is built locally <br> 2. GitHub's rate limit was reached <br> 3. No asset file was
	 * found
	 *
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	UNKNOWN,
	/**
	 * This enum value is called during these circumstances.
	 * <br><br/>
	 * 1. GitHub is down <br> 2. A Broken url was provided
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	ERROR,
	/**
	 * This enum value is called when our plugin determines that a newer version of our plugin is available for
	 * download, but is prevented from downloading the update. Additionally, this result is returned if the
	 * update is located in the update folder
	 *
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	AVAILABLE,
	/**
	 * This enum value is called when our plugin determines that the latest version is currently installed,
	 * thus no changes have taken place.
	 *
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	CURRENT,
	/**
	 * This enum value is called when our plugin successfully downloads the latest version.
	 *
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	DOWNLOADED,
	/**
	 * This enum value is called when our auto-updater is disabled inside our configuration file.
	 *
	 * <br><br/>
	 *
	 * @since 1.0
	 */
	DISABLED
}