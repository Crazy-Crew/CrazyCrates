package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Prize;
import com.badbones69.crazycrates.api.utilities.CommonUtils;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class WheelCrate implements Listener {
    
    public static Map<Player, HashMap<Integer, ItemStack>> rewards = new HashMap<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;

    @Inject private Methods methods;
    @Inject private ScheduleUtils scheduleUtils;
    @Inject private CommonUtils commonUtils;

    @Inject private CrateTaskHandler crateTaskHandler;

    public void startWheel(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inv = plugin.getServer().createInventory(null, 54, crate.getFile().getString("Crate.CrateName"));

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

        crateTaskHandler.addTask(player, scheduleUtils.timer(1L, 1L, () -> {

            final ArrayList<Integer> slots = getBorder();

            AtomicInteger item = new AtomicInteger();
            AtomicInteger slot = new AtomicInteger(17);
            AtomicInteger full = new AtomicInteger();

            final int timer = methods.randomNumber(42, 68);

            AtomicInteger slower = new AtomicInteger();
            AtomicInteger open = new AtomicInteger();
            AtomicInteger slow = new AtomicInteger();


            if (item.get() >= 18) item.set(0);

            if (slot.get() >= 18) slot.set(0);

            if (full.get() < timer) {
                if (rewards.get(player).get(slots.get(item.get())).getItemMeta().hasLore()) {
                    inv.setItem(slots.get(item.get()), new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName(rewards.get(player).get(slots.get(item.get())).getItemMeta().getDisplayName()).setLore(rewards.get(player).get(slots.get(item.get())).getItemMeta().getLore()).build());
                } else {
                    inv.setItem(slots.get(item.get()), new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName(rewards.get(player).get(slots.get(item.get())).getItemMeta().getDisplayName()).build());
                }

                inv.setItem(slots.get(slot.get()), rewards.get(player).get(slots.get(slot.get())));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

                item.incrementAndGet();
                slot.incrementAndGet();
            }

            if (full.get() >= timer) {
                //if (commonUtils.slowSpin().contains(slower.get())) rewardCheck();

                if (full.get() == timer + 47) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                if (full.get() >= timer + 47) {

                    if (slow.incrementAndGet() >= 2) {
                        ItemStack itemStack = methods.getRandomPaneColor().setName(" ").build();

                        for (int slotAdd = 0; slotAdd < 54; slotAdd++) {
                            if (!getBorder().contains(slot.get())) inv.setItem(slotAdd, itemStack);
                        }

                        slow.set(0);
                    }
                }

                if (full.get() >= (timer + 55 + 47)) {
                    Prize prize = null;

                    if (crazyManager.isInOpeningList(player)) prize = crate.getPrize(rewards.get(player).get(slots.get(slot.get())));

                    commonUtils.pickPrize(player, crate, prize);

                    player.closeInventory();
                    crazyManager.removePlayerFromOpeningList(player);
                    crateTaskHandler.endCrate(player);
                }

                slower.incrementAndGet();
            }

            full.incrementAndGet();
            open.incrementAndGet();

            if (open.incrementAndGet() > 5) {
                player.openInventory(inv);
                open.set(0);
            }
        }));
    }
    
    private static ArrayList<Integer> getBorder() {
        ArrayList<Integer> slots = new ArrayList<>();

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