package com.badbones69.crazycrates.paper.listeners.items;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class NexoInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onFurnitureInteractEvent(NexoFurnitureInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final ItemDisplay itemDisplay = event.getBaseEntity();
        final Location location = itemDisplay.getLocation();

        final Player player = event.getPlayer();

        if (this.crateManager.hasEditorCrate(player)) {
            this.crateManager.addCrateByLocation(player, location);

            event.setCancelled(true);

            return;
        }

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, Action.RIGHT_CLICK_BLOCK, location).callEvent();
        }
    }

    @EventHandler
    public void onFurnitureBreakEvent(NexoFurnitureBreakEvent event) {
        final ItemDisplay itemDisplay = event.getBaseEntity();
        final Location location = itemDisplay.getLocation();

        final Player player = event.getPlayer();

        if (player.getActiveItemHand() == EquipmentSlot.OFF_HAND) return;

        if (this.crateManager.hasEditorCrate(player)) {
            this.crateManager.removeCrateByLocation(player, location, false);

            event.setCancelled(true);

            return;
        }

        if (this.crateManager.isCrateLocation(location)) {
            if (player.isSneaking() && player.hasPermission("crazycrates.admin")) {
                this.crateManager.removeCrateByLocation(player, location, true);

                event.setCancelled(true);

                return;
            }

            new CrateInteractEvent(event, Action.LEFT_CLICK_BLOCK, location).callEvent();
        }
    }
}