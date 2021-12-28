package com.frostdeveloper.api;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class used to house redundant methods used across multiple projects, this plugin is designed to make development
 * easier for all current and future projects.
 *
 * @author OMGitzFROST
 * @version 1.0
 */
public final class FrostAPI
{
	/**
	 * A method used to return an instance of this api class
	 *
	 * @return API instance
	 * @since 1.0
	 */
	@Contract (value = " -> new", pure = true)
	public static @NotNull FrostAPI getInstance() { return new FrostAPI(); }
	
	/**
	 * A method used to return a resource from a string name as an input stream
	 *
	 * @param name Resource name
	 * @return InputStream
	 * @since 1.0
	 */
	public InputStream getResource(String name)
	{
		return this.getClass().getClassLoader().getResourceAsStream(name);
	}
	
	/**
	 * Returns an input stream as an url
	 *
	 * @param name Resource name
	 * @return Resource URL
	 * @since 1.0
	 */
	public URL getResourceURL(String name)
	{
		return this.getClass().getClassLoader().getResource(name);
	}
	
	/**
	 * A method used to rename files.
	 *
	 * @param oldFile Previous file/ location
	 * @param newFile New file/ location
	 * @since 1.0
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
	public boolean isDirectory(@NotNull File index) { return isDirectory(index.getPath()); }
	
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
	public boolean isFile(@NotNull File index) { return isFile(index.getPath()); }
	
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
	public File getParent(@NotNull File targetFile) { return targetFile.getParentFile(); }
	
	
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
	public @NotNull String  stripColor(String input)
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
	
	/**
	 * A method used to remove color codes from any given string list this method
	 * will iterate through the list and individually remove color codes.
	 *
	 * @param list Target list
	 * @return Colorless list.
	 * @since 1.0
	 */
	public @NotNull List<String> stripColor(@NotNull List<String> list)
	{
		List<String> nonColoredList = new ArrayList<>();
		
		for (String string : list) {
			nonColoredList.add(stripColor(string));
		}
		return nonColoredList;
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
	public int toSecond(Object amount) { return toInt(amount); }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in minutes
	 *
	 * @param amount Amount of minutes.
	 * @return Delay in minutes
	 * @since 1.1
	 */
	public int toMinute(Object amount) { return toSecond(60) * toInt(amount); }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in hours
	 *
	 * @param amount Amount of hours.
	 * @return Delay in hours
	 * @since 1.1
	 */
	public int toHour(Object amount)   { return toMinute(60) * toInt(amount); }
	
	/**
	 * A method used in a runnable. It's used to return the amount of time required to complete the delay in days
	 *
	 * @param amount Amount of days.
	 * @return Delay in days
	 * @since 1.1
	 */
	public int toDay(Object amount)    { return toHour(24) * toInt(amount);   }
	
	/**
	 * A method used to turn a string into a valid time, and calculate the amount required.
	 *
	 * @param input String input
	 * @return Requested duration
	 * @since 1.0
	 */
	public int convertToTime(String input)
	{
		input = input.toLowerCase();
		String[] description = {"s","m","h","d","y","second","minute","hour","day","year","seconds","minutes","hours","days","years"};
		String identifier = null;
		int time = -1;
		
		for (String s : description) {
			if (input.contains(s)) {
				identifier = s;
				time = toInt(input.replace(" ", "").split(identifier)[0]);
			}
		}
		
		switch (Objects.requireNonNull(identifier)){
			case "s":
			case "second":
			case "seconds":
				return toSecond(time);
			case "m":
			case "minute":
			case "minutes":
				return toMinute(time);
			case "h":
			case "hour":
			case "hours":
				return toHour(time);
			case "d":
			case "day":
			case "days":
				return toDay(time);
		}
		throw new IllegalArgumentException("Invalid time: " + input);
	}
	
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