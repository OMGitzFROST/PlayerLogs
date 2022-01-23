package com.frostdeveloper.playerlogs.util;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class is designed to add methods that are repetitive but are unique to this plugin.
 * These methods do not belong in our API as our api is more fitted for classes that can be used
 * in any Java Project.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class Util
{
	// CLASS INSTANCES
	private static final PlayerLogs plugin = PlayerLogs.getInstance();
	private static final FrostAPI api = plugin.getFrostAPI();
	private static final ConfigManager config = plugin.getConfigManager();
	
	/**
	 * A method used to return a file object from a path. If the path contains a '/' char
	 * It will separate the root from the file name. If the char is not specified it will
	 * assume the path is the file name and define the root for you and use the path as the name.
	 *
	 * @param path Target Path
	 * @return A path as a file.
	 * @since 1.0
	 */
	@Contract ("_ -> new")
	public static @NotNull File toFile(@NotNull String path)
	{
		return api.toFile(plugin.getDataFolder(), path);
	}
	
	/**
	 * A method used to create a file object. The optional parameters is used to include a parameter into file path
	 *
	 * @param path String path
	 * @param param Optional Parameters
	 * @return File object
	 * @since 1.0
	 */
	@Contract ("_, _ -> new")
	public static @NotNull File toFile(@NotNull String path, Object... param)
	{
		return api.toFile(plugin.getDataFolder(), api.format(path, param));
	}
	
	/**
	 * A method used to create a file object. The optional parameters is used to include a parameter into file path
	 *
	 * @param parent Parent Directory
	 * @param name File Name
	 * @param param Optional Parameters
	 * @return File object
	 * @since 1.2
	 */
	public static @NotNull File toFile(File parent, String name, Object... param)
	{
		return api.toFile(parent, api.format(name, param));
	}
	
	/**
	 * A method used to build a head for a specific string
	 *
	 * @param input Target input
	 * @param character Desired character
	 * @return Built header
	 * @since 1.2
	 */
	public static @NotNull String buildHeader(@NotNull String input, char character)
	{
		if (input.length() > 0) {
			char[] array = new char[input.length()];
			Arrays.fill(array, character);
			return new String(array);
		}
		return "";
	}
	
	/**
	 * A method used to build a header from a list of strings
	 *
	 * @param length The desired length of the header
	 * @param character Desired character
	 * @return Built header
	 * @since 1.2
	 */
	public static @NotNull String buildHeader(int length, char character)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(character);
		}
		return sb.toString();
	}
	
	/**
	 * This method is used to build a body for a header, it will take the defined length and create empty space
	 * between the body to fit the content centered.
	 *
	 * @param length Target length
	 * @param body Target body
	 * @param ends Ends which the string starts and end
	 * @return Centered string.
	 * @since 1.2
	 */
	public static @NotNull String buildBody(int length, String body, char ends)
	{
		return ends + StringUtils.center(body, length - 2) + ends;
	}
	
	/**
	 * A method used to get the longest string in a list.
	 *
	 * @param input Target list
	 * @return Longest string inside list
	 * @since 1.2
	 */
	public static int getLongestSize(@NotNull List<String> input)
	{
		Optional<String> longest = input.stream().max((e1, e2) -> e1.length() > e2.length() ? -1 : 0);
		return longest.map(String::length).orElse(0);
	}
	
	/**
	 * A method used to return this plugin's prefix format as defined in the configuration file.
	 * If the prefix is disabled, this method will return an empty string, otherwise, the prefix.
	 *
	 * @return Plugin prefix
	 * @since 1.2
	 */
	public static @NotNull String getPrefix()
	{
		String prefix = config.getString(Config.PREFIX);
		boolean usePrefix = config.getBoolean(Config.USE_PREFIX);
		
		if (usePrefix) {
			return format(prefix)  + ChatColor.RESET + " ";
		}
		return "";
	}
	
	/**
	 * A method used to add bukkit {@link ChatColor} to a message
	 *
	 * @param input Target input
	 * @return An output with color codes translated
	 * @since 1.2
	 */
	@Contract ("_ -> new")
	public static @NotNull String format(@NotNull String input)
	{
		return ChatColor.translateAlternateColorCodes('&', input.replace("§", "&"));
	}
}
