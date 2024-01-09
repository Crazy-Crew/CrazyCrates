package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.managers.InventoryManager;
import org.bukkit.entity.Firework;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.managers.crates.CrateManager;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MiscListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final InventoryManager inventoryManager = this.plugin.getCrazyHandler().getInventoryManager();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set new keys if we have to.
        this.crateManager.setNewPlayerKeys(player);

        // Just in case any old data is in there.
        this.plugin.getUserManager().loadOldOfflinePlayersKeys(player, this.crateManager.getCrates());

        // Also add the new data.
        this.plugin.getUserManager().loadOfflinePlayersKeys(player, this.crateManager.getCrates());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.inventoryManager.removeViewer(player);
        this.inventoryManager.removeCrateViewer(player);
        this.inventoryManager.removePageViewer(player);

        this.crateManager.endQuickCrate(player, player.getLocation(), this.crateManager.getOpeningCrate(player), false);

        // End just in case.
        this.crateManager.endCrate(player);
        this.crateManager.endQuadCrate(player);

        this.crateManager.removeCloser(player);
        this.crateManager.removeHands(player);
        this.crateManager.removePicker(player);
        this.crateManager.removePlayerKeyType(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework) {
            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(PersistentKeys.no_firework_damage.getNamespacedKey(this.plugin))) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(InventoryPickupItemEvent event) {
        if (this.crateManager.isDisplayReward(event.getItem())) event.setCancelled(true);
    }
}
