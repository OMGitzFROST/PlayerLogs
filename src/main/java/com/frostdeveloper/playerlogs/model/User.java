package com.frostdeveloper.playerlogs.model;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Permission;
import com.frostdeveloper.playerlogs.manager.LocaleManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is designed to create instances of a user inorder to return details about
 * a user and allow this plugin to use the information to preform tasks.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class User
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final LocaleManager locale = plugin.getLocaleManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final OfflinePlayer player;
	
	/*
	 * CLASS CONSTRUCTOR
	 */
	
	/**
	 * A constructor of the User class used to define and retrieve details from a user.
	 *
	 * @param player Target player
	 * @since 1.2
	 */
	public User(@NotNull OfflinePlayer player) { this.player = player; }
	
	/*
	 * GETTER METHODS
	 */
	
	/**
	 * A method used to return an instance of the defined {@link OfflinePlayer}, if the player
	 * is online, this method will return a {@link Player},
	 *
	 * @return Instance of a player
	 * @since 1.2
	 */
	public @NotNull OfflinePlayer getPlayer()
	{
		return player.isOnline() ? Objects.requireNonNull(player.getPlayer()) : player;
	}
	
	/**
	 * A method used to return user's uuid
	 *
	 * @return User's UUID
	 * @since 1.2
	 */
	public UUID getUUID()          { return getPlayer().getUniqueId();                                        }
	
	/**
	 * A method used to return a user's UUID as a string.
	 *
	 * @return User's string UUID
	 * @since 1.2
	 */
	public String getStringUUID()  { return String.valueOf(getUUID());                                              }
	
	/**
	 * A method used to return a players displayname
	 *
	 * @throws NullPointerException If player is not online.
	 * @return Player's displayname
	 * @since 1.2
	 */
	public String getDisplayName() { return Objects.requireNonNull(getPlayer().getPlayer()).getDisplayName(); }
	
	/**
	 * A method used to return a user's name.
	 *
	 * @return User's name
	 * @since 1.2
	 */
	public String getName()        { return getPlayer().getName();                                            }
	
	/**
	 * A method used to return a user's world.
	 *
	 * @return User's world
	 * @since 1.2
	 */
	public World getWorld()        { return Objects.requireNonNull(getPlayer().getPlayer()).getWorld();       }
	
	public String getWorldName()   { return getWorld().getName(); }
	
	/*
	 * PERMISSIBLE
	 */
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param perm Target permission
	 * @return Permission status
	 * @since 1.1
	 */
	public boolean hasPermission(@NotNull Permission perm)
	{
		return Objects.requireNonNull(getPlayer().getPlayer()).hasPermission(Permission.ALL.getPerm()) || getPlayer().getPlayer().hasPermission(perm.getPerm());
	}
	
	/**
	 * A method used tp determine whether a command sender is permitted any of the listed
	 * permissions, if any is permitted, it will return true.
	 *
	 * @param perms List of perms
	 * @return Permission status
	 * @since 1.1
	 */
	public boolean hasPermission(Permission @NotNull ... perms)
	{
		boolean isPermitted = false;
		
		for (Permission perm : perms) {
			if (hasPermission(perm)) {
				isPermitted = true;
			}
		}
		return isPermitted;
	}
	
	/**
	 * A method used to send a message to a user.
	 *
	 * @param message Target message
	 * @param param Included parameters
	 * @since 1.2
	 */
	public void sendMessage(String message, Object... param)
	{
		Objects.requireNonNull(getPlayer().getPlayer()).sendMessage(api.format(locale.getMessage(message), param));
	}
}
