package me.badbones69.crazycrates.controlers;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.controlers.FileManager.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIMenu implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	
	public static void openGUI(Player player) {
		int size = Files.CONFIG.getFile().getInt("Settings.InventorySize");
		Inventory inv = Bukkit.createInventory(null, size, Methods.color(Files.CONFIG.getFile().getString("Settings.InventoryName")));
		if(Files.CONFIG.getFile().contains("Settings.Filler.Toggle")) {
			if(Files.CONFIG.getFile().getBoolean("Settings.Filler.Toggle")) {
				String id = Files.CONFIG.getFile().getString("Settings.Filler.Item");
				String name = Files.CONFIG.getFile().getString("Settings.Filler.Name");
				List<String> lore = Files.CONFIG.getFile().getStringList("Settings.Filler.Lore");
				ItemStack item = Methods.makeItem(id, 1, name, lore);
				for(int i = 0; i < size; i++) {
					inv.setItem(i, item.clone());
				}
			}
		}
		if(Files.CONFIG.getFile().contains("Settings.GUI-Customizer")) {
			for(String custom : Files.CONFIG.getFile().getStringList("Settings.GUI-Customizer")) {
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<>();
				String[] b = custom.split(", ");
				for(String i : b) {
					if(i.contains("Item:")) {
						i = i.replace("Item:", "");
						item = i;
					}
					if(i.contains("Name:")) {
						i = i.replace("Name:", "");
						for(Crate crate : cc.getCrates()) {
							if(crate.getCrateType() != CrateType.MENU) {
								i = i.replaceAll("%" + crate.getName().toLowerCase() + "%", cc.getVirtualKeys(player, crate) + "");
							}
						}
						i = i.replaceAll("%player%", player.getName());
						name = i;
					}
					if(i.contains("Lore:")) {
						i = i.replace("Lore:", "");
						String[] d = i.split(",");
						for(String l : d) {
							for(Crate crate : cc.getCrates()) {
								if(crate.getCrateType() != CrateType.MENU) {
									i = i.replaceAll("%" + crate.getName().toLowerCase() + "%", cc.getVirtualKeys(player, crate) + "");
								}
							}
							i = i.replaceAll("%player%", player.getName());
							lore.add(l);
						}
					}
					if(i.contains("Slot:")) {
						i = i.replace("Slot:", "");
						slot = Integer.parseInt(i);
					}
				}
				if(slot > size) {
					continue;
				}
				slot--;
				inv.setItem(slot, Methods.makeItem(item, 1, name, lore));
			}
		}
		for(Crate crate : cc.getCrates()) {
			FileConfiguration file = crate.getFile();
			if(file != null) {
				if(file.getBoolean("Crate.InGUI")) {
					String path = "Crate.";
					int slot = file.getInt(path + "Slot");
					String ma = file.getString(path + "Item");
					String name = file.getString(path + "Name");
					ArrayList<String> lore = new ArrayList<>();
					String keys = NumberFormat.getNumberInstance().format(cc.getVirtualKeys(player, crate));
					Boolean glowing = false;
					for(String i : file.getStringList(path + "Lore")) {
						lore.add(i.replaceAll("%Keys%", keys).replaceAll("%keys%", keys).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
					}
					if(file.contains(path + "Glowing")) {
						if(file.getBoolean(path + "Glowing")) {
							glowing = true;
						}
					}
					if(slot > size) {
						continue;
					}
					slot--;
					inv.setItem(slot, Methods.makeItem(ma, 1, name, lore, glowing));
				}
			}
		}
		player.openInventory(inv);
	}
	
	public static void openPreview(Player player, Crate crate) {
		player.openInventory(crate.getPreview());
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		FileConfiguration config = Files.CONFIG.getFile();
		if(inv != null) {
			for(Crate crate : cc.getCrates()) {
				if(crate.getCrateType() != CrateType.MENU) {
					if(inv.getName().equals(Methods.color(crate.getFile().getString("Crate.Name")))) {
						e.setCancelled(true);
						return;
					}
				}
			}
			if(inv.getName().equals(Methods.color(config.getString("Settings.InventoryName")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasDisplayName()) {
							for(Crate crate : cc.getCrates()) {
								if(crate.getCrateType() != CrateType.MENU) {
									FileConfiguration file = crate.getFile();
									String path = "Crate.";
									if(item.getItemMeta().getDisplayName().equals(Methods.color(file.getString(path + "Name")))) {
										if(e.getAction() == InventoryAction.PICKUP_HALF) {
											if(config.getBoolean("Settings.Show-Preview")) {
												player.closeInventory();
												openPreview(player, crate);
											}
											return;
										}
										if(cc.isInOpeningList(player)) {
											player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
											return;
										}
										Boolean hasKey = false;
										KeyType keyType = KeyType.VIRTUAL_KEY;
										if(cc.getVirtualKeys(player, crate) >= 1) {
											hasKey = true;
										}else {
											if(Files.CONFIG.getFile().contains("Settings.Virtual-Accepts-Physical-Keys")) {
												if(Files.CONFIG.getFile().getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
													if(cc.hasPhysicalKey(player, crate)) {
														hasKey = true;
														keyType = KeyType.PHYSICAL_KEY;
													}
												}
											}
										}
										if(!hasKey) {
											player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
											return;
										}
										for(String world : getDisabledWorlds()) {
											if(world.equalsIgnoreCase(player.getWorld().getName())) {
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%World%", player.getWorld().getName());
												placeholders.put("%world%", player.getWorld().getName());
												player.sendMessage(Messages.WORLD_DISABLED.getMessage(placeholders));
												return;
											}
										}
										if(Methods.isInvFull(player)) {
											player.sendMessage(Messages.INVENTORY_FULL.getMessage());
											return;
										}
										cc.openCrate(player, crate, keyType, player.getLocation());
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private ArrayList<String> getDisabledWorlds() {
		return new ArrayList<>(Files.CONFIG.getFile().getStringList("Settings.DisabledWorlds"));
	}
	
}