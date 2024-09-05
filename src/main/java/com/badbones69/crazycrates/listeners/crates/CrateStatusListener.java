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
            case ended -> janitor(crate, player, location, event);

            case cycling -> crate.playSound(player, location, "cycle-sound", "block.note_block.xylophone", Sound.Source.PLAYER);

            case opened -> {

            }

            case failed -> {
                janitor(crate, player, location, event);

                // add the key back, since the crate failed.
                this.userManager.addKeys(player.getUniqueId(), crate.getFileName(), KeyType.virtual_key, 1);

                // inform the player, they have been given a refund.
                Messages.key_refund.sendMessage(player, "{crate}", crate.getCrateName());
            }

            default -> {}
        }
    }

    /**
     * Sends common tasks, for ending a crate in the cycle.
     *
     * @param crate {@link Crate}
     * @param player {@link Player}
     * @param location {@link Location}
     * @param event {@link CrateStatusEvent}
     */
    private void janitor(final Crate crate, final Player player, final Location location, final CrateStatusEvent event) {
        crate.playSound(player, location, "stop-sound", "entity.player.levelup", Sound.Source.PLAYER);

        switch (crate.getCrateType()) {
            case quad_crate -> this.crateManager.endQuadCrate(player);

            case quick_crate -> this.crateManager.endQuickCrate(player, location, crate, false);

            default -> this.crateManager.endCrate(player);
        }

        // Always remove thy player from thy opening list!
        this.crateManager.removePlayerFromOpeningList(player);

        // Always close the inventory, after 40 ticks.
        new FoliaRunnable(player.getScheduler(), null) {
            @Override
            public void run() {
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
            }
        }.runDelayed(plugin, 100); // 5 seconds, to take screenshots.
    }
}