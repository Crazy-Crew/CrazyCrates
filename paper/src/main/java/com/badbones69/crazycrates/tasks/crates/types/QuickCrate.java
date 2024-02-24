package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.ChestManager;
import com.badbones69.crazycrates.api.PrizeManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.logging.Level;

public class QuickCrate extends CrateBuilder {

    public QuickCrate(Crate crate, Player player, Location location) {
        super(crate, player, location);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // If the crate type is not fire cracker.
        if (!isFireCracker()) {
            // If the crate event failed.
            if (isCrateEventValid(type, checkHand)) {
                return;
            }
        }

        this.plugin.getCrateManager().addCrateInUse(getPlayer(), getLocation());

        int keys = switch (type) {
            case virtual_key -> this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(getPlayer().getUniqueId(), getCrate().getName());
            case physical_key -> this.plugin.getCrazyHandler().getUserManager().getPhysicalKeys(getPlayer().getUniqueId(), getCrate().getName());
            default -> 1;
        };

        if (getPlayer().isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(getPlayer())) break;
                if (used >= getCrate().getMaxMassOpen()) break;

                Prize prize = getCrate().pickPrize(getPlayer());
                PrizeManager.givePrize(getPlayer(), prize, getCrate());

                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(.5, 1, .5), null);

                used++;
            }

            boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(used, getPlayer().getUniqueId(), getCrate().getName(), type, false);

            if (!keyCheck) {
                // Send the message about failing to take the key.
                MiscUtils.failedToTakeKey(getPlayer(), getCrate());

                // Remove from opening list.
                this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                return;
            }

            this.plugin.getCrateManager().endQuickCrate(getPlayer(), getLocation(), getCrate(), true);

            return;
        }

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            return;
        }

        Prize prize = getCrate().pickPrize(getPlayer(), getLocation().clone().add(.5, 1.3, .5));
        PrizeManager.givePrize(getPlayer(), prize, getCrate());

        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));

        boolean showQuickCrateItem = this.plugin.getConfigManager().getConfig().getProperty(ConfigKeys.show_quickcrate_item);

        // Only related to the item above the crate.
        if (showQuickCrateItem) {
            // Get the display item.
            ItemStack display = prize.getDisplayItem(getPlayer());

            // Get the item meta.
            ItemMeta itemMeta = display.getItemMeta();

            // Access the pdc and set "crazycrates-item"
            PersistentKeys key = PersistentKeys.crate_prize;

            //noinspection unchecked
            itemMeta.getPersistentDataContainer().set(key.getNamespacedKey(), key.getType(), "1");

            // Set the item meta.
            display.setItemMeta(itemMeta);

            Item reward;

            try {
                reward = getPlayer().getWorld().dropItem(getLocation().clone().add(.5, 1, .5), display);
            } catch (IllegalArgumentException exception) {
                this.plugin.getLogger().warning("A prize could not be given due to an invalid display item for this prize.");
                this.plugin.getLogger().log(Level.WARNING, "Crate: " + prize.getCrateName() + " Prize: " + prize.getPrizeName(), exception);

                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(this.plugin, true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(display.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(-1);

            this.plugin.getCrateManager().addReward(getPlayer(), reward);

            // Always open the chest.
            ChestManager.openChest(getLocation().getBlock(), true);

            // Always spawn fireworks if enabled.
            if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(0.5, 1, .5), null);

            // Always end the crate.
            addCrateTask(new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getCrateManager().endQuickCrate(getPlayer(), getLocation(), getCrate(), false);
                }
            }.runTaskLater(this.plugin, 5 * 20));

            return;
        }

        // Always open the chest.
        ChestManager.openChest(getLocation().getBlock(), true);

        // Always spawn fireworks if enabled.
        if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(0.5, 1, .5), null);

        // Always end the crate.
        addCrateTask(new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCrateManager().endQuickCrate(getPlayer(), getLocation(), getCrate(), false);
            }
        }.runTaskLater(this.plugin, 40));
    }
    @Override
    public void run() {

    }
}