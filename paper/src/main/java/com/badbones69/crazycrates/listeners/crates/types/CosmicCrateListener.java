package com.badbones69.crazycrates.listeners.crates.types;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.sound.Sound;
import com.badbones69.crazycrates.managers.config.ConfigManager;
import com.badbones69.crazycrates.managers.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.utils.MsgUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;

public class CosmicCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

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

        for (int stop = 0; prize == null && stop <= 2000; stop++) {
            prize = crate.pickPrize(player, tier);
        }

        if (prize == null) return;

        PrizeManager.givePrize(player, prize, crate);

        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));

        event.setCurrentItem(prize.getDisplayItem(player, crate));

        holder.getCrate().playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);

        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

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
        if (container.has(Keys.cosmic_mystery_crate.getNamespacedKey())) {
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
                ItemBuilder builder = cosmicCrateManager.getPickedCrate().setPlayer(player)
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
                crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);
            }
        } else if (container.has(Keys.cosmic_picked_crate.getNamespacedKey())) {
            final Tier tier = this.crateManager.getTier(player, slot);

            // Get item builder.
            ItemBuilder builder = cosmicCrateManager.getMysteryCrate().setPlayer(player)
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
            crate.playSound(player, player.getLocation(), "click-sound","ui.button.click", Sound.Source.PLAYER);
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

            if (broadcastToggle) {
                if (!broadcastMessage.isBlank()) {
                    String builder = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, broadcastMessage) : broadcastMessage;

                    this.plugin.getServer().broadcast(AdvUtil.parse(builder.replaceAll("%crate%", fancyName).replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName())));
                }
            }

            this.crateManager.addRepeatingCrateTask(player, new TimerTask() {
                int time = 0;

                @Override
                public void run() {
                    try {
                        startRollingAnimation(player, view, holder);
                    } catch (Exception exception) {
                        player.getScheduler().run(plugin, scheduledTask -> {
                            PlayerReceiveKeyEvent keyEvent = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                            plugin.getServer().getPluginManager().callEvent(keyEvent);

                            // Check if event is cancelled.
                            if (!event.isCancelled()) {
                                // Add the keys
                                userManager.addKeys(uuid, fileName, type == null ? KeyType.virtual_key : type, 1);

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

                                if (MiscUtils.isLogging()) plugin.getLogger().log(Level.SEVERE, "An issue occurred when the user " + player.getName() + " was using the " + fileName + " crate and so they were issued a key refund.", exception);

                                // Play a sound
                                crate.playSound(player, player.getLocation(), "stop-sound", "block.anvil.place", Sound.Source.PLAYER);
                            }
                        }, null);

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
                        crate.playSound(player, player.getLocation(), "stop-sound", "block.anvil.place", Sound.Source.PLAYER);

                        // Cancel the task.
                        cancel();
                    }
                }
            }, 0L, 80L);
        }
    }

    private void startRollingAnimation(final Player player, final InventoryView view, final CratePrizeMenu cosmic) {
        for (int slot = 0; slot < cosmic.getSize(); slot++) {
            final Tier tier = PrizeManager.getTier(cosmic.getCrate());

            if (tier != null) view.getTopInventory().setItem(slot, tier.getTierItem(player));
        }

        cosmic.getCrate().playSound(player, player.getLocation(), "cycle-sound", "block.note_block.xylophone", Sound.Source.PLAYER);

        player.updateInventory();
    }

    private void showRewards(final Player player, final InventoryView view, final CratePrizeMenu cosmic, final CosmicCrateManager cosmicCrateManager) {
        final String rewardsName = cosmic.getCrate().getCrateName() + " - Prizes";

        cosmic.title(rewardsName);
        cosmic.sendTitleChange();

        view.getTopInventory().clear();

        cosmicCrateManager.getPrizes(player).forEach((slot, tier) -> {
            Inventory inventory = view.getTopInventory();

            inventory.setItem(slot, tier.getTierItem(player));
        });

        player.updateInventory();

        if (ConfigManager.getConfig().getProperty(ConfigKeys.cosmic_crate_timeout)) {
            this.crateManager.addCrateTask(player, new TimerTask() {
                @Override
                public void run() {
                    player.getScheduler().run(plugin, scheduledTask -> player.closeInventory(InventoryCloseEvent.Reason.UNLOADED), null);

                    if (MiscUtils.isLogging()) {
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