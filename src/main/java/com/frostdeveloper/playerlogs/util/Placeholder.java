package com.frostdeveloper.playerlogs.util;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Variable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	
	// CUSTOM VARIABLE MAP
	private static final HashMap<String, Object> customVariables = new HashMap<>();
	
	/**
	 * A method used to set placeholders which includes player variables to a list of strings.
	 *
	 * @param player Target player
	 * @param input Target string
	 * @return List of strings with variables included
	 * @since 1.2
	 */
	public static @NotNull List<String> set(Player player, @NotNull List<String> input)
	{
		return input.stream().map(line -> set(player, line)).collect(Collectors.toList());
	}
	
	/**
	 * A method used to set placeholders to a list of strings.
	 *
	 * @param input Target string
	 * @return List of strings with variables included
	 * @since 1.2
	 */
	public static List<String> set(@NotNull List<String> input)
	{
		return input.stream().map(Placeholder::set).collect(Collectors.toList());
	}
	
	/**
	 * A method used to set placeholder which includes player variables to a string
	 *
	 * @param player Target player
	 * @param input Target string
	 * @return String with variables included
	 * @since 1.2
	 */
	public static @NotNull String set(Player player, String input)
	{
		Validate.notNull(input, api.format("Could not add placeholders, the input provided is null"));
		Validate.notNull(player, api.format("Could not add placeholders, the player provided is null"));
		
		if (plugin.isPAPIHooked()) {
			return applyCustomPlaceholder(PlaceholderAPI.setPlaceholders(player, input));
		}
		return applyPlaceholder(player, input);
	}
	
	/**
	 * This method is used to accept an input
	 *
	 * @param input Provided input
	 * @since 1.2
	 */
	public static @NotNull String set(@NotNull String input)
	{
		Validate.notNull(input, api.format("Could not add placeholders, the input provided is null"));
		
		if (plugin.isPAPIHooked()) {
			return applyCustomPlaceholder(PlaceholderAPI.setPlaceholders(null, input));
		}
		return applyPlaceholder(null, input);
	}
	
	/**
	 * This method is used to apply the placeholders into the input
	 *
	 * @param input Provided input
	 * @return Reformatted output.
	 * @since 1.2
	 */
	private static @NotNull String applyPlaceholder(@Nullable Player player, String input)
	{
		Runtime r = Runtime.getRuntime();
		final int MB = 1048576;
		
		// SERVER VARIABLES
		input = replace(input, Variable.RAM_USED, (r.totalMemory() - r.freeMemory()) / MB);
		input = replace(input, Variable.RAM_TOTAL, r.totalMemory() / MB);
		input = replace(input, Variable.RAM_FREE, r.freeMemory() / MB);
		input = replace(input, Variable.RAM_MAX, r.maxMemory() / MB);
		
		// PLAYER VARIABLES
		if (player != null) {
			input = replace(input, Variable.PLAYER_NAME, player.getName());
			input = replace(input, Variable.PLAYER_DISPLAY, player.getDisplayName());
		}
		return applyCustomPlaceholder(input);
	}
	
	/**
	 * A method used to apply custom placeholder into an input
	 *
	 * @param input Target input
	 * @return A string with implemented variables
	 * @since 1.2
	 */
	private static String applyCustomPlaceholder(String input)
	{
		for (Map.Entry<String, Object> custom : customVariables.entrySet()) {
			input = replace(input, custom.getKey(), custom.getValue());
		}
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
	
	/**
	 * A method used to replace a variable inside a string with the defined value.
	 *
	 * @param input The input provided.
	 * @param var Target variable (as string)
	 * @param value Desired value
	 * @return Reformatted output.
	 * @since 1.2
	 */
	private static @NotNull String replace(@NotNull String input, @NotNull String var, Object value)
	{
		return input.replaceAll(var, api.toString(value));
	}
	
	/**
	 * A method used to add a custom variable that cannot be defined in our Placeholder class, but will
	 * be converted into a string when our {@link #set(String)} method is called.
	 *
	 * @param var Custom variable
	 * @param value Custom value assigned to variable
	 * @since 1.2
	 */
	public static void addCustom(String var, Object value)
	{
		matchPattern(var);
		
		if (!customVariables.containsKey(var)) {
			customVariables.put(var, value);
		}
		else {
			customVariables.replace(var, value);
		}
	}
	
	/**
	 * A method used to add a custom variable that cannot be defined in our Placeholder class, but will
	 * be converted into a string when our {@link #set(String)} method is called.
	 *
	 * @param var Custom variable
	 * @param value Custom value assigned to variable
	 * @since 1.2
	 */
	public static void addCustom(@NotNull Variable var, Object value)
	{
		matchPattern(var.toVar());
		
		if (!customVariables.containsKey(var.toVar())) {
			customVariables.put(var.toVar(), value);
		}
		else {
			customVariables.replace(var.toVar(), value);
		}
	}
	
	/**
	 * A method used to verify that a custom variable is in compliance with the required
	 * format, If it cannot be verified or failed verification, this method will
	 * throw an exception.
	 *
	 * @param var Target variable
	 * @since 1.2
	 */
	private static void matchPattern(@NotNull String var)
	{
		if (!var.startsWith("%") || !var.endsWith("%")) {
			throw new IllegalArgumentException("Custom variable format invalid: " + var);
		}
	}
}
