package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;

public class CrateControlListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onPistonPushCrate(BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            final Location location = block.getLocation();

            final Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPistonPullCrate(BlockPistonRetractEvent event) {
        for (final Block block : event.getBlocks()) {
            final Location location = block.getLocation();

            final Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (this.crateManager.hasCrateTask(player)) this.crateManager.endCrate(player);

        if (this.crateManager.hasQuadCrateTask(player)) this.crateManager.endQuadCrate(player);

        if (this.crateManager.isInOpeningList(player)) this.crateManager.removePlayerFromOpeningList(player);
    }
}