package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;

public class Wonder implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    public static void startWonder(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
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

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            int fulltime = 0;
            int timer = 0;
            int slot1 = 0;
            int slot2 = 44;
            final ArrayList<Integer> Slots = new ArrayList<>();
            Prize prize = null;
            
            @Override
            public void run() {
                if (timer >= 2 && fulltime <= 65) {
                    slots.remove(slot1 + "");
                    slots.remove(slot2 + "");
                    Slots.add(slot1);
                    Slots.add(slot2);
                    inv.setItem(slot1, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
                    inv.setItem(slot2, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());

                    for (String slot : slots) {
                        prize = crate.pickPrize(player);
                        inv.setItem(Integer.parseInt(slot), prize.getDisplayItem());
                    }

                    slot1++;
                    slot2--;
                }

                if (fulltime > 67) {
                    ItemStack item = Methods.getRandomPaneColor().setName(" ").build();

                    for (int slot : Slots) {
                        inv.setItem(slot, item);
                    }
                }

                player.openInventory(inv);

                if (fulltime > 100) {
                    crazyManager.endCrate(player);
                    player.closeInventory();
                    crazyManager.givePrize(player, prize, crate);

                    if (prize.useFireworks()) Methods.firework(player.getLocation().add(0, 1, 0));

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    crazyManager.removePlayerFromOpeningList(player);

                    return;
                }

                fulltime++;
                timer++;

                if (timer > 2) timer = 0;
            }
        }.runTaskTimer(plugin, 0, 2));
    }
}