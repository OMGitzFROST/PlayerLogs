package com.frostdeveloper.api;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

public final class FrostAPI
{
	private final Plugin plugin;
	
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
			return new File(plugin.getDataFolder() + File.separator + splitPath[0], splitPath[1]);
		}
		return new File(plugin.getDataFolder(), path);
	}
	
	/*
	 * INDEX TYPE METHODS
	 */
	
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
	
	public int toSecond(int amount) { return amount; }
	
	public int toMinute(int amount) { return toSecond(60) * amount; }
	
	public int toHour(int amount)   { return toMinute(60) * amount; }
	
	public int toDay(int amount)    { return toHour(24) * amount;   }
}
