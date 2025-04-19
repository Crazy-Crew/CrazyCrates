package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CasinoCrate extends CrateBuilder {

    public CasinoCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    private int counter = 0;
    private int time = 1;
    private int open = 0;

    @Override
    public void run() {
        // If cancelled, we return.
        if (this.isCancelled) {
            return;
        }

        if (this.counter <= 50) { // When the crate is currently spinning.
            playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");

            cycle();
        }

        this.open++;

        if (this.open >= 5) {
            this.player.openInventory(this.inventory);

            this.open = 0;
        }

        this.counter++;

        if (this.counter > 51) {
            if (MiscUtils.slowSpin(120, 15).contains(this.time)) {
                playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");

                cycle();
            }

            this.time++;

            if (this.time >= 60) { // When the crate task is finished.
                playSound("stop-sound", Sound.Source.MASTER, "entity.player.levelup");

                this.crateManager.endCrate(this.player);

                PrizeManager.getPrize(this.crate, this.inventory, 11, this.player);
                PrizeManager.getPrize(this.crate, this.inventory, 13, this.player);
                PrizeManager.getPrize(this.crate, this.inventory, 15, this.player);

                this.crateManager.removePlayerFromOpeningList(this.player);

                new FoliaScheduler(null, this.player) {
                    @Override
                    public void run() { //todo() use inventory holders
                        if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                    }
                }.runDelayed(40);

                cancel();

                return;
            }
        }

        this.counter++;
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final ConfigurationSection section = this.crate.getFile().getConfigurationSection("Crate.random");

        if (section != null) {
            final boolean isRandom = section.getBoolean("toggle", false);

            if (!isRandom) {
                final @Nullable Tier tier_uno = this.crate.getTier(section.getString("types.row-3", ""));
                final @Nullable Tier tier_dos = this.crate.getTier(section.getString("types.row-2", ""));
                final @Nullable Tier tier_tres = this.crate.getTier(section.getString("types.row-1", ""));

                if (tier_uno == null || tier_dos == null || tier_tres == null) {
                    if (MiscUtils.isLogging()) {
                        this.logger.warn("One of your tiers in {} could not be found, or is empty. Search for row-1, row-2 or row-3", fileName);
                    }

                    this.crateManager.endCrate(this.player);

                    this.crateManager.removeCrateTask(this.player);

                    this.crateManager.removePlayerFromOpeningList(this.player);

                    this.player.closeInventory();

                    return;
                }
            }
        }

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        setDisplayItems(true);

        runAtFixedRate(1, 1);

        this.player.openInventory(this.inventory);
    }

    private void setDisplayItems(final boolean isStatic) {
        final ConfigurationSection section = this.crate.getFile().getConfigurationSection("Crate.random");

        final boolean isGlassBorderToggled = this.crate.isGlassBorderToggled();

        if (isStatic && isGlassBorderToggled) {
            for (int index = 0; index < 27; index++) {
                setItem(index, getRandomGlassPane());
            }
        }

        if (section != null) {
            final boolean isRandom = section.getBoolean("toggle", false);

            if (isRandom) {
                List<Tier> tiers = this.crate.getTiers();

                int size = tiers.size();

                final ThreadLocalRandom random = ThreadLocalRandom.current();

                setItem(2, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(11, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(20, getDisplayItem(tiers.get(random.nextInt(size))));

                setItem(4, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(13, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(22, getDisplayItem(tiers.get(random.nextInt(size))));

                setItem(6, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(15, getDisplayItem(tiers.get(random.nextInt(size))));
                setItem(24, getDisplayItem(tiers.get(random.nextInt(size))));

                return;
            }

            final @Nullable Tier tierUno = this.crate.getTier(section.getString("types.row-1", ""));

            if (tierUno != null) {
                setItem(2, getDisplayItem(tierUno));
                setItem(11, getDisplayItem(tierUno));
                setItem(20, getDisplayItem(tierUno));
            }

            final @Nullable Tier tierDos = this.crate.getTier(section.getString("types.row-2", ""));

            if (tierDos != null) {
                setItem(4, getDisplayItem(tierDos));
                setItem(13, getDisplayItem(tierDos));
                setItem(22, getDisplayItem(tierDos));
            }

            final @Nullable Tier tierTres = this.crate.getTier(section.getString("types.row-3", ""));

            if (tierTres != null) {
                setItem(6, getDisplayItem(tierTres));
                setItem(15, getDisplayItem(tierTres));
                setItem(24, getDisplayItem(tierTres));
            }
        }
    }

    private void cycle() {
        for (int index = 0; index < 27; index++) {
            final ItemStack itemStack = this.inventory.getItem(index);

            if (itemStack != null) {
                final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

                if (!container.has(ItemKeys.crate_prize.getNamespacedKey())) {
                    setItem(index, getRandomGlassPane());
                }
            }
        }

        setDisplayItems(false);
    }
}