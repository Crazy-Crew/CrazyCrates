package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WheelCrate extends CrateBuilder {

    public WheelCrate(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    private final HashMap<UUID, HashMap<Integer, ItemStack>> rewards = new HashMap<>();

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

        for (int index = 0; index < getSize(); index ++) {
            setCustomGlassPane(index);
        }

        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int number : getBorder()) {
            Prize prize = getCrate().pickPrize(getPlayer());
            setItem(number, prize.getDisplayItem());
            items.put(number, prize.getDisplayItem());
        }

        this.rewards.put(getPlayer().getUniqueId(), items);

        getPlayer().openInventory(getInventory());

        addCrateTask(new BukkitRunnable() {
            final List<Integer> slots = getBorder();

            int uh = 0;
            int what = 17;

            int full = 0;

            final int timer = MiscUtils.randomNumber(42, 68);

            int slower = 0;
            int open = 0;
            int slow = 0;

            @Override
            public void run() {
                if (uh >= 18) uh = 0;

                if (what >= 18) what = 0;

                if (full < timer) lore();

                if (full >= timer) {
                    if (MiscUtils.slowSpin(46, 9).contains(slower)) lore();

                    if (full == timer + 47) {
                        playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");
                    }

                    if (full >= timer + 47) {
                        slow++;

                        if (slow >= 2) {
                            for (int slot = 0; slot < getSize(); slot++) {
                                if (!getBorder().contains(slot)) setCustomGlassPane(slot);
                            }

                            slow = 0;
                        }
                    }

                    if (full >= (timer + 55 + 47)) {
                        Prize prize = null;

                        if (plugin.getCrateManager().isInOpeningList(getPlayer())) {
                            prize = getCrate().getPrize(rewards.get(getPlayer().getUniqueId()).get(slots.get(what)));
                        }

                        plugin.getCrazyHandler().getPrizeManager().givePrize(getPlayer(), getCrate(), prize);

                        playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");

                        getPlayer().closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                        plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());
                        plugin.getCrateManager().endCrate(getPlayer());
                    }

                    slower++;
                }

                full++;
                open++;

                if (open > 5) {
                    getPlayer().openInventory(getInventory());
                    open = 0;
                }
            }

            private void lore() {
                HashMap<Integer, ItemStack> map = rewards.get(getPlayer().getUniqueId());

                int slot = slots.get(uh);

                ItemStack item = map.get(slot);

                ItemMeta itemMeta = item.getItemMeta();

                boolean hasLore = item.getItemMeta().hasLore();

                Material material = Material.LIME_STAINED_GLASS_PANE;

                String name = itemMeta.getDisplayName();

                if (hasLore) {
                    setItem(slot, material, name, itemMeta.getLore());
                } else {
                    setItem(slot, material, name);
                }

                int other = slots.get(what);

                setItem(other, map.get(other));
                playSound("cycle-sound", SoundCategory.MUSIC, "BLOCK_NOTE_BLOCK_XYLOPHONE");
                uh++;
                what++;
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }

    private List<Integer> getBorder() {
        return Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 25, 28, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public void run() {

    }
}