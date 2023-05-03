package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.enums.types.CrateType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class MiscListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    @EventHandler
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