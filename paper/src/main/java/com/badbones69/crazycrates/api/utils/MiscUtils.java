package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.enums.misc.Files;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MiscUtils {

    private static @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    public static void sendCommand(@NotNull final String command) {
        if (command.isEmpty()) return;

        Server server = plugin.getServer();

        server.getGlobalRegionScheduler().run(plugin, scheduledTask -> {
            ConsoleCommandSender console = server.getConsoleSender();

            server.dispatchCommand(console, command);
        });
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

        fireworkData.set(Keys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        plugin.getServer().getRegionScheduler().runDelayed(plugin, location, scheduledTask -> firework.detonate(), 3L);
    }

    public static @NotNull String location(@NotNull final Location location, boolean getName) {
        String name = getName ? location.getWorld().getName() : String.valueOf(location.getWorld().getUID());

        return name + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    /**
     * Converts a {@link String} to {@link Component}.
     *
     * @param component {@link String}
     * @return {@link Component}
     */
    public static @NotNull Component toComponent(@NotNull final String component) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(component.replace("§", "&"));
    }

    /**
     * Converts a lore to minimessage strings
     *
     * @param lore {@link List<Component>}
     * @return {@link List<Component>}
     */
    public static @NotNull List<Component> toComponent(@NotNull final List<String> lore) {
        return new ArrayList<>(lore.size()) {{
            lore.forEach(line -> add(toComponent(line)));
        }};
    }

    /**
     * Converts a lore to minimessage strings
     *
     * @param lore {@link List<Component>}
     * @return {@link List<Component>}
     */
    public static @NotNull List<String> fromComponent(@NotNull final List<Component> lore) {
        return fromComponent(lore, false);
    }

    /**
     * Converts a lore to minimessage strings
     *
     * @param lore {@link List<Component>}
     * @return {@link List<Component>}
     */
    public static @NotNull List<String> fromComponent(@NotNull final List<Component> lore, final boolean isMessage) {
        return new ArrayList<>(lore.size()) {{
            lore.forEach(line -> add(fromComponent(line, isMessage)));
        }};
    }

    /**
     * Converts a {@link Component} to {@link String}.
     *
     * @param component {@link Component}
     * @return {@link String}
     */
    public static @NotNull String fromComponent(@NotNull final Component component) {
        return fromComponent(component, false);
    }

    /**
     * Converts a {@link Component} to {@link String}.
     *
     * @param component {@link Component}
     * @param isMessage true or false
     * @return {@link String}
     */
    public static @NotNull String fromComponent(@NotNull final Component component, final boolean isMessage) {
        final String value = MiniMessage.miniMessage().serialize(component);

        if (isMessage) {
            return value.replace("\\<", "<");
        }

        return value;
    }

    /**
     * Converts a {@link String} to a {@link Component} then a {@link String}.
     *
     * @param component {@link String}
     * @return {@link String}
     */
    public static @NotNull String convert(@NotNull final String component)  {
        return convert(component, false);
    }

    /**
     * Converts a {@link List<String>} to a {@link List<Component>} then a {@link List<String>}.
     *
     * @param components {@link List<String>}
     * @return {@link List<String>}
     */
    public static @NotNull List<String> convert(@NotNull final List<String> components) {
        return convert(components, false);
    }

    /**
     * Converts a {@link List<String>} to a {@link List<Component>} then a {@link List<String>}.
     *
     * @param components {@link List<String>}
     * @param isMessage true or false
     * @return {@link List<String>}
     */
    public static @NotNull List<String> convert(@NotNull final List<String> components, final boolean isMessage) {
        return new ArrayList<>(components.size()) {{
            components.forEach(line -> add(convert(line, isMessage)));
        }};
    }

    /**
     * Converts a {@link String} to a {@link Component} then a {@link String}.
     *
     * @param component {@link String}
     * @param isMessage true or false
     * @return {@link String}
     */
    public static @NotNull String convert(@NotNull final String component, final boolean isMessage)  {
        return fromComponent(toComponent(component), isMessage);
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
        YamlConfiguration data = Files.data.getConfiguration();
        YamlConfiguration location = Files.locations.getConfiguration();

        boolean isSave = false;

        if (!location.contains("Locations")) {
            location.set("Locations.Clear", null);

            Files.locations.save();
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
            Files.data.save();
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
            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("Items cannot be null.");
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
            ).forEach(plugin.getComponentLogger()::warn);

            List.of(
                    "<red>An issue has occurred when trying to take a key.",
                    "<red>A list of potential reasons",
                    "",
                    " <yellow>-> <light_purple>Not enough keys.",
                    " <yellow>-> <light_purple>Key is in off hand."
            ).forEach(line -> player.sendRichMessage(MsgUtils.getPrefix(line)));
        }
    }

    public static long pickNumber(long min, long max) {
        max++;

        try {
            return min + ThreadLocalRandom.current().nextLong(max - min);
        } catch (IllegalArgumentException exception) {
            return min;
        }
    }

    public static int randomNumber(final int min, final int max) {
        return MiscUtils.useOtherRandom() ? min + ThreadLocalRandom.current().nextInt(max - min) : min + new Random().nextInt(max - min);
    }

    public static ItemBuilder getRandomPaneColor() {
        List<Material> panes = Arrays.asList(
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.MAGENTA_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.BROWN_STAINED_GLASS_PANE,
                Material.BLACK_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE
        );

        return new ItemBuilder(panes.get(ThreadLocalRandom.current().nextInt(panes.size())));
    }

    public static void addItem(final Player player, final ItemStack... items) {
        final Inventory inventory = player.getInventory();

        inventory.setMaxStackSize(64);
        inventory.addItem(items);
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

    public static boolean useOtherRandom() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.use_different_random);
    }

    public static void registerPermission(final String permission, final String description, final boolean isDefault) {
        if (permission.isEmpty()) return;

        PluginManager pluginManager = plugin.getServer().getPluginManager();

        if (pluginManager.getPermission(permission) != null) {
            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("Permission {} is already on the server. Pick a different name", permission);

            return;
        }

        if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("Permission {} is registered", permission);

        pluginManager.addPermission(new Permission(permission, description, isDefault ? PermissionDefault.TRUE : PermissionDefault.OP));
    }

    public static void unregisterPermission(final String permission) {
        if (permission.isEmpty()) return;

        PluginManager pluginManager = plugin.getServer().getPluginManager();

        if (pluginManager.getPermission(permission) == null) {
            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("Permission {} is not registered", permission);

            return;
        }

        if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("Permission {} is unregistered", permission);

        pluginManager.removePermission(permission);
    }

    public static void registerPermissions() {
        Arrays.stream(Permissions.values()).toList().forEach(permission -> {
            Permission newPermission = new Permission(
                    permission.getPermission(),
                    permission.getDescription(),
                    permission.isDefault(),
                    permission.getChildren()
            );

            plugin.getServer().getPluginManager().addPermission(newPermission);
        });
    }

    public static boolean isLogging() {
        return plugin.isVerbose();
    }

    public static boolean isExcellentCratesEnabled() {
        return plugin.getServer().getPluginManager().isPluginEnabled("ExcellentCrates");
    }
}