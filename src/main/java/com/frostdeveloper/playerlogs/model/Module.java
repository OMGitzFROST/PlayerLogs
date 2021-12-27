package com.frostdeveloper.playerlogs.model;

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
	 * A method called when reloading or server shutdown
	 *
	 * @since 1.2
	 */
	void shutdown();
	
	/**
	 * If granted permission, it will execute all required tasks inorder to register the module.
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
	
}
