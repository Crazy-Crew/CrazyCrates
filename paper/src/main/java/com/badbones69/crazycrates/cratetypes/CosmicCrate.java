package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.player.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.player.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.utilities.ScheduleUtils;
import com.badbones69.crazycrates.utilities.handlers.tasks.CrateTaskHandler;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class CosmicCrate implements Listener {

    private final HashMap<Player, ArrayList<Integer>> glass = new HashMap<>();
    private final HashMap<Player, ArrayList<Integer>> picks = new HashMap<>();
    private final HashMap<Player, Boolean> checkHands = new HashMap<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;
    @Inject private CrazyLogger crazyLogger;

    // Utilities
    @Inject private Methods methods;

    @Inject private ScheduleUtils scheduleUtils;

    // Task Handler
    @Inject private CrateTaskHandler crateTaskHandler;

    private void showRewards(Player player, Crate crate) {
        Inventory inv = plugin.getServer().createInventory(null, 27, crate.getFile().getString("Crate.CrateName") + " - Prizes");
        picks.get(player).forEach(i -> inv.setItem(i, pickTier(player).getTierPane()));
        player.openInventory(inv);
    }
    
    private void startRoll(Player player, Crate crate) {
        Inventory inv = plugin.getServer().createInventory(null, 27, crate.getFile().getString("Crate.CrateName") + " - Shuffling");

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, pickTier(player).getTierPane());
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.openInventory(inv);
    }
    
    private static void setChests(Inventory inv, Crate crate) {
        CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
        int slot = 1;

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, manager.getMysteryCrate().setAmount(slot).addNamePlaceholder("%Slot%", slot + "").addLorePlaceholder("%Slot%", slot + "").build());
            slot++;
        }
    }
    
    public void openCosmic(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = plugin.getServer().createInventory(null, 27, crate.getFile().getString("Crate.CrateName") + " - Choose");
        setChests(inv, crate);
        crazyManager.addPlayerKeyType(player, keyType);
        checkHands.put(player, checkHand);
        player.openInventory(inv);
    }
    
    private Tier pickTier(Player player) {
        Crate crate = crazyManager.getOpeningCrate(player);

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

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();

        final Crate crate = crazyManager.getOpeningCrate(player);

        if (crazyManager.isInOpeningList(player)) {
            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        final FileConfiguration file = crate.getFile();

        if (e.getView().getTitle().equals(file.getString("Crate.CrateName") + " - Shuffling")) e.setCancelled(true);

        if (e.getView().getTitle().equals(file.getString("Crate.CrateName") + " - Prizes")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (inCosmic(slot)) {
                for (int i : picks.get(player)) {
                    if (slot == i) {
                        ItemStack item = e.getCurrentItem();
                        Tier tier = getTier(crate, item);

                        if (item != null && tier != null) {
                            Prize prize = crate.pickPrize(player, tier);

                            for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                prize = crate.pickPrize(player, tier);
                            }

                            if (prize != null) {
                                crazyManager.givePrize(player, prize);

                                plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crazyManager.getOpeningCrate(player).getName(), prize));

                                e.setCurrentItem(prize.getDisplayItem());
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                                if (prize.useFireworks()) methods.firework(player.getLocation().add(0, 1, 0));
                            }

                            return;
                        }
                    }
                }
            }
        }

        if (e.getView().getTitle().equals(file.getString("Crate.CrateName") + " - Choose")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (inCosmic(slot)) {
                ItemStack item = e.getCurrentItem();

                if (item != null) {
                    CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
                    int totalPrizes = manager.getTotalPrizes();
                    int pickedSlot = slot + 1;

                    NBTItem nbtItem = new NBTItem(item);

                    if (nbtItem.hasNBTData()) {
                        if (nbtItem.hasKey("Cosmic-Mystery-Crate")) {
                            if (!glass.containsKey(player)) glass.put(player, new ArrayList<>());

                            if (glass.get(player).size() < totalPrizes) {
                                e.setCurrentItem(manager.getPickedCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                glass.get(player).add(slot);
                            }

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        } else if (nbtItem.hasKey("Cosmic-Picked-Crate")) {
                            if (!glass.containsKey(player)) glass.put(player, new ArrayList<>());

                            e.setCurrentItem(manager.getMysteryCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                            ArrayList<Integer> l = new ArrayList<>();

                            for (int i : glass.get(player)) if (i != slot) l.add(i);

                            glass.put(player, l);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        }
                    }

                    if (glass.get(player).size() >= totalPrizes) {
                        KeyType keyType = crazyManager.getPlayerKeyType(player);

                        if (keyType == KeyType.PHYSICAL_KEY && !crazyManager.hasPhysicalKey(player, crate, checkHands.get(player))) {
                            player.closeInventory();
                            //player.sendMessage(Messages.NO_KEY.getMessage(methods));

                            if (crazyManager.isInOpeningList(player)) {
                                crazyManager.removePlayerFromOpeningList(player);
                                crazyManager.removePlayerKeyType(player);
                            }

                            checkHands.remove(player);
                            glass.remove(player);
                            return;
                        }

                        if (crazyManager.hasPlayerKeyType(player) && !crazyManager.takeKeys(1, player, crate, keyType, checkHands.get(player))) {
                            methods.failedToTakeKey(player, crate);
                            crazyManager.removePlayerFromOpeningList(player);
                            crazyManager.removePlayerKeyType(player);
                            checkHands.remove(player);
                            glass.remove(player);
                            return;
                        }

                        crateTaskHandler.addTask(player, scheduleUtils.timer(0L, 2L, () -> {
                            AtomicInteger count = new AtomicInteger();

                            try {
                                startRoll(player, crate);
                            } catch (Exception error) {
                                // TODO() - Turn this into a universal method since its repeated a lot.
                                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                                plugin.getServer().getPluginManager().callEvent(event);

                                if (!event.isCancelled()) {
                                    crazyManager.addKeys(1, player, crate, keyType);

                                    // TODO() Experimental -> DEBUG
                                    crateTaskHandler.endCrate(player);

                                    // TODO() - Configurable error message.
                                    // player.sendMessage(methods.getPrefix("&cAn issue has occurred and so a key refund was given."));

                                    crazyLogger.debug("<red>Issue occurred when</red> <gold>" + player.getName() + "</gold> <red>was using </red><gold>" + crate.getName() + "</gold> <red>crate and were issued a key refund.</red>");
                                    error.printStackTrace();

                                    return;
                                }

                                // TODO() - Debug this.
                                if (count.incrementAndGet() == 40) {

                                    crazyLogger.debug("<red>Count:</red> <gold>" + count.get() + ".</gold>");

                                    crateTaskHandler.endCrate(player);

                                    showRewards(player, crate);

                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);

                                    scheduleUtils.later(40L, () -> {
                                        if (player.getOpenInventory().getTopInventory().equals(inv)) player.closeInventory();
                                    });
                                }
                            }
                        }));
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();
        Crate crate = crazyManager.getOpeningCrate(player);

        if (crazyManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            } else {
                String type = crate.getFile().getString("Crate.CrateType");

                assert type != null;
                if (!type.equalsIgnoreCase("Cosmic")) return;
            }
        } else {
            return;
        }

        if (e.getView().getTitle().equals(crate.getFile().getString("Crate.CrateName") + " - Prizes")) {
            boolean playSound = false;

            for (int i : picks.get(player)) {
                if (inv.getItem(i) != null) {
                    Tier tier = getTier(crate, inv.getItem(i));

                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);

                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }

                        crazyManager.givePrize(player, prize);
                        playSound = true;
                    }
                }
            }

            if (playSound) player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            crazyManager.removePlayerFromOpeningList(player);
            crazyManager.removePlayerKeyType(player);

            if (glass.containsKey(player)) {
                picks.put(player, glass.get(player));
                glass.remove(player);
            }

            checkHands.remove(player);
        }

        if (crazyManager.isInOpeningList(player) && e.getView().getTitle().equals(crate.getFile().getString("Crate.CrateName") + " - Choose")) {

            if (!glass.containsKey(player) || glass.get(player).size() < 4) {
                crazyManager.removePlayerFromOpeningList(player);
                crazyManager.removePlayerKeyType(player);
            }

            if (glass.containsKey(player)) {
                picks.put(player, glass.get(player));
                glass.remove(player);
            }

            checkHands.remove(player);
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