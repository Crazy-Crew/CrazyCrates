package com.badbones69.crazycrates.managers.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.other.MiscUtils;

public class RouletteCrate extends CrateBuilder {


    public RouletteCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            return;
        }

        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem());

        addCrateTask(new BukkitRunnable() {
            int full = 0;
            int time = 1;

            int even = 0;
            int open = 0;

            @Override
            public void run() {
                if (full <= 15) {
                    setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem());
                    setGlass();

                    getPlayer().playSound(getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                    even++;

                    if (even >= 4) {
                        even = 0;
                        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem());
                    }
                }

                open++;

                if (open >= 5) {
                    getPlayer().openInventory(getInventory());
                    open = 0;
                }

                full++;

                if (full > 16) {
                    if (MiscUtils.slowSpin().contains(time)) {
                        setGlass();
                        setItem(13, getCrate().pickPrize(getPlayer()).getDisplayItem());

                        getPlayer().playSound(getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                    }

                    time++;

                    if (time >= 23) {
                        getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);
                        plugin.getCrateManager().endCrate(getPlayer());

                        Prize prize = getCrate().getPrize(getInventory().getItem(13));

                        plugin.getCrazyHandler().getPrizeManager().checkPrize(prize, getPlayer(), getCrate());

                        plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                            }
                        }.runTaskLater(plugin, 40);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 2, 2));
    }

    private void setGlass() {
        for (int slot = 0; slot < getSize(); slot++) {
            if (slot != 13) {
                setCustomGlassPane(slot);
            }
        }
    }
}