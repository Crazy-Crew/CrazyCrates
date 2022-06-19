package com.badbones69.crazycrates.multisupport;

import com.badbones69.crazycrates.api.CrazyCrates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Events_v1_12_R1_Up implements Listener {
    
    private final CrazyCrates cc = CrazyCrates.getInstance();
    
    @EventHandler
    public void onItemPickUp(EntityPickupItemEvent e) {
        if (cc.isDisplayReward(e.getItem())) {
            e.setCancelled(true);
        } else {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (cc.isInOpeningList(player)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
}