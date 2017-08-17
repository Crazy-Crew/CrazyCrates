package me.badbones69.crazycrates.multisupport;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import me.badbones69.crazycrates.cratetypes.QCC;
import me.badbones69.crazycrates.cratetypes.QuickCrate;

@SuppressWarnings("deprecation")
public class Events_v1_11_R1_Down implements Listener{
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent e) {
		Entity item = e.getItem();
		if(item != null) {
			if(QuickCrate.Rewards.containsValue(item)){
				e.setCancelled(true);
				return;
			}
			for(Player p : QCC.Rewards.keySet()){
				if(QCC.Rewards.get(p).contains(item)){
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
}