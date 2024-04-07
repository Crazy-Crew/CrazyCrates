package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;

public class WarCrateListener implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu holder)) return;

        Player player = holder.getPlayer();

        event.setCancelled(true);

        if (this.crateManager.containsPicker(player) && this.crateManager.isInOpeningList(player)) {
            Crate crate = this.crateManager.getOpeningCrate(player);

            if (crate.getCrateType() == CrateType.war && this.crateManager.isPicker(player)) {
                ItemStack item = event.getCurrentItem();

                if (item != null && item.getType().toString().contains(Material.GLASS_PANE.toString())) {
                    int slot = event.getRawSlot();
                    Prize prize = crate.pickPrize(player);
                    inventory.setItem(slot, prize.getDisplayItem(player));

                    if (this.crateManager.hasCrateTask(player)) this.crateManager.endCrate(player);

                    this.crateManager.removePicker(player);
                    this.crateManager.addCloser(player, true);

                    PrizeManager.givePrize(player, prize, crate);

                    if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                    this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    this.crateManager.removePlayerFromOpeningList(player);

                    crate.playSound(player, player.getLocation(), "cycle-sound", "BLOCK_ANVIL_LAND", SoundCategory.PLAYERS);

                    this.crateManager.addCrateTask(player, new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                if (i != slot) inventory.setItem(i, crate.pickPrize(player).getDisplayItem(player));
                            }

                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                            // Removing other items then the prize.
                            crateManager.addCrateTask(player, new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 9; i++) {
                                        if (i != slot) inventory.setItem(i, null);
                                    }

                                    if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                    // Closing the inventory when finished.
                                    crateManager.addCrateTask(player, new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                            crateManager.removePlayerFromOpeningList(player);

                                            player.closeInventory();
                                        }
                                    }.runTaskLater(plugin, 30));
                                }
                            }.runTaskLater(plugin, 30));
                        }
                    }.runTaskLater(this.plugin, 30));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (this.crateManager.containsPicker(player) && this.crateManager.isPicker(player)) {
            for (Crate crate : this.crateManager.getUsableCrates()) {
                if (crate.getCrateType() == CrateType.war && event.getInventory().getHolder(false) instanceof CratePrizeMenu) {
                    if (this.crateManager.hasCrateTask(player)) {
                        this.crateManager.removeCloser(player);

                        this.crateManager.endCrate(player);
                    }
                }
            }
        }
    }
}