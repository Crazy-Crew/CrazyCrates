package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.controllers.FileManager;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Cosmic implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	private static HashMap<Player, ArrayList<Integer>> glass = new HashMap<>();
	private static HashMap<Player, ArrayList<Integer>> picks = new HashMap<>();
	private static HashMap<Player, HashMap<ItemStack, String>> tiers = new HashMap<>();
	
	private static void showRewards(Player player, Crate crate) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(crate.getFile().getString("Crate.CrateName") + " - Prizes"));
		for(int i : picks.get(player))
			inv.setItem(i, pickTier(player));
		player.openInventory(inv);
	}
	
	private static void startRoll(Player player, Crate crate) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(crate.getFile().getString("Crate.CrateName") + " - Shuffling"));
		for(int i = 0; i < 27; i++) {
			inv.setItem(i, pickTier(player));
		}
		if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
			player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
		}else {
			player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
		}
		player.openInventory(inv);
	}
	
	private static void setChests(Inventory inv) {
		int amount = 1;
		for(int i = 0; i < 27; i++) {
			inv.setItem(i, Methods.makeItem(Material.CHEST, amount, 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
			amount++;
		}
	}
	
	public static void openCosmic(Player player, Crate crate, KeyType key) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(crate.getFile().getString("Crate.CrateName") + " - Choose"));
		setChests(inv);
		cc.addPlayerKeyType(player, key);
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		final Inventory inv = e.getInventory();
		final Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			final Crate crate = cc.getOpeningCrate(player);
			if(cc.isInOpeningList(player)) {
				if(!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
			}else {
				return;
			}
			final FileConfiguration file = crate.getFile();
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName") + " - Shuffling"))) {
				e.setCancelled(true);
			}
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName") + " - Prizes"))) {
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)) {
					for(int i : picks.get(player)) {
						if(slot == i) {
							ItemStack item = e.getCurrentItem();
							String tier = tiers.get(player).get(item);
							if(item != null && tier != null) {
								if(item.getType() == Material.STAINED_GLASS_PANE && !tier.equals("")) {
									if(item.hasItemMeta()) {
										if(item.getItemMeta().hasDisplayName()) {
											if(item.getItemMeta().getDisplayName().equals(Methods.color(file.getString("Crate.Tiers." + tier + ".Name")))) {
												Prize prize = crate.pickPrize(player, tier);
												for(int stop = 0; prize == null && stop <= 2000; stop++) {
													prize = crate.pickPrize(player, tier);
												}
												if(prize != null) {
													cc.givePrize(player, prize);
													Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, cc.getOpeningCrate(player).getName(), prize));
													e.setCurrentItem(prize.getDisplayItem());
													if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
														player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
													}
													if(prize.useFireworks()) {
														Methods.fireWork(player.getLocation().add(0, 1, 0));
													}
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
			}
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName") + " - Choose"))) {
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)) {
					ItemStack item = e.getCurrentItem();
					if(item != null) {
						if(item.getType() == Material.CHEST) {
							if(!glass.containsKey(player)) glass.put(player, new ArrayList<>());
							if(glass.get(player).size() < 4) {
								e.setCurrentItem(Methods.makeItem(Material.THIN_GLASS, (slot + 1), 0, "&f&l???", Arrays.asList("&7You have chosen #" + (slot + 1) + ".")));
								glass.get(player).add(slot);
							}
							if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
								player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
							}else {
								player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
							}
						}
						if(item.getType() == Material.THIN_GLASS) {
							if(!glass.containsKey(player)) glass.put(player, new ArrayList<>());
							e.setCurrentItem(Methods.makeItem(Material.CHEST, (slot + 1), 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
							ArrayList<Integer> l = new ArrayList<>();
							for(int i : glass.get(player))
								if(i != slot) l.add(i);
							glass.put(player, l);
							if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
								player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
							}else {
								player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
							}
						}
						if(glass.get(player).size() >= 4) {
							KeyType keyType = cc.getPlayerKeyType(player);
							if(keyType == KeyType.PHYSICAL_KEY) {
								if(!cc.hasPhysicalKey(player, crate)) {
									player.closeInventory();
									player.sendMessage(Methods.getPrefix() + Methods.color("&cNo key was found."));
									if(cc.isInOpeningList(player)) {
										cc.removePlayerFromOpeningList(player);
									}
									glass.remove(player);
									return;
								}
							}
							if(cc.hasPlayerKeyType(player)) {
								cc.takeKeys(1, player, crate, keyType);
							}
							cc.addCrateTask(player, new BukkitRunnable() {
								int time = 0;
								@Override
								public void run() {
									try {
										startRoll(player, crate);
									}catch(Exception e) {
										cc.addKeys(1, player, crate, keyType);
										cc.endCrate(player);
										player.sendMessage(Methods.getPrefix("&cAn issue has occured and so a key refund was given."));
										System.out.println(FileManager.getInstance().getPrefix() + "An issue occured when the user " + player.getName() +
										" was using the " + crate.getName() + " crate and so they were issued a key refund.");
										e.printStackTrace();
										return;
									}
									time++;
									if(time == 40) {
										cc.endCrate(player);
										showRewards(player, crate);
										if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
											player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1, 1);
										}else {
											player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 1, 1);
										}
									}
								}
							}.runTaskTimer(Main.getPlugin(), 0, 2));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		Crate crate = cc.getOpeningCrate(player);
		if(cc.isInOpeningList(player)) {
			if(crate.getFile() == null) {
				return;
			}else {
				if(!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) {
					return;
				}
			}
		}else {
			return;
		}
		if(inv.getName().equals(Methods.color(crate.getFile().getString("Crate.CrateName") + " - Prizes"))) {
			boolean playSound = false;
			for(int i : picks.get(player)) {
				if(inv.getItem(i) != null) {
					if(inv.getItem(i).getType() == Material.STAINED_GLASS_PANE) {
						ItemStack item = inv.getItem(i);
						if(item.hasItemMeta()) {
							if(item.getItemMeta().hasDisplayName()) {
								if(tiers.containsKey(player)) {
									if(tiers.get(player).containsKey(item)) {
										String tier = tiers.get(player).get(item);
										if(item.getItemMeta().getDisplayName().equals(Methods.color(crate.getFile().getString("Crate.Tiers." + tier + ".Name")))) {
											Prize prize = crate.pickPrize(player, tier);
											for(int stop = 0; prize == null && stop <= 2000; stop++) {
												prize = crate.pickPrize(player, tier);
											}
											cc.givePrize(player, prize);
											playSound = true;
										}
									}
								}
							}
						}
					}
				}
			}
			if(playSound) {
				if(Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
					player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
				}else {
					player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
				}
			}
			if(cc.isInOpeningList(player)) {
				cc.removePlayerFromOpeningList(player);
			}
			if(glass.containsKey(player)) {
				picks.put(player, glass.get(player));
				glass.remove(player);
			}
		}
		if(cc.isInOpeningList(player)) {
			if(inv.getName().equals(Methods.color(crate.getFile().getString("Crate.CrateName") + " - Choose"))) {
				if(!glass.containsKey(player) || glass.get(player).size() < 4) {
					cc.removePlayerFromOpeningList(player);
				}
				if(glass.containsKey(player)) {
					picks.put(player, glass.get(player));
					glass.remove(player);
				}
			}
		}
	}
	
	private static ItemStack pickTier(Player player) {
		FileConfiguration file = cc.getOpeningCrate(player).getFile();
		if(!tiers.containsKey(player)) {
			HashMap<ItemStack, String> list = new HashMap<>();
			for(String tier : file.getConfigurationSection("Crate.Tiers").getKeys(false)) {
				list.put(Methods.makeItem(Material.STAINED_GLASS_PANE, 1, file.getInt("Crate.Tiers." + tier + ".Color"), file.getString("Crate.Tiers." + tier + ".Name")), tier);
			}
			tiers.put(player, list);
		}
		ItemStack item = new ItemStack(Material.AIR);
		Random r = new Random();
		int stop = 0;
		for(; item.getType() == Material.AIR; stop++) {
			if(stop == 100) {
				break;
			}
			for(String tier : tiers.get(player).values()) {
				int chance = file.getInt("Crate.Tiers." + tier + ".Chance");
				int max = file.getInt("Crate.Tiers." + tier + ".MaxRange") - 1;
				int num = 1 + r.nextInt(max);
				if(num >= 1 && num <= chance) {
					item = Methods.makeItem(Material.STAINED_GLASS_PANE, 1, file.getInt("Crate.Tiers." + tier + ".Color"), file.getString("Crate.Tiers." + tier + ".Name"));
				}
			}
		}
		return item;
	}
	
	private boolean inCosmic(int slot) {
		//The last slot in comsic crate is 27
		return slot < 27;
	}
	
}