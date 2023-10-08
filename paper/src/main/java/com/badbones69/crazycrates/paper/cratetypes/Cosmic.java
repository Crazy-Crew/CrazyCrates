package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
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
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Cosmic implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    @NotNull
    private final Methods methods = this.crazyHandler.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    private final HashMap<UUID, ArrayList<Integer>> glass = new HashMap<>();
    private final HashMap<UUID, ArrayList<Integer>> picks = new HashMap<>();
    private final HashMap<UUID, Boolean> checkHands = new HashMap<>();
    
    private void showRewards(Player player, Crate crate) {
        Inventory inv = this.plugin.getServer().createInventory(null, 27, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes"));
        this.picks.get(player.getUniqueId()).forEach(i -> inv.setItem(i, pickTier(player).getTierPane()));
        player.openInventory(inv);
    }
    
    private void startRoll(Player player, Crate crate) {
        Inventory inv = this.plugin.getServer().createInventory(null, 27, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Shuffling"));

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, pickTier(player).getTierPane());
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS,1f, 1f);
        player.openInventory(inv);
    }
    
    private void setChests(Inventory inv, Crate crate) {
        CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
        int slot = 1;

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, manager.getMysteryCrate().setAmount(slot).addNamePlaceholder("%Slot%", slot + "").addLorePlaceholder("%Slot%", slot + "").build());
            slot++;
        }
    }
    
    public void openCosmic(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = this.plugin.getServer().createInventory(null, 27, this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Choose"));
        setChests(inv, crate);
        this.crazyManager.addPlayerKeyType(player, keyType);
        this.checkHands.put(player.getUniqueId(), checkHand);
        player.openInventory(inv);
    }
    
    private Tier pickTier(Player player) {
        Crate crate = this.crazyManager.getOpeningCrate(player);

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
    public void onInvClick(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();
        final Player player = (Player) event.getWhoClicked();

        final Crate crate = this.crazyManager.getOpeningCrate(player);

        if (this.crazyManager.isInOpeningList(player)) {
            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        final FileConfiguration file = crate.getFile();

        if (event.getView().getTitle().equals(this.methods.sanitizeColor(file.getString("Crate.CrateName") + " - Shuffling"))) event.setCancelled(true);

        if (event.getView().getTitle().equals(this.methods.sanitizeColor(file.getString("Crate.CrateName") + " - Prizes"))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (inCosmic(slot)) {
                for (int i : this.picks.get(player.getUniqueId())) {
                    if (slot == i) {
                        ItemStack item = event.getCurrentItem();
                        Tier tier = getTier(crate, item);

                        if (item != null && tier != null) {
                            Prize prize = crate.pickPrize(player, tier);

                            for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                prize = crate.pickPrize(player, tier);
                            }

                            if (prize != null) {
                                this.crazyManager.givePrize(player, prize, crate);
                                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, this.crazyManager.getOpeningCrate(player).getName(), prize));
                                event.setCurrentItem(prize.getDisplayItem());
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);

                                if (prize.useFireworks()) this.methods.firework(player.getLocation().add(0, 1, 0));
                            }

                            return;
                        }
                    }
                }
            }
        }

        if (event.getView().getTitle().equals(this.methods.sanitizeColor(file.getString("Crate.CrateName") + " - Choose"))) {
            event.setCancelled(true);
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
                            if (!this.glass.containsKey(player.getUniqueId())) this.glass.put(player.getUniqueId(), new ArrayList<>());

                            if (this.glass.get(player.getUniqueId()).size() < totalPrizes) {
                                event.setCurrentItem(manager.getPickedCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                this.glass.get(player.getUniqueId()).add(slot);
                            }

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                        } else if (nbtItem.hasTag("Cosmic-Picked-Crate")) {
                            if (!this.glass.containsKey(player.getUniqueId())) this.glass.put(player.getUniqueId(), new ArrayList<>());

                            event.setCurrentItem(manager.getMysteryCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                            ArrayList<Integer> l = new ArrayList<>();

                            for (int i : this.glass.get(player.getUniqueId())) if (i != slot) l.add(i);

                            this.glass.put(player.getUniqueId(), l);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                        }
                    }

                    if (this.glass.get(player.getUniqueId()).size() >= totalPrizes) {
                        KeyType keyType = this.crazyManager.getPlayerKeyType(player);

                        if (keyType == KeyType.PHYSICAL_KEY && !this.crazyManager.hasPhysicalKey(player, crate, this.checkHands.get(player.getUniqueId()))) {
                            player.closeInventory();
                            player.sendMessage(Messages.NO_KEY.getMessage());

                            if (this.crazyManager.isInOpeningList(player)) {
                                this.crazyManager.removePlayerFromOpeningList(player);
                                this.crazyManager.removePlayerKeyType(player);
                            }

                            this.checkHands.remove(player.getUniqueId());
                            this.glass.remove(player.getUniqueId());
                            return;
                        }

                        if (this.crazyManager.hasPlayerKeyType(player) && !this.crazyManager.takeKeys(1, player, crate, keyType, this.checkHands.get(player.getUniqueId()))) {
                            this.methods.failedToTakeKey(player, crate);
                            this.crazyManager.removePlayerFromOpeningList(player);
                            this.crazyManager.removePlayerKeyType(player);
                            this.checkHands.remove(player.getUniqueId());
                            this.glass.remove(player.getUniqueId());
                            return;
                        }

                        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
                            int time = 0;

                            @Override
                            public void run() {
                                try {
                                    startRoll(player, crate);
                                } catch (Exception exception) {
                                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                                    plugin.getServer().getPluginManager().callEvent(event);

                                    if (!event.isCancelled()) {
                                        crazyManager.addKeys(1, player, crate, keyType);
                                        crazyManager.endCrate(player);
                                        cancel();
                                        player.sendMessage(methods.getPrefix("&cAn issue has occurred and so a key refund was given."));
                                        LegacyLogger.warn("An issue occurred when the user " + player.getName() +
                                        " was using the " + crate.getName() + " crate and so they were issued a key refund.", exception);
                                    }

                                    return;
                                }

                                time++;

                                if (time == 40) {
                                    crazyManager.endCrate(player);
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
                        }.runTaskTimer(this.plugin, 0, 2));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        Crate crate = this.crazyManager.getOpeningCrate(player);

        if (this.crazyManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            } else {
                if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
            }
        } else {
            return;
        }

        if (event.getView().getTitle().equals(this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes"))) {
            boolean playSound = false;

            for (int i : picks.get(player.getUniqueId())) {
                if (inventory.getItem(i) != null) {
                    Tier tier = getTier(crate, inventory.getItem(i));

                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);

                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }

                        crazyManager.givePrize(player, prize, crate);
                        playSound = true;
                    }
                }
            }

            if (playSound) player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            this.crazyManager.removePlayerFromOpeningList(player);
            this.crazyManager.removePlayerKeyType(player);

            if (this.glass.containsKey(player.getUniqueId())) {
                this.picks.put(player.getUniqueId(), glass.get(player.getUniqueId()));
                this.glass.remove(player.getUniqueId());
            }

            this.checkHands.remove(player.getUniqueId());
        }

        if (this.crazyManager.isInOpeningList(player) && event.getView().getTitle().equals(this.methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Choose"))) {

            if (!this.glass.containsKey(player.getUniqueId()) || this.glass.get(player.getUniqueId()).size() < 4) {
                this.crazyManager.removePlayerFromOpeningList(player);
                this.crazyManager.removePlayerKeyType(player);
            }

            if (this.glass.containsKey(player.getUniqueId())) {
                this.picks.put(player.getUniqueId(), this.glass.get(player.getUniqueId()));
                this.glass.remove(player.getUniqueId());
            }

            this.checkHands.remove(player.getUniqueId());
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