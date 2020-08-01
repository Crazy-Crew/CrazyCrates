package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Roulette implements Listener {
    
    private static CrazyCrates cc = CrazyCrates.getInstance();
    
    private static void setGlass(Inventory inv) {
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                ItemStack item = Methods.getRandomPaneColor().setName(" ").build();
                inv.setItem(i, item);
            }
        }
    }
    
    public static void openRoulette(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));
        setGlass(inv);
        inv.setItem(13, crate.pickPrize(player).getDisplayItem());
        player.openInventory(inv);
        if (!cc.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            cc.removePlayerFromOpeningList(player);
            return;
        }
        startRoulette(player, inv, crate);
    }
    
    private static void startRoulette(final Player player, final Inventory inv, final Crate crate) {
        cc.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int even = 0;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 15) {
                    inv.setItem(13, crate.pickPrize(player).getDisplayItem());
                    setGlass(inv);
                    player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                    even++;
                    if (even >= 4) {
                        even = 0;
                        inv.setItem(13, crate.pickPrize(player).getDisplayItem());
                    }
                }
                open++;
                if (open >= 5) {
                    player.openInventory(inv);
                    open = 0;
                }
                full++;
                if (full > 16) {
                    if (slowSpin().contains(time)) {
                        setGlass(inv);
                        inv.setItem(13, crate.pickPrize(player).getDisplayItem());
                        player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                    }
                    time++;
                    if (time >= 17) {
                        player.playSound(player.getLocation(), cc.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
                        cc.endCrate(player);
                        Prize prize = crate.getPrize(inv.getItem(13));
                        if (prize != null) {
                            cc.givePrize(player, prize);
                            if (prize.useFireworks()) {
                                Methods.fireWork(player.getLocation().add(0, 1, 0));
                            }
                            Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                        } else {
                            player.sendMessage(Methods.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
                        }
                        cc.removePlayerFromOpeningList(player);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inv)) {
                                    player.closeInventory();
                                }
                            }
                        }.runTaskLater(cc.getPlugin(), 40);
                    }
                }
            }
        }.runTaskTimer(cc.getPlugin(), 2, 2));
    }
    
    private static ArrayList<Integer> slowSpin() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 46;
        int cut = 9;
        for (int i = 46; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }
        return slow;
    }
    
}
