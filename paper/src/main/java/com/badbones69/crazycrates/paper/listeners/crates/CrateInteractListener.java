package com.badbones69.crazycrates.paper.listeners.crates;

import com.badbones69.crazycrates.paper.cache.CacheManager;
import com.badbones69.crazycrates.paper.cache.objects.ActiveCrate;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.gui.GuiKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.paper.api.events.KeyCheckEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.utils.ItemUtil;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CrateInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final CacheManager cacheManager = this.platform.getCacheManager();

    private final ConfigManager configManager = this.platform.getConfigManager();

    private final PropertyManager pluginConfig = this.configManager.getConfig();

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final InventoryManager inventoryManager = this.platform.getInventoryManager();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final BukkitUserManager userManager = this.platform.getUserManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrateInteract(final CrateInteractEvent event) {
        if (event.isCancelled()) return; // do not run this event.

        event.cancel(); // cancel the event

        final Player player = event.getPlayer();

        final CrateLocation crateLocation = event.getCrateLocation();
        final Crate crate = crateLocation.getCrate();

        final CrateType crateType = crate.getCrateType();

        final Action action = event.getAction();

        switch (action) {
            case LEFT_CLICK_BLOCK, LEFT_CLICK_AIR -> {
                if (crate.getCrateType() == CrateType.menu) {
                    preview(player, crate, true);

                    return;
                }

                if (this.pluginConfig.getProperty(CrateKeys.crate_physical_interaction)) { // left click to preview
                    preview(player, crate, false);

                    return;
                }

                // left click to open
                openCrate(player, crateLocation, crate);
            }

            case RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR -> {
                if (crate.getCrateType() == CrateType.menu) {
                    preview(player, crate, true);

                    return;
                }

                if (this.pluginConfig.getProperty(CrateKeys.crate_physical_interaction)) { // right click to open
                    openCrate(player, crateLocation, crate);

                    return;
                }

                // right-click to preview
                preview(player, crate, false);
            }
        }
    }

    private void openCrate(@NotNull final Player player, @NotNull final CrateLocation crateLocation, @NotNull final Crate crate) {
        final Location location = crateLocation.getLocation();
        final String fancyName = crate.getCrateName();

        final UUID uuid = player.getUniqueId();

        if (this.cacheManager.hasOpeningCrate(uuid)) { // if the player trying to interact again is the one currently opening.
            final Optional<ActiveCrate> index = this.cacheManager.getActiveCrate(uuid);

            if (index.isPresent()) {
                final ActiveCrate value = index.get();

                if (value.getLocationAsString().equals(crateLocation.getLocationAsString())) {
                    Message.crate_already_opened.sendMessage(player, "{crate}", fancyName);

                    return;
                }
            }
        }

        final CrateType crateType = crate.getCrateType();

        // QuadCrate spawns a schematic, so we want to check this as it can break shit.
        // QuickCrate shows an item, so if that toggle enabled. don't let them use it at the same time.
        if (!this.cacheManager.hasOpeningCrate(uuid) &&
                crateType.equals(CrateType.quad_crate) ||
                crateType.equals(CrateType.quick_crate) && this.pluginConfig.getProperty(CrateKeys.show_quickcrate_item)) { // only runs if the player is not in the map, avoids double-dipping
            final Optional<ActiveCrate> index = this.cacheManager.getActiveCrateByLocation(location); // checks the map for all locations that match.

            if (index.isPresent()) {
                final ActiveCrate value = index.get();

                if (value.getLocationAsString().equals(crateLocation.getLocationAsString())) { // checks if location matches, and returns!
                    Message.crate_already_used.sendMessage(player, "{crate}", fancyName);

                    return;
                }
            }
        }

        final KeyCheckEvent key = new KeyCheckEvent(player, crateLocation);

        this.pluginManager.callEvent(key);

        if (key.isCancelled()) return;

        boolean isPhysical = false;
        boolean hasKey = false;

        final int requiredKeys = crate.getRequiredKeys();

        final String fileName = crate.getFileName();

        final int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), fileName);

        if (requiredKeys > 0 && totalKeys < requiredKeys) { //todo() this, and the checks below should be combined. this is weird lol
            lackingKey(player, crate, location, totalKeys, requiredKeys);

            key.setCancelled(true);

            return;
        }

        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (this.pluginConfig.getProperty(CrateKeys.physical_accepts_physical_keys) && crate.getCrateType() != CrateType.crate_on_the_go && ItemUtil.isSimilar(itemStack, crate)) {
            hasKey = true;
            isPhysical = true;
        } else if (this.pluginConfig.getProperty(CrateKeys.physical_accepts_virtual_keys) && this.userManager.getVirtualKeys(player.getUniqueId(), fileName) >= 1) {
            hasKey = true;
        }

        final KeyType keyType = isPhysical ? KeyType.physical_key : KeyType.virtual_key;

        if (!hasKey) {
            lackingKey(player, crate, location, totalKeys, 1);

            key.setCancelled(true);

            return;
        }

        if (MiscUtils.isInventoryFull(player)) {
            Message.inventory_not_empty.sendMessage(player, "{crate}", fancyName);

            this.crateManager.endCrate(crate, player);

            return;
        }

        // Only cosmic crate type uses this method.
        if (crate.getCrateType() == CrateType.cosmic) this.crateManager.addPlayerKeyType(player, keyType);

        this.crateManager.openCrate(
                player,
                crate,
                keyType,
                location,
                false,
                true,
                EventType.event_crate_opened
        );
    }

    private void lackingKey(@NotNull final Player player, @NotNull final Crate crate, @NotNull final Location location, final int currentKeys, final int amount) {
        if (crate.getCrateType() != CrateType.crate_on_the_go) {
            if (crate.isKnockBackEnabled()) {
                knockback(player, location);
            }

            if (crate.isPlaySound()) {
                player.playSound(Sound.sound(Key.key(crate.getSound()), Sound.Source.MASTER, 1f, 1f));
            }

            Message.not_enough_keys.sendMessage(player, Map.of(
                    "{required_amount}", String.valueOf(amount),
                    "{key_amount}", String.valueOf(amount),
                    "{amount}", String.valueOf(currentKeys),
                    "{crate}", crate.getCrateName(),
                    "{key}", crate.getKeyName()
            ));
        }
    }

    private void knockback(@NotNull final Player player, @NotNull final Location location) {
        final Vector vector = player.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1).setY(.1);

        if (player.isInsideVehicle() && player.getVehicle() != null) {
            player.getVehicle().setVelocity(vector);

            return;
        }

        player.setVelocity(vector);
    }

    private void preview(@NotNull final Player player, @NotNull final Crate crate, final boolean skipTypeCheck) {
        if (skipTypeCheck || crate.getCrateType() == CrateType.menu) {

            // this is to stop players in QuadCrate to not be able to try and open a crate set to a menu.
            if (!this.cacheManager.hasOpeningCrate(player.getUniqueId()) && this.pluginConfig.getProperty(GuiKeys.is_crate_menu_enabled)) {
                new CrateMainMenu(
                        player,
                        this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_name),
                        this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_rows)
                ).open();
            } else {
                Message.feature_disabled.sendMessage(player);
            }
        } else {
            if (crate.isPreviewEnabled()) {
                this.inventoryManager.openNewCratePreview(player, crate);
            } else {
                Message.preview_disabled.sendMessage(player, "{crate}", crate.getCrateName());
            }
        }
    }
}