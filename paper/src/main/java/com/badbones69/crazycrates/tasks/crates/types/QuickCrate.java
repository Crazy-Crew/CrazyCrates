package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.ChestManager;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.managers.config.ConfigManager;
import com.badbones69.crazycrates.managers.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.HashMap;
import java.util.UUID;

public class QuickCrate extends CrateBuilder {

    public QuickCrate(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(crate, player, location);
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final EventType eventType) {
        // If the crate type is not fire cracker.
        if (!isFireCracker()) {
            // If the crate event failed.
            if (isCrateEventValid(type, checkHand, eventType)) {
                return;
            }
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String fileName = crate.getFileName();

        int keys = switch (type) {
            case virtual_key -> this.userManager.getVirtualKeys(uuid, fileName);
            case physical_key -> this.userManager.getPhysicalKeys(uuid, fileName);
            default -> 1;
        };

        if (crate.useRequiredKeys() && keys < crate.getRequiredKeys()) {
            final int finalKeys = keys;

            Messages.not_enough_keys.sendMessage(player, new HashMap<>() {{
                put("{required_amount}", String.valueOf(crate.getRequiredKeys()));
                put("{key_amount}", String.valueOf(crate.getRequiredKeys())); // deprecated, remove in next major version of minecraft.
                put("{amount}", String.valueOf(finalKeys));
                put("{crate}", crate.getCrateName());
                put("{key}", crate.getKeyName());
            }});

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        this.crateManager.addCrateInUse(player, getLocation());

        if (player.isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(player)) break;
                if (used >= crate.getMaxMassOpen()) break;

                Prize prize = crate.pickPrize(player);
                PrizeManager.givePrize(player, prize, crate);

                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(.5, 1, .5), null);

                used++;
            }

            final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, used, false);

            if (!keyCheck) {
                // Send the message about failing to take the key.
                MiscUtils.failedToTakeKey(player, fileName);

                // Remove from opening list.
                this.crateManager.removePlayerFromOpeningList(player);

                // Remove crates in use
                this.crateManager.removeCrateInUse(player);

                return;
            }

            this.crateManager.endQuickCrate(player, getLocation(), crate, true);

            return;
        }

        final boolean keyCheck = this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(player, fileName);

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            // Remove crates in use
            this.crateManager.removeCrateInUse(player);

            return;
        }

        Prize prize = crate.pickPrize(player, getLocation().clone().add(.5, 1.3, .5));
        PrizeManager.givePrize(player, prize, crate);

        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));

        final boolean showQuickCrateItem = ConfigManager.getConfig().getProperty(ConfigKeys.show_quickcrate_item);

        // Only related to the item above the crate.
        if (showQuickCrateItem) {
            final HologramManager manager = this.crateManager.getHolograms();

            if (manager != null && crate.getHologram().isEnabled()) {
                CrateLocation crateLocation = this.crateManager.getCrateLocation(getLocation());

                if (crateLocation != null) {
                    manager.removeHologram(crateLocation.getID());
                }
            }

            // Get the display item.
            ItemStack display = prize.getDisplayItem(player, crate);

            // Get the item meta.
            ItemMeta itemMeta = display.getItemMeta();

            // Set the key
            itemMeta.getPersistentDataContainer().set(Keys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, "1");

            // Set the item meta.
            display.setItemMeta(itemMeta);

            Item reward;

            try {
                reward = player.getWorld().dropItem(getLocation().clone().add(.5, 1, .5), display);
            } catch (IllegalArgumentException exception) {
                if (MiscUtils.isLogging()) {
                    this.plugin.getComponentLogger().warn("A prize could not be given due to an invalid display item for this prize.");
                    this.plugin.getComponentLogger().warn("Crate: {} Prize: {}", prize.getCrateName(), prize.getPrizeName(), exception);
                }

                return;
            }

            reward.setVelocity(new Vector(0, .2, 0));

            reward.customName(AdvUtil.parse(prize.getPrizeName()));

            reward.setCustomNameVisible(true);
            reward.setCanMobPickup(false);
            reward.setCanPlayerPickup(false);

            this.crateManager.addReward(player, reward);

            // Always open the chest.
            ChestManager.openChest(getLocation().getBlock(), true);

            // Always spawn fireworks if enabled.
            if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(0.5, 1, .5), null);

            // Always end the crate.
            addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
                @Override
                public void run() {
                    crateManager.endQuickCrate(player, getLocation(), crate, false);
                }
            }.runDelayed(this.plugin, 5 * 20));

            return;
        }

        // Always open the chest.
        ChestManager.openChest(getLocation().getBlock(), true);

        // Always spawn fireworks if enabled.
        if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(0.5, 1, .5), null);

        // Always end the crate.
        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            @Override
            public void run() {
                crateManager.endQuickCrate(player, getLocation(), crate, false);
            }
        }.runDelayed(this.plugin, 40));
    }
}