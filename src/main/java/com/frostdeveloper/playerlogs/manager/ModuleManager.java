package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exception.FailedMethodException;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.core.Configuration;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.Manager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.Scheduler;
import com.frostdeveloper.playerlogs.module.*;
import com.frostdeveloper.playerlogs.util.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A manager tasked with handling all tasks related to our modules, this module will register, unregister,
 * and add modules to the correct lists, and more
 *
 * @author OMGitzFROST
 * @since 1.0
 */
public class ModuleManager extends Configuration implements Manager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final FrostAPI api      = plugin.getFrostAPI();
	
	// CLASS SPECIFIC OBJECTS
	private static final ArrayList<Module> registered = new ArrayList<>();
	private static final ArrayList<Module> master = new ArrayList<>();
	
	/**
	 * A super constructor used to define the variables needed to determine how this class works.
	 *
	 * @param target This parameter is used to define the path in which the desired configuration will be located.
	 * @param reload This parameter will define if the configuration should always automatically reload its values, if
	 *               set to false, the target will only update on a complete reload or shutdown.
	 * @since 1.2
	 */
	public ModuleManager(@NotNull String target, boolean reload) { super(target, reload); }
	
	@Override
	public void applyPatch()
	{
		File oldLogDirectory = Util.toFile("player-logs");
		
		// RENAME player-logs TO log-files
		if (oldLogDirectory.exists()) {
			api.renameIndex(oldLogDirectory, getLogDirectory().getName());
		}
		
		// RENAME activity.log TO global.log
		File activityLog     = Util.toFile(getLogDirectory(), "activity.log");
		if (activityLog.exists()) {
			api.renameIndex(activityLog, "global.log");
		}
		
		// RENAME MODULE FILES FROM -activity TO -module AND MOVE UN-SUPPORTED MODULES TO UNSUPPORTED DIRECTORY
		for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
			File playerDir;
			
			if (getBoolean(Config.USE_UUID)) {
				playerDir = Util.toFile(getLogDirectory(), api.toString(offlinePlayer.getUniqueId()));
			}
			else {
				playerDir = Util.toFile(getLogDirectory(), offlinePlayer.getName());
			}
			
			if (playerDir.exists()) {
				for (File currentModuleFile : Objects.requireNonNull(playerDir.listFiles())) {
					String partialName     = currentModuleFile.getName().split("-")[0];
					Module module          = getModuleByPartial(partialName);
					
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
	
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should be used to
	 * create a configuration file and potentially include patch updates.
	 *
	 * @since 1.2
	 */
	@Override
	public void initialize() { initialize(true); }
	
	/**
	 * A method used to at the start of the {@link PlayerLogs#onEnable()} method. This method should be used to
	 * create a configuration file and potentially include patch updates.
	 *
	 * @param announce Whether this method log a current module count.
	 *
	 * @since 1.2
	 */
	public void initialize(boolean announce)
	{
		// APPLY ANY PATCHES
		applyPatch();
		
		// IF A CONFIG DOES NOT EXIST THIS METHOD WILL CREATE ONE FOR US
		saveDefaultConfig();
		
		// ATTEMPT UPDATE FOR OUR MODULE FILE
		if (exists()) {
			attemptUpdate();
		}
		
		// INITIALIZE ALL REGISTERED MODULES
		Bukkit.getScheduler().runTaskLater(plugin, () -> initializeAudit(announce), 0);
	}
	
	/**
	 * A method used to add  all available modules and add them into our {@link #master} list. This method
	 * does not add modules to the registry but should be used once per startup/reload.
	 *
	 * @since 1.2
	 */
	private void setModuleList()
	{
		master.add(new BreakModule());
		master.add(new ChatModule());
		master.add(new CMDModule());
		master.add(new DeathModule());
		master.add(new EnchantModule());
		master.add(new JoinModule());
		master.add(new PlaceModule());
		master.add(new QuitModule());
		master.add(new RamModule());
		master.add(new TeleportModule());
		master.add(new WorldModule());
	}
	
	/**
	 * A method used to return our master list. If the list does not contain any modules, this method
	 * will automatically register all modules into our {@link #master} list.
	 *
	 * @return All values housed in our registered list.
	 * @since 1.2
	 */
	public ArrayList<Module> getMasterList()
	{
		if (master.size() == 0) {
			setModuleList();
		}
		return master;
	}
	
	/**
	 * A method used to return our registry list. If the list does not contain any modules, this method
	 * will automatically register all modules available in our {@link #master} list.
	 *
	 * @return All values housed in our registered list.
	 * @since 1.2
	 */
	public ArrayList<Module> getRegisteredList() { return registered; }
	
	/**
	 * A method used to initialize our audit, the audit is tasked with verifying that all modules
	 * are still allowed to be registered, if not this method will handle removing them from our
	 * registry, if a module is not registered by is allowed to be registered, this method will
	 * also handle registering that module into the registry.
	 *
	 * @since 1.2
	 */
	public void initializeAudit() { initialize(true); }
	
	/**
	 * A method used to initialize our audit, the audit is tasked with verifying that all modules
	 * are still allowed to be registered, if not this method will handle removing them from our
	 * registry, if a module is not registered by is allowed to be registered, this method will
	 * also handle registering that module into the registry.
	 *
	 * @param announce Whether this method log a current module count.
	 *
	 * @since 1.2
	 */
	public void initializeAudit(boolean announce)
	{
		initializeCorrection();
		
		// ADD MISSING MODULES TO REGISTRY
		for (Module module : getMasterList()) {
			if (module.isEnabled() && !module.isRegistered()) {
				addToRegistry(module);
				module.initialize();
			}
			
			for (OfflinePlayer current : Bukkit.getOfflinePlayers()) {
				File moduleFile = module.getModuleFile(current);
				File inactiveFile = api.toFile(getUserDirectory(current), "inactive/" + moduleFile.getName());
				
				if (moduleFile.exists() && !module.isEnabled()) {
					api.relocateIndex(moduleFile, inactiveFile);
				}
				
				if (inactiveFile.exists() && module.isEnabled()) {
					api.relocateIndex(inactiveFile, moduleFile);
				}
				
				if (inactiveFile.getParentFile().exists()) {
					if (Objects.requireNonNull(inactiveFile.getParentFile().listFiles()).length == 0) {
						if (!inactiveFile.getParentFile().delete()) {
							throw new FailedMethodException("Failed to delete inactive directory for ", current.getName());
						}
					}
				}
			}
		}
		
		// REMOVE REGISTERED MODULES WHEN NO LONGER REGISTERED
		for (Module module : getMasterList()) {
			if (!module.isEnabled() && module.isRegistered()) {
				getRegisteredList().remove(module);
				
				if (module instanceof Scheduler) {
					((Scheduler) module).cancel();
				}
				
				module.removeListener();
				
				if (!getRegisteredList().contains(module)) {
					plugin.debug("module.unregister.success", module.getFullIdentifier());
				}
			}
		}
		
		if (announce) {
			plugin.log("module.register.total", getCount());
		}
	}
	
	/**
	 * A method used to correct all existing player files to match the desired configuration. It will rename
	 * the directories to use UUID's or Player names accordingly.
	 *
	 */
	public void initializeCorrection()
	{
		boolean useUUID = getBoolean(Config.USE_UUID);
		int changes = 0;
		
		// ITERATE THROUGH ALL PLAYER AND CORRECT THE DIRECTORIES
		for (OfflinePlayer current : Bukkit.getOfflinePlayers()) {
			File uuidDir    = Util.toFile(getLogDirectory(), api.toString(current.getUniqueId()));
			File playerDir  = Util.toFile(getLogDirectory(), current.getName());
			
			if (useUUID && playerDir.exists()) {
				api.renameIndex(playerDir, uuidDir.getName());
				changes++;
			}
			
			if (!useUUID && uuidDir.exists()) {
				api.renameIndex(uuidDir, playerDir.getName());
				changes++;
			}
		}
		
		if (changes > 0) {
			plugin.debug("user.correction.total", changes);
		}
	}
	
	/**
	 * A method used to register a module into our registry. If the module is null or invalid, this method
	 * will throw an exception.
	 *
	 * @throws IllegalArgumentException Thrown if the module is null.
	 * @param module Target module
	 * @since 1.2
	 */
	public void addToRegistry(Module module)
	{
		Validate.notNull(module, "Could not add to registry, The module defined cannot be null!");
		
		if (!registered.contains(module)) {
			registered.add(module);
			
			if (getRegisteredList().contains(module)) {
				plugin.debug("module.register.success", module.getFullIdentifier());
			}
		}
	}
	
	/**
	 * A method used to return the identifier list of all registered modules.
	 *
	 * @return Registered modules identifiers
	 * @since 1.2
	 */
	public String[] toList()
	{
		String[] modules = new String[getRegisteredList().size()];
		for (int i = 0; i < getRegisteredList().size(); i++) {
			modules[i] = getRegisteredList().get(i).getIdentifier().replace("-module", "");
		}
		return modules;
	}
	
	/* MODULE GETTERS */
	
	/**
	 * This method is used to return a module based on a partial string. If no match is found, this method
	 * will always return null until found.
	 *
	 * @param partial Partial name
	 * @return Requested module.
	 * @since 1.2
	 */
	public Module getModuleByPartial(String partial)
	{
		for (Module module : getMasterList()) {
			if (module.getFullIdentifier().toLowerCase().contains(partial)) {
				return module;
			}
		}
		return null;
	}
	
	/* GET COUNTS */
	
	/**
	 * A method used to return the total count of modules registered in this plugin.
	 *
	 * @return Total count
	 * @since 1.2
	 */
	public String getCount()                  { return getRegisteredList().size() + "/" + getMasterList().size(); }
	
	/**
	 * A method used to return the total count for a specific list, this will only return the amount
	 * for that list only.
	 *
	 * @param list Target list
	 * @return List total
	 * @since 1.2
	 */
	public String getCount(@NotNull ArrayList<Module> list) { return api.toString(list.size()); }
	
	/* GET DIRECTORIES */
	
	/**
	 * A method used to return our log directory.
	 *
	 * @return Our root log directory
	 * @since 1.2
	 */
	public File getLogDirectory()             { return Util.toFile("log-files");                           }
	
	/**
	 * A method used to return the required user directory, this method automatically configures based on the
	 * configuration's requirements.
	 *
	 * @param player Target player
	 * @return Player directory
	 * @since 1.2
	 */
	public File getUserDirectory(Player player)
	{
		if (!getBoolean(Config.USE_UUID)) {
			return Util.toFile(getLogDirectory(), player.getName());
		}
		return Util.toFile(getLogDirectory(), api.toString(player.getUniqueId()));
	}
	
	/**
	 * A method used to return the required user directory, this method automatically configures based on the
	 * configuration's requirements.
	 *
	 * @param player Target player
	 * @return Player directory
	 * @since 1.2
	 */
	public File getUserDirectory(OfflinePlayer player)
	{
		if (!getBoolean(Config.USE_UUID)) {
			return Util.toFile(getLogDirectory(), player.getName());
		}
		return Util.toFile(getLogDirectory(), api.toString(player.getUniqueId()));
	}
}
