package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.utilities.CommonUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class RouletteCrate implements Listener {

    private final CrazyCrates crazyCrates = CrazyCrates.getInstance();

    private final CrazyManager crazyManager;
    private final Methods methods;
    private final CommonUtils commonUtils;

    public RouletteCrate(CrazyManager crazyManager, Methods methods, CommonUtils commonUtils) {
        this.crazyManager = crazyManager;
        this.methods = methods;
        this.commonUtils = commonUtils;
    }

    private void setGlass(Inventory inv) {
        for (int i = 0; i < 27; i++) {
            if (i != 13) inv.setItem(i, methods.getRandomPaneColor().setName(" ").build());
        }
    }
    
    public void openRoulette(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = crazyCrates.getServer().createInventory(null, 27, crate.getFile().getString("Crate.CrateName"));
        setGlass(inv);
        inv.setItem(13, crate.pickPrize(player).getDisplayItem());
        player.openInventory(inv);

        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        startRoulette(player, inv, crate);
    }
    
    private void startRoulette(final Player player, final Inventory inv, final Crate crate) {
        crazyManager.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int even = 0;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 15) {
                    inv.setItem(13, crate.pickPrize(player).getDisplayItem());
                    setGlass(inv);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
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

                    if (commonUtils.slowSpin().contains(time)) {
                        setGlass(inv);
                        inv.setItem(13, crate.pickPrize(player).getDisplayItem());
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    time++;

                    if (time >= 23) {
                        commonUtils.endCrate(player, crate, inv);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inv)) player.closeInventory();
                            }
                        }.runTaskLater(crazyCrates, 40);
                    }
                }
            }
        }.runTaskTimer(crazyCrates, 2, 2));
    }
}