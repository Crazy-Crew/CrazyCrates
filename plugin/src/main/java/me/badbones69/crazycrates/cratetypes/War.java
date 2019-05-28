package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class War implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	private static HashMap<Player, Boolean> canPick = new HashMap<>();
	private static HashMap<Player, Boolean> canClose = new HashMap<>();
	
	public static void openWarCrate(Player player, Crate crate, KeyType key) {
		String crateName = Methods.color(crate.getFile().getString("Crate.CrateName"));
		Inventory inv = Bukkit.createInventory(null, 9, crateName);
		setRandomPrizes(player, inv, crate, crateName);
		InventoryView inventoryView = player.openInventory(inv);
		canPick.put(player, false);
		canClose.put(player, false);
		cc.takeKeys(1, player, crate, key);
		startWar(player, inv, crate, inventoryView.getTitle());
	}
	
	private static void startWar(final Player player, final Inventory inv, final Crate crate, final String inventoryTitle) {
		cc.addCrateTask(player, new BukkitRunnable() {
			int full = 0;
			int open = 0;
			@Override
			public void run() {
				if(full < 25) {//When Spinning
					setRandomPrizes(player, inv, crate, inventoryTitle);
					player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
				}
				open++;
				if(open >= 3) {
					player.openInventory(inv);
					open = 0;
				}
				full++;
				if(full == 26) {//Finished Rolling
					player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
					setRandomGlass(player, inv, inventoryTitle);
					canPick.put(player, true);
				}
			}
		}.runTaskTimer(cc.getPlugin(), 1, 3));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		final Player player = (Player) e.getWhoClicked();
		final Inventory inv = e.getInventory();
		if(inv != null) {
			for(Crate crate : cc.getCrates()) {
				if(crate.getCrateType() == CrateType.WAR) {
					if(e.getView().getTitle().equalsIgnoreCase(Methods.color(crate.getFile().getString("Crate.CrateName")))) {
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
								if(item.getType().toString().contains("STAINED_GLASS_PANE")) {
									final int slot = e.getRawSlot();
									Prize prize = crate.pickPrize(player);
									inv.setItem(slot, prize.getDisplayItem());
									if(cc.hasCrateTask(player)) {
										cc.endCrate(player);
									}
									canPick.remove(player);
									canClose.put(player, true);
									cc.givePrize(player, prize);
									if(prize.useFireworks()) {
										Methods.fireWork(player.getLocation().add(0, 1, 0));
									}
									Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
									cc.removePlayerFromOpeningList(player);
									player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
									//Sets all other non picked prizes to show what they could have been.
									cc.addCrateTask(player, new BukkitRunnable() {
										@Override
										public void run() {
											for(int i = 0; i < 9; i++) {
												if(i != slot) {
													inv.setItem(i, crate.pickPrize(player).getDisplayItem());
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
													}.runTaskLater(cc.getPlugin(), 30));
												}
											}.runTaskLater(cc.getPlugin(), 30));
										}
									}.runTaskLater(cc.getPlugin(), 30));
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
						if(e.getView().getTitle().equalsIgnoreCase(Methods.color(crate.getFile().getString("Crate.CrateName")))) {
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
	
	private static void setRandomPrizes(Player player, Inventory inv, Crate crate, String inventoryTitle) {
		if(cc.isInOpeningList(player)) {
			if(inventoryTitle.equalsIgnoreCase(Methods.color(cc.getOpeningCrate(player).getFile().getString("Crate.CrateName")))) {
				for(int i = 0; i < 9; i++) {
					inv.setItem(i, crate.pickPrize(player).getDisplayItem());
				}
			}
		}
	}
	
	private static void setRandomGlass(Player player, Inventory inv, String inventoryTitle) {
		if(cc.isInOpeningList(player)) {
			if(inventoryTitle.equalsIgnoreCase(Methods.color(cc.getOpeningCrate(player).getFile().getString("Crate.CrateName")))) {
				Material material = Methods.getRandomPaneColor();
				for(int i = 0; i < 9; i++) {
					inv.setItem(i, new ItemBuilder().setMaterial(material).setName("&" + getColorCode().get(material) + "&l???").build());
				}
			}
		}
	}
	
	private static HashMap<Material, String> getColorCode() {
		HashMap<Material, String> colorCodes = new HashMap<>();
		colorCodes.put(Material.WHITE_STAINED_GLASS_PANE, "f");
		colorCodes.put(Material.ORANGE_STAINED_GLASS_PANE, "6");
		colorCodes.put(Material.MAGENTA_STAINED_GLASS_PANE, "d");
		colorCodes.put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "3");
		colorCodes.put(Material.YELLOW_STAINED_GLASS_PANE, "e");
		colorCodes.put(Material.LIME_STAINED_GLASS_PANE, "a");
		colorCodes.put(Material.PINK_STAINED_GLASS_PANE, "c");
		colorCodes.put(Material.GRAY_STAINED_GLASS_PANE, "7");
		colorCodes.put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "7");
		colorCodes.put(Material.CYAN_STAINED_GLASS_PANE, "3");
		colorCodes.put(Material.PURPLE_STAINED_GLASS_PANE, "5");
		colorCodes.put(Material.BLUE_STAINED_GLASS_PANE, "9");
		colorCodes.put(Material.BROWN_STAINED_GLASS_PANE, "6");
		colorCodes.put(Material.GREEN_STAINED_GLASS_PANE, "2");
		colorCodes.put(Material.RED_STAINED_GLASS_PANE, "4");
		colorCodes.put(Material.BLACK_STAINED_GLASS_PANE, "8");
		return colorCodes;
	}
	
}