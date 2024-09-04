package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateStatusListener implements Listener {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrateStatus(CrateStatusEvent event) {
        final CrateStatus status = event.getStatus();
        final Player player = event.getPlayer();
        final Crate crate = event.getCrate();

        final Location location = player.getLocation();

        switch (status) {
            case ended, failed -> {
                crate.playSound(player, location, "stop-sound", "entity.player.levelup", Sound.Source.PLAYER);

                switch (crate.getCrateType()) {
                    case quad_crate -> this.crateManager.endQuadCrate(player);

                    case quick_crate -> this.crateManager.endQuickCrate(player, location, crate, false);

                    default -> this.crateManager.endCrate(player);
                }

                // Always remove thy player from thy opening list!
                this.crateManager.removePlayerFromOpeningList(player);

                final Inventory inventory = event.getInventory();

                // Close inventory, if the crate requires an inventory.
                if (inventory != null) {
                    new FoliaRunnable(player.getScheduler(), null) {
                        @Override
                        public void run() {
                            if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                        }
                    }.runDelayed(plugin, 40);
                }
            }

            case cycling -> {
                crate.playSound(player, location, "cycle-sound", "block.note_block.xylophone", Sound.Source.PLAYER);
            }

            case opened -> {

            }

            default -> {}
        }
    }
}