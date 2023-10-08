package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestManager;
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
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class QuickCrate implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    private final @NotNull ChestManager chestManager = this.crazyHandler.getChestManager();
    
    private final ArrayList<Entity> allRewards = new ArrayList<>();
    private final HashMap<UUID, Entity> rewards = new HashMap<>();
    private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    public void openCrate(Player player, Location location, Crate crate, KeyType keyType, HologramController hologramController) {
        int keys = switch (keyType) {
            case VIRTUAL_KEY -> this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName());
            case PHYSICAL_KEY -> this.userManager.getPhysicalKeys(player.getUniqueId(), crate.getName());
            default -> 1;
        }; // If the key is free it is set to one.
        
        if (player.isSneaking() && keys > 1) {
            int keysUsed = 0;
            
            // give the player the prizes
            for (;keys > 0; keys--) {
                if (this.methods.isInventoryFull(player)) break;
                if (keysUsed >= crate.getMaxMassOpen()) break;

                Prize prize = crate.pickPrize(player);
                this.crazyManager.givePrize(player, prize, crate);
                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player.getUniqueId(), crate, crate.getName(), prize));

                if (prize.useFireworks()) this.methods.firework(location.clone().add(.5, 1, .5));
                
                keysUsed++;
            }
            
            if (!this.userManager.takeKeys(keysUsed, player.getUniqueId(), crate.getName(), keyType, false)) {
                this.methods.failedToTakeKey(player.getName(), crate);
                //TODO() Unstatic this.
                //CrateControlListener.inUse.remove(player);
                this.crazyManager.removePlayerFromOpeningList(player);
                return;
            }

            endQuickCrate(player, location, crate, hologramController, true);
        } else {
            if (!this.userManager.takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
                this.methods.failedToTakeKey(player.getName(), crate);
                //TODO() Unstatic this.
                //CrateControlListener.inUse.remove(player);
                this.crazyManager.removePlayerFromOpeningList(player);
                return;
            }

            Prize prize = crate.pickPrize(player, location.clone().add(.5, 1.3, .5));
            this.crazyManager.givePrize(player, prize, crate);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player.getUniqueId(), crate, crate.getName(), prize));
            ItemStack displayItem = prize.getDisplayItem();
            NBTItem nbtItem = new NBTItem(displayItem);
            nbtItem.setBoolean("crazycrates-item", true);
            displayItem = nbtItem.getItem();
            Item reward;

            if (hologramController != null) hologramController.removeHologram(location.getBlock());

            try {
                reward = player.getWorld().dropItem(location.clone().add(.5, 1, .5), displayItem);
            } catch (IllegalArgumentException exception) {
                LegacyLogger.warn("A prize could not be given due to an invalid display item for this prize.", exception);
                LegacyLogger.warn("Crate: " + prize.getCrate() + " Prize: " + prize.getName());
                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(this.plugin, true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(displayItem.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(Integer.MAX_VALUE);
            this.rewards.put(player.getUniqueId(), reward);
            this.allRewards.add(reward);
            this.chestManager.openChest(location.getBlock(), true);

            if (prize.useFireworks()) this.methods.firework(location.clone().add(.5, 1, .5));

            this.tasks.put(player.getUniqueId(), new BukkitRunnable() {
                @Override
                public void run() {
                    endQuickCrate(player, location, crate, hologramController, false);
                }
            }.runTaskLater(this.plugin, 5 * 20));
        }
    }
    
    public void endQuickCrate(Player player, Location location, Crate crate, HologramController hologramController, boolean useQuickCrate) {
        if (this.tasks.containsKey(player.getUniqueId())) {
            this.tasks.get(player.getUniqueId()).cancel();
            this.tasks.remove(player.getUniqueId());
        }

        if (this.rewards.get(player.getUniqueId()) != null) {
            this.allRewards.remove(this.rewards.get(player.getUniqueId()));
            this.rewards.get(player.getUniqueId()).remove();
            this.rewards.remove(player.getUniqueId());
        }

        this.chestManager.closeChest(location.getBlock(), false);
        //TODO() Unstatic this
        //CrateControlListener.inUse.remove(player);
        this.crazyManager.removePlayerFromOpeningList(player);

        if (!useQuickCrate) {
            if (hologramController != null) hologramController.createHologram(location.getBlock(), crate);
        }
    }
    
    public void removeAllRewards() {
        this.allRewards.stream().filter(Objects :: nonNull).forEach(Entity :: remove);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onHopperPickUp(InventoryPickupItemEvent event) {
        if (this.crazyManager.isDisplayReward(event.getItem())) event.setCancelled(true);
    }
}