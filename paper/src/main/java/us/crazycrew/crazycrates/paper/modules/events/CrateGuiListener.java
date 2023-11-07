package us.crazycrew.crazycrates.paper.modules.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CrateAdminMenu;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CrateMainMenu;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePreviewMenu;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.modules.ModuleHandler;

public class CrateGuiListener extends ModuleHandler {

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (inventory.getHolder(false) instanceof CrateAdminMenu || inventory.getHolder(false) instanceof CrateMainMenu || inventory.getHolder(false) instanceof CratePreviewMenu || inventory.getHolder(false) instanceof CratePrizeMenu) {
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