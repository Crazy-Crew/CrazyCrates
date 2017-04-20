package me.badbones69.crazycrates.cratetypes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;

public class QuickCrate implements Listener{
	
	public static HashMap<Player, Entity> Reward = new HashMap<Player, Entity>();
	
	public static void openCrate(final Player player, final Location loc, boolean remove){
		if(remove){
			if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
				Methods.removeItem(CrateControl.Key.get(player), player);
			}
			if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
				Methods.takeKeys(1, player, GUI.crates.get(player));
			}
		}
		Prize prize = Main.CC.pickPrize(player, loc.clone().add(.5, 1.3, .5));
		Main.CC.getReward(player, prize);
		Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.QUICK_CRATE, CrateControl.Crate.get(player).getName(), prize));
		final Entity reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), prize.getDisplayItem());
		reward.setVelocity(new Vector(0,.2,0));
		reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
		reward.setCustomNameVisible(true);
		Reward.put(player, reward);
		Methods.playChestAction(loc.getBlock(), true);
		if(prize.toggleFirework()){
			Methods.fireWork(loc.clone().add(.5, 1, .5));
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				if(Reward.get(player)!=null){
					Reward.get(player).remove();
					Reward.remove(player);
					Methods.playChestAction(loc.getBlock(), false);
					GUI.crates.remove(player);
					CrateControl.InUse.remove(player);
				}
			}
		}, 5*20);
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e){
		Entity item = e.getItem();
		for(Player p : Reward.keySet()){
			if(Reward.get(p).equals(item)){
				e.setCancelled(true);
				return;
			}
		}
	}
	
}