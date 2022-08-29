package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.player.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.modules.config.files.Config;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.utilities.ScheduleUtils;
import com.badbones69.crazycrates.utilities.handlers.tasks.CrateTaskHandler;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Singleton
public class QuickCrate implements Listener {
    
    public static ArrayList<Entity> allRewards = new ArrayList<>();
    public static HashMap<Player, Entity> rewards = new HashMap<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;
    @Inject private CrazyLogger crazyLogger;

    // Utilities
    @Inject private Methods methods;
    @Inject private ScheduleUtils scheduleUtils;

    @Inject private ChestStateHandler chestStateHandler;

    // Listeners
    @Inject private CrateControlListener crateControlListener;

    // Task Handler
    @Inject private CrateTaskHandler crateTaskHandler;

    public void openCrate(final Player player, final Location loc, Crate crate, KeyType keyType) {
        int keys = switch (keyType) {
            case VIRTUAL_KEY -> crazyManager.getVirtualKeys(player, crate);
            case PHYSICAL_KEY -> crazyManager.getPhysicalKeys(player, crate);
            default -> 1;
        }; // If the key is free it is set to one.
        
        if (player.isSneaking() && keys > 1) {
            int keysUsed = 0;
            
            // give the player the prizes
            for (; keys > 0; keys--) {
                if (!player.getInventory().isEmpty()) break;
                
                Prize prize = crate.pickPrize(player);
                crazyManager.givePrize(player, prize);
                plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

                if (prize.useFireworks()) methods.firework(loc.clone().add(.5, 1, .5));
                
                keysUsed++;
            }
            
            if (!crazyManager.takeKeys(keysUsed, player, crate, keyType, false)) {
                methods.failedToTakeKey(player, crate);
                crateControlListener.removePlayer(player);
                crazyManager.removePlayerFromOpeningList(player);
                return;
            }

            endQuickCrate(player, loc);
        } else {

            if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
                methods.failedToTakeKey(player, crate);
                crateControlListener.removePlayer(player);
                crazyManager.removePlayerFromOpeningList(player);
                return;
            }

            Prize prize = crate.pickPrize(player, loc.clone().add(.5, 1.3, .5));
            crazyManager.givePrize(player, prize);
            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
            ItemStack displayItem = prize.getDisplayItem();

            NBTItem nbtItem = new NBTItem(displayItem);
            nbtItem.setBoolean("crazycrates-item", true);
            displayItem = nbtItem.getItem();

            Item reward;

            try {
                reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), displayItem);
            } catch (IllegalArgumentException e) {
                if (Config.TOGGLE_VERBOSE) {
                    crazyLogger.debug("<red>A prize could not be given due to an invalid display item for this prize.</red>");
                    crazyLogger.debug("<red>Crate:</red> <gold>" + prize.getCrate() + "</gold><red> Prize:</red> <gold>" + prize.getName() + ".</gold>");

                    e.printStackTrace();
                }

                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(plugin, true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(displayItem.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(Integer.MAX_VALUE);
            rewards.put(player, reward);
            allRewards.add(reward);

            chestStateHandler.openChest(loc.getBlock(), true);

            if (prize.useFireworks()) methods.firework(loc.clone().add(.5, 1, .5));

            CrateTaskHandler crateTaskHandler = new CrateTaskHandler();

            crateTaskHandler.addTask(player, scheduleUtils.later(5 * 20L, () -> {
                crateTaskHandler.endCrate();

                endQuickCrate(player, loc);
            }));
        }
    }
    
    public void endQuickCrate(Player player, Location loc) {
        if (rewards.get(player) != null) {
            allRewards.remove(rewards.get(player));
            rewards.get(player).remove();
            rewards.remove(player);
        }

        chestStateHandler.openChest(loc.getBlock(), false);
        crateControlListener.removePlayer(player);
        crazyManager.removePlayerFromOpeningList(player);
    }
    
    public void removeAllRewards() {
        allRewards.stream().filter(Objects :: nonNull).forEach(Entity :: remove);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHopperPickUp(InventoryPickupItemEvent e) {
        if (crazyManager.isDisplayReward(e.getItem())) e.setCancelled(true);
    }
}