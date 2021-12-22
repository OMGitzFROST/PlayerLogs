package com.frostdeveloper.playerlogs.module;

import com.frostdeveloper.api.FrostAPI;
import com.frostdeveloper.playerlogs.PlayerLogs;
import com.frostdeveloper.playerlogs.definition.Config;
import com.frostdeveloper.playerlogs.manager.ConfigManager;
import com.frostdeveloper.playerlogs.manager.ModuleManager;
import com.frostdeveloper.playerlogs.model.Module;
import com.frostdeveloper.playerlogs.model.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A class used to define a module, a modules requirements are defined in the {@link Module} interface.
 *
 * @author OMGitzFROST
 * @since 1.2
 */
public class EnchantModule extends ModuleManager implements Module, Listener
{
	// CLASS INSTANCES
	private final PlayerLogs plugin = PlayerLogs.getInstance();
	private final ConfigManager config = plugin.getConfigManager();
	private final FrostAPI api = plugin.getFrostApi();
	
	// CLASS SPECIFIC OBJECT
	private final String moduleIdentifier = "enchant-activity";
	private final Config modulePermission = Config.MODULE_ENCHANT;
	
	/**
	 * This method is used to call and handle an event and log the activity to its designated log file.
	 *
	 * @param event Event Type
	 */
	@EventHandler
	public void onEnchantEvent(EnchantItemEvent event) throws IOException
	{
		if (isRegistered()) {
			User user = new User(event.getEnchanter());
			
			String userName = user.getName();
			Material item = event.getItem().getType();
			int cost = event.getExpLevelCost();
			
			api.createFile(getModuleFile(user, moduleIdentifier));
			logActivity(getModuleFile(user, moduleIdentifier), "{0} enchanted a {1} for {2} exp", userName, item, cost);
		}
	}
	
	@Override
	public void registerModule() throws IOException
	{
		addToRegistry(this, moduleIdentifier, modulePermission);
	}
	
	@Override
	public void removeModule()
	{
		getRegistryList().remove(this);
		plugin.log("module.unload.success", moduleIdentifier);
	}
	
	@Override
	public boolean isRegistered() { return getRegistryList().contains(this); }
}
