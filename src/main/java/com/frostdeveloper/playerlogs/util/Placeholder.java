package com.frostdeveloper.playerlogs.util;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Variable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to handle our placeholder tasks, and to inject our placeholders into a
 * provided input
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class Placeholder
{
	// CLASS INSTANCES
	private static final PlayerLogs plugin = PlayerLogs.getInstance();
	private static final FrostAPI api = plugin.getFrostAPI();
	
	/**
	 * This method is used to accept an input
	 *
	 * @param input Provided input
	 * @since 1.2
	 */
	public static @NotNull String set(String input)
	{
		Validate.notNull(input, api.format("Could not add placeholders, the input provided is null"));
		
		if (plugin.isPAPIHooked()) {
			return PlaceholderAPI.setPlaceholders(null, input);
		}
		return applyPlaceholder(input);
	}
	
	/**
	 * This method is used to apply the placeholders into the input
	 *
	 * @param input Provided input
	 * @return Reformatted output.
	 * @since 1.2
	 */
	private static @NotNull String applyPlaceholder(String input)
	{
		Runtime r = Runtime.getRuntime();
		final int MB = 1048576;
		
		input = replace(input, Variable.RAM_USED, (r.totalMemory() - r.freeMemory()) / MB);
		input = replace(input, Variable.RAM_TOTAL, r.totalMemory() / MB);
		input = replace(input, Variable.RAM_FREE, r.freeMemory() / MB);
		input = replace(input, Variable.RAM_MAX, r.maxMemory() / MB);
		return input;
	}
	
	/**
	 * A method used to replace a variable inside a string with the defined value.
	 *
	 * @param input The input provided.
	 * @param var Target variable
	 * @param value Desired value
	 * @return Reformatted output.
	 * @since 1.2
	 */
	private static @NotNull String replace(@NotNull String input, @NotNull Variable var, Object value)
	{
		return input.replaceAll(var.toVar(), api.toString(value));
	}
	
}
