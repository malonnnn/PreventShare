/*
    PreventShare
    Copyright (C) 2016  NinjaStix
    ninjastix84@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.ninjaguild.preventshare;

import java.util.logging.Level;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;

public class PreventShare extends JavaPlugin {
	
	private Permission permission = null;
	private RestrictedItemManager resItemMan = null;
	
	private final String chatPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "PS" + ChatColor.DARK_GRAY + "] ";

	@Override
	public void onEnable() {
		if (!setupPermissions()) {
			getLogger().log(Level.WARNING, "No permissions plugin found! Disabling...");
			this.getPluginLoader().disablePlugin(this);
			getLogger().log(Level.INFO, "Plugin Disabled");
			return;
		}

		saveDefaultConfig();
		
		//update config version
		String currentVersion = getConfig().getString("version").trim();
		ConfigUtil cu = new ConfigUtil(this);
		cu.updateConfig(currentVersion);
		
		getServer().getPluginManager().registerEvents(new Events(this), this);
		getCommand("preventshare").setExecutor(new Commands(this));
		
		ConfigurationSerialization.registerClass(RestrictedItem.class);
		
		resItemMan = new RestrictedItemManager(this);
	}
	
	@Override
	public void onDisable() {
		//
	}
	
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider =
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	
	protected Permission getPermission() {
		return permission;
	}
	
	protected RestrictedItemManager getItemManager() {
		return resItemMan;
	}
	
	protected String getChatPrefix() {
		return chatPrefix;
	}

}
