package com.badbones69.crazycrates.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.events.KeyCheckEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.HashMap;
import java.util.Map;

public class CrateControlListener implements Listener {

    @NotNull
    private final CrazyCratesPaper plugin = CrazyCratesPaper.get();

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final UserManager userManager = this.plugin.getUserManager();

    @EventHandler
    public void onLeftClickCrate(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        boolean isKey = event.getHand() == EquipmentSlot.OFF_HAND ? this.crateManager.isKey(player.getInventory().getItemInOffHand()) : this.crateManager.isKey(player.getInventory().getItemInMainHand());

        if (isKey) {
            event.setCancelled(true);

            player.updateInventory();

            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        if (crateLocation == null) return;

        event.setCancelled(true);

        if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
            if (crateLocation.getLocation().equals(clickedBlock.getLocation())) {
                this.crateManager.removeCrateLocation(crateLocation.getID());

                player.sendMessage(Messages.removed_physical_crate.getMessage("{id}", crateLocation.getID(), player));
            }

            return;
        }

        if (crateLocation.getCrateType() == CrateType.menu) return;

        Crate crate = crateLocation.getCrate();

        if (crate.isPreviewEnabled()) {
            this.inventoryManager.addViewer(player);
            this.inventoryManager.openNewCratePreview(player, crateLocation.getCrate());
        } else {
            player.sendMessage(Messages.preview_disabled.getMessage("{crate}", crate.getName(), player));
        }
    }
    
    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        // If location is null, return.
        if (crateLocation == null) return;

        Crate crate = crateLocation.getCrate();

        // If crate is null, return.
        if (crate == null) return;

        boolean isKey = event.getHand() == EquipmentSlot.OFF_HAND ? ItemUtils.isSimilar(player.getInventory().getItemInOffHand(), crate) : ItemUtils.isSimilar(player.getInventory().getItemInMainHand(), crate);

        if (isKey) {
            event.setCancelled(true);
            player.updateInventory();
        }

        event.setCancelled(true);

        if (crate.getCrateType() == CrateType.menu) {
            // this is to stop players in QuadCrate to not be able to try and open a crate set to menu.
            if (!this.crateManager.isInOpeningList(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_size), this.config.getProperty(ConfigKeys.inventory_name));

                player.openInventory(crateMainMenu.build().getInventory());
            } else {
                player.sendMessage(Messages.feature_disabled.getMessage(player));
            }

            return;
        }

        KeyCheckEvent keyCheckEvent = new KeyCheckEvent(player, crateLocation);
        player.getServer().getPluginManager().callEvent(keyCheckEvent);

        if (keyCheckEvent.isCancelled()) return;

        boolean hasKey = false;
        boolean isPhysical = false;
        boolean useQuickCrateAgain = false;

        String keyName = crate.getKeyName();

        int requiredKeys = this.crateManager.getCrateFromName(crate.getName()).getRequiredKeys();

        int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

        if (requiredKeys > 0 && totalKeys < requiredKeys) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{key_amount}", String.valueOf(requiredKeys));
            placeholders.put("{crate}", crate.getPreviewName());
            placeholders.put("{amount}", String.valueOf(totalKeys));

            player.sendMessage(Messages.required_keys.getMessage(placeholders, player));

            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (crate.getCrateType() != CrateType.crate_on_the_go && isKey && ItemUtils.isSimilar(itemStack, crate) && this.config.getProperty(ConfigKeys.physical_accepts_physical_keys)) {
            hasKey = true;
            isPhysical = true;
        }

        if (this.config.getProperty(ConfigKeys.physical_accepts_virtual_keys) && this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) hasKey = true;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{crate}", crate.getName());
        placeholders.put("{key}", keyName);

        if (hasKey) {
            // Checks if the player uses the quick crate again.
            if (this.crateManager.isInOpeningList(player) && this.crateManager.getOpeningCrate(player).getCrateType() == CrateType.quick_crate && this.crateManager.isCrateInUse(player) && this.crateManager.getCrateInUseLocation(player).equals(crateLocation.getLocation())) {
                useQuickCrateAgain = true;
            }

            if (!useQuickCrateAgain) {
                if (this.crateManager.isInOpeningList(player)) {
                    player.sendMessage(Messages.already_opening_crate.getMessage("{crate}", crate.getName(), player));

                    return;
                }

                if (this.crateManager.getCratesInUse().containsValue(crateLocation.getLocation())) {
                    player.sendMessage(Messages.crate_in_use.getMessage("{crate}", crate.getName(), player));

                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                player.sendMessage(Messages.inventory_not_empty.getMessage("{crate}", crate.getName(), player));

                return;
            }

            if (useQuickCrateAgain) this.crateManager.endQuickCrate(player, crateLocation.getLocation(), crate, true);

            KeyType keyType = isPhysical ? KeyType.physical_key : KeyType.virtual_key;

            // Only cosmic crate type uses this method.
            if (crate.getCrateType() == CrateType.cosmic) this.crateManager.addPlayerKeyType(player, keyType);

            this.crateManager.addPlayerToOpeningList(player, crate);

            this.crateManager.openCrate(player, crate, keyType, crateLocation.getLocation(), false, true);

            return;
        }

        if (crate.getCrateType() != CrateType.crate_on_the_go) {
            if (this.config.getProperty(ConfigKeys.knock_back)) knockBack(player, clickedBlock.getLocation());

            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
            }

            player.sendMessage(Messages.no_keys.getMessage(placeholders, player));
        }
    }

    @EventHandler
    public void onPistonPushCrate(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Location location = block.getLocation();

            Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonPullCrate(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Location location = block.getLocation();

            Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

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