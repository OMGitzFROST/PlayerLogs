package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Manager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Objects;

/**
 * A class used to handle patches for a new version of this plugin
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class PatchManager implements Manager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin           = PlayerLogs.getInstance();
	private final FrostAPI api                = plugin.getFrostAPI();
	private final ModuleManager moduleManager = plugin.getModuleManager();
	
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should be used to
	 * create a configuration file and potentially include patch updates.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize()
	{
		File cacheFile = Util.toFile(".cache.yml");
		
		if (cacheFile.exists()) {
			//noinspection ResultOfMethodCallIgnored
			cacheFile.delete();
		}
		
		File oldLogDirectory = Util.toFile("player-logs");
		
		// RENAME player-logs TO log-files
		if (oldLogDirectory.exists()) {
			api.renameIndex(oldLogDirectory, moduleManager.getLogDirectory().getName());
		}
		
		// RENAME activity.log TO global.log
		File activityLog = Util.toFile(moduleManager.getLogDirectory(), "activity.log");
		if (activityLog.exists()) {
			api.renameIndex(activityLog, "global.log");
		}
		
		// RENAME MODULE FILES FROM -activity TO -module AND MOVE UN-SUPPORTED MODULES TO UNSUPPORTED DIRECTORY
		for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
			File playerDir;
			
			if (moduleManager.getBoolean(Config.USE_UUID)) {
				playerDir = Util.toFile(moduleManager.getLogDirectory(), api.toString(offlinePlayer.getUniqueId()));
			}
			else {
				playerDir = Util.toFile(moduleManager.getLogDirectory(), offlinePlayer.getName());
			}
			
			if (playerDir.exists()) {
				for (File currentModuleFile : Objects.requireNonNull(playerDir.listFiles())) {
					String partialName = currentModuleFile.getName().split("-")[0];
					Module module      = moduleManager.getModuleByPartial(partialName);
					
					if (module != null) {
						String moduleFileName  = module.getFullIdentifier() + ".log";
						api.renameIndex(currentModuleFile, moduleFileName);
					}
					else {
						api.relocateIndex(currentModuleFile, Util.toFile(playerDir, "unsupported/{0}", currentModuleFile.getName()));
					}
				}
			}
		}
	}
}
