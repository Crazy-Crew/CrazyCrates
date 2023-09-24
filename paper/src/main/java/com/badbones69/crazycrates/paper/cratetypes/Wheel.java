package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import org.bukkit.SoundCategory;
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
import java.util.Map;
import java.util.UUID;

public class Wheel implements Listener {
    
    private final Map<UUID, HashMap<Integer, ItemStack>> rewards = new HashMap<>();
    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();
    
    public void startWheel(final Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!this.crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            this.methods.failedToTakeKey(player, crate);
            this.crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inventory = this.plugin.getServer().createInventory(null, 54, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName")));

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        }

        HashMap<Integer, ItemStack> items = new HashMap<>();

        for (int i : getBorder()) {
            Prize prize = crate.pickPrize(player);
            inventory.setItem(i, prize.getDisplayItem());
            items.put(i, prize.getDisplayItem());
        }

        this.rewards.put(player.getUniqueId(), items);
        player.openInventory(inventory);

        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
            final ArrayList<Integer> slots = getBorder();
            int i = 0;
            int f = 17;
            int full = 0;
            final int timer = methods.randomNumber(42, 68);
            int slower = 0;
            int open = 0;
            int slow = 0;
            
            @Override
            public void run() {
                if (i >= 18) i = 0;

                if (f >= 18) f = 0;

                if (full < timer) checkLore();

                if (full >= timer) {
                    if (methods.slowSpin().contains(slower)) checkLore();

                    if (full == timer + 47) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    if (full >= timer + 47) {
                        slow++;

                        if (slow >= 2) {
                            ItemStack item = methods.getRandomPaneColor().setName(" ").build();

                            for (int slot = 0; slot < 54; slot++) {
                                if (!getBorder().contains(slot)) inventory.setItem(slot, item);
                            }

                            slow = 0;
                        }
                    }

                    if (full >= (timer + 55 + 47)) {
                        Prize prize = null;

                        if (crazyManager.isInOpeningList(player)) prize = crate.getPrize(rewards.get(player.getUniqueId()).get(slots.get(f)));

                        methods.checkPrize(prize, crazyManager, plugin, player, crate);

                        player.closeInventory();
                        crazyManager.removePlayerFromOpeningList(player);
                        crazyManager.endCrate(player);
                    }

                    slower++;
                }

                full++;
                open++;

                if (open > 5) {
                    player.openInventory(inventory);
                    open = 0;
                }
            }

            private void checkLore() {
                if (rewards.get(player.getUniqueId()).get(this.slots.get(i)).getItemMeta().hasLore()) {
                    inventory.setItem(slots.get(i), new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName(rewards.get(player.getUniqueId()).get(this.slots.get(i)).getItemMeta().getDisplayName()).setLore(rewards.get(player.getUniqueId()).get(this.slots.get(i)).getItemMeta().getLore()).build());
                } else {
                    inventory.setItem(this.slots.get(i), new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).setName(rewards.get(player.getUniqueId()).get(this.slots.get(i)).getItemMeta().getDisplayName()).build());
                }

                inventory.setItem(this.slots.get(f), rewards.get(player.getUniqueId()).get(slots.get(f)));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                i++;
                f++;
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }

    private ArrayList<Integer> getBorder() {
        ArrayList<Integer> slots = new ArrayList<>();

        slots.add(13);
        slots.add(14);
        slots.add(15);
        slots.add(16);
        slots.add(25);
        slots.add(34);
        slots.add(43);
        slots.add(42);
        slots.add(41);
        slots.add(40);
        slots.add(39);
        slots.add(38);
        slots.add(37);
        slots.add(28);
        slots.add(19);
        slots.add(10);
        slots.add(11);
        slots.add(12);

        return slots;
    }
}