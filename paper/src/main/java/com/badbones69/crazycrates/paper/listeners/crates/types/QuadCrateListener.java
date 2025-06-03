package com.badbones69.crazycrates.paper.listeners.crates.types;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates.QuadCrateManager;
import com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.ChestManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuadCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final SessionManager sessionManager = new SessionManager();

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
            final Prize prize = crate.pickPrize(player);

            PrizeManager.givePrize(player, block.getLocation().clone().add(0.5, 1.3, 0.5), crate, prize);

            // Get the display item.
            final ItemStack display = prize.getDisplayItem(player, crate);

            // Set the persistent data.
            display.editPersistentDataContainer(container -> container.set(ItemKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, "1"));

            // Convert the item stack to item builder.
            final ItemStack itemStack = ItemUtils.convertItemStack(display).asItemStack();

            // Drop the item.
            final Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), itemStack);

            // Set data
            reward.setVelocity(new Vector(0, .2, 0));
            reward.customName(display.displayName());
            reward.setCustomNameVisible(true);
            reward.setCanMobPickup(false);
            reward.setCanPlayerPickup(false);

            // Add open crates
            session.getCratesOpened().put(block.getLocation(), true);

            // Add display rewards
            session.getDisplayedRewards().add(reward);

            // Check if all crates have spawned, then end if so.
            if (session.allCratesOpened()) {
                new FoliaScheduler(this.plugin, null, player) {
                    @Override
                    public void run() {
                        session.endCrate(false);

                        crate.playSound(player, block.getLocation(), "stop-sound", "block.anvil.land", Sound.Source.BLOCK);
                    }
                }.runDelayed(60);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (!this.sessionManager.inSession(player)) return; // if not in session, we shouldn't check anything.

        final Location oldLocation = event.getFrom();
        final Location newLocation = event.getTo();

        if (oldLocation.getBlockX() != newLocation.getBlockX() || oldLocation.getBlockZ() != newLocation.getBlockZ()) {
            player.teleportAsync(oldLocation);
            event.setCancelled(true);
        }

        for (final Entity en : player.getNearbyEntities(2, 2, 2)) { // Someone tries to enter the crate area
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
        final Player player = event.getPlayer();

        final QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null && !player.hasPermission("crazycrates.admin")) {
            event.setCancelled(true);

            final Map<String, String> placeholders = new HashMap<>();

            final Crate crate = session.getCrate();

            placeholders.put("{crate}", crate.getCrateName());
            placeholders.put("{player}", player.getName());

            Messages.no_commands_while_in_crate.sendMessage(player, placeholders);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        final QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null && event.getCause() == TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);

            final Map<String, String> placeholders = new HashMap<>();

            final Crate crate = session.getCrate();

            placeholders.put("{crate}", crate.getCrateName());
            placeholders.put("{player}", player.getName());

            Messages.no_teleporting.sendMessage(player, placeholders);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

       final QuadCrateManager session = this.sessionManager.getSession(player);

        if (session != null) session.endCrate(true);
    }
}