package com.frostdeveloper.playerlogs.model;

import org.jetbrains.annotations.NotNull;

/**
 * An interface used to define the required classes needed in-order for a module to work.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public interface Module
{
	/**
	 * A method is called once the module is registered, and initializes the assigned arithmetic.
	 *
	 * @since 1.2
	 */
	void initialize();
	
	
	/**
	 * If granted permission, This method will add our module to our module list and
	 * will determine if the module should be initialized.
	 *
	 * @since 1.2
	 */
	void registerModule();
	
	/**
	 * A method used to determine whether a module is registered.
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	boolean isRegistered();
	
	/**
	 * A method used to return the identifier for a module. The identifier serves as the name of
	 * the module and additionally can be used to track its timer using the cache manager.
	 *
	 * @return Module identifier
	 * @since 1.0
	 */
	@NotNull String getIdentifier();
}
