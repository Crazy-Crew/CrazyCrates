package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

import java.util.concurrent.ThreadLocalRandom;

public class CasinoCrate extends CrateBuilder {

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    public CasinoCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

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
            playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");

            cycle();
        }

        this.open++;

        if (this.open >= 5) {
            getPlayer().openInventory(getInventory());
            this.open = 0;
        }

        this.counter++;

        if (this.counter > 51) {
            if (MiscUtils.slowSpin(120, 15).contains(this.time)) {
                playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");

                cycle();
            }

            this.time++;

            if (this.time >= 60) { // When the crate task is finished.
                playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");

                this.crateManager.endCrate(getPlayer());

                PrizeManager.getPrize(getCrate(), getInventory(), 11, getPlayer());
                PrizeManager.getPrize(getCrate(), getInventory(), 13, getPlayer());
                PrizeManager.getPrize(getCrate(), getInventory(), 15, getPlayer());

                this.crateManager.removePlayerFromOpeningList(getPlayer());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) getPlayer().closeInventory();
                    }
                }.runTaskLater(this.plugin, 40);

                cancel();

                return;
            }
        }

        this.counter++;
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        boolean keyCheck = this.userManager.takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            return;
        }

        setDisplayItems(true);

        runTaskTimer(this.plugin, 1, 1);

        getPlayer().openInventory(getInventory());
    }

    private void setDisplayItems(boolean isStatic) {
        ConfigurationSection section = getCrate().getFile().getConfigurationSection("Crate.random");

        if (isStatic) {
            for (int index = 0; index < 27; index++) {
                setItem(index, getRandomGlassPane());
            }
        }

        if (section != null) {
            boolean isRandom = section.getBoolean("toggle", false);

            String row_uno = section.getString("types.row-1");
            String row_dos = section.getString("types.row-2");
            String row_tres = section.getString("types.row-3");

            if (isRandom) {
                ThreadLocalRandom random = ThreadLocalRandom.current();

                setItem(2, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(11, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(20, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));

                setItem(4, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(13, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(22, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));

                setItem(6, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(15, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));
                setItem(24, getDisplayItem(getCrate().getTiers().get(random.nextInt(getCrate().getTiers().size()))));

                return;
            }

            setItem(2, getDisplayItem(getCrate().getTier(row_uno)));
            setItem(11, getDisplayItem(getCrate().getTier(row_uno)));
            setItem(20, getDisplayItem(getCrate().getTier(row_uno)));

            setItem(4, getDisplayItem(getCrate().getTier(row_dos)));
            setItem(13, getDisplayItem(getCrate().getTier(row_dos)));
            setItem(22, getDisplayItem(getCrate().getTier(row_dos)));

            setItem(6, getDisplayItem(getCrate().getTier(row_tres)));
            setItem(15, getDisplayItem(getCrate().getTier(row_tres)));
            setItem(24, getDisplayItem(getCrate().getTier(row_tres)));
        }
    }

    private void cycle() {
        for (int index = 0; index < 27; index++) {
            ItemStack itemStack = getInventory().getItem(index);

            if (itemStack != null) {
                if (itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    PersistentDataContainer container = itemMeta.getPersistentDataContainer();

                    if (!container.has(PersistentKeys.crate_prize.getNamespacedKey())) {
                        setItem(index, getRandomGlassPane());
                    }
                }
            }
        }

        setDisplayItems(false);
    }
}