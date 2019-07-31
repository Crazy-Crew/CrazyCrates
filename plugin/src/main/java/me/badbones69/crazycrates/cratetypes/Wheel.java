package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Wheel implements Listener {
	
	public static HashMap<Player, HashMap<Integer, ItemStack>> rewards = new HashMap<>();
	private static CrazyCrates cc = CrazyCrates.getInstance();
	
	public static void startWheel(final Player player, Crate crate, KeyType keyType) {
		if(!cc.takeKeys(1, player, crate, keyType)) {
			Methods.failedToTakeKey(player, crate);
			return;
		}
		final Inventory inv = Bukkit.createInventory(null, 54, Methods.color(crate.getFile().getString("Crate.CrateName")));
		for(int i = 0; i < 54; i++) {
			inv.setItem(i, new ItemBuilder().setMaterial(cc.useNewMaterial() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15").setName(" ").build());
		}
		HashMap<Integer, ItemStack> items = new HashMap<>();
		for(int i : getBorder()) {
			Prize prize = crate.pickPrize(player);
			inv.setItem(i, prize.getDisplayItem());
			items.put(i, prize.getDisplayItem());
		}
		rewards.put(player, items);
		player.openInventory(inv);
		cc.addCrateTask(player, new BukkitRunnable() {
			ArrayList<Integer> slots = getBorder();
			int i = 0;
			int f = 17;
			int full = 0;
			int timer = Methods.randomNumber(42, 68);
			int slower = 0;
			int open = 0;
			int slow = 0;
			
			@Override
			public void run() {
				if(i >= 18) {
					i = 0;
				}
				if(f >= 18) {
					f = 0;
				}
				if(full < timer) {
					if(rewards.get(player).get(slots.get(i)).getItemMeta().hasLore()) {
						inv.setItem(slots.get(i), new ItemBuilder().setMaterial(cc.useNewMaterial() ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5").setName(rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()).setLore(rewards.get(player).get(slots.get(i)).getItemMeta().getLore()).build());
					}else {
						inv.setItem(slots.get(i), new ItemBuilder().setMaterial(cc.useNewMaterial() ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5").setName(rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()).build());
					}
					inv.setItem(slots.get(f), rewards.get(player).get(slots.get(f)));
					if(Version.getCurrentVersion().isOlder(Version.v1_9_R1)) {
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}else {
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}
					i++;
					f++;
				}
				if(full >= timer) {
					if(slowSpin().contains(slower)) {
						if(rewards.get(player).get(slots.get(i)).getItemMeta().hasLore()) {
							inv.setItem(slots.get(i), new ItemBuilder().setMaterial(cc.useNewMaterial() ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5").setName(rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()).setLore(rewards.get(player).get(slots.get(i)).getItemMeta().getLore()).build());
						}else {
							inv.setItem(slots.get(i), new ItemBuilder().setMaterial(cc.useNewMaterial() ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5").setName(rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()).build());
						}
						inv.setItem(slots.get(f), rewards.get(player).get(slots.get(f)));
						if(Version.getCurrentVersion().isOlder(Version.v1_9_R1)) {
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}else {
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}
						i++;
						f++;
					}
					if(full == timer + 47) {
						if(Version.getCurrentVersion().isOlder(Version.v1_9_R1)) {
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}else {
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}
					}
					if(full >= timer + 47) {
						slow++;
						if(slow >= 2) {
							Random r = new Random();
							ItemStack item = Methods.getRandomPaneColor().setName(" ").build();
							for(int slot = 0; slot < 54; slot++) {
								if(!getBorder().contains(slot)) {
									inv.setItem(slot, item);
								}
							}
							slow = 0;
						}
					}
					if(full >= (timer + 55 + 47)) {
						Prize prize = null;
						if(cc.isInOpeningList(player)) {
							for(Prize p : crate.getPrizes()) {
								if(rewards.get(player).get(slots.get(f)).isSimilar(p.getDisplayItem())) {
									prize = p;
								}
							}
						}
						if(prize != null) {
							cc.givePrize(player, prize);
							if(prize.useFireworks()) {
								Methods.fireWork(player.getLocation().add(0, 1, 0));
							}
							Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
							player.closeInventory();
						}
						cc.removePlayerFromOpeningList(player);
						cc.endCrate(player);
					}
					slower++;
				}
				full++;
				open++;
				if(open > 5) {
					player.openInventory(inv);
					open = 0;
				}
			}
		}.runTaskTimer(cc.getPlugin(), 1, 1));
	}
	
	private static ArrayList<Integer> slowSpin() {
		ArrayList<Integer> slow = new ArrayList<>();
		int full = 46;
		int cut = 9;
		for(int i = 46; cut > 0; full--) {
			if(full <= i - cut || full >= i - cut) {
				slow.add(i);
				i = i - cut;
				cut--;
			}
		}
		return slow;
	}
	
	private static ArrayList<Integer> getBorder() {
		ArrayList<Integer> slots = new ArrayList<>();
		slots.add(13);
		slots.add(14);
		slots.add(15);
		slots.add(16);
		slots.add(25);
		slots.add(34);
		slots.add(43);
		slots.add(42);
		slots.add(41);
		slots.add(40);
		slots.add(39);
		slots.add(38);
		slots.add(37);
		slots.add(28);
		slots.add(19);
		slots.add(10);
		slots.add(11);
		slots.add(12);
		return slots;
	}
	
}