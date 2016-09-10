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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public class Commands implements CommandExecutor {

	private final PreventShare plugin;
	
	public Commands(final PreventShare plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        if (cmd.getName().equalsIgnoreCase("preventshare")) {
        	if (args.length == 0) {
        		PluginDescriptionFile pdf = plugin.getDescription();
        		
    	        sender.sendMessage(ChatColor.GOLD + "--------------------");
    	        sender.sendMessage(ChatColor.GOLD + "--  PREVENT SHARE  --");
    	        sender.sendMessage(ChatColor.GOLD + "--------------------");
    	        sender.sendMessage(ChatColor.GOLD + "Author: " + pdf.getAuthors().get(0));
    	        sender.sendMessage(ChatColor.GOLD + "Version: " + pdf.getVersion());
    	        sender.sendMessage(ChatColor.YELLOW + "/preventshare help");
    	        sender.sendMessage(ChatColor.GOLD + "--------------------");
        		
        		return true;
        	}
        	else if (args.length == 1) {
        		if (args[0].equalsIgnoreCase("help")) {
        	        sender.sendMessage(ChatColor.GOLD + "------------------------");
        	        sender.sendMessage(ChatColor.GOLD + "--  PREVENTSHARE HELP  --");
        	        sender.sendMessage(ChatColor.GOLD + "------------------------");
        	        sender.sendMessage(ChatColor.YELLOW + "Aliases: ps, pshare");
        	        sender.sendMessage(ChatColor.GOLD + "/preventshare add <member>");
        	        sender.sendMessage(ChatColor.GOLD + "/preventshare remove <member>");
        	        sender.sendMessage(ChatColor.GOLD + "/preventshare reload");
        	        sender.sendMessage(ChatColor.GOLD + "------------------------");
        	        
        	        return true;
        		}
        		else if (args[0].equalsIgnoreCase("reload")) {
        			if (sender.hasPermission("preventshare.reload")) {
        				plugin.getPluginLoader().disablePlugin(plugin);
        				plugin.getPluginLoader().enablePlugin(plugin);
        				plugin.reloadConfig();
        				sender.sendMessage(plugin.getChatPrefix() + ChatColor.GREEN + "Reload Complete!");
        			}
        			else {
        				sender.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Permission Denied!");
        			}

        			return true;
        		}
        	}
        	else if (args.length == 2) {
        		if (!(sender instanceof Player)) {
        			sender.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "This command can only be executed by a player!");
        			return true;
        		}
        		
        		Player player = (Player)sender;
        		
        		if (!sender.hasPermission("preventshare.admin")) {
        			player.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Permission Denied!");
        			return true;
        		}
        		
        		if (args[0].equalsIgnoreCase("add")) {
        			ItemStack item = player.getInventory().getItemInMainHand();
        			
        			if (item == null || item.getType() == Material.AIR) {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Invalid Item!");
        				return true;
        			}
        			
        			boolean result = plugin.getItemManager().addItem(args[1], item);
        			if (result) {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.GREEN + "Item Successfully Added");
        			}
        			else {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Failed To Add Item!");
        			}
        			
        			return true;
        		}
        		else if (args[0].equalsIgnoreCase("remove")) {
        			ItemStack item = player.getInventory().getItemInMainHand();
        			
        			if (item == null || item.getType() == Material.AIR) {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Invalid Item!");
        				return true;
        			}
        			
        			boolean result = plugin.getItemManager().removeItem(args[1], item);
        			if (result) {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.GREEN + "Item Successfully Removed");
        			}
        			else {
        				player.sendMessage(plugin.getChatPrefix() + ChatColor.RED + "Failed To Remove Item!");
        			}
        			
        			return true;
        		}
        	}
        }

		return false;
	}

}
