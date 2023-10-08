package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;

public class Roulette implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    
    private void setGlass(Inventory inventory) {
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                ItemStack item = this.methods.getRandomPaneColor().setName(" ").build();
                inventory.setItem(i, item);
            }
        }
    }
    
    public void openRoulette(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inventory = this.plugin.getServer().createInventory(null, 27, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));
        setGlass(inventory);
        inventory.setItem(13, crate.pickPrize(player).getDisplayItem());
        player.openInventory(inventory);

        if (!this.userManager.takeKeys(1, player.getUniqueId(), crate.getName(), keyType, checkHand)) {
            this.methods.failedToTakeKey(player.getName(), crate);
            this.crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        startRoulette(player, inventory, crate);
    }
    
    private void startRoulette(Player player, Inventory inventory, Crate crate) {
        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
            int time = 1;
            int even = 0;
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (full <= 15) {
                    inventory.setItem(13, crate.pickPrize(player).getDisplayItem());
                    setGlass(inventory);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    even++;

                    if (even >= 4) {
                        even = 0;
                        inventory.setItem(13, crate.pickPrize(player).getDisplayItem());
                    }
                }

                open++;

                if (open >= 5) {
                    player.openInventory(inventory);
                    open = 0;
                }

                full++;

                if (full > 16) {

                    if (methods.slowSpin().contains(time)) {
                        setGlass(inventory);
                        inventory.setItem(13, crate.pickPrize(player).getDisplayItem());
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS,1f, 1f);
                    }

                    time++;

                    if (time >= 23) {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);
                        crazyManager.endCrate(player);
                        Prize prize = crate.getPrize(inventory.getItem(13));

                        methods.checkPrize(prize, crazyManager, plugin, player, crate);

                        crazyManager.removePlayerFromOpeningList(player);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                            }
                        }.runTaskLater(plugin, 40);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 2, 2));
    }
}