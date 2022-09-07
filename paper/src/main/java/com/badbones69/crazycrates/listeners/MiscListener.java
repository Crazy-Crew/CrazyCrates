package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.common.enums.crates.CrateType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

@Singleton
public class MiscListener implements Listener {

    @Inject private CrazyManager crazyManager;

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