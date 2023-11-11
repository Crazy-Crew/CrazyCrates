package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("ALL")
public class Cosmic implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrateManager crateManager = plugin.getCrateManager();

    private static final HashMap<UUID, ArrayList<Integer>> glass = new HashMap<>();
    private static final HashMap<UUID, ArrayList<Integer>> picks = new HashMap<>();
    private static final HashMap<UUID, Boolean> checkHands = new HashMap<>();
    
    private static void showRewards(Player player, Crate crate) {
        Inventory inventory = new CratePrizeMenu(crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes")).build().getInventory();

        Tier tier = pickTier(player);

        if (tier != null) {
            picks.get(player.getUniqueId()).forEach(i -> inventory.setItem(i, tier.getTierPane()));
            player.openInventory(inventory);
        }
    }

    private static void startRoll(Player player, Crate crate) {
        Inventory inventory = new CratePrizeMenu(crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Shuffling")).build().getInventory();

        Tier tier = pickTier(player);

        if (tier != null) {
            for (int i = 0; i < 27; i++) {
                inventory.setItem(i, tier.getTierPane());
            }
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.openInventory(inventory);
    }

    private static void setChests(Inventory inv, Crate crate) {
        CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
        int slot = 1;

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, manager.getMysteryCrate().setAmount(slot).addNamePlaceholder("%Slot%", slot + "").addLorePlaceholder("%Slot%", slot + "").build());
            slot++;
        }
    }

    public static void openCosmic(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inventory = new CratePrizeMenu(crate, player, 27, MsgUtils.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Choose")).build().getInventory();
        setChests(inventory, crate);
        crateManager.addPlayerKeyType(player, keyType);
        checkHands.put(player.getUniqueId(), checkHand);
        player.openInventory(inventory);
    }

    private static Tier pickTier(Player player) {
        Crate crate = crateManager.getOpeningCrate(player);

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu crateCosmicMenu)) {
            return;
        }

        event.setCancelled(true);

        Crate crate = crateManager.getOpeningCrate(player);

        if (crateManager.isInOpeningList(player)) {
            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        FileConfiguration file = crate.getFile();

        if (crateCosmicMenu.contains(" - Prizes")) {
            int slot = event.getRawSlot();

            if (inCosmic(slot)) {
                for (int i : picks.get(player.getUniqueId())) {
                    if (slot == i) {
                        ItemStack item = event.getCurrentItem();
                        Tier tier = getTier(crate, item);

                        if (item != null && tier != null) {
                            Prize prize = crate.pickPrize(player, tier);

                            for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                prize = crate.pickPrize(player, tier);
                            }

                            if (prize != null) {
                                plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
                                plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crateManager.getOpeningCrate(player).getName(), prize));
                                event.setCurrentItem(prize.getDisplayItem());
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

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

            if (inCosmic(slot)) {
                ItemStack item = event.getCurrentItem();

                if (item != null) {
                    CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
                    int totalPrizes = manager.getTotalPrizes();
                    int pickedSlot = slot + 1;
                    NBTItem nbtItem = new NBTItem(item);

                    if (nbtItem.hasNBTData()) {
                        if (nbtItem.hasTag("Cosmic-Mystery-Crate")) {
                            if (!glass.containsKey(player.getUniqueId())) glass.put(player.getUniqueId(), new ArrayList<>());

                            if (glass.get(player.getUniqueId()).size() < totalPrizes) {
                                event.setCurrentItem(manager.getPickedCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                glass.get(player.getUniqueId()).add(slot);
                            }

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        } else if (nbtItem.hasTag("Cosmic-Picked-Crate")) {
                            if (!glass.containsKey(player.getUniqueId())) glass.put(player.getUniqueId(), new ArrayList<>());

                            event.setCurrentItem(manager.getMysteryCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                            ArrayList<Integer> slots = new ArrayList<>();

                            for (int i : glass.get(player.getUniqueId())) if (i != slot) slots.add(i);

                            glass.put(player.getUniqueId(), slots);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        }
                    }

                    if (glass.get(player.getUniqueId()).size() >= totalPrizes) {
                        KeyType keyType = crateManager.getPlayerKeyType(player);

                        if (keyType == KeyType.physical_key && !plugin.getCrazyHandler().getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), checkHands.get(player.getUniqueId()))) {
                            player.closeInventory();
                            player.sendMessage(Translation.no_keys.getString());

                            if (crateManager.isInOpeningList(player)) {
                                crateManager.removePlayerFromOpeningList(player);
                                crateManager.removePlayerKeyType(player);
                            }

                            checkHands.remove(player.getUniqueId());
                            glass.remove(player.getUniqueId());
                            return;
                        }

                        if (crateManager.hasPlayerKeyType(player) && !plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, checkHands.get(player.getUniqueId()))) {
                            MiscUtils.failedToTakeKey(player, crate);
                            crateManager.removePlayerFromOpeningList(player);
                            crateManager.removePlayerKeyType(player);
                            checkHands.remove(player.getUniqueId());
                            glass.remove(player.getUniqueId());
                            return;
                        }

                        crateManager.addCrateTask(player, new BukkitRunnable() {
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
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);

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

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu crateCosmicMenu)) return;

        Crate crate = crateManager.getOpeningCrate(player);

        if (crateManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            } else {
                if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
            }
        } else {
            return;
        }

        if (crateCosmicMenu.contains(" - Prizes")) {
            boolean playSound = false;

            for (int i : picks.get(player.getUniqueId())) {
                if (inventory.getItem(i) != null) {
                    Tier tier = getTier(crate, inventory.getItem(i));

                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);

                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }

                        plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
                        playSound = true;
                    }
                }
            }

            if (playSound) player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            crateManager.removePlayerFromOpeningList(player);
            crateManager.removePlayerKeyType(player);

            if (glass.containsKey(player.getUniqueId())) {
                picks.put(player.getUniqueId(), glass.get(player));
                glass.remove(player.getUniqueId());
            }

            checkHands.remove(player.getUniqueId());
        }

        if (crateManager.isInOpeningList(player) && crateCosmicMenu.contains(" - Choose")) {
            CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();

            if (!glass.containsKey(player.getUniqueId()) || glass.get(player.getUniqueId()).size() > manager.getTotalPrizes()) {
                crateManager.removePlayerFromOpeningList(player);
                crateManager.removePlayerKeyType(player);
            }

            if (glass.containsKey(player.getUniqueId())) {
                picks.put(player.getUniqueId(), glass.get(player.getUniqueId()));
                glass.remove(player.getUniqueId());
            }

            checkHands.remove(player.getUniqueId());
        }
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