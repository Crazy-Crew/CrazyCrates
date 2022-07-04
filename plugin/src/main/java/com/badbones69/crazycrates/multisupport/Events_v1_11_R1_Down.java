package com.badbones69.crazycrates.multisupport;

import com.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Events_v1_11_R1_Down implements Listener {
    
    private final CrazyManager cc = CrazyManager.getInstance();
    
    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent e) {
        if (cc.isDisplayReward(e.getItem())) {
            e.setCancelled(true);
        }

        if (cc.isInOpeningList(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
}