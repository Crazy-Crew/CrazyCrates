package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.cache.CacheManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
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
import java.util.UUID;

public class CrateControlListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final CacheManager cacheManager = this.platform.getCacheManager();

    private final InventoryManager inventoryManager = this.platform.getInventoryManager();

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
        final UUID uuid = player.getUniqueId();

        this.inventoryManager.removePreviewViewer(uuid);

        this.cacheManager.getActiveCrate(uuid).ifPresent(activeCrate -> {
            final Crate crate = activeCrate.getCrate();

            this.crateManager.endCrate(crate, player);
        });
    }
}