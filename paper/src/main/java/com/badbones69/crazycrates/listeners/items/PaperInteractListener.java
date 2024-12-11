package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PaperInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (block == null || block.isEmpty()) return;

        final Location location = block.getLocation();

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, location).preventUse().callEvent();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
        final ItemStack itemStack = event.getItemInHand();

        if (this.crateManager.isKey(itemStack)) {
            event.setBuild(false);
        }
    }
}