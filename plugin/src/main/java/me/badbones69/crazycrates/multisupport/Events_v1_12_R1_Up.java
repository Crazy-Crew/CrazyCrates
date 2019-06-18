package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.api.CrazyCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Events_v1_12_R1_Up implements Listener {
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	@EventHandler
	public void onItemPickUp(EntityPickupItemEvent e) {
		if(cc.isDisplayReward(e.getItem())) {
			e.setCancelled(true);
		}
	}
	
}