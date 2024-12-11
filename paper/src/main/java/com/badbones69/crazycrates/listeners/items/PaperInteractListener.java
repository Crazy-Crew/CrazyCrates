package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.nexomc.nexo.api.NexoFurniture;
import com.ryderbelserion.vital.paper.api.enums.Support;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
        final EquipmentSlot equipmentSlot = event.getHand();

        if (equipmentSlot == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();

        if (this.crateManager.hasEditorCrate(player)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            return;
        }

        // get interaction point.
        final @Nullable Block block = event.getClickedBlock();

        // check location.
        if (block == null || block.getType().isAir()) return;

        final Location location = block.getLocation();

        // check if key, cancel.
        if (this.crateManager.isKey(player, equipmentSlot)) {
            event.setUseItemInHand(Event.Result.DENY);
        }

        if (Support.nexo.isEnabled()) {
            if (NexoFurniture.isFurniture(location)) return; // return because it's furniture
        }

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(event, location);

        // call our event.
        interactEvent.callEvent();
    }
}