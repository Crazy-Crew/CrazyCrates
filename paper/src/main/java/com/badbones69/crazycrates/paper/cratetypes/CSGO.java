package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
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
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class CSGO implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();
    
    private void setGlass(Inventory inventory) {
        HashMap<Integer, ItemStack> glass = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            if (i < 9 && i != 3) glass.put(i, inventory.getItem(i));
        }

        for (int i : glass.keySet()) {
            if (inventory.getItem(i) == null) {
                ItemStack item = this.methods.getRandomPaneColor().setName(" ").build();
                inventory.setItem(i, item);
                inventory.setItem(i + 18, item);
            }
        }

        for (int i = 1; i < 10; i++) {
            if (i < 9 && i != 4) glass.put(i, inventory.getItem(i));
        }

        ItemStack item = this.methods.getRandomPaneColor().setName(" ").build();

        inventory.setItem(0, glass.get(1));
        inventory.setItem(18, glass.get(1));
        inventory.setItem(1, glass.get(2));
        inventory.setItem(1 + 18, glass.get(2));
        inventory.setItem(2, glass.get(3));
        inventory.setItem(2 + 18, glass.get(3));
        inventory.setItem(3, glass.get(5));
        inventory.setItem(3 + 18, glass.get(5));
        inventory.setItem(4, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        inventory.setItem(4 + 18, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        inventory.setItem(5, glass.get(6));
        inventory.setItem(5 + 18, glass.get(6));
        inventory.setItem(6, glass.get(7));
        inventory.setItem(6 + 18, glass.get(7));
        inventory.setItem(7, glass.get(8));
        inventory.setItem(7 + 18, glass.get(8));
        inventory.setItem(8, item);
        inventory.setItem(8 + 18, item);
    }
    
    public void openCSGO(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inventory = this.plugin.getServer().createInventory(null, 27, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));
        setGlass(inventory);

        for (int i = 9; i > 8 && i < 18; i++) {
            inventory.setItem(i, crate.pickPrize(player).getDisplayItem());
        }

        player.openInventory(inventory);

        if (this.crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            startCSGO(player, inventory, crate);
        } else {
            this.methods.failedToTakeKey(player, crate);
            this.crazyManager.removePlayerFromOpeningList(player);
        }
    }
    
    private void startCSGO(final Player player, final Inventory inventory, Crate crate) {
        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 50) { // When Spinning
                    moveItems(inventory, player, crate);
                    setGlass(inventory);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                open++;

                if (open >= 5) {
                    player.openInventory(inventory);
                    open = 0;
                }

                full++;
                if (full > 51) {
                    if (slowSpin().contains(time)) { // When Slowing Down
                        moveItems(inventory, player, crate);
                        setGlass(inventory);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    time++;

                    if (time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        crazyManager.endCrate(player);
                        Prize prize = crate.getPrize(inventory.getItem(13));

                        methods.checkPrize(prize, crazyManager, plugin, player, crate);

                        crazyManager.removePlayerFromOpeningList(player);
                        cancel();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                            }
                        }.runTaskLater(plugin, 40);
                    } else if (time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }
    
    private ArrayList<Integer> slowSpin() {
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
    
    private void moveItems(Inventory inventory, Player player, Crate crate) {
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(inventory.getItem(i));
        }

        inventory.setItem(9, crate.pickPrize(player).getDisplayItem());

        for (int i = 0; i < 8; i++) {
            inventory.setItem(i + 10, items.get(i));
        }
    }
}