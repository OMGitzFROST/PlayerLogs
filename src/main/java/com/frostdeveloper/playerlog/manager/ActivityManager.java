package com.frostdeveloper.playerlog.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlog.util.Activity;
import com.frostdeveloper.playerlog.PlayerLog;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A class used to handle our logging activity
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ActivityManager
{
	// CLASS INSTANCES
	private final PlayerLog plugin = PlayerLog.getInstance();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final File rootDir = new File(plugin.getDataFolder(), "player-logs");
	
	/**
	 * A method used to log a player's activity to the corresponding file.
	 *
	 * @param type Activity type
	 * @param player Target player
	 * @param msg Activity message
	 * @since 1.0
	 */
	public void logActivity(Activity type, @NotNull Player player, String msg)
	{
		try {
			if (!getFile(type, player).getParentFile().exists()) {
				api.createParent(getFile(type, player));
			}
			
			FileWriter fw = new FileWriter(getFile(type, player), true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("[" + api.getTimeNow() + "]: " + api.stripColor(msg));
			pw.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * A method used to return the corresponding  player file in relation to the defined
	 * activity type.
	 *
	 * @param type Activity type
	 * @param player Target Player
	 * @return Activity file
	 * @since 1.0
	 */
	public File getFile(@NotNull Activity type, @NotNull Player player)
	{
		File moduleDirectory = new File(rootDir, player.getUniqueId().toString());
		
		switch (type) {
			case ALL:
				return new File(rootDir, player.getUniqueId() + ".log");
			case BLOCK_BREAK:
				return new File(moduleDirectory, "break-activity.log");
			case BLOCK_PLACE:
				return new File(moduleDirectory, "place-activity.log");
			case PLAYER_CHAT:
				return new File(moduleDirectory, "chat-activity.log");
			case PLAYER_JOIN:
				return new File(moduleDirectory, "join-activity.log");
			case PLAYER_QUIT:
				return new File(moduleDirectory, "quit-activity.log");
			case PLAYER_DEATH:
				return new File(moduleDirectory, "death-activity.log");
			case PLAYER_COMMAND:
				return new File(moduleDirectory, "cmd-activity.log");
			case WORLD_CHANGE:
				return new File(moduleDirectory, "world-change.log");
			default:
				return null;
		}
	}
}
