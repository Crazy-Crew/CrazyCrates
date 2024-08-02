package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.ChestManager;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.UUID;
import java.util.logging.Level;

public class QuickCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    public QuickCrate(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(crate, player, location);
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // If the crate type is not fire cracker.
        if (!isFireCracker()) {
            // If the crate event failed.
            if (isCrateEventValid(type, checkHand)) {
                return;
            }
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String crateName = crate.getName();

        this.crateManager.addCrateInUse(player, getLocation());

        int keys = switch (type) {
            case virtual_key -> this.userManager.getVirtualKeys(uuid, crateName);
            case physical_key -> this.userManager.getPhysicalKeys(uuid, crateName);
            default -> 1;
        };

        if (player.isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(player)) break;
                if (used >= crate.getMaxMassOpen()) break;

                Prize prize = crate.pickPrize(player);
                PrizeManager.givePrize(player, prize, crate);

                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crateName, prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(.5, 1, .5), null);

                used++;
            }

            final boolean keyCheck = this.userManager.takeKeys(uuid, crateName, type, used, false);

            if (!keyCheck) {
                // Remove from opening list.
                this.crateManager.removePlayerFromOpeningList(player);

                return;
            }

            this.crateManager.endQuickCrate(player, getLocation(), crate, true);

            return;
        }

        final boolean keyCheck = this.userManager.takeKeys(uuid, crateName, type, 1, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(player, crateName);

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        Prize prize = crate.pickPrize(player, getLocation().clone().add(.5, 1.3, .5));
        PrizeManager.givePrize(player, prize, crate);

        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crateName, prize));

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
            ItemStack display = prize.getDisplayItem(player);

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
                reward = player.getWorld().dropItem(getLocation().clone().add(.5, 1, .5), display);
            } catch (IllegalArgumentException exception) {
                if (MiscUtils.isLogging()) {
                    this.plugin.getComponentLogger().warn("A prize could not be given due to an invalid display item for this prize.");
                    this.plugin.getComponentLogger().warn("Crate: {} Prize: {}", prize.getCrateName(), prize.getPrizeName(), exception);
                }

                return;
            }

            reward.setVelocity(new Vector(0, .2, 0));

            if (ConfigManager.getConfig().getProperty(ConfigKeys.minimessage_toggle)) {
                reward.customName(AdvUtil.parse(prize.getPrizeName()));
            } else {
                reward.setCustomName(ItemUtil.color(prize.getPrizeName()));
            }

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

    @Override
    public void run() {

    }
}