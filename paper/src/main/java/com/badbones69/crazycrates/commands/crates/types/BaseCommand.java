package com.badbones69.crazycrates.commands.crates.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.files.FileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Command(value = "crazycrates", alias = {"crates", "crate"})
public abstract class BaseCommand {

    protected final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    protected final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    protected final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    protected final @NotNull FileManager fileManager = this.plugin.getFileManager();

    protected final @NotNull SettingsManager config = ConfigManager.getConfig();

    public record CustomPlayer(String name) {
        public @NotNull OfflinePlayer getOfflinePlayer() {
            CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(name)).thenApply(OfflinePlayer::getUniqueId);

            return Bukkit.getServer().getOfflinePlayer(future.join());
        }

        public Player getPlayer() {
            return Bukkit.getServer().getPlayer(name);
        }
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
    protected void addKey(CommandSender sender, Player player, Crate crate, KeyType type, int amount) {
        addKey(sender, player, null, crate, type, amount);
    }

    /**
     * Add keys to a player who is offline.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param type the type of key.
     * @param amount the amount of keys.
     */
    protected void addKey(CommandSender sender, OfflinePlayer player, Crate crate, KeyType type, int amount) {
        addKey(sender, null, player, crate, type, amount);
    }

    /**
     * Take keys from a player who is online.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param type the type of key.
     * @param amount the amount of keys.
     */
    protected void takeKey(CommandSender sender, Player player, Crate crate, KeyType type, int amount) {
        takeKey(sender, player, null, crate, type, amount);
    }

    /**
     * Take keys from a player who is offline.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param crate the crate.
     * @param type the type of key.
     * @param amount the amount of keys.
     */
    protected void takeKey(CommandSender sender, OfflinePlayer player, Crate crate, KeyType type, int amount) {
        takeKey(sender, null, player, crate, type, amount);
    }

    /**
     * Get the key type from string
     *
     * @param sender the sender requesting to give keys.
     * @param type the keytype to check.
     * @return the keytype or virtual key if none found.
     */
    protected KeyType getKeyType(CommandSender sender, String type) {
        KeyType keyType = KeyType.getFromName(type);

        if (keyType == null || keyType == KeyType.free_key) {
            sender.sendRichMessage(MsgUtils.getPrefix("<red>You did not specify a key type, Defaulting to key type virtual"));

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
    protected Crate getCrate(CommandSender sender, String name, boolean ignoreChecks) {
        Crate crate = this.crateManager.getCrateFromName(name);

        if (ignoreChecks) {
            if (crate == null || crate.getCrateType() == CrateType.menu) {
                sender.sendRichMessage(Messages.not_a_crate.getMessage(sender, "{crate}", name));

                return null;
            }
        }

        return crate;
    }

    protected Prize getPrize(CommandSender sender, String crateName, String name, boolean ignoreChecks) {
        Crate crate = getCrate(sender, crateName, false);

        if (crate == null) {
            return null;
        }

        Prize prize = crate.getPrize(name);

        if (prize == null) {
            //todo() add better message.

            return null;
        }

        return prize;
    }

    /**
     * Get keys from player or sender or other player.
     *
     * @param player player to get keys.
     * @param sender sender to send message to.
     * @param header header of the message.
     * @param content content of the message.
     */
    protected void getKeys(Player player, CommandSender sender, String header, String content) {
        List<String> message = new ArrayList<>();

        message.add(header);

        Map<Crate, Integer> keys = new HashMap<>();

        this.plugin.getCrateManager().getUsableCrates().forEach(crate -> keys.put(crate, this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName())));

        boolean hasKeys = false;

        for (Crate crate : keys.keySet()) {
            int amount = keys.get(crate);

            if (amount > 0) {
                Map<String, String> placeholders = new HashMap<>();

                hasKeys = true;

                placeholders.put("{crate}", crate.getCrateInventoryName());
                placeholders.put("{keys}", String.valueOf(amount));
                placeholders.put("{crate_opened}", String.valueOf(this.userManager.getCrateOpened(player.getUniqueId(), crate.getName())));

                message.add(Messages.per_crate.getMessage(player, placeholders));
            }
        }

        if (Support.placeholder_api.isEnabled() ) {
            if (sender instanceof Player person) {
                if (hasKeys) {
                    message.forEach(line -> person.sendRichMessage(PlaceholderAPI.setPlaceholders(person, line)));

                    return;
                }

                sender.sendRichMessage(PlaceholderAPI.setPlaceholders(person, content));

                return;
            }

            return;
        }

        if (hasKeys) {
            message.forEach(sender::sendRichMessage);

            return;
        }

        sender.sendRichMessage(content);
    }

    @ApiStatus.Internal
    private void takeKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        if (player != null) {
            int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

            if (totalKeys < 1) {
                if (MiscUtils.isLogging())
                    this.plugin.getLogger().warning("The player " + player.getName() + " does not have enough keys to take.");

                sender.sendRichMessage(Messages.cannot_take_keys.getMessage(sender, "{player}", player.getName()));

                return;
            }

            if (totalKeys < amount) {
                amount = type == KeyType.physical_key ? this.userManager.getPhysicalKeys(player.getUniqueId(), crate.getName()) : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName());
            }

            this.userManager.takeKeys(amount, player.getUniqueId(), crate.getName(), type, false);

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{player}", player.getName());

            sender.sendRichMessage(Messages.take_player_keys.getMessage(sender, placeholders));

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", type.getFriendlyName());
        placeholders.put("{player}", offlinePlayer.getName());

        sender.sendRichMessage(Messages.take_offline_player_keys.getMessage(sender, placeholders));

        this.userManager.takeOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type);
    }

    @ApiStatus.Internal
    private void addKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        if (player != null) {
            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                player.getInventory().addItem(crate.getKey(amount, player));
            } else {
                this.userManager.addKeys(amount, player.getUniqueId(), crate.getName(), type);
            }

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{player}", player.getName());
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{key}", crate.getKeyName());

            boolean fullMessage = this.config.getProperty(ConfigKeys.notify_player_when_inventory_full);
            boolean inventoryCheck = this.config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full);

            sender.sendRichMessage(Messages.gave_a_player_keys.getMessage(sender, placeholders));

            if (!inventoryCheck || !fullMessage && !MiscUtils.isInventoryFull(player) && player.isOnline())
                player.sendRichMessage(Messages.obtaining_keys.getMessage(player, placeholders));

            return;
        }

        if (!this.userManager.addOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type)) {
            sender.sendRichMessage(Messages.internal_error.getMessage(sender));
        } else {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{player}", offlinePlayer.getName());

            sender.sendRichMessage(Messages.given_offline_player_keys.getMessage(sender, placeholders));
        }
    }
}