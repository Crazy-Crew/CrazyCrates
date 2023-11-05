package us.crazycrew.crazycrates.paper.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.users.guis.InventoryManager;

public class InventoryListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final InventoryManager inventoryManager = this.crazyHandler.getInventoryManager();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (this.inventoryManager.inCratePreview(player)) {
                // Close preview.
                this.inventoryManager.closeCratePreview(player);
            }
        }
    }
}