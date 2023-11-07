package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import us.crazycrew.crazycrates.paper.api.support.holograms.HologramHandler;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import us.crazycrew.crazycrates.paper.listeners.CrateControlListener;
import us.crazycrew.crazycrates.paper.api.support.structures.blocks.ChestManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class QuickCrate implements Listener {

    private static final HashMap<Player, BukkitTask> tasks = new HashMap<>();
    public static final List<Entity> allRewards = new ArrayList<>();
    public static final HashMap<Player, Entity> rewards = new HashMap<>();

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final ChestManager chestManager = plugin.getCrazyHandler().getChestManager();

    public static void openCrate(final Player player, final Location loc, Crate crate, KeyType keyType, HologramHandler hologramHandler) {
        int keys = switch (keyType) {
            case virtual_key -> plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName());
            case physical_key -> plugin.getCrazyHandler().getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName());
            default -> 1;
        }; // If the key is free it is set to one.
        
        if (player.isSneaking() && keys > 1) {
            int keysUsed = 0;
            
            // give the player the prizes
            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(player)) break;
                if (keysUsed >= crate.getMaxMassOpen()) break;

                Prize prize = crate.pickPrize(player);
                plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
                plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(loc.clone().add(.5, 1, .5), null);
                
                keysUsed++;
            }
            
            if (!plugin.getCrazyHandler().getUserManager().takeKeys(keysUsed, player.getUniqueId(), crate.getName(), keyType, false)) {
                MiscUtils.failedToTakeKey(player, crate);
                CrateControlListener.inUse.remove(player);
                plugin.getCrateManager().removePlayerFromOpeningList(player);
                return;
            }

            endQuickCrate(player, loc, crate, hologramHandler, true);
        } else {
            if (!plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
                MiscUtils.failedToTakeKey(player, crate);
                CrateControlListener.inUse.remove(player);
                plugin.getCrateManager().removePlayerFromOpeningList(player);
                return;
            }

            Prize prize = crate.pickPrize(player, loc.clone().add(.5, 1.3, .5));
            plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
            ItemStack displayItem = prize.getDisplayItem();
            NBTItem nbtItem = new NBTItem(displayItem);
            nbtItem.setBoolean("crazycrates-item", true);
            displayItem = nbtItem.getItem();
            Item reward;

            if (hologramHandler != null) hologramHandler.removeHologram(loc.getBlock());

            try {
                reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
            } catch (IllegalArgumentException exception) {
                plugin.getServer().getLogger().warning("A prize could not be given due to an invalid display item for this prize. ");
                plugin.getServer().getLogger().log(Level.WARNING, "Crate: " + prize.getCrate() + " Prize: " + prize.getName(), exception);

                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(plugin, true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(displayItem.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(Integer.MAX_VALUE);
            rewards.put(player, reward);
            allRewards.add(reward);
            chestManager.openChest(loc.getBlock(), true);

            if (prize.useFireworks()) MiscUtils.spawnFirework(loc.clone().add(.5, 1, .5), null);

            tasks.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    endQuickCrate(player, loc, crate, hologramHandler, false);
                }
            }.runTaskLater(plugin, 5 * 20));
        }
    }
    
    public static void endQuickCrate(Player player, Location loc, Crate crate, HologramHandler hologramHandler, boolean useQuickCrate) {
        if (tasks.containsKey(player)) {
            tasks.get(player).cancel();
            tasks.remove(player);
        }

        if (rewards.get(player) != null) {
            allRewards.remove(rewards.get(player));
            rewards.get(player).remove();
            rewards.remove(player);
        }

        chestManager.closeChest(loc.getBlock(), false);
        CrateControlListener.inUse.remove(player);
        plugin.getCrateManager().removePlayerFromOpeningList(player);

        if (!useQuickCrate) {
            if (hologramHandler != null) hologramHandler.createHologram(loc.getBlock(), crate);
        }
    }
    
    public static void removeAllRewards() {
        allRewards.stream().filter(Objects :: nonNull).forEach(Entity :: remove);
    }
    
    @EventHandler
    public void onHopperPickUp(InventoryPickupItemEvent e) {
        if (plugin.getCrateManager().isDisplayReward(e.getItem())) e.setCancelled(true);
    }
}