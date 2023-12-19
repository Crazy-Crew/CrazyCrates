package com.badbones69.crazycrates.listeners.menus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.modules.ModuleHandler;

public class CrateGuiListener extends ModuleHandler {

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (inventory.getHolder() instanceof CrateAdminMenu || inventory.getHolder() instanceof CrateMainMenu || inventory.getHolder() instanceof CratePreviewMenu || inventory.getHolder() instanceof CratePrizeMenu) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getModuleName() {
        return "Crate Gui Listener";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void reload() {}
}