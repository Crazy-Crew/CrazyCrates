package com.badbones69.crazycrates.paper.listeners.crates.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.ryderbelserion.fusion.core.api.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.sound.Sound;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

public class CosmicCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler
    public void onPrizeReceive(InventoryClickEvent event) {
        // Get the inventory.
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu holder)) return;

        final Player player = holder.getPlayer();

        // Cancel event.
        event.setCancelled(true);

        // Get opening crate.
        final Crate crate = this.crateManager.getOpeningCrate(player);

        if (crate == null) return;

        // Check if player is in the opening list.
        if (!this.crateManager.isInOpeningList(player) || crate.getCrateType() != CrateType.cosmic) return;

        // Check the title.
        if (!holder.contains(" - Prizes")) return;

        // Get the raw slot.
        final int slot = event.getRawSlot();

        if (this.crateManager.containsSlot(player) && this.crateManager.getSlots(player).contains(slot)) {
            Messages.already_redeemed_prize.sendMessage(player);

            return;
        }

        // Get inventory view.
        final InventoryView view = event.getView();

        // Check if clicking top inventory or not.
        final Inventory topInventory = view.getTopInventory();

        if (event.getClickedInventory() != topInventory) return;

        // Get item stack of clicked item.
        final ItemStack itemStack = topInventory.getItem(slot);

        // Check if null or air.
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        final Tier tier = this.crateManager.getTier(player, slot);

        // If tier is null, return
        if (tier == null) return;

        Prize prize = crate.pickPrize(player, tier);

        for (int stop = 0; prize == null && stop <= 2000; stop++) { //todo() wtf?
            prize = crate.pickPrize(player, tier);
        }

        if (prize == null) return;

        PrizeManager.givePrize(player, crate, prize);

        event.setCurrentItem(prize.getDisplayItem(player, crate));

        crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.MASTER);

        this.crateManager.addSlot(player, slot);
    }

    @EventHandler
    public void onMysteryBoxClick(InventoryClickEvent event) {
        // Get the inventory.
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu holder)) return;

        final Player player = holder.getPlayer();
        final UUID uuid = player.getUniqueId();

        // Cancel event.
        event.setCancelled(true);

        // Get opening crate.
        final Crate crate = this.crateManager.getOpeningCrate(player);

        if (crate == null) return;

        // Check if player is in the opening list.
        if (!this.crateManager.isInOpeningList(player) || crate.getCrateType() != CrateType.cosmic) return;

        // Check the title.
        if (!holder.contains(" - Choose")) return;

        // Get the raw slot.
        final int slot = event.getRawSlot();

        // Get inventory view.
        final InventoryView view = event.getView();

        // Check if clicking top inventory or not.
        final Inventory topInventory = view.getTopInventory();

        if (event.getClickedInventory() != topInventory) return;

        // Get item stack of clicked item.
        final ItemStack itemStack = topInventory.getItem(slot);

        // Check if null or air.
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        // Get crate manager.
        final CosmicCrateManager cosmicCrateManager = (CosmicCrateManager) crate.getManager();

        // Get total prizes.
        final int totalPrizes = cosmicCrateManager.getTotalPrizes();

        // Get picked slot.
        final int pickedSlot = slot+1;

        // Get the pdc container.
        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        // Check if it has the mystery crate key otherwise check picked key.
        if (container.has(ItemKeys.cosmic_mystery_crate.getNamespacedKey())) {
            int size = cosmicCrateManager.getPrizes(player).size();

            // Check if prizes is less than or equal to totalPrizes before we change any items.
            if (size < totalPrizes) {
                final Tier tier = this.crateManager.getTier(player, slot);

                if (tier == null) {
                    return;
                }

                // Gets the tier name.
                final String tierName = tier.getName();

                // Get item builder.
                LegacyItemBuilder builder = cosmicCrateManager.getPickedCrate().setPlayer(player)
                        .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                        .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

                // Set the amount.
                builder.setAmount(pickedSlot);

                // Set the tier name from before to the pdc if it exists.
                cosmicCrateManager.setTier(builder, tierName);

                // Overwrite the current item.
                event.setCurrentItem(builder.asItemStack());

                // Add the picked prize.
                cosmicCrateManager.addPickedPrize(player, slot, tier);

                // Play a sound to indicate they clicked a chest.
                crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.MASTER);
            }
        } else if (container.has(ItemKeys.cosmic_picked_crate.getNamespacedKey())) {
            final Tier tier = this.crateManager.getTier(player, slot);

            // Get item builder.
            LegacyItemBuilder builder = cosmicCrateManager.getMysteryCrate().setPlayer(player)
                    .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                    .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

            // Set the amount.
            builder.setAmount(pickedSlot);

            // Gets the tier name.
            final String tierName = tier.getName();

            // Set the tier name from before to the pdc if it exists.
            cosmicCrateManager.setTier(builder, tierName);

            // Overwrite the current item.
            event.setCurrentItem(builder.asItemStack());

            // Remove slot if we click it.
            cosmicCrateManager.removePickedPrize(player, slot);

            // Play a sound to indicate they clicked a chest.
            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.MASTER);
        }

        // Get the crate name.
        final String fileName = crate.getFileName();
        final String fancyName = crate.getCrateName();

        // Check picked prizes size to total prizes allowed, so we know when to take the key.
        final int size = cosmicCrateManager.getPrizes(player).size();

        if (size >= totalPrizes) {
            final KeyType playerType = this.crateManager.getPlayerKeyType(player);

            final KeyType type = playerType == null ? KeyType.virtual_key : playerType;

            final boolean value = type == KeyType.physical_key && !this.userManager.hasPhysicalKey(uuid, fileName, this.crateManager.getHand(player));

            // If they don't have enough keys.
            if (value) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("{crate}", fancyName);
                placeholders.put("{key}", crate.getKeyName());

                // Send no keys message.
                Messages.no_keys.sendMessage(player, placeholders);

                // Remove opening stuff.
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                this.crateManager.removeTier(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                // Remove the player from the hashmap.
                cosmicCrateManager.removePickedPlayer(player);

                // Close inventory.
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                // Return.
                return;
            }

            final boolean cannotTakeKey = this.crateManager.hasPlayerKeyType(player) && !this.userManager.takeKeys(uuid, fileName, type, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, this.crateManager.getHand(player));

            if (cannotTakeKey) {
                // Notify player/console.
                MiscUtils.failedToTakeKey(player, fileName); //todo() change this

                // Remove opening stuff.
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                this.crateManager.removeTier(player);

                // Remove hand checks.
                this.crateManager.removeHands(player);

                // Remove the player from the hashmap.
                cosmicCrateManager.removePickedPlayer(player);

                // Close inventory.
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                return;
            }

            // Get new name.
            final String shufflingName = fancyName + " - Shuffling";

            // Update the cosmic name.
            holder.title(shufflingName);
            holder.sendTitleChange();

            // Clear the top inventory.
            view.getTopInventory().clear();

            YamlConfiguration configuration = crate.getFile();

            final String broadcastMessage = configuration.getString("Crate.BroadCast", "");
            final boolean broadcastToggle = configuration.getBoolean("Crate.OpeningBroadCast", false);

            if (broadcastToggle) {  //todo() add a permission?
                if (!broadcastMessage.isBlank()) {
                    String builder = Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, broadcastMessage) : broadcastMessage;

                    this.server.broadcast(AdvUtils.parse(builder.replaceAll("%crate%", fancyName)
                            .replaceAll("%prefix%", this.config.getProperty(ConfigKeys.command_prefix))
                            .replaceAll("%player%", player.getName())));
                }
            }

            EventManager.logEvent(EventType.event_crate_opened, player.getName(), player, crate, type, 1);

            this.crateManager.addRepeatingCrateTask(player, new TimerTask() {
                int time = 0;

                @Override
                public void run() {
                    try {
                        startRollingAnimation(player, view, holder);
                    } catch (final Exception exception) {
                        new FoliaScheduler(plugin, null, player) {
                            @Override
                            public void run() {
                                PlayerReceiveKeyEvent keyEvent = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);

                                pluginManager.callEvent(keyEvent);

                                // Check if event is cancelled.
                                if (!event.isCancelled()) {
                                    // Add the keys
                                    userManager.addKeys(uuid, fileName, type, 1);

                                    // Remove opening stuff.
                                    crateManager.removePlayerFromOpeningList(player);
                                    crateManager.removePlayerKeyType(player);

                                    crateManager.removeTier(player);

                                    // Cancel crate task.
                                    crateManager.removeCrateTask(player);

                                    // Remove hand checks.
                                    crateManager.removeHands(player);

                                    // Remove the player from the hashmap.
                                    cosmicCrateManager.removePickedPlayer(player);

                                    Messages.key_refund.sendMessage(player, "{crate}", fancyName);

                                    if (MiscUtils.isLogging()) logger.error("An issue occurred when the user {} was using the {} crate and so they were issued a key refund.", player.getName(), fileName, exception);

                                    // Play a sound
                                    crate.playSound(player, player.getLocation(), "stop-sound", "block.anvil.place", Sound.Source.MASTER);
                                }
                            }
                        }.runNow();

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
                        showRewards(player, view, holder, cosmicCrateManager);

                        // Play a sound
                        crate.playSound(player, player.getLocation(), "stop-sound", "block.anvil.place", Sound.Source.MASTER);

                        // Cancel the task.
                        cancel();
                    }
                }
            }, 0L, 80L);
        }
    }

    private void startRollingAnimation(@NotNull final Player player, @NotNull final InventoryView view, @NotNull final CratePrizeMenu cosmic) {
        final Crate crate = cosmic.getCrate();

        for (int slot = 0; slot < cosmic.getSize(); slot++) {
            final Tier tier = PrizeManager.getTier(crate);

            if (tier != null) view.getTopInventory().setItem(slot, tier.getTierItem(player, crate));
        }

        crate.playSound(player, player.getLocation(), "cycle-sound", "block.note_block.xylophone", Sound.Source.MASTER);

        player.updateInventory();
    }

    private void showRewards(@NotNull final Player player, @NotNull final InventoryView view, @NotNull final CratePrizeMenu cosmic, @NotNull final CosmicCrateManager cosmicCrateManager) {
        final Crate crate = cosmic.getCrate();

        final String rewardsName = crate.getCrateName() + " - Prizes";

        cosmic.title(rewardsName);
        cosmic.sendTitleChange();

        view.getTopInventory().clear();

        cosmicCrateManager.getPrizes(player).forEach((slot, tier) -> view.getTopInventory().setItem(slot, tier.getTierItem(player, crate)));

        player.updateInventory();

        if (ConfigManager.getConfig().getProperty(ConfigKeys.cosmic_crate_timeout)) {
            this.crateManager.addCrateTask(player, new TimerTask() {
                @Override
                public void run() {
                    new FoliaScheduler(plugin, null, player) {
                        @Override
                        public void run() {
                            player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
                        }
                    }.runNow();

                    if (MiscUtils.isLogging()) {
                        List.of(
                                player.getName() + " spent 10 seconds staring at a gui instead of collecting their prizes",
                                "The task has been cancelled, They have been given their prizes and the gui is closed."
                        ).forEach(logger::info);
                    }
                }
            }, 10000L);
        }
    }
}