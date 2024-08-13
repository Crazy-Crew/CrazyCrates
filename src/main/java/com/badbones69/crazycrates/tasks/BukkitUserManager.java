package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.Files;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager extends UserManager {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final Files data = Files.data;

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
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Player with the uuid: {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final Player player = getUser(uuid);

        final int keys = getVirtualKeys(uuid, crate.getName());

        final YamlConfiguration configuration = this.data.getConfiguration();

        if (!configuration.contains("Players." + uuid + ".Name")) configuration.set("Players." + uuid + ".Name", player.getName());

        configuration.set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));

        this.data.save();
    }

    @Override
    public void setKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Player with the uuid: {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final Player player = getUser(uuid);

        final YamlConfiguration configuration = this.data.getConfiguration();

        configuration.set("Players." + player.getUniqueId() + ".Name", player.getName());
        configuration.set("Players." + player.getUniqueId() + "." + crate.getName(), amount);

        this.data.save();
    }

    private boolean isPlayerNull(UUID uuid) {
        return getUser(uuid) == null;
    }

    @Override
    public void addKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull final KeyType keyType, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Player with the uuid {} is null.", uuid);

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final Player player = getUser(uuid);

        final SettingsManager config = ConfigManager.getConfig();

        switch (keyType) {
            case physical_key -> {
                if (!MiscUtils.isInventoryFull(player)) {
                    MiscUtils.addItem(player, crate.getKey(amount, player));

                    return;
                }

                if (config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full)) {
                    addVirtualKeys(player.getUniqueId(), crate.getName(), amount);

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

                player.getWorld().dropItem(player.getLocation(), crate.getKey(amount, player));
            }

            case virtual_key -> addVirtualKeys(player.getUniqueId(), crate.getName(), amount);
        }
    }

    @Override
    public int getTotalKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        return getVirtualKeys(uuid, crateName) + getPhysicalKeys(uuid, crateName);
    }

    @Override
    public int getPhysicalKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Player with the uuid {} is null.", uuid);

            return 0;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return 0;
        }

        final Player player = getUser(uuid);
        
        int keys = 0;

        for (final ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (!item.hasItemMeta()) continue;

            if (ItemUtils.isSimilar(item, crate)) keys += item.getAmount();
        }

        return keys;
    }

    @Override
    public boolean takeKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull final KeyType keyType, final int amount, final boolean checkHand) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Player with the uuid {} is null.", uuid);

            return false;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return false;
        }

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

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), crate.getName(), amount);

                                takeAmount -= keyAmount;
                            } else {
                                item.setAmount(keyAmount - takeAmount);

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), crate.getName(), amount);

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

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), crate.getName(), amount);
                        } else {
                            item.setAmount(keyAmount - takeAmount);

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), crate.getName(), amount);

                            takeAmount = 0;
                        }

                        if (takeAmount <= 0) return true;
                    }
                }
            }

            case virtual_key -> {
                final int keys = getVirtualKeys(uuid, crate.getName());

                final YamlConfiguration configuration = this.data.getConfiguration();

                configuration.set("Players." + uuid + ".Name", player.getName());

                final int newAmount = Math.max((keys - amount), 0);

                if (newAmount < 1) {
                    configuration.set("Players." + uuid + "." + crate.getName(), null);
                } else {
                    configuration.set("Players." + uuid + "." + crate.getName(), newAmount);
                }

                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), crate.getName(), amount);

                this.data.save();

                return true;
            }

            case free_key -> {
                return true;
            }
        }

        MiscUtils.failedToTakeKey(player, crate.getName());

        return false;
    }

    @Override
    public boolean hasPhysicalKey(@NotNull final UUID uuid, @NotNull final String crateName, final boolean checkHand) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

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
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        try {
            final YamlConfiguration configuration = this.data.getConfiguration();

            if (keyType == KeyType.physical_key) {
                if (configuration.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) keys += configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), keys);

                this.data.save();

                return true;
            }

            if (configuration.contains("Offline-Players." + uuid + "." + crate.getName())) keys += configuration.getInt("Offline-Players." + uuid + "." + crate.getName());

            configuration.set("Offline-Players." + uuid + "." + crate.getName(), keys);

            this.data.save();

            return true;
        } catch (Exception exception) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().error("Could not add keys to offline player with uuid: {}", uuid, exception);

            return false;
        }
    }

    @Override
    public boolean takeOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int keys) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return false;
        }

        try {
            final YamlConfiguration configuration = this.data.getConfiguration();

            if (keyType == KeyType.physical_key) {
                final int offlineKeys = configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                // If the offline keys are less than the keys the person wants to take. We will set the keys variable to how many offline keys they have.
                if (offlineKeys < keys) {
                    keys = offlineKeys;
                }

                configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) - keys);

                // Remove the data if 0 keys remain after if checks.
                if (configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) <= 0) configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);

                this.data.save();

                return true;
            }

            configuration.set("Offline-Players." + uuid + "." + crate.getName(), configuration.getInt("Offline-Players." + uuid + "." + crate.getName()) - keys);

            this.data.save();

            return true;
        } catch (Exception exception) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().error("Could not take keys from offline player with uuid: {}", uuid, exception);

            return false;
        }
    }

    public void loadOldOfflinePlayersKeys(@NotNull final Player player, @NotNull final List<Crate> crates) {
        final String name = player.getName().toLowerCase();

        final YamlConfiguration configuration = this.data.getConfiguration();

        if (configuration.contains("Offline-Players." + name)) {
            for (final Crate crate : crates) {
                if (configuration.contains("Offline-Players." + name + "." + crate.getName())) {
                    final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        final int keys = getVirtualKeys(player.getUniqueId(), crate.getName());
                        final int addedKeys = configuration.getInt("Offline-Players." + name + "." + crate.getName());

                        configuration.set("Players." + player.getUniqueId() + "." + crate.getName(), (Math.max((keys + addedKeys), 0)));

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
            if (configuration.contains("Offline-Players." + uuid + "." + crate.getName())) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                int amount = configuration.getInt("Offline-Players." + uuid + "." + crate.getName());

                //todo() Instead of dropping the keys, make it so they need to empty their inventory and prompt them to open a gui.
                while (keysGiven < amount) {
                    // If the inventory is full, drop the remaining keys then stop.
                    if (crate.getCrateType() == CrateType.crate_on_the_go) {
                        // If the inventory is full, drop the items then stop.
                        if (MiscUtils.isInventoryFull(player)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), crate.getKey(amount, player));
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
                    addVirtualKeys(uuid, crate.getName(), amount);
                }

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) configuration.set("Offline-Players." + uuid + "." + crate.getName(), null);
            }

            if (configuration.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                final int amount = configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                while (keysGiven < amount) {
                    // If the inventory is full, drop the remaining keys then stop.
                    if (MiscUtils.isInventoryFull(player)) {
                        player.getWorld().dropItemNaturally(player.getLocation(), crate.getKey(amount - keysGiven, player));

                        break;
                    }

                    keysGiven++;
                }

                // If the inventory not full, add to inventory.
                MiscUtils.addItem(player, crate.getKey(keysGiven, player));

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);
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
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return 0;
        }

        return this.data.getConfiguration().getInt("Players." + uuid + ".tracking." + crateName, 0);
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        final boolean hasValue = configuration.contains("Players." + uuid + ".tracking." + crate.getName());

        int newAmount;

        if (hasValue) {
            newAmount = configuration.getInt("Players." + uuid + ".tracking." + crate.getName()) + amount;

            configuration.set("Players." + uuid + ".tracking." + crate.getName(), newAmount);
            configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates") + amount);

            this.data.save();

            return;
        }

        configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates", 0)+amount);
        configuration.set("Players." + uuid + ".tracking." + crate.getName(), amount);

        this.data.save();
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate {} doesn't exist.", crateName);

            return;
        }

        final YamlConfiguration configuration = this.data.getConfiguration();

        boolean hasValue = configuration.contains("Players." + uuid + ".tracking." + crate.getName());

        int amount;

        if (hasValue) {
            amount = configuration.getInt("Players." + uuid + ".tracking." + crate.getName());

            configuration.set("Players." + uuid + ".tracking." + crate.getName(), amount + 1);
            configuration.set("Players." + uuid + ".tracking.total-crates", configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1);

            this.data.save();

            return;
        }

        amount = configuration.contains("Players." + uuid + ".tracking.total-crates") ? configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1 : 1;

        configuration.set("Players." + uuid + ".tracking.total-crates", amount);
        configuration.set("Players." + uuid + ".tracking." + crate.getName(), 1);

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
    public ItemBuilder addPlaceholders(@NotNull final ItemBuilder itemBuilder, @NotNull final Crate crate) {
        final String name = crate.getName();
        if (name.isEmpty()) return itemBuilder;

        final UUID uuid = itemBuilder.getPlayer();
        if (uuid == null) return itemBuilder;

        return itemBuilder.addNamePlaceholder("%keys%", NumberFormat.getNumberInstance().format(getVirtualKeys(uuid, name)))
                .addNamePlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(getPhysicalKeys(uuid, name)))
                .addNamePlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(getTotalKeys(uuid, name)))
                .addNamePlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(getCrateOpened(uuid, name)));
    }
}