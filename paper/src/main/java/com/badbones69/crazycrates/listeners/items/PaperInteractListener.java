package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class PaperInteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        // get interaction point.
        final @Nullable Block block = event.getClickedBlock();

        // check location.
        if (block == null) return;

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