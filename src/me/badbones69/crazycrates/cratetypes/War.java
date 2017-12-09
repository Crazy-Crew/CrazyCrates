package me.badbones69.crazycrates.cratetypes;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.multisupport.Version;

public class War implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	private static HashMap<Player, Boolean> canPick = new HashMap<Player, Boolean>();
	private static HashMap<Player, Boolean> canClose = new HashMap<Player, Boolean>();
	
	public static void openWarCrate(Player player, Crate crate, KeyType key) {
		Inventory inv = Bukkit.createInventory(null, 9, Methods.color(crate.getFile().getString("Crate.CrateName")));
		setRandomPrizes(player, inv, crate);
		player.openInventory(inv);
		canPick.put(player, false);
		canClose.put(player, false);
		cc.takeKeys(1, player, crate, key);
		startWar(player, inv, crate);
	}
	
	private static void startWar(final Player player, final Inventory inv, Crate crate) {
		cc.addCrateTask(player, new BukkitRunnable() {
			int full = 0;
			int open = 0;
			@Override
			public void run() {
				if(full < 25) {//When Spinning
					setRandomPrizes(player, inv, crate);
					if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
						player.playSound(player.getLocation(), Sound.valueOf("BLOCK_LAVA_POP"), 1, 1);
					}else {
						player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
					}
				}
				open++;
				if(open >= 3) {
					player.openInventory(inv);
					open = 0;
				}
				full++;
				if(full == 26) {//Finished Rolling
					if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
						player.playSound(player.getLocation(), Sound.valueOf("BLOCK_LAVA_POP"), 1, 1);
					}else {
						player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
					}
					setRandomGlass(player, inv);
					canPick.put(player, true);
					return;
				}
			}
		}.runTaskTimer(Main.getPlugin(), 1, 3));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		final Player player = (Player) e.getWhoClicked();
		final Inventory inv = e.getInventory();
		if(inv != null) {
			for(Crate crate : cc.getCrates()) {
				if(crate.getCrateType() == CrateType.WAR) {
					if(inv.getName().equalsIgnoreCase(Methods.color(crate.getFile().getString("Crate.CrateName")))) {
						e.setCancelled(true);
					}
				}
			}
			if(canPick.containsKey(player)) {
				if(cc.isInOpeningList(player)) {
					Crate crate = cc.getOpeningCrate(player);
					if(crate.getCrateType() == CrateType.WAR) {
						if(canPick.get(player)) {
							ItemStack item = e.getCurrentItem();
							if(item != null) {
								if(item.getType() == Material.STAINED_GLASS_PANE) {
									final int slot = e.getRawSlot();
									Prize prize = cc.pickPrize(player, crate);
									inv.setItem(slot, prize.getDisplayItem());
									if(cc.hasCrateTask(player)) {
										cc.endCrate(player);
									}
									canPick.remove(player);
									canClose.put(player, true);
									cc.getReward(player, prize);
									if(prize.toggleFirework()) {
										Methods.fireWork(player.getLocation().add(0, 1, 0));
									}
									Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CSGO, crate.getName(), prize));
									cc.removePlayerFromOpeningList(player);
									if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
										player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_PLACE"), 1, 1);
									}else {
										player.playSound(player.getLocation(), Sound.valueOf("ANVIL_USE"), 1, 1);
									}
									//Sets all other non picked prizes to show what they could have been.
									cc.addCrateTask(player, new BukkitRunnable() {
										@Override
										public void run() {
											for(int i = 0; i < 9; i++) {
												if(i != slot) {
													inv.setItem(i, cc.pickPrize(player, crate).getDisplayItem());
												}
											}
											if(cc.hasCrateTask(player)) {
												cc.endCrate(player);
											}
											//Removing other items then the prize.
											cc.addCrateTask(player, new BukkitRunnable() {
												@Override
												public void run() {
													for(int i = 0; i < 9; i++) {
														if(i != slot) {
															inv.setItem(i, new ItemStack(Material.AIR));
														}
													}
													if(cc.hasCrateTask(player)) {
														cc.endCrate(player);
													}
													//Closing the inventory when finished.
													cc.addCrateTask(player, new BukkitRunnable() {
														@Override
														public void run() {
															if(cc.hasCrateTask(player)) {
																cc.endCrate(player);
															}
															player.closeInventory();
														}
													}.runTaskLater(Main.getPlugin(), 30));
												}
											}.runTaskLater(Main.getPlugin(), 30));
										}
									}.runTaskLater(Main.getPlugin(), 30));
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		if(canClose.containsKey(player)) {
			if(canClose.get(player)) {
				for(Crate crate : cc.getCrates()) {
					if(crate.getCrateType() == CrateType.WAR) {
						if(inv.getName().equalsIgnoreCase(Methods.color(crate.getFile().getString("Crate.CrateName")))) {
							canClose.remove(player);
							if(cc.hasCrateTask(player)) {
								cc.endCrate(player);
							}
						}
					}
				}
			}
		}
	}
	
	private static void setRandomPrizes(Player player, Inventory inv, Crate crate) {
		if(cc.isInOpeningList(player)) {
			if(inv.getName().equalsIgnoreCase(Methods.color(cc.getOpeningCrate(player).getFile().getString("Crate.CrateName")))) {
				for(int i = 0; i < 9; i++) {
					inv.setItem(i, cc.pickPrize(player, crate).getDisplayItem());
				}
			}
		}
	}
	
	private static void setRandomGlass(Player player, Inventory inv) {
		if(cc.isInOpeningList(player)) {
			if(inv.getName().equalsIgnoreCase(Methods.color(cc.getOpeningCrate(player).getFile().getString("Crate.CrateName")))) {
				int color = new Random().nextInt(15);
				if(color == 8) {
					color = 0;
				}
				for(int i = 0; i < 9; i++) {
					inv.setItem(i, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, "&" + getColorCode().get(color) + "&l???"));
				}
			}
		}
	}
	
	private static HashMap<Integer, String> getColorCode() {
		HashMap<Integer, String> colorCodes = new HashMap<Integer, String>();
		colorCodes.put(0, "f");
		colorCodes.put(1, "6");
		colorCodes.put(2, "d");
		colorCodes.put(3, "3");
		colorCodes.put(4, "e");
		colorCodes.put(5, "a");
		colorCodes.put(6, "c");
		colorCodes.put(7, "7");
		colorCodes.put(8, "7");
		colorCodes.put(9, "3");
		colorCodes.put(10, "5");
		colorCodes.put(11, "9");
		colorCodes.put(12, "6");
		colorCodes.put(13, "2");
		colorCodes.put(14, "4");
		colorCodes.put(15, "8");
		return colorCodes;
	}
	
}