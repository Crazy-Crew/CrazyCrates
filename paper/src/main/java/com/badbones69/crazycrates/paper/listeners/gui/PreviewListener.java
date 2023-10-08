package com.badbones69.crazycrates.paper.listeners.gui;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.managers.MenuManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.UUID;

public class PreviewListener implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull MenuManager menuManager = this.crazyHandler.getMenuManager();

    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        if (e.getClickedInventory() == null || this.menuManager.getPlayerCrate().get(uuid) == null) return;

        Crate crate = this.menuManager.getPlayerCrate().get(player.getUniqueId());

        if (!crate.isPreview(e.getView())) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        if (e.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.
            if (this.menuManager.playerInMenu(player)) this.crazyHandler.getMenuManager().openMainMenu(player);
        } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(5)) { // Clicked the next button.
            if (this.menuManager.getPage(uuid) < crate.getMaxPage()) {
                nextPage(uuid);
                this.menuManager.openPreview(player, crate);
            }
        } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(3)) { // Clicked the back button.
            if (this.menuManager.getPage(uuid) > 1 && this.menuManager.getPage(uuid) <= crate.getMaxPage()) {
                backPage(uuid);
                this.menuManager.openPreview(player, crate);
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