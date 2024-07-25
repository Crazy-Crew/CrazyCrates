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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
    public void onGroundClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.getAction().isRightClick()) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final boolean isKey = event.getHand() == EquipmentSlot.OFF_HAND ? this.crateManager.isKey(player.getInventory().getItemInOffHand()) : this.crateManager.isKey(player.getInventory().getItemInMainHand());

        if (isKey) {
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.getAction().isLeftClick()) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        // If location is null, return.
        if (crateLocation == null) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        final Crate crate = crateLocation.getCrate();

        if (player.getGameMode() == GameMode.CREATIVE && player.isSneaking() && player.hasPermission("crazycrates.admin")) {
            if (crateLocation.getLocation().equals(clickedBlock.getLocation())) {
                this.crateManager.removeCrateLocation(crateLocation.getID());

                Messages.removed_physical_crate.sendMessage(player, "{id}", crateLocation.getID());
            }

            return;
        }

        if (crate.getCrateType() == CrateType.menu) {
            // this is to stop players in QuadCrate to not be able to try and open a crate set to menu.
            if (!this.crateManager.isInOpeningList(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                final CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_name), this.config.getProperty(ConfigKeys.inventory_size));

                player.openInventory(crateMainMenu.build().getInventory());
            } else {
                Messages.feature_disabled.sendMessage(player);
            }
        } else {
            if (crate.isPreviewEnabled()) {
                this.inventoryManager.addViewer(player);
                this.inventoryManager.openNewCratePreview(player, crateLocation.getCrate());
            } else {
                Messages.preview_disabled.sendMessage(player, "{crate}", crate.getName());
            }
        }
    }
    
    // This must run as highest, so it doesn't cause other plugins to check
    // the items that were added to the players inventory and replaced the item in the player's hand.
    // This is only an issue with QuickCrate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.getAction().isRightClick() || event.getHand() != EquipmentSlot.HAND) return;

        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final CrateLocation crateLocation = this.crateManager.getCrateLocation(clickedBlock.getLocation());

        if (crateLocation == null) return;

        final Crate crate = crateLocation.getCrate();

        if (crate.getCrateType() == CrateType.menu) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        final KeyCheckEvent key = new KeyCheckEvent(player, crateLocation);
        player.getServer().getPluginManager().callEvent(key);

        if (key.isCancelled()) return;

        boolean hasKey = false;
        boolean isPhysical = false;
        boolean useQuickCrateAgain = false;

        final int requiredKeys = crate.getRequiredKeys();

        final int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

        if (requiredKeys > 0 && totalKeys < requiredKeys) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{required_amount}", String.valueOf(requiredKeys));
            placeholders.put("{key_amount}", String.valueOf(requiredKeys)); // deprecated, remove in next major version of minecraft.
            placeholders.put("{amount}", String.valueOf(totalKeys));
            placeholders.put("{crate}", crate.getPreviewName());
            placeholders.put("{key}", crate.getKeyName());

            Messages.not_enough_keys.sendMessage(player, placeholders);

            lackingKey(player, crate, clickedBlock, false);

            key.setCancelled(true);

            return;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (this.config.getProperty(ConfigKeys.physical_accepts_physical_keys) && crate.getCrateType() != CrateType.crate_on_the_go && ItemUtils.isSimilar(itemStack, crate)) {
            hasKey = true;
            isPhysical = true;
        } else if (this.config.getProperty(ConfigKeys.physical_accepts_virtual_keys) && this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        }

        if (hasKey) {
            // Checks if the player uses the quick crate again.
            if (this.crateManager.isInOpeningList(player) && this.crateManager.getOpeningCrate(player).getCrateType() == CrateType.quick_crate && this.crateManager.isCrateInUse(player) && this.crateManager.getCrateInUseLocation(player).equals(crateLocation.getLocation())) {
                useQuickCrateAgain = true;
            }

            if (!useQuickCrateAgain) {
                if (this.crateManager.isInOpeningList(player)) {
                    Messages.already_opening_crate.sendMessage(player, "{crate}", crate.getName());

                    return;
                }

                if (this.crateManager.getCratesInUse().containsValue(crateLocation.getLocation())) {
                    Messages.crate_in_use.sendMessage(player, "{crate}", crate.getName());

                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                Messages.inventory_not_empty.sendMessage(player, "{crate}", crate.getName());

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

        lackingKey(player, crate, clickedBlock, true);

        key.setCancelled(true);
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

    private void lackingKey(final Player player, final Crate crate, final Block clickedBlock, final boolean sendMessage) {
        final String keyName = crate.getKeyName();

        final Map<String, String> placeholders = new HashMap<>() {{
            put("{crate}", crate.getName());
            put("{key}", keyName);
        }};

        if (crate.getCrateType() != CrateType.crate_on_the_go) {
            if (this.config.getProperty(ConfigKeys.knock_back)) knockBack(player, clickedBlock.getLocation());

            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                net.kyori.adventure.sound.Sound sound = net.kyori.adventure.sound.Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

                player.playSound(sound);
            }

            if (sendMessage) Messages.no_keys.sendMessage(player, placeholders);
        }
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