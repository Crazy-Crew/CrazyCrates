package me.badbones69.crazycrates.multisupport.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import me.badbones69.crazycrates.cratetypes.QuadCrate;
import me.badbones69.crazycrates.cratetypes.QuickCrate;

public class Events_v1_12_R1_Up implements Listener {
	
	@EventHandler
	public void onItemPickUp(EntityPickupItemEvent e) {
		Entity item = e.getItem();
		if(item != null) {
			if(QuickCrate.Rewards.containsValue(item)) {
				e.setCancelled(true);
				return;
			}
			for(Player p : QuadCrate.Rewards.keySet()) {
				if(QuadCrate.Rewards.get(p).contains(item)) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
}