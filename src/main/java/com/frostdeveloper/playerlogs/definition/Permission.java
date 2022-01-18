package com.frostdeveloper.playerlogs.definition;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to define and retrieve methods and getters for our all permissions, this class can
 * also be used to verify our permissions.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public enum Permission
{
	/**
	 * This value defines the permission string that allows any permitted user
	 * to have the ability to execute any commands available in this plugin.
	 *
	 * @since 1.2
	 */
	ALL("playerlogs.command.*"),
	/**
	 * This value defines the permission string that allows any permitted user
	 * to execute the update command for this plugin.
	 *
	 * @since 1.2
	 */
	CMD_UPDATE("playerlogs.command.update"),
	/**
	 * This value defines the permission string that allows any permitted user
	 * to execute the reload command for this plugin.
	 *
	 * @since 1.2
	 */
	CMD_RELOAD("playerlogs.command.reload");
	
	/*
	 * --------------------------------------------------------------
	 */
	
	// CLASS INSTANCES
	private static final PlayerLogs plugin = PlayerLogs.getInstance();
	private static final FrostAPI api = plugin.getFrostAPI();
	
	// CLASS SPECIFIC OBJECTS
	private final String perm;
	
	/**
	 * A constructor used to define the required parameters for an enum object.
	 *
	 * @param perm Permission string
	 * @since 1.2
	 */
	Permission(String perm) { this.perm = perm; }
	
	/**
	 * A method used to verify that our enum permissions and our plugin.yml permissions
	 * are the same, if there are any differences in these lists, this method will throw
	 * a {@link NullPointerException}.
	 *
	 * @since 1.2
	 */
	public static void initVerification()
	{
		boolean errorFound = false;
		
		List<String> internalList = new ArrayList<>();
		for (org.bukkit.permissions.Permission perm : plugin.getDescription().getPermissions()) {
			internalList.add(perm.getName());
		}
		
		List<String> enumList = new ArrayList<>();
		for (Permission enumPerm : Permission.values()) {
			enumList.add(enumPerm.toString());
		}
		
		if (!internalList.equals(enumList)) {
			errorFound = true;
		}
		
		if (errorFound) {
			throw new IllegalArgumentException(api.format("Our ({0}) enum failed verification", Permission.class.getSimpleName()));
		}
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param sender Command sender
	 * @param perm Target permission
	 * @return Permission status
	 * @since 1.2
	 */
	public static boolean isPermitted(@NotNull CommandSender sender, @NotNull Permission perm)
	{
		return sender.hasPermission(Permission.ALL.toString()) || sender.hasPermission(perm.toString());
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param sender Command sender
	 * @param perms List of perms
	 * @return Permission status
	 * @since 1.2
	 */
	public static boolean isPermitted(CommandSender sender, Permission @NotNull ... perms)
	{
		for (Permission perm : perms) {
			if (!sender.hasPermission(perm.toString())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param player Target player
	 * @param perm Target permission
	 * @return Permission status
	 * @since 1.2
	 */
	public static boolean isPermitted(@NotNull Player player, @NotNull Permission perm)
	{
		return player.hasPermission(perm.toString()) || player.hasPermission(perm.toString());
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param player Target player
	 * @param perms List of perms
	 * @return Permission status
	 * @since 1.2
	 */
	public static boolean isPermitted(@NotNull Player player, Permission @NotNull ... perms)
	{
		for (Permission perm : perms) {
			if (!player.hasPermission(perm.toString())) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * GETTER METHODS
	 */
	
	/**
	 * A method used to return the string attached to a Permission object.
	 *
	 * @return String permission
	 * @since 1.2
	 */
	@Override
	public String toString() { return perm;      }
}
