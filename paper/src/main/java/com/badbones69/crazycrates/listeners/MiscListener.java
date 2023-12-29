package com.badbones69.crazycrates.listeners;

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

public class MiscListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set new keys if we have to.
        this.plugin.getCrateManager().setNewPlayerKeys(player);

        // Just in case any old data is in there.
        this.plugin.getUserManager().loadOldOfflinePlayersKeys(player, this.crateManager.getCrates());

        // Also add the new data.
        this.plugin.getUserManager().loadOfflinePlayersKeys(player, this.crateManager.getCrates());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getCrazyHandler().getInventoryManager().removeViewer(player);
        this.plugin.getCrazyHandler().getInventoryManager().removeCrateViewer(player);
        this.plugin.getCrazyHandler().getInventoryManager().removePageViewer(player);

        this.plugin.getCrazyHandler().getCrateManager().removePlayerFromOpeningList(player);
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
        if (this.plugin.getCrateManager().isDisplayReward(event.getItem())) event.setCancelled(true);
    }
}