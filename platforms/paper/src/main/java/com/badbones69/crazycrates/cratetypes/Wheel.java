package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.crateoptions.WheelOptions;
import com.badbones69.crazycrates.api.managers.CrateManager;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.enums.types.KeyType;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wheel implements CrateManager, Listener {

    public static Map<Player, HashMap<Integer, ItemStack>> rewards = new HashMap<>();

    public void openCrate(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inv = plugin.getServer().createInventory(null, 54, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        }

        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int i : getBorder()) {
            Prize prize = crate.pickPrize(player);
            inv.setItem(i, prize.getDisplayItem());
            items.put(i, prize.getDisplayItem());
        }

        rewards.put(player, items);
        player.openInventory(inv);
        ItemBuilder selectorItem = ((WheelOptions) crate.getOptions()).getSelectorItem();

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            final List<Integer> slots = getBorder();
            int selectedSlot = 0;
            int resetSlot = 17;
            int full = 0;
            final int timer = Methods.randomNumber(42, 68);
            int slower = 0;
            int open = 0;
            int slow = 0;

            @Override
            public void run() {

                if (selectedSlot >= 18) selectedSlot = 0;

                if (resetSlot >= 18) resetSlot = 0;

                if (full < timer) checkLore();

                if (full >= timer) {
                    if (Methods.slowSpin().contains(slower)) checkLore();

                    if (full == timer + 47) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    if (full >= timer + 47) {
                        slow++;

                        if (slow >= 2) {
                            ItemStack item = Methods.getRandomPaneColor().setName(" ").build();

                            for (int slot = 0; slot < 54; slot++) {
                                if (!getBorder().contains(slot)) inv.setItem(slot, item);
                            }

                            slow = 0;
                        }
                    }

                    if (full >= (timer + 55 + 47)) {
                        Prize prize = null;

                        if (crazyManager.isInOpeningList(player)) prize = crate.getPrize(rewards.get(player).get(getResetSlot()));

                        Methods.checkPrize(prize, crazyManager, plugin, player, crate);

                        //player.closeInventory();
                        crazyManager.removePlayerFromOpeningList(player);
                        crazyManager.endCrate(player);
                    }

                    slower++;
                }

                full++;
                open++;

                if (open > 5) {
                    player.openInventory(inv);
                    open = 0;
                }
            }

            private void checkLore() {
                if (rewards.get(player).get(getSelectedSlot()).getItemMeta().hasLore()) {
                    inv.setItem(getSelectedSlot(), selectorItem.setName(rewards.get(player).get(getSelectedSlot()).getItemMeta().getDisplayName()).setLore(rewards.get(player).get(getSelectedSlot()).getItemMeta().getLore()).build());
                } else {
                    inv.setItem(getSelectedSlot(), selectorItem.setName(rewards.get(player).get(getSelectedSlot()).getItemMeta().getDisplayName()).build());
                }
                inv.setItem(getResetSlot(), rewards.get(player).get(getResetSlot()));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                selectedSlot++;
                resetSlot++;
            }

            /**
             * Get the slot that has the selector is now on to set it as the selector item.
             * @return The slot for the selector.
             */
            private int getSelectedSlot() {
                return slots.get(selectedSlot);
            }

            /**
             * Get the slot the selector was just on to reset it to the price item.
             * @return The slot the selector was on before.
             */
            private int getResetSlot() {
                return slots.get(resetSlot);
            }
        }.runTaskTimer(plugin, 1, 1));
    }

    private static List<Integer> getBorder() {
        List<Integer> slots = new ArrayList<>();

        slots.add(13);
        slots.add(14);
        slots.add(15);
        slots.add(16);
        slots.add(25);
        slots.add(34);
        slots.add(43);
        slots.add(42);
        slots.add(41);
        slots.add(40);
        slots.add(39);
        slots.add(38);
        slots.add(37);
        slots.add(28);
        slots.add(19);
        slots.add(10);
        slots.add(11);
        slots.add(12);

        return slots;
    }

}