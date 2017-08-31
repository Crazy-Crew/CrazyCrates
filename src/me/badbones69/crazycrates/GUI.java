package me.badbones69.crazycrates;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazycrates.api.Crate;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.Messages;
import me.badbones69.crazycrates.api.Prize;
import me.badbones69.crazycrates.cratetypes.CSGO;
import me.badbones69.crazycrates.cratetypes.Cosmic;
import me.badbones69.crazycrates.cratetypes.QCC;
import me.badbones69.crazycrates.cratetypes.Roulette;
import me.badbones69.crazycrates.cratetypes.War;
import me.badbones69.crazycrates.cratetypes.Wheel;
import me.badbones69.crazycrates.cratetypes.Wonder;

public class GUI implements Listener {
	
	/**
	 * The crate that the player is opening.
	 */
	public static HashMap<Player, Crate> crates = new HashMap<Player, Crate>();
	/**
	 * All the preview inventories.
	 */
	private static HashMap<Crate, Inventory> previews = new HashMap<Crate, Inventory>();
	
	public static void openGUI(Player player) {
		int size = Main.settings.getConfig().getInt("Settings.InventorySize");
		Inventory inv = Bukkit.createInventory(null, size, Methods.color(Main.settings.getConfig().getString("Settings.InventoryName")));
		if(Main.settings.getConfig().contains("Settings.Filler.Toggle")) {
			if(Main.settings.getConfig().getBoolean("Settings.Filler.Toggle")) {
				String id = Main.settings.getConfig().getString("Settings.Filler.Item");
				String name = Main.settings.getConfig().getString("Settings.Filler.Name");
				List<String> lore = Main.settings.getConfig().getStringList("Settings.Filler.Lore");
				ItemStack item = Methods.makeItem(id, 1, name, lore);
				for(int i = 0; i < size; i++) {
					inv.setItem(i, item.clone());
				}
			}
		}
		if(Main.settings.getConfig().contains("Settings.GUI-Customizer")) {
			for(String custom : Main.settings.getConfig().getStringList("Settings.GUI-Customizer")) {
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<String>();
				String[] b = custom.split(", ");
				for(String i : b) {
					if(i.contains("Item:")) {
						i = i.replace("Item:", "");
						item = i;
					}
					if(i.contains("Name:")) {
						i = i.replace("Name:", "");
						for(Crate c : Main.CC.getCrates()) {
							i = i.replaceAll("%" + c.getName().toLowerCase() + "%", Methods.getKeys(player, c) + "");
						}
						i = i.replaceAll("%player%", player.getName());
						name = i;
					}
					if(i.contains("Lore:")) {
						i = i.replace("Lore:", "");
						String[] d = i.split(",");
						for(String l : d) {
							for(Crate c : Main.CC.getCrates()) {
								i = i.replaceAll("%" + c.getName().toLowerCase() + "%", Methods.getKeys(player, c) + "");
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
		for(Crate crate : Main.CC.getCrates()) {
			FileConfiguration file = crate.getFile();
			if(!file.contains("Crate.InGUI")) {
				file.set("Crate.InGUI", true);
				Main.settings.saveAll();
			}
			if(file.getBoolean("Crate.InGUI")) {
				String path = "Crate.";
				int slot = file.getInt(path + "Slot");
				String ma = file.getString(path + "Item");
				String name = file.getString(path + "Name");
				ArrayList<String> lore = new ArrayList<String>();
				String keys = NumberFormat.getNumberInstance().format(Methods.getKeys(player, crate));
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
		player.openInventory(inv);
	}
	
	public static void openPreview(Player player, Crate crate) {
		Inventory inv;
		if(previews.containsKey(crate)) {
			inv = previews.get(crate);
		}else {
			loadPreviews(crate);
			inv = previews.get(crate);
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		FileConfiguration config = Main.settings.getConfig();
		if(inv != null) {
			for(Crate crate : Main.CC.getCrates()) {
				if(inv.getName().equals(Methods.color(crate.getFile().getString("Crate.Name")))) {
					e.setCancelled(true);
					return;
				}
			}
			if(inv.getName().equals(Methods.color(config.getString("Settings.InventoryName")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasDisplayName()) {
							for(Crate crate : Main.CC.getCrates()) {
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
									if(crates.containsKey(player)) {
										player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
										return;
									}
									if(Methods.getKeys(player, crate) < 1) {
										String msg = config.getString("Settings.NoVirtualKeyMsg");
										player.sendMessage(Methods.color(Methods.getPrefix() + msg));
										return;
									}
									for(String world : getDisabledWorlds()) {
										if(world.equalsIgnoreCase(player.getWorld().getName())) {
											HashMap<String, String> placeholders = new HashMap<String, String>();
											placeholders.put("%World%", player.getWorld().getName());
											placeholders.put("%world%", player.getWorld().getName());
											player.sendMessage(Messages.WORLD_DISABLED_MESSAGE.getMessage(placeholders));
											return;
										}
									}
									if(Methods.isInvFull(player)) {
										if(config.contains("Settings.Inventory-Full")) {
											player.sendMessage(Messages.INVENTORY_FULL.getMessage());
										}else {
											player.sendMessage(Methods.color(Methods.getPrefix() + "&cYour inventory is full, please make room before opening a crate."));
										}
										return;
									}
									switch(CrateType.getFromName(file.getString("Crate.CrateType"))) {
										case COSMIC:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											Cosmic.openCosmic(player);
											break;
										case CRATE_ON_THE_GO:
											player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
											return;
										case CSGO:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											CSGO.openCSGO(player);
											break;
										case FIRE_CRACKER:
											player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
											return;
										case MENU:
											break;
										case QUAD_CRATE:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											QCC.startBuild(player, player.getLocation(), Material.CHEST);
											break;
										case QUICK_CRATE:
											player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
											return;
										case ROULETTE:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											Roulette.openRoulette(player);
											break;
										case WHEEL:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											Wheel.startWheel(player);
											break;
										case WONDER:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											Wonder.startWonder(player);
											break;
										case WAR:
											crates.put(player, crate);
											CrateControl.crates.put(player, crate);
											Methods.Key.put(player, KeyType.VIRTUAL_KEY);
											War.openWarCrate(player);
											break;
									}
									if(file.getBoolean("Crate.OpeningBroadCast")) {
										Bukkit.broadcastMessage(Methods.color(file.getString("Crate.BroadCast").replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
									}
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void loadPreviews() {
		previews.clear();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> Loading all crate preview inventories...");
				for(Crate crate : Main.CC.getCrates()) {
					Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> Loading " + crate.getName() + " preview....");
					int slots = 9;
					for(int size = crate.getPrizes().size(); size > 9 && slots < 54; size -= 9) {
						slots += 9;
					}
					Inventory inv = Bukkit.createInventory(null, slots, Methods.color(crate.getFile().getString("Crate.Name")));
					for(Prize prize : crate.getPrizes()) {
						inv.addItem(prize.getDisplayItem());
					}
					Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> " + crate.getName() + " loaded.");
					previews.put(crate, inv);
				}
				Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> All crate previews have been loaded.");
			}
		}, 10);
	}
	
	public static void loadPreviews(Crate crate) {
		Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> Loading " + crate + " preview....");
		FileConfiguration file = crate.getFile();
		int slots = 9;
		for(int size = file.getConfigurationSection("Crate.Prizes").getKeys(false).size(); size > 9 && slots < 54; size -= 9) {
			slots += 9;
		}
		Inventory inv = Bukkit.createInventory(null, slots, Methods.color(file.getString("Crate.Name")));
		for(String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
			String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
			String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
			List<String> lore = file.getStringList("Crate.Prizes." + reward + ".Lore");
			HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			Boolean glowing = false;
			int amount = 1;
			if(file.contains("Crate.Prizes." + reward + ".Glowing")) {
				glowing = file.getBoolean("Crate.Prizes." + reward + ".Glowing");
			}
			if(file.contains("Crate.Prizes." + reward + ".DisplayAmount")) {
				amount = file.getInt("Crate.Prizes." + reward + ".DisplayAmount");
			}
			if(file.contains("Crate.Prizes." + reward + ".DisplayEnchantments")) {
				for(String enchant : file.getStringList("Crate.Prizes." + reward + ".DisplayEnchantments")) {
					String[] b = enchant.split(":");
					enchantments.put(Enchantment.getByName(b[0]), Integer.parseInt(b[1]));
				}
			}
			try {
				if(enchantments.size() > 0) {
					inv.setItem(inv.firstEmpty(), Methods.makeItem(id, amount, name, lore, enchantments, glowing));
				}else {
					inv.setItem(inv.firstEmpty(), Methods.makeItem(id, amount, name, lore, glowing));
				}
			}catch(Exception e) {
				inv.addItem(Methods.makeItem(Material.STAINED_CLAY, 1, 14, "&c&lERROR", Arrays.asList("&cThere is an error", "&cFor the reward: &c" + reward)));
			}
		}
		Bukkit.getLogger().log(Level.INFO, "[Crazy Crates]>> " + crate + " loaded.");
		previews.put(crate, inv);
	}
	
	private ArrayList<String> getDisabledWorlds() {
		ArrayList<String> worlds = new ArrayList<String>();
		for(String world : Main.settings.getConfig().getStringList("Settings.DisabledWorlds")) {
			worlds.add(world);
		}
		return worlds;
	}
	
}