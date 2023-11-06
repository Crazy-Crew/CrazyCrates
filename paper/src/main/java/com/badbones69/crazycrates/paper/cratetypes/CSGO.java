package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSGO implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrateManager crateManager = plugin.getCrateManager();
    
    private static void setGlass(Inventory inv) {
        HashMap<Integer, ItemStack> glass = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            if (i < 9 && i != 3) glass.put(i, inv.getItem(i));
        }

        for (int i : glass.keySet()) {
            if (inv.getItem(i) == null) {
                ItemStack item = MiscUtils.getRandomPaneColor().setName(" ").build();
                inv.setItem(i, item);
                inv.setItem(i + 18, item);
            }
        }

        for (int i = 1; i < 10; i++) {
            if (i < 9 && i != 4) glass.put(i, inv.getItem(i));
        }

        ItemStack item = MiscUtils.getRandomPaneColor().setName(" ").build();

        inv.setItem(0, glass.get(1));
        inv.setItem(18, glass.get(1));
        inv.setItem(1, glass.get(2));
        inv.setItem(1 + 18, glass.get(2));
        inv.setItem(2, glass.get(3));
        inv.setItem(2 + 18, glass.get(3));
        inv.setItem(3, glass.get(5));
        inv.setItem(3 + 18, glass.get(5));
        inv.setItem(4, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        inv.setItem(4 + 18, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
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
        Inventory inventory = new CratePrizeMenu(plugin, crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName"))).build().getInventory();
        setGlass(inventory);

        for (int i = 9; i > 8 && i < 18; i++) {
            inventory.setItem(i, crate.pickPrize(player).getDisplayItem());
        }

        player.openInventory(inventory);

        if (plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, checkHand)) {
            startCSGO(player, inventory, crate);
        } else {
            MiscUtils.failedToTakeKey(player, crate);
            crateManager.removePlayerFromOpeningList(player);
        }
    }
    
    private static void startCSGO(final Player player, final Inventory inv, Crate crate) {
        crateManager.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 50) { // When Spinning
                    moveItems(inv, player, crate);
                    setGlass(inv);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                open++;

                if (open >= 5) {
                    player.openInventory(inv);
                    open = 0;
                }

                full++;
                if (full > 51) {
                    if (slowSpin().contains(time)) { // When Slowing Down
                        moveItems(inv, player, crate);
                        setGlass(inv);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    time++;

                    if (time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        crateManager.endCrate(player);
                        Prize prize = crate.getPrize(inv.getItem(13));

                        plugin.getCrazyHandler().getPrizeManager().checkPrize(prize, player, crate);

                        crateManager.removePlayerFromOpeningList(player);
                        cancel();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inv)) player.closeInventory();
                            }
                        }.runTaskLater(plugin, 40);
                    } else if (time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 1, 1));
    }
    
    private static List<Integer> slowSpin() {
        List<Integer> slow = new ArrayList<>();
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
        List<ItemStack> items = new ArrayList<>();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(inv.getItem(i));
        }

        inv.setItem(9, crate.pickPrize(player).getDisplayItem());

        for (int i = 0; i < 8; i++) {
            inv.setItem(i + 10, items.get(i));
        }
    }
}