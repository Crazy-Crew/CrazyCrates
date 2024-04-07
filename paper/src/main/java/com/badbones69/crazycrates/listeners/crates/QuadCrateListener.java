package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuadCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    private final SessionManager sessionManager = new SessionManager();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.sessionManager.inSession(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onChestClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!this.sessionManager.inSession(player)) return;

        QuadCrateManager session = sessionManager.getSession(player);

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (session.getCrateLocations().contains(block.getLocation())) {
                event.setCancelled(true);

                if (session.getCratesOpened().get(block.getLocation())) return;

                ChestManager.openChest(block, true);

                Crate crate = session.getCrate();
                Prize prize = crate.pickPrize(player, block.getLocation().add(.5, 1.3, .5));

                PrizeManager.givePrize(player, prize, crate);

                // Get the display item.
                ItemStack display = prize.getDisplayItem(player);

                // Get the item meta.
                ItemMeta itemMeta = display.getItemMeta();

                // Access the pdc and set "crazycrates-item"
                PersistentKeys key = PersistentKeys.crate_prize;

                //noinspection unchecked
                itemMeta.getPersistentDataContainer().set(key.getNamespacedKey(), key.getType(), "1");

                // Set the item meta.
                display.setItemMeta(itemMeta);

                // Convert the item stack to item builder.
                ItemBuilder itemBuilder = ItemBuilder.convertItemStack(display);

                // Makes sure items do not merge.
                itemBuilder.addLore(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) + "");

                // Builds the item.
                ItemStack item = itemBuilder.build();

                // Drop the item.
                Item reward = player.getWorld().dropItem(block.getLocation().add(.5, 1, .5), item);

                // Set data
                reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(plugin, true));
                reward.setVelocity(new Vector(0, .2, 0));
                reward.setCustomName(itemMeta.getDisplayName());
                reward.setCustomNameVisible(true);
                reward.setPickupDelay(Integer.MAX_VALUE);

                // Add open crates
                session.getCratesOpened().put(block.getLocation(), true);

                // Add display rewards
                session.getDisplayedRewards().add(reward);

                // Check if all crates have spawned then end if so.
                if (session.allCratesOpened()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            session.endCrate(false);
                            crate.playSound(player, block.getLocation(), "stop-sound", "BLOCK_ANVIL_LAND", SoundCategory.BLOCKS);
                        }
                    }.runTaskLater(this.plugin, 60);
                }
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
                player.teleport(oldLocation);
                event.setCancelled(true);
            }
        }

        for (Entity en : player.getNearbyEntities(2, 2, 2)) { // Someone tries to enter the crate area
            if (en instanceof Player p) {
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

        if (this.sessionManager.inSession(player) && !player.hasPermission("crazycrates.admin")) {
            event.setCancelled(true);

            Map<String, String> placeholders = new HashMap<>();

            Crate crate = this.sessionManager.getSession(player).getCrate();

            if (crate != null) placeholders.put("{crate}", crate.getName());

            placeholders.put("{player}", player.getName());

            player.sendMessage(Messages.no_commands_while_in_crate.getMessage(placeholders, player));
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (this.sessionManager.inSession(player) && event.getCause() == TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);

            Map<String, String> placeholders = new HashMap<>();

            Crate crate = this.sessionManager.getSession(player).getCrate();

            if (crate != null) placeholders.put("{crate}", crate.getName());

            placeholders.put("{player}", player.getName());

            player.sendMessage(Messages.no_teleporting.getMessage(placeholders, player));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.sessionManager.inSession(player)) {
            this.sessionManager.getSession(player).endCrate(true);
        }
    }
}