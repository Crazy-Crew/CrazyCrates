package com.badbones69.crazycrates.managers.crates.types;

import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.other.MiscUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

import java.util.*;

public class CsgoCrateTriple extends CrateBuilder {

    public CsgoCrateTriple(Crate crate, Player player, int size) {
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

        // Set the glass/display items to the inventory.
        populate();

        // Open the inventory.
        getPlayer().openInventory(getInventory());

        addCrateTask(new BukkitRunnable() {
            int time = 1;

            int full = 0;

            int open = 0;

            @Override
            public void run() {
                if (full <= 50) { // When Spinning
                    cycle();

                    sound(Sound.UI_BUTTON_CLICK);
                }

                open++;

                if (open >= 5) {
                    getPlayer().openInventory(getInventory());
                    open = 0;
                }

                full++;

                if (full > 51) {
                    if (calculateSpinDelays().contains(time)) { // When Slowing Down
                        cycle();

                        sound(Sound.UI_BUTTON_CLICK);
                    }

                    time++;

                    if (time == 60) { // When done
                        sound(Sound.ENTITY_PLAYER_LEVELUP);

                        cancel();
                    } else if (time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }

    private void populate() {
        for (int index = 0; index < 9; index++) {
            setCustomGlassPane(index);
            setCustomGlassPane(index+18);
            setCustomGlassPane(index+36);
        }
    }

    private List<Integer> calculateSpinDelays() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 120;
        int cut = 15;

        for (int i = 120; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }

        return slow;
    }

    private void cycle() {
        setItem(9, getCrate().pickPrize(getPlayer()).getDisplayItem());

        /*List<ItemStack> items = new ArrayList<>();

        for (int index = 0; index < 9; index++) {
            items.add(getInventory().getItem(index));
        }

        for (int index = 0; index < 8; index++) {
            setItem(index, items.get(index));
        }*/
    }

    private void sound(Sound sound) {
        getPlayer().playSound(getPlayer().getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
    }
}