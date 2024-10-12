package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.api.PrizeManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

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
            playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");

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
                playSound("cycle-sound", Sound.Source.PLAYER, "block.note_block.xylophone");

                cycle();
            }

            this.time++;

            if (this.time >= 60) { // When the crate task is finished.
                playSound("stop-sound", Sound.Source.PLAYER, "entity.player.levelup");

                this.crateManager.endCrate(this.player);

                PrizeManager.getPrize(this.crate, this.inventory, 11, this.player);
                PrizeManager.getPrize(this.crate, this.inventory, 13, this.player);
                PrizeManager.getPrize(this.crate, this.inventory, 15, this.player);

                this.crateManager.removePlayerFromOpeningList(this.player);

                new FoliaRunnable(this.player.getScheduler(), null) {
                    @Override
                    public void run() { //todo() use inventory holders
                        if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                    }
                }.runDelayed(this.plugin, 40);

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
                final String row_uno = section.getString("types.row-1", "");
                final String row_dos = section.getString("types.row-2", "");
                final String row_tres = section.getString("types.row-3", "");

                if (row_uno.isEmpty() || row_dos.isEmpty() || row_tres.isEmpty()) {
                    if (MiscUtils.isLogging()) {
                        final ComponentLogger logger = this.plugin.getComponentLogger();

                        logger.warn("One of your tiers in the config is empty.");
                        logger.warn("Tier 1: {}", row_uno);
                        logger.warn("Tier 2: {}", row_dos);
                        logger.warn("Tier 3: {}", row_tres);
                    }

                    return;
                }

                final Tier tier_uno = this.crate.getTier(row_uno);
                final Tier tier_dos = this.crate.getTier(row_dos);
                final Tier tier_tres = this.crate.getTier(row_tres);

                if (tier_uno == null || tier_dos == null || tier_tres == null) {
                    final Logger logger = this.plugin.getLogger();

                    if (MiscUtils.isLogging()) {
                        List.of(
                                "One of your rows has a tier that doesn't exist supplied in " + fileName,
                                "You can find this in your crate config, search for row-1, row-2, and row-3"
                        ).forEach(logger::warning);
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

        runAtFixedRate(this.plugin, 1, 1);

        this.player.openInventory(this.inventory);
    }

    private void setDisplayItems(final boolean isStatic) {
        final ConfigurationSection section = this.crate.getFile().getConfigurationSection("Crate.random");

        if (isStatic) {
            for (int index = 0; index < 27; index++) {
                setItem(index, getRandomGlassPane());
            }
        }

        if (section != null) {
            final boolean isRandom = section.getBoolean("toggle", false);

            if (isRandom) {
                List<Tier> tiers = this.crate.getTiers();

                int size = tiers.size();

                ThreadLocalRandom random = ThreadLocalRandom.current();

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

            final String row_uno = section.getString("types.row-1", null);
            final String row_dos = section.getString("types.row-2", null);
            final String row_tres = section.getString("types.row-3", null);

            final Tier tierUno = this.crate.getTier(row_uno);

            if (tierUno != null) {
                setItem(2, getDisplayItem(tierUno));
                setItem(11, getDisplayItem(tierUno));
                setItem(20, getDisplayItem(tierUno));
            }

            final Tier tierDos = this.crate.getTier(row_dos);

            if (tierDos != null) {
                setItem(4, getDisplayItem(tierDos));
                setItem(13, getDisplayItem(tierDos));
                setItem(22, getDisplayItem(tierDos));
            }

            final Tier tierTres = this.crate.getTier(row_tres);

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

                if (!container.has(Keys.crate_prize.getNamespacedKey())) {
                    setItem(index, getRandomGlassPane());
                }
            }
        }

        setDisplayItems(false);
    }
}