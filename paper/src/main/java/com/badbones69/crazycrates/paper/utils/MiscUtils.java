package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.api.enums.Permissions;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.ryderbelserion.fusion.core.files.FileAction;
import com.ryderbelserion.fusion.core.utils.FileUtils;
import com.ryderbelserion.fusion.paper.api.enums.Scheduler;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MiscUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    
    private static final ComponentLogger logger = plugin.getComponentLogger();

    public static void sendCommand(@Nullable final CommandSender sender, @NotNull final String command, @NotNull final Map<String, String> placeholders) {
        if (command.isEmpty()) return;

        final Server server = plugin.getServer();

        final String result = populatePlaceholders(sender, command, placeholders);

        new FoliaScheduler(plugin, Scheduler.global_scheduler) {
            @Override
            public void run() {
                server.dispatchCommand(server.getConsoleSender(), result);
            }
        }.runNow();
    }

    public static void sendCommand(@NotNull final String command, @NotNull final Map<String, String> placeholders) {
        sendCommand(null, command, placeholders);
    }

    public static void sendCommand(@NotNull final String command) {
        sendCommand(command, new HashMap<>());
    }

    public static String populatePlaceholders(@Nullable final CommandSender sender, @NotNull String line, @NotNull final Map<String, String> placeholders) {
        if (sender != null && Plugins.placeholder_api.isEnabled()) {
            if (sender instanceof Player player) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
        }

        if (!placeholders.isEmpty()) {
            for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {

                if (placeholder != null) {
                    final String key = placeholder.getKey();
                    final String value = placeholder.getValue();

                    if (key != null && value != null) {
                        line = line.replace(key, value).replace(key.toLowerCase(), value);
                    }
                }
            }
        }

        return line;
    }

    public static void janitor() {
        final File logsFolder = new File(plugin.getDataFolder(), "logs");

        if (logsFolder.exists() && ConfigManager.getConfig().getProperty(ConfigKeys.log_to_file)) {
            final File crateLog = FileKeys.crate_log.getFile();
            final File keyLog = FileKeys.key_log.getFile();

            try {
                FileUtils.compress(logsFolder.toPath(), null, "", new ArrayList<>() {{
                    add(FileAction.DELETE);
                }});

                if (!crateLog.exists()) {
                    crateLog.createNewFile();
                }

                if (!keyLog.exists()) {
                    keyLog.createNewFile();
                }
            } catch (final IOException exception) {
                if (isLogging()) logger.warn("Failed to create log files.", exception);
            }
        }
    }

    public static double calculateWeight(final int chance, final int maxRange) {
        return new BigDecimal((double) chance / maxRange * 100D).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public static void spawnFirework(@NotNull final Location location, @Nullable final Color color) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect.@NotNull Builder effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .trail(false)
                .flicker(false);

        if (color != null) effect.withColor(color); else effect.withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW);

        fireworkMeta.addEffects(effect.build());
        fireworkMeta.setPower(0);

        firework.setFireworkMeta(fireworkMeta);

        PersistentDataContainer fireworkData = firework.getPersistentDataContainer();

        fireworkData.set(ItemKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        firework.getScheduler().runDelayed(plugin, scheduledTask -> firework.detonate(), null, 3L);
    }

    public static @NotNull String location(@NotNull final Location location, final boolean getName) {
        String name = getName ? location.getWorld().getName() : String.valueOf(location.getWorld().getUID());

        return name + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    /**
     * Converts a {@link Location} without the world name.
     *
     * @param location {@link Location}
     * @return {@link String}
     */
    public static @NotNull String location(@NotNull final Location location) {
        return location(location, false);
    }

    /**
     * Checks if the player's inventory is empty by checking if the first empty slot is -1.
     *
     * @return true if inventory is empty otherwise false
     */
    public static boolean isInventoryFull(@NotNull final Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static void save() {
        YamlConfiguration data = FileKeys.data.getConfiguration();
        YamlConfiguration location = FileKeys.locations.getConfiguration();

        boolean isSave = false;

        if (!location.contains("Locations")) {
            location.set("Locations.Clear", null);

            isSave = true;
        }

        if (!data.contains("Players")) {
            data.set("Players.Clear", null);

            isSave = true;
        }

        if (!data.contains("Prizes")) {
            data.set("Prizes.Clear", null);

            isSave = true;
        }

        if (isSave) {
            FileKeys.data.save();
        }
    }

    // ElectronicBoy is the author.
    public static @Nullable Map<Integer, ItemStack> removeMultipleItemStacks(@NotNull final Inventory inventory, @NotNull final ItemStack... items) {
        if (items != null) {
            Map<Integer, ItemStack> leftover = new HashMap<>();

            // TODO: optimization

            for (int i = 0; i < items.length; i++) {
                ItemStack item = items[i];

                int toDelete = item.getAmount();

                while (true) {
                    // Paper start - Allow searching entire contents
                    ItemStack[] toSearch = inventory.getContents();

                    int first = getFirstItem(item, false, toSearch);
                    // Paper end

                    // Drat! we don't have this type in the inventory
                    if (first == -1) {
                        item.setAmount(toDelete);

                        leftover.put(i, item);

                        break;
                    } else {
                        ItemStack itemStack = inventory.getItem(first);

                        if (itemStack != null) {
                            int amount = itemStack.getAmount();

                            if (amount <= toDelete) {
                                toDelete -= amount;
                                // clear the slot, all used up
                                inventory.clear(first);
                            } else {
                                // split the stack and store
                                itemStack.setAmount(amount - toDelete);

                                inventory.setItem(first, itemStack);

                                toDelete = 0;
                            }
                        }
                    }

                    // Bail when done
                    if (toDelete <= 0) {
                        break;
                    }
                }
            }

            return leftover;
        } else {
            if (MiscUtils.isLogging()) logger.warn("Items cannot be null.");
        }

        return null;
    }

    /**
     * Gets the first item amount in an inventory.
     *
     * @return -1 or item amount
     */
    private static int getFirstItem(@Nullable final ItemStack item, final boolean getAmount, @Nullable final ItemStack[] inventory) {
        if (item == null) return -1;

        for (int i = 0; i < inventory.length; i++) {
            ItemStack inventoryItem = inventory[i];

            if (inventoryItem != null) {
                if ((getAmount && item.equals(inventoryItem)) || (!getAmount && item.isSimilar(inventoryItem))) return i;
            }
        }

        return -1;
    }

    public static void failedToTakeKey(@NotNull final CommandSender player, @NotNull final String crateName) {
        if (MiscUtils.isLogging()) {
            List.of(
                    "An error has occurred while trying to take a key from a player.",
                    "Player: " + player.getName(),
                    "Key: " + crateName
            ).forEach(logger::warn);

            List.of(
                    "=== === === === === === Crates === === === === === ===",
                    "<red>An issue has occurred when trying to take a key.",
                    "<red>A list of potential reasons",
                    "",
                    " <yellow>-> <light_purple>Not enough keys.",
                    " <yellow>-> <light_purple>Key is in off hand.",
                    "=== === === === === === Crates === === === === === ==="
            ).forEach(player::sendRichMessage);
        }
    }

    public static long pickNumber(final long min, long max) {
        max++;

        try {
            return min + getRandom().nextLong(max - min);
        } catch (IllegalArgumentException exception) {
            return min;
        }
    }

    public static int randomNumber(final int min, final int max) {
        return min + getRandom().nextInt(max - min);
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static Random getRandom() {
        return useDifferentRandom() ? ThreadLocalRandom.current() : new Random();
    }

    public static LegacyItemBuilder getRandomPaneColor() {
        List<ItemType> panes = Arrays.asList(
                ItemType.LIGHT_BLUE_STAINED_GLASS_PANE,
                ItemType.MAGENTA_STAINED_GLASS_PANE,
                ItemType.YELLOW_STAINED_GLASS_PANE,
                ItemType.PURPLE_STAINED_GLASS_PANE,
                ItemType.ORANGE_STAINED_GLASS_PANE,
                ItemType.GREEN_STAINED_GLASS_PANE,
                ItemType.BROWN_STAINED_GLASS_PANE,
                ItemType.BLACK_STAINED_GLASS_PANE,
                ItemType.BLUE_STAINED_GLASS_PANE,
                ItemType.CYAN_STAINED_GLASS_PANE,
                ItemType.GRAY_STAINED_GLASS_PANE,
                ItemType.LIME_STAINED_GLASS_PANE,
                ItemType.PINK_STAINED_GLASS_PANE,
                ItemType.RED_STAINED_GLASS_PANE
        );

        return new LegacyItemBuilder(plugin, panes.get(ThreadLocalRandom.current().nextInt(panes.size())));
    }

    public static void addItem(@NotNull final Player player, @NotNull final ItemStack... items) {
        final Inventory inventory = player.getInventory();

        inventory.setMaxStackSize(64);

        Arrays.asList(items).forEach(item -> inventory.addItem(item.clone()));
    }

    /**
     * Decides when the crate should start to slow down.
     */
    public static List<Integer> slowSpin(int full, int cut) {
        List<Integer> slow = new ArrayList<>();

        for (int index = full; cut > 0; full--) {
            if (full <= index - cut || full >= index - cut) {
                slow.add(index);
                index -= cut;
                cut--;
            }
        }

        return slow;
    }

    public static boolean useDifferentRandom() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.use_different_random);
    }

    public static void registerPermission(@NotNull final String permission, @NotNull final String description, final boolean isDefault) {
        if (permission.isEmpty()) return;

        if (pluginManager.getPermission(permission) != null) {
            if (isLogging()) logger.warn("Permission {} is already on the server. Pick a different name", permission);

            return;
        }

        if (isLogging()) logger.warn("Permission {} is registered", permission);

        pluginManager.addPermission(new Permission(permission, description, isDefault ? PermissionDefault.TRUE : PermissionDefault.OP));
    }

    public static void unregisterPermission(final String permission) {
        if (permission.isEmpty()) return;

        if (pluginManager.getPermission(permission) == null) {
            if (isLogging()) logger.warn("Permission {} is not registered", permission);

            return;
        }

        if (isLogging()) logger.warn("Permission {} is unregistered", permission);

        pluginManager.removePermission(permission);
    }

    private static final PluginManager pluginManager = plugin.getServer().getPluginManager();

    public static void registerPermissions() {
        Arrays.stream(Permissions.values()).toList().forEach(permission -> {
            Permission newPermission = new Permission(
                    permission.getPermission(),
                    permission.getDescription(),
                    permission.isDefault(),
                    permission.getChildren()
            );

            pluginManager.addPermission(newPermission);
        });
    }

    public static boolean isLogging() {
        return plugin.getFusion().isVerbose();
    }

    public static boolean isExcellentCratesEnabled() {
        return pluginManager.isPluginEnabled("ExcellentCrates");
    }
}