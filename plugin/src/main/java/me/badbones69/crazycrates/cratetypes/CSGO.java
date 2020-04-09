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
import java.util.HashMap;

public class CSGO implements Listener {
    
    private static CrazyCrates cc = CrazyCrates.getInstance();
    
    private static void setGlass(Inventory inv) {
        HashMap<Integer, ItemStack> glass = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            if (i < 9 && i != 3) {
                glass.put(i, inv.getItem(i));
            }
        }
        for (int i : glass.keySet()) {
            if (inv.getItem(i) == null) {
                ItemStack item = Methods.getRandomPaneColor().setName(" ").build();
                inv.setItem(i, item);
                inv.setItem(i + 18, item);
            }
        }
        for (int i = 1; i < 10; i++) {
            if (i < 9 && i != 4) {
                glass.put(i, inv.getItem(i));
            }
        }
        ItemStack item = Methods.getRandomPaneColor().setName(" ").build();
        inv.setItem(0, glass.get(1));
        inv.setItem(18, glass.get(1));
        inv.setItem(1, glass.get(2));
        inv.setItem(1 + 18, glass.get(2));
        inv.setItem(2, glass.get(3));
        inv.setItem(2 + 18, glass.get(3));
        inv.setItem(3, glass.get(5));
        inv.setItem(3 + 18, glass.get(5));
        inv.setItem(4, new ItemBuilder().setMaterial("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:15").setName(" ").build());
        inv.setItem(4 + 18, new ItemBuilder().setMaterial("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:15").setName(" ").build());
        inv.setItem(5, glass.get(6));
        inv.setItem(5 + 18, glass.get(6));
        inv.setItem(6, glass.get(7));
        inv.setItem(6 + 18, glass.get(7));
        inv.setItem(7, glass.get(8));
        inv.setItem(7 + 18, glass.get(8));
        inv.setItem(8, item);
        inv.setItem(8 + 18, item);
    }
    
    public static void openCSGO(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));
        setGlass(inv);
        for (int i = 9; i > 8 && i < 18; i++) {
            inv.setItem(i, crate.pickPrize(player).getDisplayItem());
        }
        player.openInventory(inv);
        if (cc.takeKeys(1, player, crate, keyType, checkHand)) {
            startCSGO(player, inv, crate);
        } else {
            Methods.failedToTakeKey(player, crate);
            cc.removePlayerFromOpeningList(player);
        }
    }
    
    private static void startCSGO(final Player player, final Inventory inv, Crate crate) {
        cc.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 50) {//When Spinning
                    moveItems(inv, player, crate);
                    setGlass(inv);
                    player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                }
                open++;
                if (open >= 5) {
                    player.openInventory(inv);
                    open = 0;
                }
                full++;
                if (full > 51) {
                    if (slowSpin().contains(time)) {//When Slowing Down
                        moveItems(inv, player, crate);
                        setGlass(inv);
                        player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                    }
                    time++;
                    if (time == 60) {// When done
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
                        cancel();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inv)) {
                                    player.closeInventory();
                                }
                            }
                        }.runTaskLater(cc.getPlugin(), 40);
                    } else if (time > 60) {//Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(cc.getPlugin(), 1, 1));
        
    }
    
    private static ArrayList<Integer> slowSpin() {
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
    
    private static void moveItems(Inventory inv, Player player, Crate crate) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(inv.getItem(i));
        }
        inv.setItem(9, crate.pickPrize(player).getDisplayItem());
        for (int i = 0; i < 8; i++) {
            inv.setItem(i + 10, items.get(i));
        }
    }
    
}