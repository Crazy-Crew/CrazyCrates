package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public class NexoInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onNexoFurnitureInteractEvent(NexoFurnitureInteractEvent event) {
        final ItemDisplay itemDisplay = event.getBaseEntity();
        final Location location = itemDisplay.getLocation();

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, Action.RIGHT_CLICK_BLOCK, location).preventUse().callEvent();
        }
    }

    @EventHandler
    public void onNexoFurnitureBreakEvent(NexoFurnitureBreakEvent event) {
        final ItemDisplay itemDisplay = event.getBaseEntity();
        final Location location = itemDisplay.getLocation();

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, Action.LEFT_CLICK_BLOCK, location).preventUse().callEvent();

            event.setCancelled(true);
        }
    }
}