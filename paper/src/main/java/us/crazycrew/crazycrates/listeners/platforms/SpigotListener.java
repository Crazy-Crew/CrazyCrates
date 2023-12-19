package us.crazycrew.crazycrates.listeners.platforms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CrazyCrates;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.managers.crates.CrateManager;

public class SpigotListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.crateManager.isDisplayReward(event.getItem())) {
            event.setCancelled(true);
            return;
        }

        if (this.crateManager.isInOpeningList(event.getPlayer())) {
            // DrBot Start
            if (this.crateManager.getOpeningCrate(event.getPlayer()).getCrateType().equals(CrateType.quick_crate)) return;

            // DrBot End
            event.setCancelled(true);
        }
    }
}