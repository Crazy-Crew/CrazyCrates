package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.ChestManager;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.HashMap;
import java.util.UUID;

public class QuickCrate extends CrateBuilder {

    public QuickCrate(@NotNull final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        super(crate, player, location);
    }

    private final PluginManager server = this.plugin.getServer().getPluginManager();

    private final Player player = getPlayer();
    private final Location location = getLocation();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        int keys = switch (type) {
            case virtual_key -> this.userManager.getVirtualKeys(this.uuid, fileName);
            case physical_key -> this.userManager.getPhysicalKeys(this.uuid, fileName);
            default -> 1;
        };

        if (this.crate.useRequiredKeys() && keys < this.crate.getRequiredKeys()) {
            final int finalKeys = keys;

            Messages.not_enough_keys.sendMessage(this.player, new HashMap<>() {{
                put("{required_amount}", String.valueOf(crate.getRequiredKeys()));
                put("{key_amount}", String.valueOf(crate.getRequiredKeys())); // deprecated, remove in next major version of minecraft.
                put("{amount}", String.valueOf(finalKeys));
                put("{crate}", crate.getCrateName());
                put("{key}", crate.getKeyName());
            }});

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        this.crateManager.addCrateInUse(this.player, this.location);

        if (this.player.isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(this.player)) break;
                if (used >= this.crate.getMaxMassOpen()) break;

                Prize prize = this.crate.pickPrize(this.player);
                PrizeManager.givePrize(this.player, prize, this.crate);

                this.server.callEvent(new PlayerPrizeEvent(this.player, this.crate, prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(this.location.clone().add(.5, 1, .5), null);

                used++;
            }

            final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, used, false);

            if (!keyCheck) {
                // Send the message about failing to take the key.
                MiscUtils.failedToTakeKey(this.player, fileName);

                // Remove from opening list.
                this.crateManager.removePlayerFromOpeningList(this.player);

                // Remove crates in use
                this.crateManager.removeCrateInUse(this.player);

                return;
            }

            this.crateManager.endQuickCrate(this.player, this.location, this.crate, true);

            return;
        }

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(this.player, fileName);

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            // Remove crates in use
            this.crateManager.removeCrateInUse(this.player);

            return;
        }

        Prize prize = this.crate.pickPrize(this.player, this.location.clone().add(.5, 1.3, .5));

        if (this.crate.isCyclePrize() && !PrizeManager.isCapped(this.crate, this.player)) { // re-open this menu
            new CrateSpinMenu(this.player, new GuiSettings(this.crate, prize, Files.respin_gui.getConfiguration())).open();

            this.crateManager.removePlayerFromOpeningList(this.player);
            this.crateManager.removeCrateInUse(this.player);

            return;
        } else {
            this.userManager.removeRespinPrize(this.uuid, fileName);

            // remove from the cache
            this.userManager.removeRespinCrate(this.uuid, fileName, 0, false);
        }

        PrizeManager.givePrize(this.player, this.crate, prize);

        this.server.callEvent(new PlayerPrizeEvent(this.player, this.crate, prize));

        final boolean showQuickCrateItem = ConfigManager.getConfig().getProperty(ConfigKeys.show_quickcrate_item);

        // Only related to the item above the crate.
        if (showQuickCrateItem) {
            final HologramManager manager = this.crateManager.getHolograms();

            if (manager != null && this.crate.getHologram().isEnabled()) {
                CrateLocation crateLocation = this.crateManager.getCrateLocation(this.location);

                if (crateLocation != null) {
                    manager.removeHologram(crateLocation.getID());
                }
            }

            // Get the display item.
            ItemStack display = prize.getDisplayItem(this.player, this.crate); //todo() use display entities

            // Get the item meta.
            display.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(Keys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, "1"));

            Item reward;

            try {
                reward = this.player.getWorld().dropItem(this.location.clone().add(.5, 1, .5), display);
            } catch (IllegalArgumentException exception) {
                if (MiscUtils.isLogging()) {
                    final ComponentLogger logger = this.plugin.getComponentLogger();

                    logger.warn("A prize could not be given due to an invalid display item for this prize.");
                    logger.warn("Crate: {} Prize: {}", prize.getCrateName(), prize.getPrizeName(), exception);
                }

                return;
            }

            reward.setVelocity(new Vector(0, .2, 0));

            reward.customName(AdvUtil.parse(prize.getPrizeName()));

            reward.setCustomNameVisible(true);
            reward.setCanMobPickup(false);
            reward.setCanPlayerPickup(false);

            this.crateManager.addReward(this.player, reward);

            // Always open the chest.
            ChestManager.openChest(this.location.getBlock(), true);

            // Always spawn fireworks if enabled.
            if (prize.useFireworks()) MiscUtils.spawnFirework(this.location.clone().add(0.5, 1, .5), null);

            // Always end the crate.
            addCrateTask(new FoliaRunnable(this.player.getScheduler(), null) {
                @Override
                public void run() {
                    crateManager.endQuickCrate(player, location, crate, false);
                }
            }.runDelayed(this.plugin, 5 * 20));

            return;
        }

        // Always open the chest.
        ChestManager.openChest(this.location.getBlock(), true);

        // Always spawn fireworks if enabled.
        if (prize.useFireworks()) MiscUtils.spawnFirework(location.clone().add(0.5, 1, .5), null);

        // Always end the crate.
        addCrateTask(new FoliaRunnable(this.player.getScheduler(), null) {
            @Override
            public void run() {
                crateManager.endQuickCrate(player, location, crate, false);
            }
        }.runDelayed(this.plugin, 40));
    }
}