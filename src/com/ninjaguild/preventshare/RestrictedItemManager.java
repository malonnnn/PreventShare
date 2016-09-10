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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RestrictedItemManager {
	
	private final File itemsFile;
	private final FileConfiguration itemsConfig;
	
	private Map<String, Set<RestrictedItem>> items = null;

	public RestrictedItemManager(final PreventShare plugin) {
		items = new HashMap<>();
		
		itemsFile = new File(plugin.getDataFolder(), "items.yml");
		if (!itemsFile.exists()) {
			try {
				itemsFile.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
		load();
	}

	private void load() {
		ConfigurationSection groupSec = itemsConfig.getConfigurationSection("groups");
		if (groupSec != null) {
			for (String group : groupSec.getKeys(false)) {
				items.put(group, new HashSet<RestrictedItem>());
				@SuppressWarnings("unchecked")
				Set<RestrictedItem> rItems = (Set<RestrictedItem>)groupSec.get(group);
				for (RestrictedItem ri : rItems) {
					items.get(group).add(new RestrictedItem(ri.getDisplayName(), ri.getLore()));
				}
			}
		}
	}
	
	protected boolean addItem(String group, ItemStack item) {
		group = group.toLowerCase();
		
		if (hasItem(group, item)) {
			return false;
		}
		
		if (!groupExists(group)) {
			items.put(group, new HashSet<RestrictedItem>());
		}
		
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		        RestrictedItem ri = new RestrictedItem(meta.getDisplayName(), lore);
		        
				boolean result = items.get(group).add(ri);
				if (result) {
					@SuppressWarnings("unchecked")
					Set<RestrictedItem> stacks = (Set<RestrictedItem>)itemsConfig.get("groups." + group);
					if (stacks == null) {
						stacks = items.get(group);
					}
					stacks.add(ri);
					itemsConfig.set("groups." + group, stacks);
					try {
						itemsConfig.save(itemsFile);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				return result;
			}
		}
		
		return false;
	}
	
	protected boolean removeItem(String group, ItemStack item) {
		group = group.toLowerCase();
		
		if (!hasItem(group, item)) {
			return false;
		}
		
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		        RestrictedItem ri = new RestrictedItem(meta.getDisplayName(), lore);
		        
				boolean result = items.get(group).remove(ri);
				if (result) {
					@SuppressWarnings("unchecked")
					Set<RestrictedItem> items = (Set<RestrictedItem>)itemsConfig.get("groups." + group);
					items.remove(ri);
					itemsConfig.set("groups." + group, items);
					try {
						itemsConfig.save(itemsFile);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				return result;
			}
		}
		
        return false;
	}
	
	protected boolean hasItem(String group, ItemStack item) {
		group = group.toLowerCase();
		
		if (!items.containsKey(group)) {
			return false;
		}

		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
				for (RestrictedItem ri : items.get(group)) {
					if (isEqual(ri, new RestrictedItem(meta.getDisplayName(), lore))) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	protected boolean isRestricted(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return false;
		}
		
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();	
				RestrictedItem ri = new RestrictedItem(meta.getDisplayName(), lore);
				for (String key : items.keySet()) {
					Set<RestrictedItem> rItems = items.get(key);
					if (rItems != null && !rItems.isEmpty()) {
						if (rItems.contains(ri)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
	
	protected boolean canUse(String group, ItemStack item) {
		if (items == null || items.isEmpty()) {
			return true;
		}

		if (item != null && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();	
				RestrictedItem resItem = new RestrictedItem(meta.getDisplayName(), lore);

				group = group.toLowerCase();
				if (groupExists(group)) {
					for (RestrictedItem ri : items.get(group)) {
						if (isEqual(ri, resItem)) {
							return true;
						}
					}
				}

				for (String key : items.keySet()) {
					if (key.equals(group)) {
						continue;
					}
					for (RestrictedItem ri : items.get(key)) {
						if (isEqual(ri, resItem)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}
	
	private boolean isEqual(RestrictedItem item1, RestrictedItem item2) {
		if (item1 == null || item2 == null) {
			return false;
		}
		
		return item1.equals(item2);
	}
	
	private boolean groupExists(String group) {
		if (items.containsKey(group.toLowerCase())) {
			return true;
		}
		return false;
	}
	
}
