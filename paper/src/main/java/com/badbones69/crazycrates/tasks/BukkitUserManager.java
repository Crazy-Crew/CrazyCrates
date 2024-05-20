package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.CustomFiles;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.core.config.YamlFile;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.ConfigurationSection;
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
import java.util.logging.Level;

public class BukkitUserManager extends UserManager {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final CustomFiles data = CustomFiles.data;
    private @NotNull final YamlFile configuration = data.getYamlFile();

    @Override
    public Player getUser(@NotNull final UUID uuid) {
        return this.plugin.getServer().getPlayer(uuid);
    }

    @Override
    public int getVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName) {
        return this.configuration.getInt("Players." + uuid + "." + crateName, 0);
    }

    @Override
    public void addVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid: " + uuid + " is null.");

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        final Player player = getUser(uuid);

        final int keys = getVirtualKeys(uuid, crate.getName());

        if (!this.configuration.contains("Players." + uuid + ".Name")) this.configuration.set("Players." + uuid + ".Name", player.getName());

        this.configuration.set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));

        this.data.save();
    }

    @Override
    public void setKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid: " + uuid + " is null.");

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        final Player player = getUser(uuid);

        this.configuration.set("Players." + player.getUniqueId() + ".Name", player.getName());
        this.configuration.set("Players." + player.getUniqueId() + "." + crate.getName(), amount);

        this.data.save();
    }

    private boolean isPlayerNull(UUID uuid) {
        return getUser(uuid) == null;
    }

    @Override
    public void addKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull final KeyType keyType, final int amount) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        final Player player = getUser(uuid);

        final SettingsManager config = ConfigManager.getConfig();

        switch (keyType) {
            case physical_key -> {
                if (!MiscUtils.isInventoryFull(player)) {
                    player.getInventory().addItem(crate.getKey(amount, player));

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

                        player.sendRichMessage(Messages.cannot_give_player_keys.getMessage(player, placeholders));
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
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return 0;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

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
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return false;
        }

        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

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

                this.configuration.set("Players." + uuid + ".Name", player.getName());

                final int newAmount = Math.max((keys - amount), 0);

                if (newAmount < 1) {
                    this.configuration.set("Players." + uuid + "." + crate.getName(), null);
                } else {
                    this.configuration.set("Players." + uuid + "." + crate.getName(), newAmount);
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
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

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
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        try {
            if (keyType == KeyType.physical_key) {
                if (this.configuration.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) keys += this.configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                this.configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), keys);

                this.data.save();

                return true;
            }

            if (this.configuration.contains("Offline-Players." + uuid + "." + crate.getName())) keys += this.configuration.getInt("Offline-Players." + uuid + "." + crate.getName());

            this.configuration.set("Offline-Players." + uuid + "." + crate.getName(), keys);

            this.data.save();

            return true;
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not add keys to offline player with uuid: " + uuid, exception);

            return false;
        }
    }

    @Override
    public boolean takeOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int keys) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        try {
            if (keyType == KeyType.physical_key) {
                final int offlineKeys = this.configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                // If the offline keys are less than the keys the person wants to take. We will set the keys variable to how many offline keys they have.
                if (offlineKeys < keys) {
                    keys = offlineKeys;
                }

                this.configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), this.configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) - keys);

                // Remove the data if 0 keys remain after if checks.
                if (this.configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) <= 0) this.configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);

                this.data.save();

                return true;
            }

            //this.configuration.set("Offline-Players." + uuid + "." + crate.getName(), this.configuration.getInt("Offline-Players." + uuid + "." + crate.getName()) - keys);

            //this.data.save(this.fileManager);

            return true;
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not take keys from offline player with uuid: " + uuid, exception);

            return false;
        }
    }

    public void loadOldOfflinePlayersKeys(@NotNull final Player player, @NotNull final List<Crate> crates) {
        final String name = player.getName().toLowerCase();

        if (this.configuration.contains("Offline-Players." + name)) {
            for (final Crate crate : crates) {
                if (this.configuration.contains("Offline-Players." + name + "." + crate.getName())) {
                    final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        final int keys = getVirtualKeys(player.getUniqueId(), crate.getName());
                        final int addedKeys = this.configuration.getInt("Offline-Players." + name + "." + crate.getName());

                        this.configuration.set("Players." + player.getUniqueId() + "." + crate.getName(), (Math.max((keys + addedKeys), 0)));

                        this.data.save();
                    }
                }
            }

            this.configuration.set("Offline-Players." + name, null);

            this.data.save();
        }
    }

    /**
     * Load the offline keys of a player who has come online.
     *
     * @param player The player which you would like to load the offline keys for.
     */
    public void loadOfflinePlayersKeys(@NotNull final Player player, @NotNull final List<Crate> crates) {
        if (!this.configuration.contains("Offline-Players." + player.getUniqueId()) || crates.isEmpty()) return;

        final UUID uuid = player.getUniqueId();

        for (final Crate crate : crates) {
            if (this.configuration.contains("Offline-Players." + uuid + "." + crate.getName())) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                int amount = this.configuration.getInt("Offline-Players." + uuid + "." + crate.getName());

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
                    player.getInventory().addItem(crate.getKey(amount, player));
                } else {
                    // Otherwise add virtual keys.
                    addVirtualKeys(uuid, crate.getName(), amount);
                }

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) this.configuration.set("Offline-Players." + uuid + "." + crate.getName(), null);
            }

            if (this.configuration.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) {
                final PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                final int amount = this.configuration.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                while (keysGiven < amount) {
                    // If the inventory is full, drop the remaining keys then stop.
                    if (MiscUtils.isInventoryFull(player)) {
                        player.getWorld().dropItemNaturally(player.getLocation(), crate.getKey(amount - keysGiven, player));

                        break;
                    }

                    keysGiven++;
                }

                // If the inventory not full, add to inventory.
                player.getInventory().addItem(crate.getKey(keysGiven, player));

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) this.configuration.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);
            }
        }

        final ConfigurationSection physicalSection = this.configuration.getConfigurationSection("Offline-Players." + uuid + ".Physical");

        if (physicalSection != null) {
            if (physicalSection.getKeys(false).isEmpty()) this.configuration.set("Offline-Players." + uuid + ".Physical", null);
        }

        final ConfigurationSection section = this.configuration.getConfigurationSection("Offline-Players." + uuid);

        if (section != null) {
            if (section.getKeys(false).isEmpty()) this.configuration.set("Offline-Players." + uuid, null);
        }

        this.data.save();
    }

    @Override
    public int getTotalCratesOpened(@NotNull final UUID uuid) {
        return this.configuration.getInt("Players." + uuid + ".tracking.total-crates", 0);
    }

    @Override
    public int getCrateOpened(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return 0;
        }

        return this.configuration.getInt("Players." + uuid + ".tracking." + crateName, 0);
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        final boolean hasValue = this.configuration.contains("Players." + uuid + ".tracking." + crate.getName());

        int newAmount;

        if (hasValue) {
            newAmount = this.configuration.getInt("Players." + uuid + ".tracking." + crate.getName()) + amount;

            this.configuration.set("Players." + uuid + ".tracking." + crate.getName(), newAmount);
            this.configuration.set("Players." + uuid + ".tracking.total-crates", this.configuration.getInt("Players." + uuid + ".tracking.total-crates") + amount);

            this.data.save();

            return;
        }

        this.configuration.set("Players." + uuid + ".tracking.total-crates", this.configuration.getInt("Players." + uuid + ".tracking.total-crates", 0)+amount);
        this.configuration.set("Players." + uuid + ".tracking." + crate.getName(), amount);

        this.data.save();
    }

    @Override
    public void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName) {
        final Crate crate = isCrateInvalid(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        boolean hasValue = this.configuration.contains("Players." + uuid + ".tracking." + crate.getName());

        int amount;

        if (hasValue) {
            amount = this.configuration.getInt("Players." + uuid + ".tracking." + crate.getName());

            this.configuration.set("Players." + uuid + ".tracking." + crate.getName(), amount + 1);
            this.configuration.set("Players." + uuid + ".tracking.total-crates", this.configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1);

            this.data.save();

            return;
        }

        amount = this.configuration.contains("Players." + uuid + ".tracking.total-crates") ? this.configuration.getInt("Players." + uuid + ".tracking.total-crates") + 1 : 1;

        this.configuration.set("Players." + uuid + ".tracking.total-crates", amount);
        this.configuration.set("Players." + uuid + ".tracking." + crate.getName(), 1);

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