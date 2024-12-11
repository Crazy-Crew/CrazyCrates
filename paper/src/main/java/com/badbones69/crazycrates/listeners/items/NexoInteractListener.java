package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public class NexoInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNexoFurnitureInteractEvent(NexoFurnitureInteractEvent event) {
        final Player player = event.getPlayer();

        // get item display.
        final ItemDisplay itemDisplay = event.getBaseEntity();

        // fetch location.
        final Location location = itemDisplay.getLocation();

        if (this.crateManager.hasEditorCrate(player)) {
            if (!player.hasPermission("crazycrates.editor")) {
                this.crateManager.removeEditorCrate(player);

                Messages.force_editor_exit.sendMessage(player, "{reason}", "Lacking permission crazycrates.editor");

                return;
            }

            this.crateManager.addEditorCrateLocation(player, location);

            event.setCancelled(true);

            return;
        }

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(location, event.getHand(), player, Action.RIGHT_CLICK_BLOCK);

        // call our interact event.
        interactEvent.callEvent();

        // cancel the interact event.
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNexoFurnitureBreakEvent(NexoFurnitureBreakEvent event) {
        final Player player = event.getPlayer();

        if (this.crateManager.hasEditorCrate(player)) {
            event.setCancelled(true);

            return;
        }

        // get item display.
        final ItemDisplay itemDisplay = event.getBaseEntity();

        // fetch location.
        final Location location = itemDisplay.getLocation();

        // build our interact event.
        final CrateInteractEvent interactEvent = new CrateInteractEvent(location, player.getActiveItemHand(), player, Action.LEFT_CLICK_BLOCK);

        // call our interact event.
        interactEvent.callEvent();

        // cancel break event.
        event.setCancelled(true);
    }
}