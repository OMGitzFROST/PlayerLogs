package com.frostdeveloper.playerlogs.util;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
	private static final FrostAPI api = plugin.getFrostApi();
	
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
		if (path.contains("/")) {
			String[] splitPath = path.split("/");
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
	public static @NotNull File toFile(@NotNull String path, Object... param)
	{
		if (path.contains("/")) {
			String[] splitPath = path.split("/");
			return new File(plugin.getDataFolder() + File.separator + splitPath[0], api.format(splitPath[1], param));
		}
		return new File(plugin.getDataFolder(), api.format(path, param));
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
		return new File(parent, api.format(name, param));
	}
	
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param sender Command sender
	 * @param perm Target permission
	 * @return Permission status
	 * @since 1.1
	 */
	public static boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission perm)
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
	public static boolean hasPermission(CommandSender sender, Permission @NotNull ... perms)
	{
		boolean isPermitted = false;
		
		for (Permission perm : perms) {
			if (hasPermission(sender, perm)) {
				isPermitted = true;
			}
		}
		return isPermitted;
	}
}
