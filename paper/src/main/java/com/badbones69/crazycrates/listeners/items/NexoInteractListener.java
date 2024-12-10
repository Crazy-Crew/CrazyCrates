package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public class NexoInteractListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNexoFurnitureInteractEvent(NexoFurnitureInteractEvent event) {
        // get item display.
        final ItemDisplay itemDisplay = event.getBaseEntity();

        // fetch location.
        final Location location = itemDisplay.getLocation();

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(location, event.getHand(), event.getPlayer(), Action.RIGHT_CLICK_BLOCK);

        // call our interact event.
        interactEvent.callEvent();

        // cancel the interact event.
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNexoFurnitureBreakEvent(NexoFurnitureBreakEvent event) {
        // get item display.
        final ItemDisplay itemDisplay = event.getBaseEntity();

        // fetch location.
        final Location location = itemDisplay.getLocation();

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(location, event.getPlayer().getActiveItemHand(), event.getPlayer(), Action.LEFT_CLICK_BLOCK);

        // call our interact event.
        interactEvent.callEvent();

        // cancel break event.
        event.setCancelled(true);
    }
}