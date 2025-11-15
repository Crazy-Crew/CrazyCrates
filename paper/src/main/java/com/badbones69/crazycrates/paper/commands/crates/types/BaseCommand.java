package com.badbones69.crazycrates.paper.commands.crates.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.files.FileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Command(value = "crazycrates", alias = {"crates", "crate", "cc"})
public abstract class BaseCommand {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final Path path = this.plugin.getDataPath();

    protected final Server server = this.plugin.getServer();

    protected final PluginManager pluginManager = this.server.getPluginManager();

    protected final ComponentLogger logger = this.plugin.getComponentLogger();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final FileManager fileManager = this.plugin.getFileManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected void addKey(@NotNull final CommandSender sender, @NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType type, final int amount, final boolean isSilent, final boolean isGiveAll) {
        addKey(sender, player, null, crate, type, amount, isSilent, isGiveAll);
    }

    /**
     * Add keys to a player who is online.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param type the type of key.
     * @param amount the amount of keys.
     */
    protected void addKey(@NotNull final CommandSender sender, @NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType type, final int amount, final boolean isSilent) {
        addKey(sender, player, null, crate, type, amount, isSilent, false);
    }

    /**
     * Add keys to a player who is offline.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param keyType the type of key.
     * @param amount the amount of keys.
     */
    protected void addKey(@NotNull final CommandSender sender, @Nullable final OfflinePlayer player, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount, final boolean isSilent) {
        addKey(sender, null, player, crate, keyType, amount, isSilent, false);
    }

    /**
     * Take keys from a player who is online.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param keyType the type of key.
     * @param amount the amount of keys.
     */
    protected void takeKey(@NotNull final CommandSender sender, @NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        takeKey(sender, player, null, crate, keyType, amount);
    }

    /**
     * Take keys from a player who is offline.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param keyType the type of key.
     * @param amount the amount of keys.
     */
    protected void takeKey(@NotNull final CommandSender sender, @Nullable final OfflinePlayer player, @NotNull final Crate crate, @NotNull final KeyType keyType, final int amount) {
        takeKey(sender, null, player, crate, keyType, amount);
    }

    /**
     * Get the key type from string
     *
     * @param type the keytype to check.
     * @return the keytype or virtual key if none found.
     */
    protected @NotNull final KeyType getKeyType(@NotNull final String type) {
        if (type.isEmpty()) return KeyType.virtual_key;

        final KeyType keyType = KeyType.getFromName(type);

        if (keyType == null || keyType == KeyType.free_key) {
            return KeyType.virtual_key;
        }

        return keyType;
    }

    /**
     * Get crate by name.
     *
     * @param sender the sender requesting the crate.
     * @param name the name of the crate.
     * @return the crate object or null if not found.
     */
    protected final Crate getCrate(@NotNull final CommandSender sender, @NotNull final String name, final boolean ignoreChecks) {
        if (name.isEmpty()) return null;

        final Crate crate = this.crateManager.getCrateFromName(name);

        if (ignoreChecks) {
            if (crate == null || crate.getCrateType() == CrateType.menu) {
                Messages.not_a_crate.sendMessage(sender, "{crate}", name);

                return null;
            }
        }

        return crate;
    }

    @ApiStatus.Internal
    private void takeKey(@NotNull final CommandSender sender, @Nullable final Player player, @Nullable final OfflinePlayer offlinePlayer, @NotNull final Crate crate, @NotNull final KeyType type, int amount) {
        final String fileName = crate.getFileName();

        if (player != null) {
            final UUID uuid = player.getUniqueId();
            final String name = player.getName();

            final int totalKeys = this.userManager.getTotalKeys(uuid, fileName);

            if (totalKeys < 1) {
                if (MiscUtils.isLogging()) this.logger.warn("The player {} does not have enough keys to take.", name);

                Messages.cannot_take_keys.sendMessage(sender, "{player}", name);

                return;
            }

            if (totalKeys < amount) {
                amount = type == KeyType.physical_key ? this.userManager.getPhysicalKeys(uuid, fileName) : this.userManager.getVirtualKeys(uuid, fileName);
            }

            this.userManager.takeKeys(uuid, fileName, type, amount, false);

            final int finalAmount = amount;

            Messages.take_player_keys.sendMessage(sender, Map.of(
                "{keytype}", type.getFriendlyName(),
                "{amount}", String.valueOf(finalAmount),
                "{player}", name,
                "{key}", crate.getKeyName()
            ));

            EventManager.logEvent(EventType.event_key_removed, name, sender, crate, type, amount);

            return;
        }

        if (offlinePlayer != null) {
            final String name = offlinePlayer.getName();

            Messages.take_offline_player_keys.sendMessage(sender, Map.of(
                "{amount}", String.valueOf(amount),
                "{keytype}", type.getFriendlyName(),
                "{key}", crate.getKeyName(),
                "{player}", name == null ? "N/A" : name)
            );

            this.userManager.takeOfflineKeys(offlinePlayer.getUniqueId(), fileName, type, amount);

            EventManager.logEvent(EventType.event_key_removed, name == null ? "N/A" : name, sender, crate, type, amount);
        }
    }

    @ApiStatus.Internal
    private void addKey(@NotNull final CommandSender sender, @Nullable final Player player, @Nullable final OfflinePlayer offlinePlayer, @NotNull final Crate crate, @NotNull final KeyType type, final int amount, final boolean isSilent, final boolean isGiveAll) {
        final String fileName = crate.getFileName();

        if (player != null) {
            if (!isGiveAll) { // we already call the key event when doing give all. we do not need to call it twice.
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

                this.pluginManager.callEvent(event);

                if (event.isCancelled()) return;
            }

            final UUID uuid = player.getUniqueId();
            final String name = player.getName();

            this.userManager.addKeys(uuid, fileName, crate.getCrateType() == CrateType.crate_on_the_go ? KeyType.physical_key : type, amount);

            final Map<String, String> placeholders = Map.of(
                "{keytype}", type.getFriendlyName(),
                "{amount}", String.valueOf(amount),
                "{player}", name,
                "{key}", crate.getKeyName()
            );

            boolean fullMessage = this.config.getProperty(ConfigKeys.notify_player_when_inventory_full);
            boolean inventoryCheck = this.config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full);

            EventManager.logEvent(EventType.event_key_given, name, sender, crate, type, amount);

            if (!isGiveAll) Messages.gave_a_player_keys.sendMessage(sender, placeholders);

            if (isSilent) return;

            if (!inventoryCheck || !fullMessage && !MiscUtils.isInventoryFull(player) && player.isOnline()) {
                Messages.obtaining_keys.sendMessage(player, placeholders);
            }

            return;
        }

        if (offlinePlayer != null) {
            final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(offlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

            this.pluginManager.callEvent(event);

            if (event.isCancelled()) return;

            if (!this.userManager.addOfflineKeys(offlinePlayer.getUniqueId(), fileName, type, amount)) {
                Messages.internal_error.sendMessage(sender);
            } else {
                final String name = offlinePlayer.getName();

                final Map<String, String> placeholders = Map.of(
                    "{keytype}", type.getFriendlyName(),
                    "{amount}", String.valueOf(amount),
                    "{key}", crate.getKeyName(),
                    "{player}", name == null ? "N/A" : name
                );

                Messages.given_offline_player_keys.sendMessage(sender, placeholders);

                EventManager.logEvent(EventType.event_key_given, name == null ? "N/A" : name, sender, crate, type, amount);
            }
        }
    }
}