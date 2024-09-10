package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;

public class CrateStatusListener implements Listener {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final UserManager userManager = this.plugin.getUserManager();

    @EventHandler
    public void onPlayerPrize(PlayerPrizeEvent event) {
        final Player player = event.getPlayer();
        final Prize prize = event.getPrize();
        final Crate crate = event.getCrate();

        final CrateStatusEvent status = event.getEvent();

        if (prize == null) {
            if (status != null) {
                status.setStatus(CrateStatus.failed).callEvent();
            }

            if (MiscUtils.isLogging()) {
                this.plugin.getComponentLogger().warn("Prize was not found, thus the crate failed! A refund has been given to {} for the crate {}", player.getName(), crate.getFileName());
            }

            event.setCancelled(true);

            return;
        }

        PrizeManager.givePrize(player, prize, crate);

        if (prize.useFireworks()) {
            MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);
        }

        if (status != null) {
            status.setStatus(CrateStatus.ended).callEvent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrateStatus(CrateStatusEvent event) {
        final CrateStatus status = event.getStatus();
        final Player player = event.getPlayer();
        final Crate crate = event.getCrate();

        final Location location = player.getLocation();

        switch (status) {
            case ended -> janitor(crate, player, location, event);

            case cycling -> crate.playSound(player, location, "cycle-sound", "block.note_block.xylophone", Sound.Source.PLAYER);

            case opened -> {

            }

            case failed -> {
                janitor(crate, player, location, event);

                // add the key back, since the crate failed.
                this.userManager.addKeys(player.getUniqueId(), crate.getFileName(), KeyType.virtual_key, 1);

                // inform the player, they have been given a refund.
                Messages.key_refund.sendMessage(player, "{crate}", crate.getCrateName());
            }

            case silent -> {

            }

            default -> {}
        }
    }

    /**
     * Sends common tasks, for ending a crate in the cycle.
     *
     * @param crate {@link Crate}
     * @param player {@link Player}
     * @param location {@link Location}
     * @param event {@link CrateStatusEvent}
     */
    private void janitor(final Crate crate, final Player player, final Location location, final CrateStatusEvent event) {
        crate.playSound(player, location, "stop-sound", "entity.player.levelup", Sound.Source.PLAYER);

        switch (crate.getCrateType()) {
            case quad_crate -> this.crateManager.endQuadCrate(player);

            case quick_crate -> this.crateManager.endQuickCrate(player, location, crate, false);

            default -> this.crateManager.endCrate(player);
        }

        // Always remove thy player from thy opening list!
        this.crateManager.removePlayerFromOpeningList(player);

        // Always close the inventory, after 40 ticks.
        new FoliaRunnable(player.getScheduler(), null) {
            @Override
            public void run() {
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
            }
        }.runDelayed(plugin, 100); // 5 seconds, to take screenshots.
    }
}