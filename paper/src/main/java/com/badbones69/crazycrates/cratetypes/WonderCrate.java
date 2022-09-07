package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.api.events.player.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.utilities.ScheduleUtils;
import com.badbones69.crazycrates.utilities.handlers.tasks.CrateTaskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class WonderCrate implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;

    @Inject private Methods methods;
    @Inject private ScheduleUtils scheduleUtils;

    // Task Handler
    @Inject private CrateTaskHandler crateTaskHandler;

    public void startWonder(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inv = plugin.getServer().createInventory(null, 45, crate.getCrateInventoryName());
        final ArrayList<String> slots = new ArrayList<>();

        for (int i = 0; i < 45; i++) {
            Prize prize = crate.pickPrize(player);
            slots.add(i + "");
            inv.setItem(i, prize.getDisplayItem());
        }

        player.openInventory(inv);

        crateTaskHandler.addTask(player, scheduleUtils.timer(2L, 0L, () -> {
            AtomicInteger fullTime = new AtomicInteger();
            AtomicInteger timer = new AtomicInteger();
            AtomicInteger slotOne = new AtomicInteger();
            AtomicInteger slotTwo = new AtomicInteger(44);

            final ArrayList<Integer> Slots = new ArrayList<>();
            Prize prize = null;

            if (timer.get() >= 2 && fullTime.get() <= 65) {
                slots.remove(slotOne + "");
                slots.remove(slotTwo + "");
                Slots.add(slotOne.get());
                Slots.add(slotTwo.get());
                inv.setItem(slotOne.get(), new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
                inv.setItem(slotTwo.get(), new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());

                for (String slot : slots) {
                    prize = crate.pickPrize(player);
                    inv.setItem(Integer.parseInt(slot), prize.getDisplayItem());
                }

                slotOne.incrementAndGet();
                slotTwo.decrementAndGet();
            }

            if (fullTime.get() > 67) {
                ItemStack item = methods.getRandomPaneColor().setName(" ").build();

                Slots.forEach(slot -> inv.setItem(slot, item));
            }

            player.openInventory(inv);

            if (fullTime.get() > 100) {
                crateTaskHandler.endCrate(player);
                player.closeInventory();
                crazyManager.givePrize(player, prize);

                assert prize != null;
                if (prize.useFireworks()) methods.firework(player.getLocation().add(0, 1, 0));

                plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

                crazyManager.removePlayerFromOpeningList(player);

                return;
            }

            fullTime.incrementAndGet();
            timer.incrementAndGet();

            if (timer.get() > 2) timer.set(0);
        }));
    }
}