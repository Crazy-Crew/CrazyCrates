package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.QuadCrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.SessionManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.ChestManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuadCrateListener implements Listener {

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final SessionManager sessionManager = new SessionManager();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.sessionManager.inSession(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onChestClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();

            if (block == null) return;

            if (!this.sessionManager.inSession(player)) return;

            final QuadCrateManager session = this.sessionManager.getSession(player);

            if (session == null) return;

            final List<Location> crateLocation = session.getCrateLocations();

            if (!crateLocation.contains(block.getLocation())) return;

            event.setCancelled(true);

            if (session.getCratesOpened().get(block.getLocation())) return;

            ChestManager.openChest(block, true);

            final Crate crate = session.getCrate();
            final Prize prize = crate.pickPrize(player, block.getLocation().add(.5, 1.3, .5));

            PrizeManager.givePrize(player, prize, crate);

            // Get the display item.
            final ItemStack display = prize.getDisplayItem(player);

            // Get the item meta.
            final ItemMeta itemMeta = display.getItemMeta();

            // Access the pdc and set "crazycrates-item"
            itemMeta.getPersistentDataContainer().set(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, "1");

            // Set the item meta.
            display.setItemMeta(itemMeta);

            // Convert the item stack to item builder.
            final ItemStack itemStack = ItemUtils.convertItemStack(display).getStack();

            // Drop the item.
            final Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), itemStack);

            // Set data
            reward.setVelocity(new Vector(0, .2, 0));
            reward.customName(itemMeta.displayName());
            reward.setCustomNameVisible(true);
            reward.setCanMobPickup(false);
            reward.setCanPlayerPickup(false);

            // Add open crates
            session.getCratesOpened().put(block.getLocation(), true);

            // Add display rewards
            session.getDisplayedRewards().add(reward);

            // Check if all crates have spawned then end if so.
            if (session.allCratesOpened()) {
                new FoliaRunnable(player.getScheduler(), null) {
                    @Override
                    public void run() {
                        session.endCrate(false);

                        crate.playSound(player, block.getLocation(), "stop-sound", "block.anvil.land", Sound.Source.BLOCK);
                    }
                }.runDelayed(this.plugin, 60);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.sessionManager.inSession(player)) { // Player tries to walk away from the crate area
            Location oldLocation = event.getFrom();
            Location newLocation = event.getTo();

            if (oldLocation.getBlockX() != newLocation.getBlockX() || oldLocation.getBlockZ() != newLocation.getBlockZ()) {
                player.teleportAsync(oldLocation);
                event.setCancelled(true);
            }
        }

        for (Entity en : player.getNearbyEntities(2, 2, 2)) { // Someone tries to enter the crate area
            if (en instanceof final Player p) {
                if (this.sessionManager.inSession(p)) {
                    Vector velocity = player.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().setY(1);

                    if (player.isInsideVehicle() && player.getVehicle() != null) {
                        player.getVehicle().setVelocity(velocity);
                    } else {
                        player.setVelocity(velocity);
                    }

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.sessionManager.inSession(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null && !player.hasPermission("crazycrates.admin")) {
            event.setCancelled(true);

            Map<String, String> placeholders = new HashMap<>();

            Crate crate = session.getCrate();

            placeholders.put("{crate}", crate.getCrateName());
            placeholders.put("{player}", player.getName());

            Messages.no_commands_while_in_crate.sendMessage(player, placeholders);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null && event.getCause() == TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);

            Map<String, String> placeholders = new HashMap<>();

            Crate crate = session.getCrate();

            placeholders.put("{crate}", crate.getCrateName());
            placeholders.put("{player}", player.getName());

            Messages.no_teleporting.sendMessage(player, placeholders);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null) session.endCrate(true);
    }
}