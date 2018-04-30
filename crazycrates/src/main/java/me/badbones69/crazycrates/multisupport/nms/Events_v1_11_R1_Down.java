package me.badbones69.crazycrates.multisupport.nms;

import me.badbones69.crazycrates.cratetypes.QuickCrate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Events_v1_11_R1_Down implements Listener {
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent e) {
		if(QuickCrate.isReward(e.getItem())) {
			e.setCancelled(true);
		}
	}
	
}