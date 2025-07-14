package com.badbones69.crazycrates.paper.managers;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager extends UserManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final FileKeys data = FileKeys.data;

    @Override
    public Player getUser(@NotNull final UUID uuid) {
        return this.plugin.getServer().getPlayer(uuid);
    }

    @Override
    public int getVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        return this.data.getConfiguration().getInt("Players." + uuid + "." + crateName, 0);
    }

    @Override
    public void addVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.logger.warn("Player with the uuid: {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final String fileName = crate.getFileName();

        final Player player = getUser(uuid);

        final int keys = getVirtualKeys(uuid, fileName);

        final YamlConfiguration configuration = this.data.getConfiguration();

        if (!configuration.contains("Players." + uuid + ".Name")) configuration.set("Players." + uuid + ".Name", player.getName());

        configuration.set("Players." + uuid + "." + fileName, (Math.max((keys + amount), 0)));

        this.data.save();
    }

    @Override
    public void setKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.logger.warn("Player with the uuid: {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final String fileName = crate.getFileName();

        final Player player = getUser(uuid);

        final YamlConfiguration configuration = this.data.getConfiguration();

        configuration.set("Players." + player.getUniqueId() + ".Name", player.getName());
        configuration.set("Players." + player.getUniqueId() + "." + fileName, amount);

        this.data.save();
    }

    private boolean isPlayerNull(UUID uuid) {
        return getUser(uuid) == null;
    }

    @Override
    public void addKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull final KeyType keyType, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.logger.warn("Player with the uuid {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final String fileName = crate.getFileName();

        final Player player = getUser(uuid);

        final SettingsManager config = ConfigManager.getConfig();

        switch (keyType) {
            case physical_key -> {
                if (!MiscUtils.isInventoryFull(player)) {
                    MiscUtils.addItem(player, crate.getKey(amount, player));

                    return;
                }

                if (config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full)) {
                    addVirtualKeys(player.getUniqueId(), fileName, amount);

                    if (config.getProperty(ConfigKeys.notify_player_when_inventory_full)) {
                        final Map<String, String> placeholders = new HashMap<>();

                        placeholders.put("{amount}", String.valueOf(amount));
                        placeholders.put("{player}", player.getName());
                        placeholders.put("{keytype}", keyType.getFriendlyName());
                        placeholders.put("{key}", crate.getKeyName());

                        Messages.cannot_give_player_keys.sendMessage(player, placeholders);
                    }

                    return;
                }

                new FoliaScheduler(plugin, player.getLocation()) {
                    @Override
                    public void run() {
                        player.getWorld().dropItem(player.getLocation(), crate.getKey(amount, player));
                    }
                }.runNow();
            }

            case virtual_key -> addVirtualKeys(player.getUniqueId(), fileName, amount);
        }
    }

    @Override
    public int getTotalKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        return getVirtualKeys(uuid, crateName) + getPhysicalKeys(uuid, crateName);
    }

    @Override
    public int getPhysicalKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.logger.warn("Player with the uuid {} is null.", uuid);

            return 0;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return 0;
        }

        final Player player = getUser(uuid);
        
        int keys = 0;

        for (final ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (ItemUtils.isSimilar(item, crate)) keys += item.getAmount();
        }

        return keys;
    }

    @Override
    public boolean takeKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull final KeyType keyType, final int amount, final boolean checkHand) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.logger.warn("Player with the uuid {} is null.", uuid);

            return false;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        final String fileName = crate.getFileName();

        final Player player = getUser(uuid);

        switch (keyType) {
            case physical_key -> {
                int takeAmount = amount;

                final List<ItemStack> items = new ArrayList<>();

                if (checkHand) {
                    items.add(player.getEquipment().getItemInMainHand());
                    items.add(player.getEquipment().getItemInOffHand());
                } else {
                    items.addAll(Arrays.asList(player.getInventory().getContents()));
                }

                for (final ItemStack item : items) {
                    if (item != null) {
                        if (ItemUtils.isSimilar(item, crate)) {
                            final int keyAmount = item.getAmount();

                            if ((takeAmount - keyAmount) >= 0) {
                                MiscUtils.removeMultipleItemStacks(player.getInventory(), item);

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), fileName, amount);

                                takeAmount -= keyAmount;
                            } else {
                                item.setAmount(keyAmount - takeAmount);

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), fileName, amount);

                                takeAmount = 0;
                            }

                            if (takeAmount <= 0) return true;
                        }
                    }
                }

                // This needs to be done as player.getInventory().removeItem(ItemStack); does NOT remove from the offhand.
                if (takeAmount > 0) {
                    final ItemStack item = player.getEquipment().getItemInOffHand();

                    if (ItemUtils.isSimilar(item, crate)) {
                        final int keyAmount = item.getAmount();

                        if ((takeAmount - keyAmount) >= 0) {
                            player.getEquipment().setItemInOffHand(null);
                            takeAmount -= keyAmount;

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), fileName, amount);
                        } else {
                            item.setAmount(keyAmount - takeAmount);

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), fileName, amount);

                            takeAmount = 0;
                        }

                        if (takeAmount <= 0) return true;
                    }
                }
            }

            case virtual_key -> {
                final int keys = getVirtualKeys(uuid, fileName);

                final YamlConfiguration configuration = this.data.getConfiguration();

                configuration.set("Players." + uuid + ".Name", player.getName());

                final int newAmount = Math.max((keys - amount), 0);

                if (newAmount < 1) {
                    configuration.set("Players." + uuid + "." + fileName, null);
                } else {
                    configuration.set("Players." + uuid + "." + fileName, newAmount);
                }

                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), fileName, amount);

                this.data.save();

                return true;
            }

            case free_key -> {
                return true;
            }
        }

        MiscUtils.failedToTakeKey(player, fileName);

        return false;
    }

    @Override
    public boolean hasPhysicalKey(@NotNull final UUID uuid, @NotNull final String crateName, final boolean checkHand) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        final Player player = getUser(uuid);

        final List<ItemStack> items = new ArrayList<>();

        if (checkHand) {
            items.add(player.getEquipment().getItemInMainHand());
            items.add(player.getEquipment().getItemInOffHand());
        } else {
            items.addAll(Arrays.asList(player.getInventory().getContents()));
            items.removeAll(Arrays.asList(player.getInventory().getArmorContents()));
        }

        for (ItemStack item : items) {
            if (item != null) {
                if (ItemUtils.isSimilar(item, crate)) return true;
            }
        }

        return false;
    }

    @Override
    public boolean addOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int keys) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        final String fileName = crate.getFileName();

        try {
            final YamlConfiguration configuration = this.data.getConfiguration();

            if (keyType == KeyType.physical_key) {
                if (configuration.contains("Offline-Players." + uuid + ".Physical." + fileName)) keys += configuration.getInt("Offline-Players." + uuid + ".Physical." + fileName);

                configuration.set("Offline-Players." + uuid + ".Physical." + fileName, keys);

                this.data.save();

                return true;
            }

            if (configuration.contains("Offline-Players." + uuid + "." + fileName)) keys += configuration.getInt("Offline-Players." + uuid + "." + fileName);

            configuration.set("Offline-Players." + uuid + "." + fileName, keys);

            this.data.save();

            return true;
        } catch (Exception exception) {
            if (MiscUtils.isLogging()) this.logger.error("Could not add keys to offline player with uuid: {}", uuid, exception);

            return false;
        }
    }

    @Override
    public boolean takeOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int keys) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        final String fileName = crate.getFileName();

        try {
            final YamlConfiguration configuration = this.data.getConfiguration();

            if (keyType == KeyType.physical_key) {
                final int offlineKeys = configuration.getInt("Offline-Players." + uuid + ".Physical." + fileName);

                // If the offline keys are less than the keys the person wants to take. We will set the keys variable to how many offline keys they have.
                if (offlineKeys < keys) {
                    keys = offlineKeys;
                }

                configuration.set("Offline-Players." + uuid + ".Physical." + fileName, configuration.getInt("Offline-Players." + uuid + ".Physical." + fileName) - keys);

                // Remove the data if 0 keys remain after if checks.
                if (configuration.getInt("Offline-Players." + uuid + ".Physical." + fileName) <= 0) configuration.set("Offline-Players." + uuid + ".Physical." + fileName, null);

                this.data.save();

                return true;
            }

            configuration.set("Offline-Players." + uuid + "." + fileName, configuration.getInt("Offline-Players." + uuid + "." + fileName) - keys);

            this.data.save();

            return true;
        } catch (final Exception exception) {
            if (MiscUtils.isLogging()) this.logger.error("Could not take keys from offline player with uuid: {}", uuid, exception);

            return false;
        }
    }

    public void loadOldOfflinePlayersKeys(@NotNull final Player player, @NotNull final List<Crate> crates) {
        final String name = player.getName().toLowerCase();

        final YamlConfiguration configuration = this.data.getConfiguration();

        if (configuration.contains("Offline-Players." + name)) {
            for (final Crate crate : crates) {
                final String fileName = crate.getFileName();

                if (configuration.contains("Offline-Players." + name + "." + fileName)) {
                    final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        final int keys = getVirtualKeys(player.getUniqueId(), fileName);
                        final int addedKeys = configuration.getInt("Offline-Players." + name + "." + fileName);

                        configuration.set("Players." + player.getUniqueId() + "." + fileName, (Math.max((keys + addedKeys), 0)));

                        this.data.save();
                    }
                }
            }

            configuration.set("Offline-Players." + name, null);

            this.data.save();
        }
    }

    /**
     * Load the offline keys of a player who has come online.
     *
     * @param player The player which you would like to load the offline keys for.
     */
    public void loadOfflinePlayersKeys(@NotNull final Player player, @NotNull final List<Crate> crates) {
        final YamlConfiguration configuration = this.data.getConfiguration();

        if (!configuration.contains("Offline-Players." + player.getUniqueId()) || crates.isEmpty()) return;

        final UUID uuid = player.getUniqueId();

        for (final Crate crate : crates) {
            final String fileName = crate.getFileName();

            if (configuration.contains("Offline-Players." + uuid + "." + fileName)) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                int amount = configuration.getInt("Offline-Players." + uuid + "." + fileName);

                //todo() Instead of dropping the keys, make it so they need to empty their inventory and prompt them to open a gui.
                while (keysGiven < amount) {
                    // If the inventory is full, drop the remaining keys then stop.
                    if (crate.getCrateType() == CrateType.crate_on_the_go) {
                        // If the inventory is full, drop the items then stop.
                        if (MiscUtils.isInventoryFull(player)) {
                            new FoliaScheduler(plugin, player.getLocation()) {
                                @Override
                                public void run() {
                                    player.getWorld().dropItemNaturally(player.getLocation(), crate.getKey(amount, player));
                                }
                            }.runNow();
                            break;
                        }
                    }

                    keysGiven++;
                }

                // If the crate type is on the go.
                if (crate.getCrateType() == CrateType.crate_on_the_go) {
                    // If the inventory not full, add to inventory.
                    MiscUtils.addItem(player, crate.getKey(amount, player));
                } else {
                    // Otherwise add virtual keys.
                    addVirtualKeys(uuid, fileName, amount);
                }

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) configuration.set("Offline-Players." + uuid + "." + crate.getFileName(), null);
            }

            if (configuration.contains("Offline-Players." + uuid + ".Physical." + fileName)) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                final int amount = configuration.getInt("Offline-Players." + uuid + ".Physical." + fileName);

                while (keysGiven < amount) {
                    // If the inventory is full, drop the remaining keys then stop.
                    if (MiscUtils.isInventoryFull(player)) {
                        final int keysToDrop = amount - keysGiven;
                        new FoliaScheduler(plugin, player.getLocation()) {
                            @Override
                            public void run() {
                                player.getWorld().dropItemNaturally(player.getLocation(), crate.getKey(keysToDrop, player));
                            }
                        }.runNow();

                        break;
                    }

                    keysGiven++;
                }

                // If the inventory not full, add to inventory.
                MiscUtils.addItem(player, crate.getKey(keysGiven, player));

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) configuration.set("Offline-Players." + uuid + ".Physical." + fileName, null);
            }
        }

        final ConfigurationSection physicalSection = configuration.getConfigurationSection("Offline-Players." + uuid + ".Physical");

        if (physicalSection != null) {
            if (physicalSection.getKeys(false).isEmpty()) configuration.set("Offline-Players." + uuid + ".Physical", null);
        }

        final ConfigurationSection section = configuration.getConfigurationSection("Offline-Players." + uuid);

        if (section != null) {
            if (section.getKeys(false).isEmpty()) configuration.set("Offline-Players." + uuid, null);
        }

        this.data.save();
    }

    @Override
    public int getTotalCratesOpened(@NotNull final UUID uuid) {
        return this.data.getConfiguration().getInt("Players." + uuid + ".tracking.total-crates", 0);
    }

    @Override
    public int getCrateOpened(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return 0;
        }

        return crate.isTrackingOpening() ? this.data.getConfiguration().getInt("Players." + uuid + ".tracking." + crateName, 0) : 0;
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        if (!crate.isTrackingOpening()) return;

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        final boolean hasValue = configuration.contains("Players." + uuid + ".tracking." + fileName);

        int newAmount;

        if (hasValue) {
            newAmount = configuration.getInt("Players." + uuid + ".tracking." + fileName) + amount;

            configuration.set("Players." + uuid + ".tracking." + fileName, newAmount);
            configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates") + amount);

            this.data.save();

            return;
        }

        configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates", 0)+amount);
        configuration.set("Players." + uuid + ".tracking." + fileName, amount);

        this.data.save();
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        if (!crate.isTrackingOpening()) return;

        final String fileName = crate.getFileName();

        final YamlConfiguration configuration = this.data.getConfiguration();

        boolean hasValue = configuration.contains("Players." + uuid + ".tracking." + fileName);

        int amount;

        if (hasValue) {
            amount = configuration.getInt("Players." + uuid + ".tracking." + fileName);

            configuration.set("Players." + uuid + ".tracking." + fileName, amount + 1);
            configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1);

            this.data.save();

            return;
        }

        amount = configuration.contains("Players." + uuid + ".tracking.total-crates") ? configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1 : 1;

        configuration.set("Players." + uuid + ".tracking.total-crates", amount);
        configuration.set("Players." + uuid + ".tracking." + fileName, 1);

        this.data.save();
    }

    public int getCrateRespin(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return 0;
        }

        return this.data.getConfiguration().getInt("Players." + uuid + ".respins." + crateName + ".amount", 0);
    }

    public void addRespinPrize(@NotNull final UUID uuid, @NotNull final String crateName, final String prize) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        configuration.set("Players." + uuid + ".respins." + fileName + ".prize", prize);

        this.data.save();
    }

    public final boolean hasRespinPrize(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        return configuration.contains("Players." + uuid + ".respins." + fileName + ".prize");
    }

    public void removeRespinPrize(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        String prize = configuration.getString("Players." + uuid + ".respins." + fileName + ".prize");

        if (prize == null) {
            return;
        }

        configuration.set("Players." + uuid + ".respins." + fileName + ".prize", null);

        this.data.save();
    }

    public @NotNull final String getRespinPrize(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return "";
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        return configuration.getString("Players." + uuid + ".respins." + fileName + ".prize", "");
    }

    public void removeRespinCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final String fileName = crate.getFileName();

        final boolean hasValue = configuration.contains("Players." + uuid + ".respins." + fileName);

        int newAmount;

        if (hasValue) {
            newAmount = configuration.getInt("Players." + uuid + ".respins." + fileName + ".amount") - amount;

            configuration.set("Players." + uuid + ".respins." + fileName + ".amount", newAmount <= 0 ? null : newAmount);

            this.data.save();
        }
    }

    public void addRespinCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.logger.warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final String fileName = crate.getFileName();

        final YamlConfiguration configuration = this.data.getConfiguration();

        final boolean hasValue = configuration.contains("Players." + uuid + ".respins." + fileName);

        int newAmount;

        if (hasValue) {
            newAmount = configuration.getInt("Players." + uuid + ".respins." + fileName + ".amount") + amount;

            configuration.set("Players." + uuid + ".respins." + fileName + ".amount", newAmount);

            this.data.save();

            return;
        }

        configuration.set("Players." + uuid + ".respins." + fileName + ".amount", amount);

        this.data.save();
    }

    private @Nullable Crate isCrateInvalid(@NotNull final String crateName) {
        if (crateName.isEmpty()) return null;

        if (this.crateManager.getCrateFromName(crateName) != null) {
            return this.crateManager.getCrateFromName(crateName);
        }

        return null;
    }

    /**
     * Adds internal placeholders to the itembuilder.
     *
     * @param itemBuilder the itembuilder
     * @param crate the crate
     * @return the itembuilder
     */
    public LegacyItemBuilder addPlaceholders(@NotNull final LegacyItemBuilder itemBuilder, @NotNull final Crate crate) {
        final String fileName = crate.getFileName();

        if (fileName.isEmpty()) return itemBuilder;

        final UUID uuid = itemBuilder.getPlayer();

        if (uuid == null) return itemBuilder;

        final int virtualKeys = getVirtualKeys(uuid, fileName);
        final int physicalKeys = getPhysicalKeys(uuid, fileName);

        final int totalKeys = virtualKeys + physicalKeys;

        final int openedCrates = getCrateOpened(uuid, fileName);

        final NumberFormat instance = NumberFormat.getNumberInstance();

        return itemBuilder.addNamePlaceholder("%keys%", instance.format(virtualKeys))
                .addNamePlaceholder("%keys_physical%", instance.format(physicalKeys))
                .addNamePlaceholder("%keys_total%", instance.format(totalKeys))
                .addNamePlaceholder("%crate_opened%", instance.format(openedCrates))
                .addNamePlaceholder("%keys_raw%", String.valueOf(virtualKeys))
                .addNamePlaceholder("%keys_physical_raw%", String.valueOf(physicalKeys))
                .addNamePlaceholder("%keys_total_raw%", String.valueOf(totalKeys))
                .addNamePlaceholder("%crate_opened_raw", String.valueOf(openedCrates));
    }
}