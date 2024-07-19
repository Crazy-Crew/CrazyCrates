package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.PaginationManager;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MiscListener implements Listener {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final PaginationManager paginationManager = this.plugin.getPaginationManager();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.crateManager.setNewPlayerKeys(player);

        this.userManager.loadOldOfflinePlayersKeys(player, this.crateManager.getUsableCrates());

        this.userManager.loadOfflinePlayersKeys(player, this.crateManager.getUsableCrates());
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageEvent event) {
        final Entity directEntity = event.getDamageSource().getDirectEntity();

        if (directEntity instanceof final Firework firework) {
            final PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(PersistentKeys.no_firework_damage.getNamespacedKey())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttemptPickUp(PlayerAttemptPickupItemEvent event) {
        if (this.crateManager.isDisplayReward(event.getItem())) {
            event.setCancelled(true);

            return;
        }

        if (this.crateManager.isInOpeningList(event.getPlayer())) {
            if (this.crateManager.getOpeningCrate(event.getPlayer()).getCrateType().equals(CrateType.quick_crate)) return; //drbot

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        // remove player from the page viewers
        this.paginationManager.remove(player);

        // stop the crate tasks.
        this.crateManager.endQuickCrate(player, player.getLocation(), this.crateManager.getOpeningCrate(player), false);
        this.crateManager.endCrate(player);
        this.crateManager.endQuadCrate(player);

        // remove all data related to crates.
        this.crateManager.removeCloser(player);
        this.crateManager.removeHands(player);
        this.crateManager.removePicker(player);
        this.crateManager.removePlayerKeyType(player);
    }

    @EventHandler
    public void onPagedMenuClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        // remove player from the page viewers
        this.paginationManager.remove(player);
    }

    @EventHandler
    public void onCrateMenuClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu holder)) return;

        final Player player = holder.getPlayer();

        final Crate crate = this.crateManager.getOpeningCrate(player);

        if (!this.crateManager.isInOpeningList(player) || crate == null) return;

        switch (crate.getCrateType()) {
            case war -> {
                if (this.crateManager.hasCrateTask(player)) {
                    this.crateManager.removeCloser(player);

                    this.crateManager.removePlayerFromOpeningList(player);
                    this.crateManager.removePlayerKeyType(player);

                    this.crateManager.endCrate(player);
                }
            }

            case cosmic -> {
                final CosmicCrateManager crateManager = (CosmicCrateManager) crate.getManager();

                boolean playSound = false;

                if (holder.contains(" - Prizes")) {
                    for (final Integer key : crateManager.getPrizes(player).keySet()) {
                        final ItemStack item = inventory.getItem(key);

                        if (item != null) {
                            final Tier tier = this.crateManager.getTier(crate, item);

                            if (tier != null) {
                                Prize prize = crate.pickPrize(player, tier);

                                for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                    prize = crate.pickPrize(player, tier);
                                }

                                PrizeManager.givePrize(player, prize, crate);

                                playSound = true;
                            }
                        }
                    }
                }

                // Play sound.
                if (playSound) crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", Sound.Source.PLAYER);

                // Remove opening stuff.
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                // Cancel crate task just in case.
                this.crateManager.removeCrateTask(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                // Remove the player from the hashmap.
                crateManager.removePickedPlayer(player);
            }
        }
    }

    @EventHandler
    public void onItemPickUp(InventoryPickupItemEvent event) {
        if (this.crateManager.isDisplayReward(event.getItem())) event.setCancelled(true);
    }
}