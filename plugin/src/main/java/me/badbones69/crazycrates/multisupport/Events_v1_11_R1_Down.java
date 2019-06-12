package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.api.CrazyCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Events_v1_11_R1_Down implements Listener {
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent e) {
		if(cc.isDisplayReward(e.getItem())) {
			e.setCancelled(true);
		}
	}
	
}