package us.crazycrew.crazycrates.paper.listeners.crates;

import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.builders.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKeys;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import us.crazycrew.crazycrates.paper.other.MsgUtils;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;

public class CosmicCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onSlotPicks(InventoryClickEvent event) {
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
        if (container.has(PersistentKeys.cosmic_mystery_crate.getNamespacedKey(this.plugin))) {
            int size = cosmicCrateManager.getPickedPrizes(player).size();

            // Check if prizes is greater than or equal to totalPrizes before we change any items.
            if (size < totalPrizes) {
                // Get item builder.
                ItemBuilder builder = cosmicCrateManager.getPickedCrate().setAmount(pickedSlot)
                        .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                        .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

                // Overwrite the current item.
                event.setCurrentItem(builder.build());

                cosmicCrateManager.addPickedPrize(player, slot);

                // Play a sound to indicate they clicked a chest.
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);

                return;
            }
        } else if (container.has(PersistentKeys.cosmic_picked_crate.getNamespacedKey(this.plugin))) {
            // Get item builder.
            ItemBuilder builder = cosmicCrateManager.getMysteryCrate().setAmount(pickedSlot)
                    .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                    .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot));

            // Overwrite the current item.
            event.setCurrentItem(builder.build());

            // Remove slot if we click it.
            cosmicCrateManager.removePickedPrize(player, slot);

            // Play a sound to indicate they clicked a chest.
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
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
                // Send no keys message
                player.sendMessage(Translation.no_keys.getString());

                // Check if in opening list and remove from opening lists
                if (this.crateManager.isInOpeningList(player)) {
                    this.crateManager.removePlayerFromOpeningList(player);
                    this.crateManager.removePlayerKeyType(player);
                }

                // Remove the player from the hashmap.
                cosmicCrateManager.removePickedPlayer(player);

                // Close inventory.
                player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                // Return
                return;
            }

            boolean hasKey = this.crateManager.hasPlayerKeyType(player) && !this.plugin.getUserManager().takeKeys(1, uuid, crateName, type, this.crateManager.getHand(player));

            if (hasKey) {
                MiscUtils.failedToTakeKey(player, crate);

                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);
                this.crateManager.removeHands(player);

                cosmicCrateManager.removePickedPlayer(player);

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
                        PlayerReceiveKeyEvent keyEvent = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.REFUND, 1);
                        // Call the event.
                        plugin.getServer().getPluginManager().callEvent(keyEvent);

                        // Check if event is cancelled.
                        if (!event.isCancelled()) {
                            // Add the keys
                            plugin.getUserManager().addKeys(1, uuid, crateName, type);

                            // End the crate and task.
                            crateManager.removeCrateTask(player);

                            player.sendMessage(MsgUtils.getPrefix("&cAn issue has occurred and so a key refund was given."));
                            plugin.getServer().getLogger().log(Level.SEVERE, "An issue occurred when the user " + player.getName() + " was using the " + crate.getName() + " crate and so they were issued a key refund.", exception);
                        }

                        // Wrap it all up.
                        return;
                    }

                    // Increment the time?
                    time++;

                    if (time == 40) {
                        // End the crate and task.
                        crateManager.removeCrateTask(player);

                        // Show their rewards after the animation is done.
                        //TODO() Not done yet.

                        // Play a sound
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1F, 1F);
                    }
                }
            }, 0L, 2L);
        }
    }

    private void startRollingAnimation(Player player, Crate crate, InventoryView view, CratePrizeMenu cosmic) {
        String shufflingName = crate.getFile().getString("Crate.CrateName") + " - Shuffling";

        // Update the cosmic name.
        cosmic.title(shufflingName);

        // Set the new title.
        view.setTitle(shufflingName);

        // Clear the top inventory.
        view.getTopInventory().clear();
    }
}