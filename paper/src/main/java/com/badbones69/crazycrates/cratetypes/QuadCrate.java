package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Prize;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import java.util.Random;

/**
 * Controller class for the quad-crate crate type.
 * Display items are controlled from the quick crate due to them using nbt tags.
 */
@Singleton
public class QuadCrate implements Listener {


    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;

    // Utilities
    @Inject private ChestStateHandler chestStateHandler;
    @Inject private ScheduleUtils scheduleUtils;

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (SessionManager.inSession(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChestClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (SessionManager.inSession(player)) {
            QuadCrateManager session = SessionManager.getSession(player);

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = e.getClickedBlock();

                assert block != null;
                assert session != null;
                if (session.getCrateLocations().contains(block.getLocation())) {
                    e.setCancelled(true);

                    if (!session.getCratesOpened().get(block.getLocation())) {

                        chestStateHandler.openChest(block, true);

                        Crate crate = session.getCrate();
                        Prize prize = crate.pickPrize(player, block.getLocation().add(.5, 1.3, .5));
                        crazyManager.givePrize(player, prize);

                        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(prize.getDisplayItem());
                        itemBuilder.addLore(new Random().nextInt(Integer.MAX_VALUE) + ""); // Makes sure items don't merge

                        ItemStack item = itemBuilder.build();

                        NBTItem nbtItem = new NBTItem(item);
                        nbtItem.setBoolean("crazycrates-item", true);
                        item = nbtItem.getItem();

                        Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), item);

                        reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(plugin, true));
                        reward.setVelocity(new Vector(0, .2, 0));

                        reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
                        reward.setCustomNameVisible(true);
                        reward.setPickupDelay(Integer.MAX_VALUE);

                        session.getCratesOpened().put(block.getLocation(), true);

                        session.getDisplayedRewards().add(reward);

                        if (session.allCratesOpened()) { // All 4 crates have been opened
                            scheduleUtils.later(60L, () -> {
                                session.endCrate();
                                player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, 1, 1);
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (SessionManager.inSession(player)) { // Player tries to walk away from the crate area
            Location from = e.getFrom();
            Location to = e.getTo();

            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                e.setCancelled(true);
                player.teleport(from);
                return;
            }
        }

        for (Entity entity : player.getNearbyEntities(2, 2, 2)) { // Someone tries to enter the crate area
            if (entity instanceof Player entityPlayer) {
                if (SessionManager.inSession(entityPlayer)) {
                    Vector v = player.getLocation().toVector().subtract(entityPlayer.getLocation().toVector()).normalize().setY(1);

                    if (player.isInsideVehicle() && player.getVehicle() != null) {
                        player.getVehicle().setVelocity(v);
                    } else {
                        player.setVelocity(v);
                    }

                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (SessionManager.inSession(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPreCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (SessionManager.inSession(player) && !player.hasPermission("crazycrates.admin")) {
            e.setCancelled(true);
            //player.sendMessage(Messages.NO_COMMANDS_WHILE_CRATE_OPENED.getMessage("%Player%", player.getName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (SessionManager.inSession(player) && e.getCause() == TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            //player.sendMessage(Messages.NO_TELEPORTING.getMessage("%Player%", player.getName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        QuadCrateManager session = SessionManager.getSession(player);

        if (SessionManager.inSession(player)) {
            if (session != null) session.endCrate();
        }
    }
}