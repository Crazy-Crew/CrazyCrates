package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.users.guis.InventoryManager;

public class PreviewListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final InventoryManager inventoryManager = this.crazyHandler.getInventoryManager();

    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() != null && this.inventoryManager.getCratePreview(player) != null) {
            Crate crate = this.inventoryManager.getCratePreview(player);

            if (e.getCurrentItem() != null) {
                if (crate.isPreview(e.getView())) {
                    e.setCancelled(true);

                    if (e.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.
                        if (this.inventoryManager.inCratePreview(player)) {
                            this.inventoryManager.removeViewer(player);
                            this.inventoryManager.closeCratePreview(player);
                            this.inventoryManager.openGUI(player);
                        }
                    } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(5)) { // Clicked the next button.
                        if (this.inventoryManager.getPage(player) < crate.getMaxPage()) {
                            this.inventoryManager.nextPage(player);

                            this.inventoryManager.openCratePreview(player, crate);
                        }
                    } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(3)) { // Clicked the back button.
                        if (this.inventoryManager.getPage(player) > 1 && this.inventoryManager.getPage(player) <= crate.getMaxPage()) {
                            this.inventoryManager.backPage(player);

                            this.inventoryManager.openCratePreview(player, crate);
                        }
                    }
                }
            }
        }
    }
}