package com.badbones69.crazycrates.paper.listeners;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MiscListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @EventHandler
    public void onPlayerPickUp(PlayerAttemptPickupItemEvent event) {
        if (this.crazyManager.isDisplayReward(event.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (this.crazyManager.isInOpeningList(event.getPlayer())) {
            // DrBot Start
            if (this.crazyManager.getOpeningCrate(event.getPlayer()).getCrateType().equals(CrateType.quick_crate)) return;
            // DrBot End
            event.setCancelled(true);
        }
    }
}