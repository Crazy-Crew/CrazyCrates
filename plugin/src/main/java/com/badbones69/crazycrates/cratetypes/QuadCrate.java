package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.api.objects.QuadCrateSession;
import de.tr7zw.changeme.nbtapi.NBTItem;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.nms.NMSSupport;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Controller class for the quadcrate crate type.
 *
 * Display items are controlled from the quick crate due to them using nbt tags.
 */
public class QuadCrate implements Listener {
    
    private CrazyManager cc = CrazyManager.getInstance();
    private NMSSupport nms = cc.getNMSSupport();
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (QuadCrateSession.inSession(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onChestClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (QuadCrateSession.inSession(player)) {
            QuadCrateSession session = QuadCrateSession.getSession(player);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = e.getClickedBlock();
                if (session.getChestLocations().contains(block.getLocation())) {
                    e.setCancelled(true);
                    if (!session.hasChestBeenOpened(block.getLocation())) {
                        nms.openChest(block, true);
                        Crate crate = session.getCrate();
                        Prize prize = crate.pickPrize(player, block.getLocation().add(.5, 1.3, .5));
                        cc.givePrize(player, prize);
                        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(prize.getDisplayItem());
                        itemBuilder.addLore(new Random().nextInt(Integer.MAX_VALUE) + "");//Makes sure items don't merge
                        ItemStack item = itemBuilder.build();
                        NBTItem nbtItem = new NBTItem(item);
                        nbtItem.setBoolean("crazycrates-item", true);
                        item = nbtItem.getItem();
                        Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), item);
                        reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(CrazyManager.getJavaPlugin(), true));
                        reward.setVelocity(new Vector(0, .2, 0));
                        reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
                        reward.setCustomNameVisible(true);
                        reward.setPickupDelay(Integer.MAX_VALUE);
                        session.getDisplayedRewards().add(reward);
                        session.setChestOpened(block.getLocation(), true);
                        if (session.allChestsOpened()) {//All 4 crates have been opened
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    session.endCrate();
                                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, 1, 1);
                                }
                            }.runTaskLater(CrazyManager.getJavaPlugin(), 60);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (QuadCrateSession.inSession(player)) {//Player tries to walk away from the crate area
            Location from = e.getFrom();
            Location to = e.getTo();
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                e.setCancelled(true);
                player.teleport(from);
                return;
            }
        }
        for (Entity en : player.getNearbyEntities(2, 2, 2)) {//Someone tries to enter the crate area
            if (en instanceof Player) {
                Player p = (Player) en;
                if (QuadCrateSession.inSession(p)) {
                    Vector v = player.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().setY(1);
                    if (player.isInsideVehicle()) {
                        player.getVehicle().setVelocity(v);
                    } else {
                        player.setVelocity(v);
                    }
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (QuadCrateSession.inSession(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCMD(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (QuadCrateSession.inSession(player) && !player.hasPermission("crazycrates.admin")) {
            e.setCancelled(true);
            player.sendMessage(Messages.NO_COMMANDS_WHILE_CRATE_OPENED.getMessage("%Player%", player.getName()));
        }
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (QuadCrateSession.inSession(player) && e.getCause() == TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            player.sendMessage(Messages.NO_TELEPORTING.getMessage("%Player%", player.getName()));
        }
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (QuadCrateSession.inSession(player)) {
            QuadCrateSession.getSession(player).endCrate();
        }
    }
}