package com.badbones69.crazycrates.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.events.KeyCheckEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.HashMap;

public class CrateControlListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getCrazyHandler().getInventoryManager();

    @NotNull
    private final SettingsManager config = this.plugin.getConfigManager().getConfig();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();
    
    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrateOpen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            if (this.plugin.getCrateManager().isKey(player.getInventory().getItemInOffHand())) {
                e.setCancelled(true);
                player.updateInventory();
            }

            return;
        }

        Block clickedBlock = e.getClickedBlock();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Loops through all loaded physical locations.
            for (CrateLocation loc : this.plugin.getCrateManager().getCrateLocations()) {
                // Checks to see if the clicked block is the same as a physical crate.
                //todo() if someone complains, remove null check.
                if (clickedBlock != null && loc.getLocation().equals(clickedBlock.getLocation())) {
                    // Checks to see if the player is removing a crate location.
                    if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
                        e.setCancelled(true);
                        this.plugin.getCrateManager().removeCrateLocation(loc.getID());
                        player.sendMessage(Messages.removed_physical_crate.getMessage("%id%", loc.getID(), player));
                        return;
                    }

                    e.setCancelled(true);

                    if (loc.getCrateType() != CrateType.menu) {
                        Crate crate = loc.getCrate();

                        if (loc.getCrate().isPreviewEnabled()) {
                            this.inventoryManager.addViewer(player);
                            this.inventoryManager.openNewCratePreview(player, loc.getCrate(), crate.getCrateType() == CrateType.cosmic || crate.getCrateType() == CrateType.casino);
                        } else {
                            player.sendMessage(Messages.preview_disabled.getMessage(player));
                        }
                    }
                }
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Checks if the item in their hand is a key and if so it stops them from right-clicking with it.
            ItemStack key = player.getInventory().getItemInMainHand();
            boolean keyInHand = this.crateManager.isKey(key);

            if (!keyInHand) keyInHand = this.crateManager.isKey(player.getEquipment().getItemInOffHand());

            if (keyInHand) {
                e.setCancelled(true);
                player.updateInventory();
            }

            //Checks to see if the clicked block is a physical crate.
            CrateLocation crateLocation = null;

            //todo() if someone complains, remove null check.
            if (clickedBlock != null) {
                crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());
            }

            if (crateLocation != null && crateLocation.getCrate() != null) {
                Crate crate = crateLocation.getCrate();
                e.setCancelled(true);

                if (crate.getCrateType() == CrateType.menu) {
                    //This is to stop players in QuadCrate to not be able to try and open a crate set to menu.
                    if (!this.crateManager.isInOpeningList(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                        CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_size), this.config.getProperty(ConfigKeys.inventory_name));

                        player.openInventory(crateMainMenu.build().getInventory());
                    } else {
                        player.sendMessage(Messages.feature_disabled.getMessage(player));
                    }

                    return;
                }

                KeyCheckEvent event = new KeyCheckEvent(player, crateLocation);
                player.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    boolean hasKey = false;
                    boolean isPhysical = false;
                    boolean useQuickCrateAgain = false;
                    String keyName = crate.getKeyName();

                    int requiredKeys = this.plugin.getCrateManager().getCrateFromName(crate.getName()).getRequiredKeys();

                    int totalKeys = this.plugin.getCrazyHandler().getUserManager().getTotalKeys(player.getUniqueId(), crate.getName());

                    if (requiredKeys > 0 && totalKeys < requiredKeys) {
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%key-amount%", String.valueOf(requiredKeys));
                        placeholders.put("%crate%", crate.getPreviewName());
                        placeholders.put("%amount%", String.valueOf(totalKeys));

                        player.sendMessage(Messages.required_keys.getMessage(placeholders, player));
                        return;
                    }

                    if (crate.getCrateType() != CrateType.crate_on_the_go && keyInHand && this.crateManager.isKeyFromCrate(key, crate) && this.config.getProperty(ConfigKeys.physical_accepts_physical_keys)) {
                        hasKey = true;
                        isPhysical = true;
                    }

                    if (this.config.getProperty(ConfigKeys.physical_accepts_virtual_keys) && this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) hasKey = true;

                    if (hasKey) {
                        // Checks if the player uses the quick crate again.
                        if (this.crateManager.isInOpeningList(player) && this.crateManager.getOpeningCrate(player).getCrateType() == CrateType.quick_crate && this.crateManager.isCrateInUse(player) && this.crateManager.getCrateInUseLocation(player).equals(crateLocation.getLocation())) {
                            useQuickCrateAgain = true;
                        }

                        if (!useQuickCrateAgain) {
                            if (this.crateManager.isInOpeningList(player)) {
                                player.sendMessage(Messages.already_opening_crate.getMessage("%key%", keyName, player));
                                return;
                            }

                            if (this.crateManager.getCratesInUse().containsValue(crateLocation.getLocation())) {
                                player.sendMessage(Messages.quick_crate_in_use.getMessage(player));
                                return;
                            }
                        }

                        if (MiscUtils.isInventoryFull(player)) {
                            player.sendMessage(Messages.inventory_not_empty.getMessage(player));
                            return;
                        }

                        if (useQuickCrateAgain) this.plugin.getCrateManager().endQuickCrate(player, crateLocation.getLocation(), crate, true);

                        KeyType keyType = isPhysical ? KeyType.physical_key : KeyType.virtual_key;

                        // Only cosmic crate type uses this method.
                        if (crate.getCrateType() == CrateType.cosmic) this.crateManager.addPlayerKeyType(player, keyType);

                        this.crateManager.addPlayerToOpeningList(player, crate);

                        this.crateManager.openCrate(player, crate, keyType, crateLocation.getLocation(), false,true);
                    } else {
                        if (crate.getCrateType() != CrateType.crate_on_the_go) {
                            if (this.config.getProperty(ConfigKeys.knock_back)) knockBack(player, clickedBlock.getLocation());

                            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
                            }

                            player.sendMessage(Messages.no_keys.getMessage("%key%", keyName, player));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (this.crateManager.hasCrateTask(player)) this.crateManager.endCrate(player);

        if (this.crateManager.hasQuadCrateTask(player)) this.crateManager.endQuadCrate(player);

        if (this.crateManager.isInOpeningList(player)) this.crateManager.removePlayerFromOpeningList(player);
    }
    
    private void knockBack(Player player, Location location) {
        Vector vector = player.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1).setY(.1);

        if (player.isInsideVehicle() && player.getVehicle() != null) {
            player.getVehicle().setVelocity(vector);
            return;
        }

        player.setVelocity(vector);
    }
}