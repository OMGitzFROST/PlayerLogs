package com.frostdeveloper.playerlogs.model;

import java.io.IOException;

/**
 * An interface used to define the required classes needed in-order for a module to work.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public interface Module
{
	/**
	 * If granted permission, it will execute all required tasks inorder to register the module.
	 *
	 * @throws IOException Thrown if the module file could not be created.
	 * @since 1.2
	 */
	void registerModule() throws IOException;
	
	/**
	 * A method used to unload a module from the registry.
	 *
	 * @since 1.2
	 */
	void removeModule();
	
	/**
	 * A method used to determine whether a module is registered.
	 *
	 * @return Module registry status
	 * @since 1.2
	 */
	boolean isRegistered();
}
