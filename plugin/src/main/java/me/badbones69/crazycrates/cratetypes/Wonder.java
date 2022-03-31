package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class Wonder implements Listener {
    
    private static final CrazyCrates cc = CrazyCrates.getInstance();
    
    public static void startWonder(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!cc.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            cc.removePlayerFromOpeningList(player);
            return;
        }
        final Inventory inv = Bukkit.createInventory(null, 45, crate.getCrateInventoryName());
        final ArrayList<String> slots = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            Prize prize = crate.pickPrize(player);
            slots.add(i + "");
            inv.setItem(i, prize.getDisplayItem());
        }
        player.openInventory(inv);
        cc.addCrateTask(player, new BukkitRunnable() {
            int fulltime = 0;
            int timer = 0;
            int slot1 = 0;
            int slot2 = 44;
            final Random r = new Random();
            final ArrayList<Integer> Slots = new ArrayList<>();
            Prize prize = null;
            
            @Override
            public void run() {
                if (timer >= 2 && fulltime <= 65) {
                    slots.remove(slot1 + "");
                    slots.remove(slot2 + "");
                    Slots.add(slot1);
                    Slots.add(slot2);
                    inv.setItem(slot1, new ItemBuilder().setMaterial("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:15").setName(" ").build());
                    inv.setItem(slot2, new ItemBuilder().setMaterial("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:15").setName(" ").build());
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
                    cc.endCrate(player);
                    player.closeInventory();
                    cc.givePrize(player, prize);
                    if (prize.useFireworks()) {
                        Methods.fireWork(player.getLocation().add(0, 1, 0));
                    }
                    Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    cc.removePlayerFromOpeningList(player);
                    return;
                }
                fulltime++;
                timer++;
                if (timer > 2) {
                    timer = 0;
                }
            }
        }.runTaskTimer(cc.getPlugin(), 0, 2));
    }
    
}