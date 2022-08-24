package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.utilities.ScheduleUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;

public class WonderCrate implements Listener {

    private final CrazyCrates crazyCrates = CrazyCrates.getInstance();

    private final CrazyManager crazyManager;
    private final Methods methods;
    private final ScheduleUtils scheduleUtils;

    public WonderCrate(CrazyManager crazyManager, Methods methods, ScheduleUtils scheduleUtils) {
        this.crazyManager = crazyManager;
        this.methods = methods;
        this.scheduleUtils = scheduleUtils;
    }

    public void startWonder(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inv = crazyCrates.getServer().createInventory(null, 45, crate.getCrateInventoryName());
        final ArrayList<String> slots = new ArrayList<>();

        for (int i = 0; i < 45; i++) {
            Prize prize = crate.pickPrize(player);
            slots.add(i + "");
            inv.setItem(i, prize.getDisplayItem());
        }

        player.openInventory(inv);

        crazyManager.addCrateTask(player, scheduleUtils.timer(2L, 0L, new BukkitRunnable() {

            int fullTime = 0;
            int timer = 0;
            int slotOne = 0;
            int slotTwo = 44;

            @Override
            public void run() {
                final ArrayList<Integer> Slots = new ArrayList<>();
                Prize prize = null;

                if (timer >= 2 && fullTime <= 65) {
                    slots.remove(slotOne + "");
                    slots.remove(slotTwo + "");
                    Slots.add(slotOne);
                    Slots.add(slotTwo);
                    inv.setItem(slotOne, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
                    inv.setItem(slotTwo, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());

                    for (String slot : slots) {
                        prize = crate.pickPrize(player);
                        inv.setItem(Integer.parseInt(slot), prize.getDisplayItem());
                    }

                    slotOne++;
                    slotTwo--;
                }

                if (fullTime > 67) {
                    ItemStack item = methods.getRandomPaneColor().setName(" ").build();

                    Slots.forEach(slot -> inv.setItem(slot, item));
                }

                player.openInventory(inv);

                if (fullTime > 100) {
                    crazyManager.endCrate(player);
                    player.closeInventory();
                    crazyManager.givePrize(player, prize);

                    assert prize != null;
                    if (prize.useFireworks()) methods.firework(player.getLocation().add(0, 1, 0));

                    crazyCrates.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

                    crazyManager.removePlayerFromOpeningList(player);
                    return;
                }

                fullTime++;
                timer++;

                if (timer > 2) timer = 0;
            }
        }));
    }
}