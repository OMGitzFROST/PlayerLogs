package com.frostdeveloper.api;

import com.frostdeveloper.playerlog.util.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A class used to house redundant methods used across multiple projects, this plugin is designed to make development
 * easier for all current and future projects.
 *
 * @author OMGitzFROST
 * @version 1.0
 */
public final class FrostAPI
{
	// CLASS SPECIFIC OBJECTS
	private final Plugin plugin;
	
	/**
	 * A constructor for the FrostAPI class
	 *
	 * @param plugin Instance of the main plugin
	 * @since 1.0
	 */
	public FrostAPI(Plugin plugin) { this.plugin = plugin; }
	
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
	public @NotNull File toFile(@NotNull String path)
	{
		if (path.contains("/")) {
			String[] splitPath = path.split("/");
			
			if (splitPath[0].equalsIgnoreCase("Plugins")) {
				return new File(getPluginFolder() + File.separator, splitPath[1]);
			}
			return new File(plugin.getDataFolder() + File.separator + splitPath[0], splitPath[1]);
		}
		return new File(plugin.getDataFolder(), path);
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
	public @NotNull File toFile(@NotNull String path, Object... param)
	{
		if (path.contains("/")) {
			String[] splitPath = path.split("/");
			return new File(plugin.getDataFolder() + File.separator + splitPath[0], format(splitPath[1], param));
		}
		return new File(plugin.getDataFolder(), format(path, param));
	}
	
	/*
	 * INDEX TYPE METHODS
	 */
	
	public void renameFile(@NotNull File oldFile, File newFile)
	{
		if (oldFile.exists() && !oldFile.renameTo(newFile)) {
			throw new IllegalArgumentException("Failed to rename file!");
		}
	}
	
	/**
	 * A method used to return whether a string is a valid directory
	 *
	 * @param path Target path.
	 * @return Whether the string path is a file.
	 * @since 1.0
	 */
	public boolean isDirectory(@NotNull String path)
	{
		int index = path.lastIndexOf(".");
		return index == -1;
	}
	
	/**
	 * A method used to return whether an index is a valid file.
	 *
	 * @param index Target index.
	 * @return Whether the index is a file.
	 * @since 1.0
	 */
	public boolean isDirectory(@NotNull File index)
	{
		return isDirectory(index.getPath());
	}
	
	/**
	 * A method used to return whether a string is a valid file.
	 *
	 * @param path Target path.
	 * @return Whether the string path is a file.
	 * @since 1.0
	 */
	public boolean isFile(@NotNull String path)
	{
		int index = path.lastIndexOf(".");
		return index > 0;
	}
	
	/**
	 * A method used to return whether an index is a valid file.
	 *
	 * @param index Target index.
	 * @return Whether the index is a file.
	 * @since 1.0
	 */
	public boolean isFile(@NotNull File index)
	{
		return isFile(index.getPath());
	}
	
	/*
	 * INDEX MAKERS
	 */
	
	/**
	 * A method used to create the parent file for the target file if it does not exist.
	 *
	 * @param targetFile Top level file.
	 * @since 1.0
	 */
	@SuppressWarnings ("ResultOfMethodCallIgnored")
	public void createParent(@NotNull File targetFile)
	{
		if (isDirectory(targetFile.getParentFile()) && !targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
	}
	
	/**
	 * A method used to return the parent file of a file.
	 *
	 * @param targetFile Target file
	 * @return Parent file
	 * @since 1.0
	 */
	public File getParent(@NotNull File targetFile)
	{
		return targetFile.getParentFile();
	}
	
	/**
	 * A method used to return the server's plugin folder
	 *
	 * @return Plugin's Folder
	 * @since 1.0
	 */
	public File getPluginFolder() { return plugin.getDataFolder().getParentFile(); }
	
	/*
	 * FORMATTERS
	 */
	
	/**
	 * A method used to format a string and translate Bukkit color codes
	 *
	 * @param msg Target message
	 * @return Color coded string.
	 * @since 1.0
	 */
	public @NotNull String format(String msg, Object... param)
	{
		msg = msg.replaceAll("§", "&");
		msg = msg.replaceAll("\\u00A7", "&");
		return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(msg, param));
	}
	
	/**
	 * A method used to format a string and translate Bukkit color codes
	 *
	 * @param stripColor Condition to strip color codes
	 * @param msg Target message
	 * @param param Optional params
	 * @return Formatted message
	 * @since 1.1
	 */
	public @NotNull String format(boolean stripColor, String msg, Object... param)
	{
		if (stripColor) {
			msg = stripColor(msg);
		}
		return format(msg, param);
	}
	
	/**
	 * A method used to strip color codes from an input, it will remove any {@link ChatColor} present.
	 *
	 * @param input Target input
	 * @return Colorless output.
	 * @since 1.1
	 */
	public @NotNull String stripColor(@NotNull String input)
	{
		input = input.replaceAll("§", "&");
		
		if (input.contains("&")) {
			input = input.replace("&a", "");
			input = input.replace("&b", "");
			input = input.replace("&c", "");
			input = input.replace("&d", "");
			input = input.replace("&e", "");
			input = input.replace("&f", "");
			
			input = input.replace("&0", "");
			input = input.replace("&1", "");
			input = input.replace("&2", "");
			input = input.replace("&3", "");
			input = input.replace("&4", "");
			input = input.replace("&5", "");
			input = input.replace("&6", "");
			input = input.replace("&7", "");
			input = input.replace("&8", "");
			input = input.replace("&9", "");
			
			input = input.replace("&r", "");
		}
		return input;
	}
	
	/*
	 * TIME
	 */
	
	/**
	 * A method used to return the current time
	 *
	 * @return Current Time
	 * @since 1.0
	 */
	public @NotNull String getTimeNow()
	{
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat date = new SimpleDateFormat(pattern);
		return date.format(System.currentTimeMillis());
	}
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in seconds
	 *
	 * @param amount Amount of seconds.
	 * @return Delay in seconds
	 * @since 1.1
	 */
	public int toSecond(int amount) { return amount; }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in minutes
	 *
	 * @param amount Amount of minutes.
	 * @return Delay in minutes
	 * @since 1.1
	 */
	public int toMinute(int amount) { return toSecond(60) * amount; }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in hours
	 *
	 * @param amount Amount of hours.
	 * @return Delay in hours
	 * @since 1.1
	 */
	public int toHour(int amount)   { return toMinute(60) * amount; }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in days
	 *
	 * @param amount Amount of days.
	 * @return Delay in days
	 * @since 1.1
	 */
	public int toDay(int amount)    { return toHour(24) * amount;   }
	
	/*
	 * PLAYER METHODS
	 */
	
	/*
	 * OBJECT FORMATS
	 */
	
	/**
	 * A method used to return an object as a string
	 *
	 * @param obj Target object
	 * @return Object as a string
	 * @since 1.0
	 */
	public String toString(@NotNull Object obj) { return String.valueOf(obj);  }
	
	/**
	 * A method used to return an object as an int
	 *
	 * @param obj Target object
	 * @return Object as an int
	 * @since 1.0
	 */
	public int toInt(@NotNull Object obj) { return Integer.parseInt(toString(obj)); }
	
	/**
	 * A method used to return an object as a double
	 *
	 * @param obj Target object
	 * @return Object as a double
	 * @since 1.0
	 */
	public double toDouble(@NotNull Object obj) { return Double.parseDouble(toString(obj)); }
	
	/**
	 * A method used to return an object as a float
	 *
	 * @param obj Target float
	 * @return Object as a float
	 * @since 1.0
	 */
	public float toFloat(@NotNull String obj) { return Float.parseFloat(toString(obj)); }
	
	/**
	 * A method used to return an object as a boolean
	 *
	 * @param obj Target object
	 * @return Object as a boolean
	 * @since 1.0
	 */
	public boolean toBoolean(@NotNull Object obj) { return Boolean.parseBoolean(toString(obj)); }
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param sender Command sender
	 * @param perm Target permission
	 * @return Permission status
	 * @since 1.1
	 */
	public boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission perm)
	{
		return sender.hasPermission(Permission.ALL.getPerm()) || sender.hasPermission(perm.getPerm());
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param sender Command sender
	 * @param perms List of perms
	 * @return Permission status
	 * @since 1.1
	 */
	public boolean hasPermission(CommandSender sender, Permission @NotNull ... perms)
	{
		boolean isPermitted = false;
		
		for (Permission perm : perms) {
			if (hasPermission(sender, perm)) {
				isPermitted = true;
			}
		}
		return isPermitted;
	}
	
	/*
	 * PLUGIN DESCRIPTION
	 */
	
	/**
	 * A method used to return a plugin's name located inside the plugin.yml file
	 *
	 * @return Plugin name
	 * @since 1.1
	 */
	public @NotNull String getName() { return plugin.getDescription().getName(); }
	
	/**
	 * A method used to return a plugin's full name created using the plugin.yml file
	 *
	 * @return Plugin full name
	 * @since 1.1
	 */
	public @NotNull String getFullName() { return plugin.getDescription().getFullName(); }
	
	/**
	 * A method used to return a plugin's version located inside the plugin.yml file
	 *
	 * @return Plugin version
	 * @since 1.1
	 */
	public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
	
	/**
	 * A method used to return a plugin's prefix located inside the plugin.yml file
	 *
	 * @return Plugin prefix
	 * @since 1.1
	 */
	public String getPrefix()
	{
		return plugin.getDescription().getPrefix() != null ? plugin.getDescription().getPrefix() : "";
	}
	
	/*
	 * UTILITY METHODS
	 */
	
	/**
	 * A method used to add a string to a list if a condition is met.
	 *
	 * @param list Target list
	 * @param value Target value
	 * @param condition Required condition
	 * @since 1.1
	 */
	public void addToList(List<String> list, String value, boolean condition)
	{
		if (condition) {
			list.add(value);
		}
	}
}
