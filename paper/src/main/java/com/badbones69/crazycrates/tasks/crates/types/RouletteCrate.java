package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import fr.euphyllia.energie.model.SchedulerType;
import fr.euphyllia.energie.utils.SchedulerTaskRunnable;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;

public class RouletteCrate extends CrateBuilder {

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    public RouletteCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
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

        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));

        addCrateTask(new SchedulerTaskRunnable() {
            int full = 0;
            int time = 1;

            int even = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 15) {
                    setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
                    setGlass();

                    playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");
                    this.even++;

                    if (this.even >= 4) {
                        this.even = 0;
                        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));
                    }
                }

                this.open++;

                if (this.open >= 5) {
                    getPlayer().openInventory(getInventory());
                    this.open = 0;
                }

                this.full++;

                if (this.full > 16) {
                    if (MiscUtils.slowSpin(46, 9).contains(this.time)) {
                        setGlass();
                        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem(getPlayer()));

                        playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");
                    }

                    this.time++;

                    if (this.time >= 23) {
                        playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");
                        crateManager.endCrate(getPlayer());

                        ItemStack item = getInventory().getItem(13);

                        if (item != null) {
                            Prize prize = getCrate().getPrize(item);
                            PrizeManager.givePrize(getPlayer(), getCrate(), prize);
                        }

                        crateManager.removePlayerFromOpeningList(getPlayer());

                        new SchedulerTaskRunnable() {
                            @Override
                            public void run() {
                                if (getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                            }
                        }.runDelayed(plugin, SchedulerType.SYNC, getPlayer(), null,40);
                    }
                }
            }
        }.runAtFixedRate(this.plugin, SchedulerType.SYNC, getPlayer(), null, 2, 2));
    }

    private void setGlass() {
        for (int slot = 0; slot < getSize(); slot++) {
            if (slot != 13) {
                setCustomGlassPane(slot);
            }
        }
    }

    @Override
    public void run() {

    }
}