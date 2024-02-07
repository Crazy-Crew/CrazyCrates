package com.badbones69.crazycrates.managers.crates.types.csgo;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.managers.PrizeManager;
import com.badbones69.crazycrates.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class TripleAnimation extends CrateBuilder {

    public TripleAnimation(Crate crate, Player player, int size) {
        super(crate, player, size);

        runTaskTimer(CrazyCrates.get(), 1, 1);
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

        if (counter <= 50) { // When the crate is currently spinning.
            playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");

            cycle();
        }

        open++;

        if (open >= 5) {
            getPlayer().openInventory(getInventory());
            open = 0;
        }

        counter++;

        if (counter > 51) {
            if (MiscUtils.slowSpin(120, 15).contains(time)) {
                playSound("cycle-sound", SoundCategory.PLAYERS, "BLOCK_NOTE_BLOCK_XYLOPHONE");

                cycle();
            }

            time++;

            if (time >= 60) { // When the crate task is finished.
                playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");

                plugin.getCrateManager().endCrate(getPlayer());

                PrizeManager manager = plugin.getCrazyHandler().getPrizeManager();

                manager.checkPrize(getPrize(13), getPlayer(), getCrate());
                manager.checkPrize(getPrize(31), getPlayer(), getCrate());
                manager.checkPrize(getPrize(49), getPlayer(), getCrate());

                plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                cancel();

                return;
            }
        }

        counter++;
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

        populate();

        getPlayer().openInventory(getInventory());
    }

    private void populate() {
        setItem(13, getDisplayItem());
        setItem(31, getDisplayItem());
        setItem(49, getDisplayItem());

        ItemStack glass = new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();

        setItem(4, glass);
        setItem(22, glass);
        setItem(40, glass);

        // Exclude 13,31 and 49
        for (int index = 9; index < 18; index++) {
            if (index != 13) setItem(index, getDisplayItem());
        }

        for (int index = 27; index < 36; index++) {
            if (index != 31) setItem(index, getDisplayItem());
        }

        for (int index = 44; index < 54; index++) {
            if (index != 49) setItem(index, getDisplayItem());
        }
    }

    private void cycle() {
        setItem(13, getDisplayItem());
        setItem(31, getDisplayItem());
        setItem(49, getDisplayItem());

        // Exclude 40, 22 and 4
        for (int index = 8; index >= 0; index--) {
            if (index != 4) setItem(index, getRandomGlassPane());
        }

        for (int index = 18; index < 27; index++) {
            if (index != 22) setItem(index, getRandomGlassPane());
        }

        for (int index = 44; index >= 36; index--) {
            if (index != 40) setItem(index, getRandomGlassPane());
        }

        for (int index = 9; index < 18; index++) {
            setItem(index, getDisplayItem());
        }

        for (int index = 27; index < 36; index++) {
            setItem(index, getDisplayItem());
        }

        for (int index = 45; index < 54; index++) {
            setItem(index, getDisplayItem());
        }
    }
}