package me.badbones69.crazycrates.controllers;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.controllers.FileManager.Files;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class CrateControl implements Listener { //Crate Control
	
	/**
	 * The keys for the /CE Admin menu.
	 */
	public static HashMap<Player, HashMap<ItemStack, ItemStack>> previewKeys = new HashMap<>();
	/**
	 * A list of crate locations that are in use.
	 */
	public static HashMap<Player, Location> inUse = new HashMap<>();
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	/**
	 * This event controls when a player tries to click in a GUI based crate type. This will stop them from taking items out of their inventories.
	 */
	@EventHandler
	public void onCrateInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			for(Crate crate : cc.getCrates()) {
				if(e.getView().getTitle().equals(crate.getCrateInventoryName())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		FileConfiguration config = Files.CONFIG.getFile();
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			if(e.getHand() == EquipmentSlot.OFF_HAND) {
				if(cc.isKey(player.getInventory().getItemInOffHand())) {
					e.setCancelled(true);
					player.updateInventory();
				}
				return;
			}
		}
		Block clickedBlock = e.getClickedBlock();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			//Loops through all loaded physical locations.
			for(CrateLocation loc : cc.getCrateLocations()) {
				//Checks to see if the clicked block is the same as a physical crate.
				if(loc.getLocation().equals(clickedBlock.getLocation())) {
					//Checks to see if the player is removing a crate location.
					if(player.getGameMode() == GameMode.CREATIVE) {
						if(player.isSneaking()) {
							if(player.hasPermission("crazycrates.admin")) {
								e.setCancelled(true);
								cc.removeCrateLocation(loc.getID());
								player.sendMessage(Methods.color(Methods.getPrefix() + "&7You have just removed &6" + loc.getID() + "&7."));
								return;
							}
						}
					}
					e.setCancelled(true);
					if(loc.getCrateType() == CrateType.MENU) {
						return;
					}else {
						if(loc.getCrate().isPreviewEnabled()) {
							Preview.setPlayerInMenu(player, false);
							Preview.openNewPreview(player, loc.getCrate());
						}else {
							player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
						}
					}
				}
			}
		}else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			for(Crate crate : cc.getCrates()) {
				if(crate.getCrateType() != CrateType.MENU) {
					if(Methods.isSimilar(player, crate.getKey())) {
						e.setCancelled(true);
						player.updateInventory();
					}
				}
			}
			//Loops through all loaded physical locations.
			for(CrateLocation loc : cc.getCrateLocations()) {
				//Checks to see if the clicked block is the same as a physical crate.
				if(loc.getLocation().equals(clickedBlock.getLocation())) {
					Crate crate = loc.getCrate();
					e.setCancelled(true);
					if(crate.getCrateType() == CrateType.MENU) {
						GUIMenu.openGUI(player);
						return;
					}
					String KeyName = Methods.color(crate.getFile().getString("Crate.PhysicalKey.Name"));
					boolean hasKey = false;
					boolean isPhysical = false;
					boolean useQuickCrateAgain = false;
					//					if(crate.getCrateType() == CrateType.QUAD_CRATE) {
					//						player.sendMessage(Messages.QUAD_CRATE_DISABLED.getMessage());
					//						return;
					//					}
					if(crate.getCrateType() != CrateType.CRATE_ON_THE_GO) {
						if(Methods.isSimilar(player, crate.getKey())) {
							hasKey = true;
							isPhysical = true;
						}
					}
					if(config.getBoolean("Settings.Physical-Accepts-Virtual-Keys")) {
						if(cc.getVirtualKeys(player, crate) >= 1) {
							hasKey = true;
						}
					}
					if(hasKey) {
						if(cc.isInOpeningList(player)) {
							if(cc.getOpeningCrate(player).getCrateType() == CrateType.QUICK_CRATE) {// Checks if the player uses the quick crate again.
								if(inUse.containsKey(player)) {
									if(inUse.get(player).equals(loc.getLocation())) {
										useQuickCrateAgain = true;
									}
								}
							}
						}
						if(!useQuickCrateAgain) {
							if(cc.isInOpeningList(player)) {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%Key%", KeyName);
								placeholders.put("%key%", KeyName);
								player.sendMessage(Messages.ALREADY_OPENING_CRATE.getMessage(placeholders));
								return;
							}
							if(inUse.containsValue(loc.getLocation())) {
								player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
								return;
							}
						}
						if(Methods.isInventoryFull(player)) {
							player.sendMessage(Messages.INVENTORY_FULL.getMessage());
							return;
						}
						if(useQuickCrateAgain) {
							QuickCrate.endQuickCrate(player, loc.getLocation());
						}
						KeyType keyType;
						if(isPhysical) {
							keyType = KeyType.PHYSICAL_KEY;
						}else {
							keyType = KeyType.VIRTUAL_KEY;
						}
						cc.addPlayerKeyType(player, keyType);
						cc.addPlayerToOpeningList(player, crate);
						cc.openCrate(player, crate, keyType, loc.getLocation(), false);
						return;
					}else {
						if(config.getBoolean("Settings.KnockBack")) {
							knockBack(player, clickedBlock.getLocation());
						}
						if(config.contains("Settings.Need-Key-Sound")) {
							Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));
							if(sound != null) {
								player.playSound(player.getLocation(), sound, 1f, 1f);
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%Key%", KeyName);
						placeholders.put("%key%", KeyName);
						player.sendMessage(Messages.NO_KEY.getMessage(placeholders));
						return;
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void onAdminMenuClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			if(e.getView().getTitle().equals(Methods.color("&4&lAdmin Keys"))) {
				e.setCancelled(true);
				if(!Methods.permCheck(player, "admin")) {
					player.closeInventory();
					return;
				}
				int slot = e.getRawSlot();
				if(slot < inv.getSize()) {
					if(e.getAction() == InventoryAction.PICKUP_ALL) {
						ItemStack item = inv.getItem(slot);
						if(item != null) {
							if(item.getType() != Material.AIR) {
								player.getInventory().addItem(previewKeys.get(player).get(item));
							}
						}
					}else if(e.getAction() == InventoryAction.PICKUP_HALF) {
						ItemStack item = inv.getItem(slot);
						if(cc.isKey(previewKeys.get(player).get(item))) {
							Crate crate = cc.getCrateFromKey(previewKeys.get(player).get(item));
							if(crate != null) {
								cc.addKeys(1, player, crate, KeyType.VIRTUAL_KEY);
								String name = null;
								ItemStack key = crate.getKey();
								if(key.hasItemMeta()) {
									if(key.getItemMeta().hasDisplayName()) {
										name = key.getItemMeta().getDisplayName();
									}
								}
								player.sendMessage(Methods.getPrefix() + Methods.color("&a&l+1 " + (name != null ? name : crate.getName())));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(cc.hasCrateTask(player)) {
			cc.endCrate(player);
		}
		if(cc.hasQuadCrateTask(player)) {
			cc.endQuadCrate(player);
		}
		if(cc.isInOpeningList(player)) {
			cc.removePlayerFromOpeningList(player);
		}
	}
	
	private void knockBack(Player player, Location loc) {
		Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1).setY(.1);
		if(player.isInsideVehicle()) {
			player.getVehicle().setVelocity(v);
			return;
		}
		player.setVelocity(v);
	}
	
}