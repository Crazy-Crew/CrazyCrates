package com.badbones69.crazycrates.paper.listeners.items;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PaperInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEditorClick(final PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();

        if (block == null || block.isEmpty()) return;

        final Player player = event.getPlayer();

        final Action action = event.getAction();

        if (!this.crateManager.hasEditorCrate(player)) return;

        final Location location = block.getLocation();

        switch (action) {
            case RIGHT_CLICK_BLOCK -> this.crateManager.addCrateByLocation(player, location);
            case LEFT_CLICK_BLOCK -> this.crateManager.removeCrateByLocation(player, location);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();

        if (block == null || block.isEmpty()) return;

        if (this.crateManager.hasEditorCrate(event.getPlayer())) {
            event.setCancelled(true);

            return;
        }

        final Location location = block.getLocation();

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, location).callEvent();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
        final ItemStack itemStack = event.getItemInHand();

        if (itemStack.isEmpty()) return;

        if (this.crateManager.isKey(itemStack)) {
            event.setBuild(false);
        }
    }
}