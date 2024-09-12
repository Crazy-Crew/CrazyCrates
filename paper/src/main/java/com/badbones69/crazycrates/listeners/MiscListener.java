package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import net.kyori.adventure.sound.Sound;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

import java.util.List;

public class MiscListener implements Listener {

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private @NotNull final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Set new keys if we have to.
        this.crateManager.setNewPlayerKeys(player);

        // Just in case any old data is in there.
        this.userManager.loadOldOfflinePlayersKeys(player, this.crateManager.getUsableCrates());

        // Also add the new data.
        this.userManager.loadOfflinePlayersKeys(player, this.crateManager.getUsableCrates());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttemptPickUp(PlayerAttemptPickupItemEvent event) {
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.inventoryManager.removePreviewViewer(player.getUniqueId());

        this.crateManager.removeTier(player);

        this.crateManager.endQuickCrate(player, player.getLocation(), this.crateManager.getOpeningCrate(player), false);

        // End just in case.
        this.crateManager.endCrate(player);
        this.crateManager.endQuadCrate(player);

        this.crateManager.removeCloser(player);
        this.crateManager.removeHands(player);
        this.crateManager.removePicker(player);
        this.crateManager.removePlayerKeyType(player);

        this.crateManager.removeSlot(player);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
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

                this.crateManager.removeTier(player);

                // Cancel crate task just in case.
                this.crateManager.removeCrateTask(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                this.crateManager.removeSlot(player);

                // Remove the player from the hashmap.
                crateManager.removePickedPlayer(player);
            }
        }
    }

    @EventHandler
    public void onItemPickUp(InventoryPickupItemEvent event) {
        if (this.crateManager.isDisplayReward(event.getItem())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        final Inventory inventory = event.getView().getTopInventory();

        if (inventory.getHolder(false) instanceof CratePrizeMenu) {
            event.setCancelled(true);
        }
    }
}