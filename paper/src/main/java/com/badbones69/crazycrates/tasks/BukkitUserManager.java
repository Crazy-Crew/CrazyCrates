package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class BukkitUserManager extends UserManager {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final FileConfiguration data = Files.DATA.getFile();

    @Override
    public Player getUser(UUID uuid) {
        return this.plugin.getServer().getPlayer(uuid);
    }

    @Override
    public int getVirtualKeys(UUID uuid, String crateName) {
        return this.data.getInt("Players." + uuid + "." + crateName, 0);
    }

    @Override
    public void addVirtualKeys(int amount, UUID uuid, String crateName) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid: " + uuid + " is null.");

            return;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        Player player = getUser(uuid);

        int keys = getVirtualKeys(uuid, crate.getName());

        if (!this.data.contains("Players." + uuid + ".Name")) this.data.set("Players." + uuid + ".Name", player.getName());

        this.data.set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));

        Files.DATA.saveFile();
    }

    @Override
    public void setKeys(int amount, UUID uuid, String crateName) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid: " + uuid + " is null.");

            return;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        Player player = getUser(uuid);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        this.data.set("Players." + player.getUniqueId() + ".Name", player.getName());
        this.data.set("Players." + player.getUniqueId() + "." + crate.getName(), amount);

        Files.DATA.saveFile();
    }

    private boolean isPlayerNull(UUID uuid) {
        return getUser(uuid) == null;
    }

    @Override
    public void addKeys(int amount, UUID uuid, String crateName, KeyType keyType) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        Player player = getUser(uuid);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        SettingsManager config = ConfigManager.getConfig();

        switch (keyType) {
            case physical_key -> {
                if (!MiscUtils.isInventoryFull(player)) {
                    player.getInventory().addItem(crate.getKey(amount, player));

                    return;
                }

                if (config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full)) {
                    addVirtualKeys(amount, player.getUniqueId(), crate.getName());

                    if (config.getProperty(ConfigKeys.notify_player_when_inventory_full)) {
                        Map<String, String> placeholders = new HashMap<>();

                        placeholders.put("{amount}", String.valueOf(amount));
                        placeholders.put("{player}", player.getName());
                        placeholders.put("{keytype}", keyType.getFriendlyName());
                        placeholders.put("{key}", crate.getKeyName());

                        player.sendMessage(Messages.cannot_give_player_keys.getMessage(placeholders, player));
                    }

                    return;
                }

                player.getWorld().dropItem(player.getLocation(), crate.getKey(amount, player));
            }

            case virtual_key -> addVirtualKeys(amount, player.getUniqueId(), crate.getName());
        }
    }

    @Override
    public int getTotalKeys(UUID uuid, String crateName) {
        return getVirtualKeys(uuid, crateName) + getPhysicalKeys(uuid, crateName);
    }

    @Override
    public int getPhysicalKeys(UUID uuid, String crateName) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return 0;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return 0;
        }

        Player player = getUser(uuid);
        
        int keys = 0;

        Crate crate = this.crateManager.getCrateFromName(crateName);

        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (!item.hasItemMeta() && !MiscUtils.useLegacyChecks()) continue;

            if (ItemUtils.isSimilar(item, crate)) keys += item.getAmount();
        }

        return keys;
    }

    @Override
    public boolean takeKeys(int amount, UUID uuid, String crateName, KeyType keyType, boolean checkHand) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return false;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        Player player = getUser(uuid);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        switch (keyType) {
            case physical_key -> {
                int takeAmount = amount;

                List<ItemStack> items = new ArrayList<>();

                if (checkHand) {
                    items.add(player.getEquipment().getItemInMainHand());
                    items.add(player.getEquipment().getItemInOffHand());
                } else {
                    items.addAll(Arrays.asList(player.getInventory().getContents()));
                }

                for (ItemStack item : items) {
                    if (item != null) {
                        if (ItemUtils.isSimilar(item, crate)) {
                            int keyAmount = item.getAmount();

                            if ((takeAmount - keyAmount) >= 0) {
                                MiscUtils.removeMultipleItemStacks(player.getInventory(), item);

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), amount, crate.getName());

                                takeAmount -= keyAmount;
                            } else {
                                item.setAmount(keyAmount - takeAmount);

                                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), amount, crate.getName());

                                takeAmount = 0;
                            }

                            if (takeAmount <= 0) return true;
                        }
                    }
                }

                // This needs to be done as player.getInventory().removeItem(ItemStack); does NOT remove from the offhand.
                if (takeAmount > 0) {
                    ItemStack item = player.getEquipment().getItemInOffHand();

                    if (ItemUtils.isSimilar(item, crate)) {
                        int keyAmount = item.getAmount();

                        if ((takeAmount - keyAmount) >= 0) {
                            player.getEquipment().setItemInOffHand(null);
                            takeAmount -= keyAmount;

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), amount, crate.getName());
                        } else {
                            item.setAmount(keyAmount - takeAmount);

                            if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), amount, crate.getName());

                            takeAmount = 0;
                        }

                        if (takeAmount <= 0) return true;
                    }
                }
            }

            case virtual_key -> {
                int keys = getVirtualKeys(uuid, crate.getName());

                this.data.set("Players." + uuid + ".Name", player.getName());

                int newAmount = Math.max((keys - amount), 0);

                if (newAmount < 1) {
                    this.data.set("Players." + uuid + "." + crate.getName(), null);
                } else {
                    this.data.set("Players." + uuid + "." + crate.getName(), newAmount);
                }

                if (crate.getCrateType() == CrateType.cosmic) addOpenedCrate(player.getUniqueId(), amount, crate.getName());

                Files.DATA.saveFile();

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
    public boolean hasPhysicalKey(UUID uuid, String crateName, boolean checkHand) {
        if (isPlayerNull(uuid)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Player with the uuid " + uuid + " is null.");

            return false;
        }

        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        Player player = getUser(uuid);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        List<ItemStack> items = new ArrayList<>();

        if (checkHand) {
            items.add(player.getEquipment().getItemInMainHand());
            items.add(player.getEquipment().getItemInOffHand());
        } else {
            items.addAll(Arrays.asList(player.getInventory().getContents()));
            items.removeAll(Arrays.asList(player.getInventory().getArmorContents()));
        }

        for (ItemStack item : items) {
            if (item != null) {
                if (ItemUtils.isSimilar(item, crate)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean addOfflineKeys(UUID uuid, String crateName, int keys, KeyType keyType) {
        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        try {
            if (keyType == KeyType.physical_key) {
                if (this.data.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) keys += this.data.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                this.data.set("Offline-Players." + uuid + ".Physical." + crate.getName(), keys);

                Files.DATA.saveFile();

                return true;
            }

            if (this.data.contains("Offline-Players." + uuid + "." + crate.getName())) keys += this.data.getInt("Offline-Players." + uuid + "." + crate.getName());

            this.data.set("Offline-Players." + uuid + "." + crate.getName(), keys);

            Files.DATA.saveFile();

            return true;
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not add keys to offline player with uuid: " + uuid, exception);

            return false;
        }
    }

    @Override
    public boolean takeOfflineKeys(UUID uuid, String crateName, int keys, KeyType keyType) {
        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return false;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        try {
            if (keyType == KeyType.physical_key) {
                int offlineKeys = this.data.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

                // If the offline keys are less than the keys the person wants to take. We will set the keys variable to how many offline keys they have.
                if (offlineKeys < keys) {
                    keys = offlineKeys;
                }

                this.data.set("Offline-Players." + uuid + ".Physical." + crate.getName(), this.data.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) - keys);

                // Remove the data if 0 keys remain after if checks.
                if (this.data.getInt("Offline-Players." + uuid + ".Physical." + crate.getName()) <= 0) this.data.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);

                Files.DATA.saveFile();

                return true;
            }

            this.data.set("Offline-Players." + uuid + "." + crate.getName(), this.data.getInt("Offline-Players." + uuid + "." + crate.getName()) - keys);

            Files.DATA.saveFile();

            return true;
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not take keys from offline player with uuid: " + uuid, exception);

            return false;
        }
    }

    public void loadOldOfflinePlayersKeys(Player player, List<Crate> crates) {
        FileConfiguration data = Files.DATA.getFile();
        String name = player.getName().toLowerCase();

        if (data.contains("Offline-Players." + name)) {
            for (Crate crate : crates) {
                if (data.contains("Offline-Players." + name + "." + crate.getName())) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        int keys = getVirtualKeys(player.getUniqueId(), crate.getName());
                        int addedKeys = data.getInt("Offline-Players." + name + "." + crate.getName());

                        data.set("Players." + player.getUniqueId() + "." + crate.getName(), (Math.max((keys + addedKeys), 0)));

                        Files.DATA.saveFile();
                    }
                }
            }

            data.set("Offline-Players." + name, null);

            Files.DATA.saveFile();
        }
    }

    /**
     * Load the offline keys of a player who has come online.
     *
     * @param player The player which you would like to load the offline keys for.
     */
    public void loadOfflinePlayersKeys(Player player, List<Crate> crates) {
        if (!this.data.contains("Offline-Players." + player.getUniqueId()) || crates.isEmpty()) return;

        UUID uuid = player.getUniqueId();

        for (Crate crate : crates) {
            if (this.data.contains("Offline-Players." + uuid + "." + crate.getName())) {
                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                int amount = this.data.getInt("Offline-Players." + uuid + "." + crate.getName());

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
                    addVirtualKeys(amount, uuid, crate.getName());
                }

                // If keys given is greater or equal than, remove data.
                if (keysGiven >= amount) this.data.set("Offline-Players." + uuid + "." + crate.getName(), null);
            }

            if (this.data.contains("Offline-Players." + uuid + ".Physical." + crate.getName())) {
                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.OFFLINE_PLAYER, 1);
                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) return;

                int keysGiven = 0;

                int amount = this.data.getInt("Offline-Players." + uuid + ".Physical." + crate.getName());

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
                if (keysGiven >= amount) this.data.set("Offline-Players." + uuid + ".Physical." + crate.getName(), null);
            }
        }

        ConfigurationSection physicalSection = this.data.getConfigurationSection("Offline-Players." + uuid + ".Physical");

        if (physicalSection != null) {
            if (physicalSection.getKeys(false).isEmpty()) this.data.set("Offline-Players." + uuid + ".Physical", null);
        }

        ConfigurationSection section = this.data.getConfigurationSection("Offline-Players." + uuid);

        if (section != null) {
            if (section.getKeys(false).isEmpty()) this.data.set("Offline-Players." + uuid, null);
        }

        Files.DATA.saveFile();
    }

    @Override
    public int getTotalCratesOpened(UUID uuid) {
        return this.data.getInt("Players." + uuid + ".tracking.total-crates", 0);
    }

    @Override
    public int getCrateOpened(UUID uuid, String crateName) {
        if (isCrateInvalid(crateName)) {
            this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");
            return 0;
        }
        
        return this.data.getInt("Players." + uuid + ".tracking." + crateName, 0);
    }

    @Override
    public void addOpenedCrate(UUID uuid, int amount, String crateName) {
        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        boolean hasValue = this.data.contains("Players." + uuid + ".tracking." + crate.getName());

        int newAmount;

        if (hasValue) {
            newAmount = this.data.getInt("Players." + uuid + ".tracking." + crate.getName()) + amount;

            this.data.set("Players." + uuid + ".tracking." + crate.getName(), newAmount);
            this.data.set("Players." + uuid + ".tracking.total-crates", this.data.getInt("Players." + uuid + ".tracking.total-crates") + amount);

            Files.DATA.saveFile();

            return;
        }

        this.data.set("Players." + uuid + ".tracking.total-crates", this.data.getInt("Players." + uuid + ".tracking.total-crates", 0)+amount);
        this.data.set("Players." + uuid + ".tracking." + crate.getName(), amount);

        Files.DATA.saveFile();
    }

    @Override
    public void addOpenedCrate(UUID uuid, String crateName) {
        if (isCrateInvalid(crateName)) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Crate " + crateName + " doesn't exist.");

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        boolean hasValue = this.data.contains("Players." + uuid + ".tracking." + crate.getName());

        int amount;

        if (hasValue) {
            amount = this.data.getInt("Players." + uuid + ".tracking." + crate.getName());

            this.data.set("Players." + uuid + ".tracking." + crate.getName(), amount + 1);
            this.data.set("Players." + uuid + ".tracking.total-crates", this.data.getInt("Players." + uuid + ".tracking.total-crates") + 1);

            Files.DATA.saveFile();

            return;
        }

        amount = this.data.contains("Players." + uuid + ".tracking.total-crates") ? this.data.getInt("Players." + uuid + ".tracking.total-crates") + 1 : 1;

        this.data.set("Players." + uuid + ".tracking.total-crates", amount);
        this.data.set("Players." + uuid + ".tracking." + crate.getName(), 1);

        Files.DATA.saveFile();
    }
    
    private boolean isCrateInvalid(String crateName) {
        return crateName.isBlank() || this.crateManager.getCrateFromName(crateName) == null;
    }
}