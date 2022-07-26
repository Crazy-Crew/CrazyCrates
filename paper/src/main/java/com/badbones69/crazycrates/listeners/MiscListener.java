package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class MiscListener implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickUp(PlayerAttemptPickupItemEvent event) {
        if (crazyManager.isDisplayReward(event.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (crazyManager.isInOpeningList(event.getPlayer())) {
            // DrBot Start
            if (crazyManager.getOpeningCrate(event.getPlayer()).getCrateType().equals(CrateType.QUICK_CRATE)) return;
            // DrBot End
            event.setCancelled(true);
        }
    }
}