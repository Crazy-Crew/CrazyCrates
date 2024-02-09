package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;

public class CosmicCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof CratePrizeMenu cosmic)) return;

        // Get opening crate.
        Crate crate = this.crateManager.getOpeningCrate(player);

        // Check if player is in the opening list.
        if (!this.crateManager.isInOpeningList(player) || crate.getCrateType() != CrateType.cosmic) return;

        // Get crate manager.
        CosmicCrateManager cosmicCrateManager = (CosmicCrateManager) crate.getManager();

        boolean playSound = false;

        if (cosmic.contains(" - Prizes")) {
            for (int amount : cosmicCrateManager.getPickedPrizes(player)) {
                ItemStack item = inventory.getItem(amount);

                if (item != null) {
                    Tier tier = getTier(crate, item);

                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);

                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }

                        this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);

                        playSound = true;
                    }
                }
            }
        }

        // Play sound.
        if (playSound) cosmic.getCrate().playSound(player, player.getLocation(), "click-sound", "UI_BUTTON_CLICK", SoundCategory.PLAYERS);

        // Remove opening stuff.
        this.crateManager.removePlayerFromOpeningList(player);
        this.crateManager.removePlayerKeyType(player);

        // Cancel crate task just in case.
        this.crateManager.removeCrateTask(player);

        // Remove hand checks.
        this.crateManager.removeHands(player);

        // Remove the player from the hashmap.
        cosmicCrateManager.removePickedPlayer(player);
    }

    @EventHandler
    public void onInventoryClickPrize(InventoryClickEvent event) {
        // Get the inventory.
        Inventory inventory = event.getInventory();

        // Get the player.
        Player player = (Player) event.getWhoClicked();

        // Check if inventory holder is instance of CratePrizeMenu
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof CratePrizeMenu cosmic)) return;

        // Cancel event.
        event.setCancelled(true);

        // Get opening crate.
        Crate crate = this.crateManager.getOpeningCrate(player);

        // Check if player is in the opening list.
        if (!this.crateManager.isInOpeningList(player) || crate.getCrateType() != CrateType.cosmic) return;

        // Check the title.
        if (!cosmic.contains(" - Prizes")) return;

        // Get the raw slot.
        int slot = event.getRawSlot();

        // Get inventory view.
        InventoryView view = event.getView();

        // Check if clicking top inventory or not.
        Inventory topInventory = view.getTopInventory();

        if (event.getClickedInventory() != topInventory) return;

        // Get item stack of clicked item.
        ItemStack itemStack = topInventory.getItem(slot);

        // Check if null or air.
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        Tier tier = getTier(crate, itemStack);

        // If tier is null, return
        if (tier == null) return;

        Prize prize = crate.pickPrize(player, tier);

        for (int stop = 0; prize == null && stop <= 2000; stop++) {
            prize = crate.pickPrize(player, tier);
        }

        if (prize == null) return;

        this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

        event.setCurrentItem(prize.getDisplayItem());

        cosmic.getCrate().playSound(player, player.getLocation(), "click-sound","UI_BUTTON_CLICK", SoundCategory.PLAYERS);

        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Get the inventory.
        Inventory inventory = event.getInventory();

        // Get the player.
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        // Check if inventory holder is instance of CratePrizeMenu
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof CratePrizeMenu cosmic)) return;

        // Cancel event.
        event.setCancelled(true);

        // Get opening crate.
        Crate crate = this.crateManager.getOpeningCrate(player);

        // Check if player is in the opening list.
        if (!this.crateManager.isInOpeningList(player) || crate.getCrateType() != CrateType.cosmic) return;

        // Check the title.
        if (!cosmic.contains(" - Choose")) return;

        // Get the raw slot.
        int slot = event.getRawSlot();

        // Get inventory view.
        InventoryView view = event.getView();

        // Check if clicking top inventory or not.
        Inventory topInventory = view.getTopInventory();

        if (event.getClickedInventory() != topInventory) return;

        // Get item stack of clicked item.
        ItemStack itemStack = topInventory.getItem(slot);

        // Check if null or air.
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        // Get crate manager.
        CosmicCrateManager cosmicCrateManager = (CosmicCrateManager) crate.getManager();

        // Get total prizes.
        int totalPrizes = cosmicCrateManager.getTotalPrizes();

        // Get picked slot.
        int pickedSlot = slot+1;

        // Get clicked item's item meta.
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Get the pdc container.
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Check if it has the mystery crate key otherwise check picked key.
        if (container.has(PersistentKeys.cosmic_mystery_crate.getNamespacedKey())) {
            int size = cosmicCrateManager.getPickedPrizes(player).size();

            // Check if prizes is less than or equal to totalPrizes before we change any items.
            if (size < totalPrizes) {
                // Get item builder.
                ItemBuilder builder = cosmicCrateManager.getPickedCrate().setAmount(pickedSlot)
                        .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                        .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

                // Overwrite the current item.
                event.setCurrentItem(builder.build());

                cosmicCrateManager.addPickedPrize(player, slot);

                // Play a sound to indicate they clicked a chest.
                cosmic.getCrate().playSound(player, player.getLocation(), "click-sound","UI_BUTTON_CLICK", SoundCategory.PLAYERS);
            }
        } else if (container.has(PersistentKeys.cosmic_picked_crate.getNamespacedKey())) {
            // Get item builder.
            ItemBuilder builder = cosmicCrateManager.getMysteryCrate().setAmount(pickedSlot)
                    .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                    .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

            // Overwrite the current item.
            event.setCurrentItem(builder.build());

            // Remove slot if we click it.
            cosmicCrateManager.removePickedPrize(player, slot);

            // Play a sound to indicate they clicked a chest.
            cosmic.getCrate().playSound(player, player.getLocation(), "click-sound","UI_BUTTON_CLICK", SoundCategory.PLAYERS);
        }

        // Get the crate name.
        String crateName = crate.getName();

        // Check picked prizes size to total prizes allowed, so we know when to take the key.
        int size = cosmicCrateManager.getPickedPrizes(player).size();

        if (size >= totalPrizes) {
            KeyType type = this.crateManager.getPlayerKeyType(player);

            boolean value = type == KeyType.physical_key && !this.plugin.getUserManager().hasPhysicalKey(uuid, crateName, this.crateManager.getHand(player));

            // If they don't have enough keys.
            if (value) {
                // Send no keys message.
                player.sendMessage(Messages.no_keys.getString());

                // Remove opening stuff.
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                // Remove the player from the hashmap.
                cosmicCrateManager.removePickedPlayer(player);

                // Close inventory.
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                // Return.
                return;
            }

            boolean hasKey = this.crateManager.hasPlayerKeyType(player) && !this.plugin.getUserManager().takeKeys(1, uuid, crateName, type, this.crateManager.getHand(player));

            if (hasKey) {
                // Notify player/console.
                MiscUtils.failedToTakeKey(player, crate);

                // Remove opening stuff.
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                // Remove the player from the hashmap.
                cosmicCrateManager.removePickedPlayer(player);

                // Close inventory.
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                return;
            }

            // Get new name.
            String shufflingName = crate.getFile().getString("Crate.CrateName") + " - Shuffling";

            // Update the cosmic name.
            cosmic.title(shufflingName);

            // Set the new title.
            view.setTitle(MsgUtils.color(shufflingName));

            // Clear the top inventory.
            view.getTopInventory().clear();

            this.crateManager.addRepeatingCrateTask(player, new TimerTask() {
                int time = 0;

                @Override
                public void run() {
                    try {
                        startRollingAnimation(player, view, cosmic);
                    } catch (Exception exception) {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            // Call the event.
                            PlayerReceiveKeyEvent keyEvent = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                            plugin.getServer().getPluginManager().callEvent(keyEvent);

                            // Check if event is cancelled.
                            if (!event.isCancelled()) {
                                // Add the keys
                                plugin.getUserManager().addKeys(1, uuid, crateName, type);

                                // Remove opening stuff.
                                crateManager.removePlayerFromOpeningList(player);
                                crateManager.removePlayerKeyType(player);

                                // Cancel crate task.
                                crateManager.removeCrateTask(player);

                                // Remove hand checks.
                                crateManager.removeHands(player);

                                // Remove the player from the hashmap.
                                cosmicCrateManager.removePickedPlayer(player);

                                // Send refund notices.
                                player.sendMessage(MsgUtils.getPrefix("&cAn issue has occurred and so a key refund was given."));
                                plugin.getServer().getLogger().log(Level.SEVERE, "An issue occurred when the user " + player.getName() + " was using the " + crate.getName() + " crate and so they were issued a key refund.", exception);

                                // Play a sound
                                crate.playSound(player, player.getLocation(), "stop-sound", "BLOCK_ANVIL_PLACE", SoundCategory.PLAYERS);
                            }
                        });

                        // Cancel the task.
                        cancel();

                        // Wrap it all up.
                        return;
                    }

                    // Increment the count?
                    time++;

                    if (time == 40) {
                        // End the crate and task before as we start a new task.
                        crateManager.removeCrateTask(player);

                        // Show their rewards after the animation is done.
                        showRewards(player, view, cosmic, cosmicCrateManager);

                        // Play a sound
                        crate.playSound(player, player.getLocation(), "stop-sound", "BLOCK_ANVIL_PLACE", SoundCategory.PLAYERS);

                        // Cancel the task.
                        cancel();
                    }
                }
            }, 0L, 80L);
        }
    }

    private void startRollingAnimation(Player player, InventoryView view, CratePrizeMenu cosmic) {
        for (int slot = 0; slot < cosmic.getSize(); slot++) {
            Tier tier = pickTier(cosmic.getCrate());

            if (tier != null) view.getTopInventory().setItem(slot, tier.getTierItem());
        }

        cosmic.getCrate().playSound(player, player.getLocation(), "cycle-sound", "BLOCK_NOTE_BLOCK_XYLOPHONE", SoundCategory.PLAYERS);
        player.updateInventory();
    }

    private void showRewards(Player player, InventoryView view, CratePrizeMenu cosmic, CosmicCrateManager crateManager) {
        String rewardsName = cosmic.getCrate().getFile().getString("Crate.CrateName") + " - Prizes";

        cosmic.title(rewardsName);

        view.setTitle(MsgUtils.color(rewardsName));

        view.getTopInventory().clear();

        Tier tier = pickTier(cosmic.getCrate());

        if (tier != null) {
            crateManager.getPickedPrizes(player).forEach(slot -> view.setItem(slot, tier.getTierItem()));
            player.updateInventory();

            if (this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(ConfigKeys.cosmic_crate_timeout)) {
                this.plugin.getCrateManager().addCrateTask(player, new TimerTask() {
                    @Override
                    public void run() {
                        // Close inventory.
                        plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory(InventoryCloseEvent.Reason.UNLOADED));

                        // Log it
                        if (plugin.isLogging()) {
                            List.of(
                                    player.getName() + " spent 10 seconds staring at a gui instead of collecting their prizes",
                                    "The task has been cancelled, They have been given their prizes and the gui is closed."
                            ).forEach(plugin.getLogger()::info);
                        }
                    }
                }, 10000L);
            }
        }
    }

    private Tier getTier(Crate crate, ItemStack item) {
        for (Tier tier : crate.getTiers()) {
            if (tier.getTierItem().isSimilar(item)) return tier;
        }

        return null;
    }

    private Tier pickTier(Crate crate) {
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
}