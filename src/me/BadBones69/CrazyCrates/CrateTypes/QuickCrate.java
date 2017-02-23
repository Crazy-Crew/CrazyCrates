package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.BadBones69.CrazyCrates.CrateControl;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.API.CrateType;
import me.BadBones69.CrazyCrates.API.CrazyCrates;
import me.BadBones69.CrazyCrates.API.KeyType;
import me.BadBones69.CrazyCrates.API.PlayerPrizeEvent;

public class QuickCrate implements Listener{
	
	public static HashMap<Player, Entity> Reward = new HashMap<Player, Entity>();
	private static CrazyCrates CC = CrazyCrates.getInstance();
	
	public static void openCrate(final Player player, final Location loc, boolean remove){
		if(remove){
			if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
				Methods.removeItem(CrateControl.Key.get(player), player);
			}
			if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
				Methods.takeKeys(1, player, GUI.Crate.get(player));
			}
		}
		if(!CrateControl.Rewards.containsKey(player)){
			CC.getItems(player);
		}
		ItemStack item = CC.pickItem(player, loc.clone().add(.5, 1.3, .5));
		String path = CrateControl.Rewards.get(player).get(item);
		CC.getReward(player, path);
		Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.QUICK_CRATE, CrateControl.Crate.get(player), path.replace("Crate.Prizes.", "")));
		String name = Methods.color(Main.settings.getFile(GUI.Crate.get(player)).getString(path+".DisplayName"));
		final Entity reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), item);
		reward.setVelocity(new Vector(0,.2,0));
		reward.setCustomName(name);
		reward.setCustomNameVisible(true);
		Reward.put(player, reward);
		Methods.playChestAction(loc.getBlock(), true);
		if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean("Crate.Prizes."+path.replace("Crate.Prizes.", "")+".Firework")){
			Methods.fireWork(loc.clone().add(.5, 1, .5));
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				if(Reward.get(player)!=null){
					Reward.get(player).remove();
					Reward.remove(player);
					Methods.playChestAction(loc.getBlock(), false);
					GUI.Crate.remove(player);
					CrateControl.InUse.remove(player);
					CrateControl.Rewards.remove(player);
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