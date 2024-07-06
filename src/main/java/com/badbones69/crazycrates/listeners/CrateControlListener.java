package com.badbones69.crazycrates.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.events.KeyCheckEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.HashMap;
import java.util.Map;

public class CrateControlListener implements Listener {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private @NotNull final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    @EventHandler
    public void onLeftClickCrate(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        if (crateLocation == null) return;

        final boolean isKey = event.getHand() == EquipmentSlot.OFF_HAND ? this.crateManager.isKey(player.getInventory().getItemInOffHand()) : this.crateManager.isKey(player.getInventory().getItemInMainHand());

        if (isKey) {
            event.setCancelled(true);

            player.updateInventory();

            return;
        }

        event.setCancelled(true);

        if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
            if (crateLocation.getLocation().equals(clickedBlock.getLocation())) {
                this.crateManager.removeCrateLocation(crateLocation.getID());

                player.sendRichMessage(Messages.removed_physical_crate.getMessage(player, "{id}", crateLocation.getID()));
            }

            return;
        }

        if (crateLocation.getCrateType() == CrateType.menu) return;

        final Crate crate = crateLocation.getCrate();

        if (crate.isPreviewEnabled()) {
            this.inventoryManager.addViewer(player);
            this.inventoryManager.openNewCratePreview(player, crateLocation.getCrate());
        } else {
            player.sendRichMessage(Messages.preview_disabled.getMessage(player, "{crate}", crate.getName()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        // If location is null, return.
        if (crateLocation == null) return;

        final Crate crate = crateLocation.getCrate();

        event.setCancelled(true);

        if (crate.getCrateType() == CrateType.menu) {
            // this is to stop players in QuadCrate to not be able to try and open a crate set to menu.
            if (!this.crateManager.isInOpeningList(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                final CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_size));

                player.openInventory(crateMainMenu.build().getInventory());
            } else {
                player.sendRichMessage(Messages.feature_disabled.getMessage(player));
            }
        }
    }
    
    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        // If location is null, return.
        if (crateLocation == null) return;

        final Crate crate = crateLocation.getCrate();

        if (crate.getCrateType() == CrateType.menu) return;

        final boolean isKey = event.getHand() == EquipmentSlot.OFF_HAND ? ItemUtils.isSimilar(player.getInventory().getItemInOffHand(), crate) : ItemUtils.isSimilar(player.getInventory().getItemInMainHand(), crate);

        if (isKey) {
            event.setCancelled(true);
            player.updateInventory();
        }

        event.setCancelled(true);

        final KeyCheckEvent keyCheckEvent = new KeyCheckEvent(player, crateLocation);
        player.getServer().getPluginManager().callEvent(keyCheckEvent);

        if (keyCheckEvent.isCancelled()) return;

        boolean hasKey = false;
        boolean isPhysical = false;
        boolean useQuickCrateAgain = false;

        final String keyName = crate.getKeyName();

        final int requiredKeys = crate.getRequiredKeys();

        final int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

        if (requiredKeys > 0 && totalKeys < requiredKeys) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{key_amount}", String.valueOf(requiredKeys));
            placeholders.put("{crate}", crate.getPreviewName());
            placeholders.put("{amount}", String.valueOf(totalKeys));

            player.sendRichMessage(Messages.required_keys.getMessage(player, placeholders));

            return;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (crate.getCrateType() != CrateType.crate_on_the_go && isKey && ItemUtils.isSimilar(itemStack, crate) && this.config.getProperty(ConfigKeys.physical_accepts_physical_keys)) {
            hasKey = true;
            isPhysical = true;
        }

        if (this.config.getProperty(ConfigKeys.physical_accepts_virtual_keys) && this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) hasKey = true;

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{key}", keyName);

        if (hasKey) {
            // Checks if the player uses the quick crate again.
            if (this.crateManager.isInOpeningList(player) && this.crateManager.getOpeningCrate(player).getCrateType() == CrateType.quick_crate && this.crateManager.isCrateInUse(player) && this.crateManager.getCrateInUseLocation(player).equals(crateLocation.getLocation())) {
                useQuickCrateAgain = true;
            }

            if (!useQuickCrateAgain) {
                if (this.crateManager.isInOpeningList(player)) {
                    player.sendRichMessage(Messages.already_opening_crate.getMessage(player, "{crate}", crate.getName()));

                    return;
                }

                if (this.crateManager.getCratesInUse().containsValue(crateLocation.getLocation())) {
                    player.sendRichMessage(Messages.crate_in_use.getMessage(player, "{crate}", crate.getName()));

                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                player.sendRichMessage(Messages.inventory_not_empty.getMessage(player, "{crate}", crate.getName()));

                return;
            }

            if (useQuickCrateAgain) this.crateManager.endQuickCrate(player, crateLocation.getLocation(), crate, true);

            final KeyType keyType = isPhysical ? KeyType.physical_key : KeyType.virtual_key;

            // Only cosmic crate type uses this method.
            if (crate.getCrateType() == CrateType.cosmic) this.crateManager.addPlayerKeyType(player, keyType);

            this.crateManager.addPlayerToOpeningList(player, crate);

            this.crateManager.openCrate(player, crate, keyType, crateLocation.getLocation(), false, true);

            return;
        }

        if (crate.getCrateType() != CrateType.crate_on_the_go) {
            if (this.config.getProperty(ConfigKeys.knock_back)) knockBack(player, clickedBlock.getLocation());

            //todo() convert this to a bean property!
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                net.kyori.adventure.sound.Sound sound = net.kyori.adventure.sound.Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

                player.playSound(sound);
            }

            player.sendRichMessage(Messages.no_keys.getMessage(player, placeholders));
        }
    }

    @EventHandler
    public void onPistonPushCrate(BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            final Location location = block.getLocation();

            final Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPistonPullCrate(BlockPistonRetractEvent event) {
        for (final Block block : event.getBlocks()) {
            final Location location = block.getLocation();

            final Crate crate = this.crateManager.getCrateFromLocation(location);

            if (crate != null) {
                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (this.crateManager.hasCrateTask(player)) this.crateManager.endCrate(player);

        if (this.crateManager.hasQuadCrateTask(player)) this.crateManager.endQuadCrate(player);

        if (this.crateManager.isInOpeningList(player)) this.crateManager.removePlayerFromOpeningList(player);
    }
    
    private void knockBack(final Player player, final Location location) {
        final Vector vector = player.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1).setY(.1);

        if (player.isInsideVehicle() && player.getVehicle() != null) {
            player.getVehicle().setVelocity(vector);

            return;
        }

        player.setVelocity(vector);
    }
}