package com.badbones69.crazycrates.paper.listeners.gui;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.crates.CratePreviewMenu;
import com.badbones69.crazycrates.paper.api.managers.MenuManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.UUID;

public class CratePreviewListener implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull MenuManager menuManager = this.crazyHandler.getMenuManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        if (inventory.getHolder() instanceof CratePreviewMenu) {
            ItemStack itemStack = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();

            Crate crate = this.menuManager.getPlayerCrate().get(player.getUniqueId());

            event.setCancelled(true);

            if (itemStack == null) return;

            if (event.getRawSlot() == crate.getAbsoluteItemPosition(4)) {
                if (this.menuManager.playerInMenu(player)) this.menuManager.openMainMenu(player);
            } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(5)) {
                if (this.menuManager.getPage(uuid) < crate.getMaxPage()) {
                    nextPage(uuid);
                    this.menuManager.openPreview(player, crate);
                }
            } else if (event.getRawSlot() == crate.getAbsoluteItemPosition(3)) {
                if (this.menuManager.getPage(uuid) > 1 && this.menuManager.getPage(uuid) <= crate.getMaxPage()) {
                    backPage(uuid);
                    this.menuManager.openPreview(player, crate);
                }
            }
        }
    }

    private void nextPage(UUID uuid) {
        this.menuManager.setPage(uuid, this.menuManager.getPage(uuid) + 1);
    }

    private void backPage(UUID uuid) {
        this.menuManager.setPage(uuid, this.menuManager.getPage(UUID.randomUUID()) - 1);
    }
}