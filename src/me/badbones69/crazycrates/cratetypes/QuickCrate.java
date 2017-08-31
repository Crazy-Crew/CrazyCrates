package me.badbones69.crazycrates.cratetypes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;

public class QuickCrate implements Listener {
	
	public static HashMap<Player, Entity> Rewards = new HashMap<>();
	private static HashMap<Player, BukkitTask> tasks = new HashMap<>();
	
	public static void openCrate(final Player player, final Location loc, boolean remove) {
		if(remove) {
			if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY) {
				Methods.removeItem(CrateControl.keys.get(player), player);
			}
			if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY) {
				Methods.takeKeys(1, player, GUI.crates.get(player));
			}
		}
		Prize prize = Main.CC.pickPrize(player, loc.clone().add(.5, 1.3, .5));
		Main.CC.getReward(player, prize);
		Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.QUICK_CRATE, CrateControl.crates.get(player).getName(), prize));
		final Entity reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), prize.getDisplayItem());
		reward.setVelocity(new Vector(0, .2, 0));
		reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
		reward.setCustomNameVisible(true);
		Rewards.put(player, reward);
		Methods.playChestAction(loc.getBlock(), true);
		if(prize.toggleFirework()) {
			Methods.fireWork(loc.clone().add(.5, 1, .5));
		}
		tasks.put(player, new BukkitRunnable() {
			@Override
			public void run() {
				endQuickCrate(player, loc);
			}
		}.runTaskLater(Main.getPlugin(), 5 * 20));
	}
	
	public static void endQuickCrate(Player player, Location loc) {
		if(tasks.containsKey(player)) {
			tasks.get(player).cancel();
			tasks.remove(player);
		}
		if(Rewards.get(player) != null) {
			Rewards.get(player).remove();
			Rewards.remove(player);
			Methods.playChestAction(loc.getBlock(), false);
			GUI.crates.remove(player);
			CrateControl.inUse.remove(player);
		}
	}
	
	@EventHandler
	public void onHopperPickUp(InventoryPickupItemEvent e) {
		if(Rewards.containsValue(e.getItem())) {
			e.setCancelled(true);
		}
	}
	
}