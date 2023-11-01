package us.crazycrew.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.listeners.PreviewListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (PreviewListener.inPreview(player)) {
                PreviewListener.closePreview(player, InventoryCloseEvent.Reason.PLAYER);
            }
        }
    }
}