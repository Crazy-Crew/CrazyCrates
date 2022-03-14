package me.badbones69.crazycrates.cratetypes;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.controllers.CrateControl;
import org.bukkit.Bukkit;
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
    private static CrazyCrates cc = CrazyCrates.getInstance();
    private static HashMap<Player, BukkitTask> tasks = new HashMap<>();
    
    public static void openCrate(final Player player, final Location loc, Crate crate, KeyType keyType) {
        int keys; // If the key is free it is set to one.
        switch (keyType) {
            case VIRTUAL_KEY:
                keys = cc.getVirtualKeys(player, crate);
                break;
            case PHYSICAL_KEY:
                keys = cc.getPhysicalKeys(player, crate);
                break;
            default:
                keys = 1;
                break;
        }

        if (player.isSneaking() && keys > 1) {
            int keysUsed = 0;

            // give the player the prizes
            for(; keys > 0 && keysUsed < 64; keys--) {
                if(Methods.isInventoryFull(player)) break;

                Prize prize = crate.pickPrize(player);
                cc.givePrize(player, prize);
                Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
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
            Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
            ItemStack displayItem = prize.getDisplayItem();
            NBTItem nbtItem = new NBTItem(displayItem);
            nbtItem.setBoolean("crazycrates-item", true);
            displayItem = nbtItem.getItem();
            Item reward;
            try {
                reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
            } catch (IllegalArgumentException e) {
                Bukkit.getServer().getLogger().warning("[CrazyCrates] An prize could not be given due to an invalid display item for this prize. ");
                Bukkit.getServer().getLogger().warning("[CrazyCrates] Crate: " + prize.getCrate() + " Prize: " + prize.getName());
                e.printStackTrace();
                return;
            }
            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(cc.getPlugin(), true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(displayItem.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(Integer.MAX_VALUE);
            rewards.put(player, reward);
            allRewards.add(reward);
            cc.getNMSSupport().openChest(loc.getBlock(), true);
            if (prize.useFireworks()) {
                Methods.fireWork(loc.clone().add(.5, 1, .5));
            }
            tasks.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    endQuickCrate(player, loc);
                }
            }.runTaskLater(cc.getPlugin(), 5 * 20));
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
        cc.getNMSSupport().openChest(loc.getBlock(), false);
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