package com.frostdeveloper.playerlogs.definition;

/**
 * An enum used to define and return the variables available in this plugin.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public enum Variable
{
	/**
	 * A value used to define the variable used to return the server's used ram.
	 *
	 * @since 1.2
	 */
	RAM_USED("%server_ram_used%"),
	/**
	 * A value used to define the variable used to return the server's used total.
	 *
	 * @since 1.2
	 */
	RAM_TOTAL("%server_ram_total%"),
	/**
	 * A value used to define the variable used to return the server's used free.
	 *
	 * @since 1.2
	 */
	RAM_FREE("%server_ram_free%"),
	/**
	 * A value used to define the variable used to return the server's max ram.
	 *
	 * @since 1.2
	 */
	RAM_MAX("%server_ram_max%");
	
	/*
	 * --------------------------------------------------------------
	 */
	
	// CLASS SPECIFIC OBJECTS
	private final String var;
	
	/**
	 * A constructor used to define the required parameters needed for each enum value.
	 *
	 * @param var Assigned variable
	 * @since 1.2
	 */
	Variable(String var)  { this.var = var; }
	
	/**
	 * A method used to return the variable assigned to the designated enum.
	 *
	 * @return The assigned variable
	 * @since 1.2
	 */
	public String toVar() { return var;     }
}
