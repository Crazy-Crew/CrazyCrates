package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
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
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import java.util.ArrayList;
import java.util.List;

public class Wonder implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrateManager crateManager = plugin.getCrateManager();
    
    public static void startWonder(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, checkHand)) {
            MiscUtils.failedToTakeKey(player, crate);
            crateManager.removePlayerFromOpeningList(player);
            return;
        }

        Inventory inventory = new CratePrizeMenu(plugin, crate, player, 45, crate.getCrateInventoryName()).build().getInventory();

        final List<String> slots = new ArrayList<>();

        for (int i = 0; i < 45; i++) {
            Prize prize = crate.pickPrize(player);
            slots.add(i + "");
            inventory.setItem(i, prize.getDisplayItem());
        }

        player.openInventory(inventory);

        crateManager.addCrateTask(player, new BukkitRunnable() {
            int fullTime = 0;
            int timer = 0;
            int slot1 = 0;
            int slot2 = 44;
            final List<Integer> Slots = new ArrayList<>();
            Prize prize = null;
            
            @Override
            public void run() {
                if (timer >= 2 && fullTime <= 65) {
                    slots.remove(slot1 + "");
                    slots.remove(slot2 + "");
                    Slots.add(slot1);
                    Slots.add(slot2);
                    inventory.setItem(slot1, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
                    inventory.setItem(slot2, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());

                    for (String slot : slots) {
                        prize = crate.pickPrize(player);
                        inventory.setItem(Integer.parseInt(slot), prize.getDisplayItem());
                    }

                    slot1++;
                    slot2--;
                }

                if (fullTime > 67) {
                    ItemStack item = MiscUtils.getRandomPaneColor().setName(" ").build();

                    for (int slot : Slots) {
                        inventory.setItem(slot, item);
                    }
                }

                player.openInventory(inventory);

                if (fullTime > 100) {
                    crateManager.endCrate(player);
                    player.closeInventory();
                    plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);

                    if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    crateManager.removePlayerFromOpeningList(player);

                    return;
                }

                fullTime++;
                timer++;

                if (timer > 2) timer = 0;
            }
        }.runTaskTimer(plugin, 0, 2));
    }
}