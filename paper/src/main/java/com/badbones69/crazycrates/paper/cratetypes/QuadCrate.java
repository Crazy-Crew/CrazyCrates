package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.managers.QuadCrateManager;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.Random;

/**
 * Controller class for the quad-crate crate type.
 * Display items are controlled from the quick crate due to them using nbt tags.
 */
public class QuadCrate implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    private final @NotNull ChestManager chestManager = this.crazyHandler.getChestManager();
    private final @NotNull SessionManager sessionManager = new SessionManager();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.sessionManager.inSession(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onChestClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!this.sessionManager.inSession(player)) return;

        QuadCrateManager session = this.sessionManager.getSession(player);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block == null) return;

        if (!session.getCrateLocations().contains(block.getLocation())) return;

        event.setCancelled(true);

        if (session.getCratesOpened().get(block.getLocation())) return;

        this.chestManager.openChest(block, true);

        Crate crate = session.getCrate();
        Prize prize = crate.pickPrize(player, block.getLocation().add(.5, 1.3, .5));
        this.crazyManager.givePrize(player, prize, crate);

        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(prize.getDisplayItem());
        itemBuilder.addLore(new Random().nextInt(Integer.MAX_VALUE) + ""); // Makes sure items don't merge

        ItemStack item = itemBuilder.build();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("crazycrates-item", true);
        item = nbtItem.getItem();
        Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), item);

        reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(this.plugin, true));
        reward.setVelocity(new Vector(0, .2, 0));

        reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
        reward.setCustomNameVisible(true);
        reward.setPickupDelay(Integer.MAX_VALUE);

        session.getCratesOpened().put(block.getLocation(), true);

        session.getDisplayedRewards().add(reward);

        if (session.allCratesOpened()) { // All 4 crates have been opened
            new BukkitRunnable() {
                @Override
                public void run() {
                    session.endCrate();
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, SoundCategory.BLOCKS, 1f, 1f);
                }
            }.runTaskLater(this.plugin, 60);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.sessionManager.inSession(player)) { // Player tries to walk away from the crate area
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                event.setCancelled(true);
                player.teleport(from);
                return;
            }
        }

        for (Entity entity : player.getNearbyEntities(2, 2, 2)) { // Someone tries to enter the crate area
            if (entity instanceof Player entityPlayer) {
                if (this.sessionManager.inSession(entityPlayer)) {
                    Vector vector = player.getLocation().toVector().subtract(entityPlayer.getLocation().toVector()).normalize().setY(1);

                    if (player.isInsideVehicle() && player.getVehicle() != null) {
                        player.getVehicle().setVelocity(vector);
                    } else {
                        player.setVelocity(vector);
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
    public void onPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!this.sessionManager.inSession(player) && player.hasPermission("crazycrates.admin")) return;

        event.setCancelled(true);

        //TODO() Update message enum.
        //player.sendMessage(Messages.NO_COMMANDS_WHILE_CRATE_OPENED.getMessage("{player}", player.getName()));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!this.sessionManager.inSession(player) && event.getCause() == TeleportCause.ENDER_PEARL) return;

        event.setCancelled(true);

        //TODO() Update message enum.
        //player.sendMessage(Messages.NO_TELEPORTING.getMessage("{player}", player.getName()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.sessionManager.inSession(player)) this.sessionManager.getSession(player).endCrate();
    }
}