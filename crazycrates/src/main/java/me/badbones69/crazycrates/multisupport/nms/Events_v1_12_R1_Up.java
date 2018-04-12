package me.badbones69.crazycrates.multisupport.nms;

import me.badbones69.crazycrates.cratetypes.QuickCrate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Events_v1_12_R1_Up implements Listener {
	
	@EventHandler
	public void onItemPickUp(EntityPickupItemEvent e) {
		if(QuickCrate.isReward(e.getItem())) {
			e.setCancelled(true);
		}
	}
	
}