package us.crazycrew.crazycrates.paper.listeners.crates;

import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.builders.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import us.crazycrew.crazycrates.paper.other.MsgUtils;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class CosmicCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu crateCosmicMenu)) {
            return;
        }

        event.setCancelled(true);

        Crate crate = this.crateManager.getOpeningCrate(player);

        if (this.crateManager.isInOpeningList(player)) {
            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        if (crateCosmicMenu.contains(" - Prizes")) {
            int slot = event.getRawSlot();

            if (inCosmic(slot)) {
                for (int i : this.crateManager.getPicks(player)) {
                    if (slot == i) {
                        ItemStack item = event.getCurrentItem();
                        Tier tier = getTier(crate, item);

                        if (item != null && tier != null) {
                            Prize prize = crate.pickPrize(player, tier);

                            for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                prize = crate.pickPrize(player, tier);
                            }

                            if (prize != null) {
                                this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
                                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, this.crateManager.getOpeningCrate(player).getName(), prize));
                                event.setCurrentItem(prize.getDisplayItem());
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);

                                if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);
                            }

                            return;
                        }
                    }
                }
            }
        }

        if (crateCosmicMenu.contains(" - Choose")) {
            int slot = event.getRawSlot();

            this.plugin.getLogger().warning("Slot: " + slot);

            if (inCosmic(slot)) {
                ItemStack item = event.getCurrentItem();

                if (item != null) {
                    CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
                    int totalPrizes = manager.getTotalPrizes();
                    int pickedSlot = slot + 1;

                    NBTItem nbtItem = new NBTItem(item);

                    if (nbtItem.hasNBTData()) {
                        if (nbtItem.hasTag("Cosmic-Mystery-Crate")) {
                            this.crateManager.addNewGlassPlayer(player);

                            if (this.crateManager.getGlass(player).size() < totalPrizes) {
                                event.setCurrentItem(manager.getPickedCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                this.crateManager.addGlass(player, slot);
                            }

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                        } else if (nbtItem.hasTag("Cosmic-Picked-Crate")) {
                            this.crateManager.addNewGlassPlayer(player);

                            event.setCurrentItem(manager.getMysteryCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                            ArrayList<Integer> slots = new ArrayList<>();

                            for (int index : this.crateManager.getGlass(player)) if (index != slot) slots.add(index);

                            this.crateManager.setGlass(player, slots);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                        }
                    }

                    if (this.crateManager.getGlass(player).size() >= totalPrizes) {
                        KeyType keyType = this.crateManager.getPlayerKeyType(player);

                        if (keyType == KeyType.physical_key && !this.crazyHandler.getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), this.crateManager.getHand(player))) {
                            player.closeInventory();
                            player.sendMessage(Translation.no_keys.getString());

                            if (this.crateManager.isInOpeningList(player)) {
                                this.crateManager.removePlayerFromOpeningList(player);
                                this.crateManager.removePlayerKeyType(player);
                            }

                            this.crateManager.removeHands(player);
                            this.crateManager.removeGlass(player);
                            return;
                        }

                        if (this.crateManager.hasPlayerKeyType(player) && !this.crazyHandler.getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, this.crateManager.getHand(player))) {
                            MiscUtils.failedToTakeKey(player, crate);
                            this.crateManager.removePlayerFromOpeningList(player);
                            this.crateManager.removePlayerKeyType(player);
                            this.crateManager.removeHands(player);
                            this.crateManager.removeGlass(player);
                            return;
                        }

                        this.crateManager.addCrateTask(player, new BukkitRunnable() {
                            int time = 0;

                            @Override
                            public void run() {
                                try {
                                    startRoll(player, crate);
                                } catch (Exception e) {
                                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                                    plugin.getServer().getPluginManager().callEvent(event);

                                    if (!event.isCancelled()) {
                                        plugin.getCrazyHandler().getUserManager().addKeys(1, player.getUniqueId(), crate.getName(), keyType);
                                        crateManager.endCrate(player);
                                        cancel();
                                        player.sendMessage(MsgUtils.getPrefix("&cAn issue has occurred and so a key refund was given."));
                                        plugin.getServer().getLogger().log(Level.SEVERE, "An issue occurred when the user " + player.getName() + " was using the " + crate.getName() + " crate and so they were issued a key refund.", e);
                                    }

                                    return;
                                }

                                time++;

                                if (time == 40) {
                                    crateManager.endCrate(player);
                                    showRewards(player, crate);
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f);

                                    crateManager.removePicks(player);

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                                        }
                                    }.runTaskLater(plugin, 40);
                                }
                            }
                        }.runTaskTimer(plugin, 0, 2));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getPlayer();

        Crate crate = this.crateManager.getOpeningCrate(player);

        if (this.crateManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            } else {
                if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
            }
        } else {
            return;
        }

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu crateCosmicMenu)) {
            return;
        }

        if (crateCosmicMenu.contains(" - Prizes")) {
            boolean playSound = false;

            for (int i : this.crateManager.getPicks(player)) {
                if (inventory.getItem(i) != null) {
                    Tier tier = getTier(crate, inventory.getItem(i));

                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);

                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }

                        this.crazyHandler.getPrizeManager().givePrize(player, prize, crate);
                        playSound = true;
                    }
                }
            }

            if (playSound) player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS,1f, 1f);

            this.crateManager.removePlayerFromOpeningList(player);
            this.crateManager.removePlayerKeyType(player);

            if (this.crateManager.containsGlass(player)) {
                this.crateManager.addPicks(player, this.crateManager.getGlass(player));
                this.crateManager.removeGlass(player);
            }

            this.crateManager.removeHands(player);
        }

        if (this.crateManager.isInOpeningList(player) && crateCosmicMenu.contains(" - Choose")) {
            CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();

            if (!this.crateManager.containsGlass(player) || this.crateManager.getGlass(player).size() > manager.getTotalPrizes()) {
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);
            }

            if (this.crateManager.containsGlass(player)) {
                this.crateManager.addPicks(player, this.crateManager.getGlass(player));
                this.crateManager.removeGlass(player);
            }

            this.crateManager.removeHands(player);
        }
    }
    
    private void showRewards(Player player, Crate crate) {
        Inventory inventory = new CratePrizeMenu(crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes")).build().getInventory();

        Tier tier = pickTier(player);

        if (tier != null) {
            this.crateManager.getPicks(player).forEach(value -> inventory.setItem(value, tier.getTierPane()));
            player.openInventory(inventory);
        }
    }

    private void startRoll(Player player, Crate crate) {
        Inventory inventory = new CratePrizeMenu(crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Shuffling")).build().getInventory();

        Tier tier = pickTier(player);

        if (tier != null) {
            for (int i = 0; i < 27; i++) {
                inventory.setItem(i, tier.getTierPane());
            }
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
        player.openInventory(inventory);
    }

    private Tier pickTier(Player player) {
        Crate crate = this.crateManager.getOpeningCrate(player);

        if (crate.getTiers() != null && !crate.getTiers().isEmpty()) {
            for (int stopLoop = 0; stopLoop <= 100; stopLoop++) {
                for (Tier tier : crate.getTiers()) {
                    int chance = tier.getChance();
                    int num = new Random().nextInt(tier.getMaxRange());

                    if (num >= 1 && num <= chance) return tier;
                }
            }
        }

        return null;
    }
    
    private Tier getTier(Crate crate, ItemStack item) {
        for (Tier tier : crate.getTiers()) {
            if (tier.getTierPane().isSimilar(item)) return tier;
        }

        return null;
    }
    
    private boolean inCosmic(int slot) {
        // The last slot in cosmic crate is 27
        return slot < 27;
    }
}