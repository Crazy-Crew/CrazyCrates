package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.controllers.CrateControl;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class QuickCrate implements Listener {
    
    public static ArrayList<Entity> allRewards = new ArrayList<>();
    public static HashMap<Player, Entity> rewards = new HashMap<>();
    private static CrazyManager cc = CrazyManager.getInstance();
    private static HashMap<Player, BukkitTask> tasks = new HashMap<>();
    
    public static void openCrate(final Player player, final Location loc, Crate crate, KeyType keyType) {
        int keys = switch (keyType) {
            case VIRTUAL_KEY -> cc.getVirtualKeys(player, crate);
            case PHYSICAL_KEY -> cc.getPhysicalKeys(player, crate);
            default -> 1;
        }; // If the key is free it is set to one.
        
        if (player.isSneaking() && keys > 1) {
            int keysUsed = 0;
            
            // give the player the prizes
            for (; keys > 0; keys--) {
                if (Methods.isInventoryFull(player)) break;
                
                Prize prize = crate.pickPrize(player);
                cc.givePrize(player, prize);
                CrazyManager.getJavaPlugin().getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

                if (prize.useFireworks()) {
                    Methods.fireWork(loc.clone().add(.5, 1, .5));
                }
                
                keysUsed++;
            }
            
            if (!cc.takeKeys(keysUsed, player, crate, keyType, false)) {
                Methods.failedToTakeKey(player, crate);
                CrateControl.inUse.remove(player);
                cc.removePlayerFromOpeningList(player);
                return;
            }

            endQuickCrate(player, loc);
        } else {

            if (!cc.takeKeys(1, player, crate, keyType, true)) {
                Methods.failedToTakeKey(player, crate);
                CrateControl.inUse.remove(player);
                cc.removePlayerFromOpeningList(player);
                return;
            }

            Prize prize = crate.pickPrize(player, loc.clone().add(.5, 1.3, .5));
            cc.givePrize(player, prize);
            CrazyManager.getJavaPlugin().getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
            ItemStack displayItem = prize.getDisplayItem();
            NBTItem nbtItem = new NBTItem(displayItem);
            nbtItem.setBoolean("crazycrates-item", true);
            displayItem = nbtItem.getItem();
            Item reward;

            try {
                reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
            } catch (IllegalArgumentException e) {
                CrazyManager.getJavaPlugin().getServer().getLogger().warning("[CrazyCrates] An prize could not be given due to an invalid display item for this prize. ");
                CrazyManager.getJavaPlugin().getServer().getLogger().warning("[CrazyCrates] Crate: " + prize.getCrate() + " Prize: " + prize.getName());
                e.printStackTrace();
                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(CrazyManager.getJavaPlugin(), true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(displayItem.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(Integer.MAX_VALUE);
            rewards.put(player, reward);
            allRewards.add(reward);
            new ChestStateHandler().openChest(loc.getBlock(), true);

            if (prize.useFireworks()) {
                Methods.fireWork(loc.clone().add(.5, 1, .5));
            }

            tasks.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    endQuickCrate(player, loc);
                }
            }.runTaskLater(CrazyManager.getJavaPlugin(), 5 * 20));
        }
    }
    
    public static void endQuickCrate(Player player, Location loc) {
        if (tasks.containsKey(player)) {
            tasks.get(player).cancel();
            tasks.remove(player);
        }

        if (rewards.get(player) != null) {
            allRewards.remove(rewards.get(player));
            rewards.get(player).remove();
            rewards.remove(player);
        }

        new ChestStateHandler().openChest(loc.getBlock(), false);
        CrateControl.inUse.remove(player);
        cc.removePlayerFromOpeningList(player);
    }
    
    public static void removeAllRewards() {
        allRewards.stream().filter(Objects :: nonNull).forEach(Entity :: remove);
    }
    
    @EventHandler
    public void onHopperPickUp(InventoryPickupItemEvent e) {
        if (cc.isDisplayReward(e.getItem())) {
            e.setCancelled(true);
        }
    }
}