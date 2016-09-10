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

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Events implements Listener {

	private final PreventShare plugin;
	private final Random rand;
	
	public Events(final PreventShare plugin) {
		this.plugin = plugin;
		this.rand = new Random();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		Player player = e.getPlayer();
		
		if (player.hasPermission("preventshare.bypass")) {
			return;
		}
		
		Item drop = e.getItem();
		ItemStack dropStack = drop.getItemStack();
		
		String playerPrimaryGroup = plugin.getPermission().getPrimaryGroup(player.getWorld().getName(), player);
		if (!plugin.getItemManager().canUse(playerPrimaryGroup, dropStack)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
		boolean disableArrowPickup = plugin.getConfig().getBoolean("disable-arrow-pickup");
		if (disableArrowPickup) {
			e.setCancelled(true);
		}
		else if (e.getItem().hasMetadata("arrowdata")) {
			e.setCancelled(true);
			
			Player player = e.getPlayer();
			ItemStack arrowStack = (ItemStack)e.getItem().getMetadata("arrowdata").get(0).value();
			String playerPrimaryGroup = plugin.getPermission().getPrimaryGroup(player.getWorld().getName(), player);
			
			if (player.hasPermission("preventshare.bypass") || plugin.getItemManager().canUse(playerPrimaryGroup, arrowStack)) {
				float pitch = ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F;
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2F, pitch);

				player.getInventory().addItem(arrowStack);
				e.getItem().removeMetadata("arrowdata", plugin);
				e.getItem().remove();
			}
		}
	}
	
	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent e) {
		if (e.isCancelled() || e.getEntityType() != EntityType.PLAYER) {
			return;
		}

		boolean pickupArrows = !plugin.getConfig().getBoolean("disable-arrow-pickup");
		boolean storeArrowData = plugin.getConfig().getBoolean("store-arrow-data");

		if (pickupArrows && storeArrowData) {
			Player player = (Player)e.getEntity();
			ItemStack arrowStack = getArrowStack(player);
			if (arrowStack != null) {
				boolean alwaysStore = false;
				if (arrowStack.hasItemMeta()) {
					alwaysStore = plugin.getConfig().getBoolean("only-store-restricted");
				}
				ItemStack compStack = arrowStack.clone();
				compStack.setAmount(1);
				if (alwaysStore || plugin.getItemManager().isRestricted(compStack)) {
					Entity arrow = e.getProjectile();
					arrow.setMetadata("arrowdata", new FixedMetadataValue(plugin, compStack));
				}
			}
		}
	}
	
	private ItemStack getArrowStack(Player player) {
		if (isArrow(player.getInventory().getItemInOffHand())) {
			return player.getInventory().getItemInOffHand();
		}
		else if (isArrow(player.getInventory().getItemInMainHand())) {
			return player.getInventory().getItemInMainHand();
		}

		for (ItemStack stack : player.getInventory().getContents()) {
			if (isArrow(stack)) {
				return stack;
			}
		}

		return null;
	}

	private boolean isArrow(ItemStack stack) {
		return ((stack != null) && (stack.getType() == Material.ARROW));
	}
	
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		boolean cancelable = false;
		ItemStack dispensedStack = e.getItem();
		
		if (dispensedStack.getType() == Material.ARROW) {
			boolean pickupArrows = !plugin.getConfig().getBoolean("disable-arrow-pickup");
			boolean disableDispenserArrows = plugin.getConfig().getBoolean("disable-dispenser-arrows");
			
			if (pickupArrows && disableDispenserArrows) {
				cancelable = true;
			}
		}
		else if (plugin.getConfig().getBoolean("disable-dispenser-armor", true)) {
            if (
                    dispensedStack.getType() == Material.LEATHER_HELMET ||
                    dispensedStack.getType() == Material.LEATHER_CHESTPLATE ||
                    dispensedStack.getType() == Material.LEATHER_LEGGINGS ||
                    dispensedStack.getType() == Material.LEATHER_BOOTS ||

                    dispensedStack.getType() == Material.IRON_HELMET ||
                    dispensedStack.getType() == Material.IRON_CHESTPLATE ||
                    dispensedStack.getType() == Material.IRON_LEGGINGS ||
                    dispensedStack.getType() == Material.IRON_BOOTS ||

                    dispensedStack.getType() == Material.GOLD_HELMET ||
                    dispensedStack.getType() == Material.GOLD_CHESTPLATE ||
                    dispensedStack.getType() == Material.GOLD_LEGGINGS ||
                    dispensedStack.getType() == Material.GOLD_BOOTS ||

                    dispensedStack.getType() == Material.CHAINMAIL_HELMET ||
                    dispensedStack.getType() == Material.CHAINMAIL_CHESTPLATE ||
                    dispensedStack.getType() == Material.CHAINMAIL_LEGGINGS ||
                    dispensedStack.getType() == Material.CHAINMAIL_BOOTS ||

                    dispensedStack.getType() == Material.DIAMOND_HELMET ||
                    dispensedStack.getType() == Material.DIAMOND_CHESTPLATE ||
                    dispensedStack.getType() == Material.DIAMOND_LEGGINGS ||
                    dispensedStack.getType() == Material.DIAMOND_BOOTS ||

                    dispensedStack.getType() == Material.ELYTRA ||
                    dispensedStack.getType() == Material.SHIELD
                    )
            {
                cancelable = true;
            }
        }
		
		if (cancelable) {
			ItemStack clone = dispensedStack.clone();
			clone.setAmount(1);
			if (plugin.getItemManager().isRestricted(clone)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		Player player = (Player)e.getWhoClicked();

		if (!player.hasPermission("preventshare.bypass") && plugin.getConfig().getBoolean("disable-inventory")) {
			if (e.getClickedInventory() != null && !e.getClickedInventory().equals(player.getInventory())) {
				ItemStack clickedItem = e.getCurrentItem();
				String playerPrimaryGroup = plugin.getPermission().getPrimaryGroup(player.getWorld().getName(), player);
				if (!plugin.getItemManager().canUse(playerPrimaryGroup, clickedItem)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if (plugin.getConfig().getBoolean("drop-on-held")) {
			int newSlot = e.getNewSlot();
			Player player = e.getPlayer();
			String playerPrimaryGroup = plugin.getPermission().getPrimaryGroup(player.getWorld().getName(), player);
			ItemStack heldItem = e.getPlayer().getInventory().getItem(newSlot);
			if (!plugin.getItemManager().canUse(playerPrimaryGroup, heldItem)) {
				if (!plugin.getConfig().getBoolean("despawn-held-item")) {
					player.getWorld().dropItem(player.getLocation(), heldItem);
				}

				player.getInventory().setItem(newSlot, new ItemStack(Material.AIR, 1));
			}
		}
	}

}
