package com.frostdeveloper.playerlogs.manager;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.api.exceptions.ModuleRegistryException;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.model.User;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This class is designed to handle and register all current and new modules. It also houses methods
 * to get module counts and provides access to required methods used in our module classes.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class ModuleManager
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECTS
	private final File rootDir = new File(plugin.getDataFolder(), "activity-logs");
	private final File allActivityFile = new File(rootDir, "activity.log");
	
	// MODULE LISTS
	private static final ArrayList<Module> registered = new ArrayList<>();
	private static final ArrayList<Module> total = new ArrayList<>();
	
	public void runTask()
	{
		try {
			applyPatches();
			api.createDirectory(rootDir);
			api.createFile(allActivityFile);
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	private void applyPatches()
	{
		System.out.println(api.toFile("player-logs"));
		
		api.renameFile(api.toFile("player-logs"), rootDir);
	}
	
	// ##################################################### \\
	// #              NOT ACTIVELY MAINTAINED              # \\
	// ##################################################### \\
	
	/**
	 * A method used to return a list of all REGISTERED modules, keep in mind this method
	 * is not intended to return a complete list of the modules, but only those enabled in
	 * the plugin's configuration file.
	 *
	 * @return Registered list.
	 * @since 1.2
	 */
	public ArrayList<Module> getRegistryList() { return registered;                                        }
	
	/**
	 * A method used to return a TOTAL list of all available modules. What defines a module as available
	 * is its status inside our configuration file. Its status inside the configuration file does not affect
	 * this list, even if the module is disabled, it should be included in this list.
	 *
	 * @return All available modules.
	 * @since 1.2
	 */
	public ArrayList<Module> getTotalList()    { return total;                                             }
	
	/**
	 * A method used to log a player's activity to the corresponding file.
	 *
	 * @param msg Activity message
	 * @since 1.0
	 */
	protected void logActivity(File targetFile, String msg, Object... param)
	{
		try {
			FileWriter allWriter = new FileWriter(allActivityFile, true);
			PrintWriter allPrint = new PrintWriter(allWriter);
			allPrint.println("[" + api.getTimeNow() + "]: " + api.format(api.stripColor(msg), param));
			allWriter.close();
			
			if (config.getBoolean(Config.MODULARIZE)) {
				FileWriter moduleWriter = new FileWriter(targetFile, true);
				PrintWriter modulePrint = new PrintWriter(moduleWriter);
				modulePrint.println("[" + api.getTimeNow() + "]: " + api.format(api.stripColor(msg), param));
				moduleWriter.close();
			}
		}
		catch (IOException ex) {
			ReportManager.createReport(getClass(), ex, true);
		}
	}
	
	/**
	 * A method used to handle creating a modules log file for a specified user, this method features
	 * a loop to iterate through all online players and create a file for each one automatically.
	 * <br><br/>
	 *
	 * <br><br/>
	 * NOTE: WE DO NOT ITERATE THROUGH OFFLINE PLAYERS AS THERE IS NO NEED, WE ONLY WANT ACTIVE PLAYERS TO
	 * RECEIVE A TRACKER.
	 *
	 * @param identifier Module identifier
	 * @throws IOException Thrown when the file failed to create.
	 * @since 1.2
	 */
	private void createModuleFile(String identifier) throws IOException
	{
		correctUserFiles();
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			User user = new User(player);
			api.createDirectory(getUserFile(user));
			api.createFile(getModuleFile(user, identifier));
		}
	}
	
	/**
	 * A method used to correct player files, a server can define whether player names, or uuids should be used
	 * to identify a players' folder, therefore this method is tasked with adjusting already existing folders
	 * to correctly display the intended name.
	 *
	 * @since 1.2
	 */
	public void correctUserFiles()
	{
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (config.getBoolean(Config.USE_UUID)) {
				User user = new User(player);
				File oldFile = api.toFile(getRootDir(), user.getName());
				api.renameFile(oldFile, getUserFile(user));
			}
			else {
				User user = new User(player);
				File oldFile = api.toFile(getRootDir(), user.getStringUUID());
				api.renameFile(oldFile, getUserFile(user));
			}
		}
		for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
			if (config.getBoolean(Config.USE_UUID)) {
				User user = new User(player);
				File oldFile = api.toFile(getRootDir(), user.getName());
				api.renameFile(oldFile, getUserFile(user));
			}
			else {
				User user = new User(player);
				File oldFile = api.toFile(getRootDir(), user.getStringUUID());
				api.renameFile(oldFile, getUserFile(user));
			}
		}
	}
	
	/**
	 * A method used to add modules to our registry.
	 *
	 * @param module Instance of the module.
	 * @param moduleIdentifier Intentifer used to create module logger.
	 * @param modulePermission Permission used to check if module is enabled.
	 * @throws IOException Thrown if the module's file failed to create for all players.
	 * @since 1.2
	 */
	protected void addToRegistry(Module module, String moduleIdentifier, Config modulePermission) throws IOException
	{
		try {
			total.add(module);
			if (config.getBoolean(modulePermission)) {
				registered.add(module);
				
				createModuleFile(moduleIdentifier);
				Bukkit.getPluginManager().registerEvents((Listener) module, plugin);
				plugin.debug("module.register.success", moduleIdentifier);
			}
			else {
				registered.remove(module);
				plugin.debug("module.register.disabled", moduleIdentifier);
			}
		}
		catch (ModuleRegistryException ex) {
			ReportManager.createReport(module.getClass(), ex, true);
		}
	}
	
	/**
	 * A method used (typically on disable) to unregister all our modules from memory.
	 *
	 * @since 1.2
	 */
	public void unloadModules()
	{
		int cachedRegistered = registered.size();
		
		registered.clear();
		total.clear();
		
		if (registered.size() == 0) {
			plugin.log("module.unload.success", cachedRegistered);
		}
	}
	
	/**
	 * A method used to return an instance of a player's module file, it takes our {@link User} parameter
	 * and the modules identifier inorder to build the modules log file.
	 *
	 * @param user Target user
	 * @param identifier Module's identify
	 * @return Modules log file
	 * @since 1.2
	 */
	@Contract ("_, _ -> new")
	protected File getModuleFile(User user, String identifier)
	{
		return api.toFile(getUserFile(user), identifier + ".log");
	}
	
	/**
	 * A method used to return a user's identifier, this identifier returns as a string and either presents
	 * as that user's name, or UUID.
	 *
	 * @param user Target user
	 * @return User's identifier
	 * @since 1.2
	 */
	private String getUserIdentifier(User user)
	{
		return config.getBoolean(Config.USE_UUID) ? user.getStringUUID() : user.getName();
	}
	
	/**
	 * A method used to return a user's directory, this directory houses all loggers available to this plugin.
	 *
	 * @param user Target user.
	 * @return User's directory.
	 * @since 1.2
	 */
	@Contract ("_ -> new")
	private @NotNull File getUserFile(User user) { return api.toFile(getRootDir(), getUserIdentifier(user)); }
	
	/**
	 * A method used to return the root directory, this directory can be defined as the folder that houses
	 * all player directories as well as our global logger.
	 *
	 * @return Root directory
	 * @since 1.2
	 */
	private @NotNull File getRootDir()           { return rootDir;                                           }
	
	/**
	 * A method used to return the amount of registered modules.
	 *
	 * @return Total modules registered
	 * @since 1.2
	 */
	public int getRegisteredTotal()              { return registered.size();                                 }
	
	/**
	 * A method used to return the total amount of modules available in this plugin, this method bypasses the
	 * registry and lists ALL available modules.
	 *
	 * @return Modules total.
	 * @since 1.2
	 */
	public int getTotal()                        { return total.size();                                      }
	
}
