package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class PaperInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (this.crateManager.hasEditorCrate(event.getPlayer())) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            return;
        }

        // get interaction point.
        final @Nullable Block block = event.getClickedBlock();

        // check location.
        if (block == null || block.getType().isAir()) return;

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(event, block.getLocation());

        // check if key, cancel.
        if (interactEvent.isKey()) {
            event.setUseItemInHand(Event.Result.DENY);
        }

        // call our event.
        interactEvent.callEvent();
    }
}