package com.badbones69.crazycrates.paper.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.crates.menus.InventoryManager;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CrateMainMenu;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePreviewMenu;

public class PreviewListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final InventoryManager inventoryManager = this.crazyHandler.getInventoryManager();

    @NotNull
    private final ConfigManager configManager = this.plugin.getConfigManager();

    @NotNull
    private final SettingsManager config = this.configManager.getConfig();

    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null) return;

        if (this.inventoryManager.getCratePreview(player) == null) return;

        if (!(e.getClickedInventory().getHolder() instanceof CratePreviewMenu)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        Crate crate = this.inventoryManager.getCratePreview(player);

        if (e.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.
            if (this.inventoryManager.inCratePreview(player)) {
                this.inventoryManager.removeViewer(player);
                this.inventoryManager.closeCratePreview(player);

                CrateMainMenu crateMainMenu = new CrateMainMenu(this.plugin, player, this.config.getProperty(Config.inventory_size), this.config.getProperty(Config.inventory_name));

                player.openInventory(crateMainMenu.build().getInventory());
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