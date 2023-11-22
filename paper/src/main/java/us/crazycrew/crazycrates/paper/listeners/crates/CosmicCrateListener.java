package us.crazycrew.crazycrates.paper.listeners.crates;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.builders.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKeys;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import java.util.Timer;
import java.util.TimerTask;

public class CosmicCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onSlotPicks(InventoryClickEvent event) {
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

        // Check if null or air
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        // Get crate manager.
        CosmicCrateManager cosmicCrateManager = (CosmicCrateManager) crate.getManager();

        // Get total prizes.
        int totalPrizes = cosmicCrateManager.getTotalPrizes();

        // Get picked slot.
        int pickedSlot = slot+1;

        // Get clicked item's item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Get PDC
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

            // Play the no sound.
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.NEUTRAL, 1F, 1F);

            // Get item builder.
            ItemStack builder = cosmicCrateManager.getMysteryCrate().setAmount(pickedSlot)
                    .addNamePlaceholder("%Slot%", String.valueOf(pickedSlot))
                    .addLorePlaceholder("%Slot%", String.valueOf(pickedSlot)).build();

            ItemStack other = cosmicCrateManager.getAlreadyPicked().build();

            // If current item is the same type as the BARRIER block, we do nothing as the task is already running.
            if (itemStack.getType() == other.getType()) return;

            // Set to the barrier block.
            event.setCurrentItem(other);

            // Run the timer.
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // Set old item.
                    event.setCurrentItem(builder);
                }
            };

            // Runs the task 3 seconds later.
            timer.schedule(task, 3000);
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

        // Check picked prizes size to total prizes allowed, so we know when to take the key.
    }

    /*@EventHandler
    public void onPrizeChoose(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu crateCosmicMenu)) {
            return;
        }

        event.setCancelled(true);

        Crate crate = this.crateManager.getOpeningCrate(player);

        if (this.crateManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            }

            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        if (!crateCosmicMenu.contains(" - Choose")) return;

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



                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (player.getOpenInventory().getTopInventory().equals(inventory))
                                            player.closeInventory();
                                    }
                                }.runTaskLater(plugin, 40);
                            }
                        }
                    }.runTaskTimer(plugin, 0, 2));
                }
            }
        }
    }

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
            if (crate.getFile() == null) {
                return;
            }

            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
        } else {
            return;
        }

        if (!crateCosmicMenu.contains(" - Prizes")) return;

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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getPlayer();

        Crate crate = this.crateManager.getOpeningCrate(player);

        if (this.crateManager.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            }

            if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
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
     */
}