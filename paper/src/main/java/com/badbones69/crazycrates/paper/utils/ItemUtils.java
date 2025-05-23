package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.ryderbelserion.fusion.core.utils.NumberUtils;
import com.ryderbelserion.fusion.paper.api.builders.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.api.builders.items.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.api.builders.items.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.api.builders.items.types.SkullBuilder;
import com.ryderbelserion.fusion.paper.api.builders.items.types.SpawnerBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final ComponentLogger logger = plugin.getComponentLogger();

    private static final CrateManager crateManager = plugin.getCrateManager();

    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        final int amount = item.getAmount();

        final PlayerInventory inventory = player.getInventory();

        if (amount <= 1 && !inventory.isEmpty()) {
            inventory.removeItem(item);

            return;
        }

        item.setAmount(amount - 1);
    }

    public static String getEnchant(@NotNull final String enchant) {
        if (enchant.isEmpty()) return "";

        switch (enchant) {
            case "PROTECTION_ENVIRONMENTAL" -> {
                return "protection";
            }

            case "PROTECTION_FIRE" -> {
                return "fire_protection";
            }

            case "PROTECTION_FALL" -> {
                return "feather_falling";
            }

            case "PROTECTION_EXPLOSIONS" -> {
                return "blast_protection";
            }

            case "PROTECTION_PROJECTILE" -> {
                return "projectile_protection";
            }

            case "OXYGEN" -> {
                return "respiration";
            }

            case "WATER_WORKER" -> {
                return "aqua_affinity";
            }

            case "DAMAGE_ALL" -> {
                return "sharpness";
            }

            case "DAMAGE_UNDEAD" -> {
                return "smite";
            }

            case "DAMAGE_ARTHROPODS" -> {
                return "bane_of_arthropods";
            }

            case "LOOT_BONUS_MOBS" -> {
                return "looting";
            }

            case "SWEEPING_EDGE" -> {
                return "sweeping";
            }

            case "DIG_SPEED" -> {
                return "efficiency";
            }

            case "DURABILITY" -> {
                return "unbreaking";
            }

            case "LOOT_BONUS_BLOCKS" -> {
                return "fortune";
            }

            case "ARROW_DAMAGE" -> {
                return "power";
            }

            case "ARROW_KNOCKBACK" -> {
                return "punch";
            }

            case "ARROW_FIRE" -> {
                return "flame";
            }

            case "ARROW_INFINITE" -> {
                return "infinity";
            }

            case "LUCK" -> {
                return "luck_of_the_sea";
            }

            default -> {
                return enchant.toLowerCase();
            }
        }
    }

    public static String getPotion(@NotNull final String potion) {
        return potion.isEmpty() ? "" : potion.toLowerCase();
    }

    public static boolean isSimilar(@NotNull final ItemStack itemStack, @NotNull final Crate crate) {
        return crateManager.isKeyFromCrate(itemStack, crate);
    }

    public static String getKey(@NotNull final PersistentDataContainerView container) {
        return container.get(ItemKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }

    public static @NotNull LegacyItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final LegacyItemBuilder builder, @NotNull final Player player) {
        return getItem(section, builder.setPlayer(player));
    }

    public static @NotNull LegacyItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final LegacyItemBuilder builder) {
        if (section.contains("Glowing")) {
            builder.setGlowing(section.getBoolean("Glowing", false));
        }
        
        builder.setDamage(section.getInt("DisplayDamage", 0));
        
        builder.setDisplayLore(section.getStringList("Lore"));

        builder.addPatterns(section.getStringList("Patterns"));

        builder.setHidingItemFlags(section.getBoolean("HideItemFlags", false) || !section.getStringList("Flags").isEmpty());

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        if (section.contains("Skull")) {
            builder.setSkull(section.getString("Skull", ""));
        }
        
        if (section.contains("Player") && builder.isPlayerHead()) {
            builder.setPlayer(section.getString("Player", ""));
        }

        builder.setCustomModelData(section.getString("Custom-Model-Data", ""));

        builder.setItemModel(section.getString("Model.Namespace", ""), section.getString("Model.Id", ""));
        
        if (section.contains("DisplayTrim.Pattern") && builder.isArmor()) {
            builder.applyTrimPattern(section.getString("DisplayTrim.Pattern", "sentry"));
        }
        
        if (section.contains("DisplayTrim.Material") && builder.isArmor()) {
            builder.applyTrimMaterial(section.getString("DisplayTrim.Material", "quartz"));
        }
        
        if (section.contains("DisplayEnchantments")) {
            for (final String ench : section.getStringList("DisplayEnchantments")) {
                String[] value = ench.split(":");

                builder.addEnchantment(value[0], Integer.parseInt(value[1]), true);
            }
        }
        
        return builder;
    }

    public static LegacyItemBuilder convertItemStack(@Nullable final Player player, @NotNull final ItemStack itemStack) {
        LegacyItemBuilder itemBuilder = new LegacyItemBuilder(itemStack);

        if (player != null) {
            itemBuilder.setPlayer(player);
        }

        return itemBuilder;
    }

    public static LegacyItemBuilder convertItemStack(@NotNull final ItemStack itemStack) {
        return convertItemStack(null, itemStack);
    }

    public static List<ItemBuilder> convertConfigurationSection(final ConfigurationSection section) {
        final List<ItemBuilder> cache = new ArrayList<>();

        if (section == null) return cache;

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(key);

            if (item == null) continue;

            final ItemBuilder itemBuilder = ItemBuilder.from(item.getString("material", "stone"));

            if (item.contains("data")) {
                final String base64 = item.getString("data", null);

                if (base64 != null && !base64.isEmpty()) { //todo() move this if check to fusion's itembuilder as we should not set a name if it's empty to ensure Minecraft can do it's thing.
                    itemBuilder.withBase64(base64);
                }
            }

            if (item.contains("name")) { //todo() move this if check to fusion's itembuilder as we should not set a name if it's empty to ensure Minecraft can do it's thing.
                itemBuilder.setDisplayName(item.getString("name", ""));
            }

            if (item.contains("lore")) {
                itemBuilder.withDisplayLore(item.getStringList("lore"));
            }

            if (item.isString("amount")) {
                final Optional<Number> integer = NumberUtils.tryParseInt(item.getString("amount", "1"));

                integer.ifPresent(number -> itemBuilder.setAmount(number.intValue()));
            } else {
                itemBuilder.setAmount(item.getInt("amount", 1));
            }

            final ConfigurationSection enchantments = item.getConfigurationSection("enchantments");

            if (enchantments != null) {
                for (final String enchantment : enchantments.getKeys(false)) {
                    final int level = enchantments.getInt(enchantment);

                    itemBuilder.addEnchantment(enchantment, level);
                }
            }

            itemBuilder.setCustomModelData(item.getString("custom-model-data", ""));

            if (item.getBoolean("hide-tool-tip", false)) {
                itemBuilder.hideToolTip();
            }

            itemBuilder.setUnbreakable(item.getBoolean("unbreakable-item", false));

            // settings
            itemBuilder.setEnchantGlint(item.getBoolean("settings.glowing", false));

            final String player = item.getString("settings.player", null);

            if (player != null && !player.isEmpty()) {
                final SkullBuilder skullBuilder = itemBuilder.asSkullBuilder();

                skullBuilder.withName(player).build();
            }

            itemBuilder.setItemDamage(item.getInt("settings.damage", 0));

            itemBuilder.withSkull(item.getString("settings.skull", ""));

            final String rgb = item.getString("settings.rgb", "");

            final String color = item.getString("settings.color", "");

            itemBuilder.setColor(!color.isEmpty() ? color : !rgb.isEmpty() ? rgb : "");

            final String mobType = item.getString("settings.mob.type", null);

            if (mobType != null && !mobType.isEmpty()) {
                final SpawnerBuilder spawnerBuilder = itemBuilder.asSpawnerBuilder();

                spawnerBuilder.withEntityType(com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(mobType)).build();
            }

            if (itemBuilder.isArmor()) { //todo() move to setTrim method in fusion
                itemBuilder.setTrim(item.getString("settings.trim.pattern", ""), item.getString("settings.trim.material", ""));
            }

            final ConfigurationSection potions = item.getConfigurationSection("settings.potions");

            if (potions != null) {
                final PotionBuilder potionBuilder = itemBuilder.asPotionBuilder();

                for (final String potion : potions.getKeys(false)) {
                    final PotionEffectType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getPotionEffect(potion);

                    if (type != null) {
                        final ConfigurationSection data = potions.getConfigurationSection(potion);

                        if (data != null) {
                            final int duration = data.getInt("duration", 10) * 20;
                            final int level = data.getInt("level", 1);

                            final boolean icon = data.getBoolean("style.icon", false);
                            final boolean ambient = data.getBoolean("style.ambient", false);
                            final boolean particles = data.getBoolean("style.particles", false);

                            potionBuilder.withPotionEffect(type, duration, level, ambient, particles, icon);
                        }
                    }
                }

                potionBuilder.build();
            }

            final ConfigurationSection patterns = item.getConfigurationSection("settings.patterns");

            if (patterns != null) {
                for (final String pattern : patterns.getKeys(false)) {
                    final String patternColor = patterns.getString(pattern, "white");

                    final PatternBuilder patternBuilder = itemBuilder.asPatternBuilder();

                    patternBuilder.addPattern(pattern, patternColor);

                    patternBuilder.build();
                }
            }

            cache.add(itemBuilder);
        }

        return cache;
    }

    public static List<ItemBuilder> convertStringList(@NotNull final List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    public static List<ItemBuilder> convertStringList(@NotNull final List<String> itemStrings, @Nullable final String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    public static ItemBuilder convertString(@NotNull final String itemString) {
        return convertString(itemString, null);
    }

    public static ItemBuilder convertString(@NotNull final String itemString, @Nullable final String section) {
        ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE);

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];

                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.withCustomItem(value.toLowerCase());
                    case "data" -> itemBuilder = itemBuilder.withBase64(value);
                    case "name" -> itemBuilder.setDisplayName(value);
                    case "mob" -> {
                        final SpawnerBuilder builder = itemBuilder.asSpawnerBuilder();

                        final EntityType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(value);

                        if (type != null) {
                            builder.withEntityType(type);
                        }
                    }

                    case "glowing" -> itemBuilder.setEnchantGlint(NumberUtils.tryParseBoolean(value).orElse(false));
                    case "amount" -> {
                        final Optional<Number> amount = NumberUtils.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }

                    case "damage" -> {
                        final Optional<Number> amount = NumberUtils.tryParseInt(value);
                        itemBuilder.setItemDamage(amount.map(Number::intValue).orElse(0));
                    }

                    case "lore" -> itemBuilder.withDisplayLore(List.of(value.split(",")));
                    case "player" -> {
                        final SkullBuilder skullBuilder = itemBuilder.asSkullBuilder();

                        skullBuilder.withName(value);
                    }

                    case "skull" -> {
                        final SkullBuilder skullBuilder = itemBuilder.asSkullBuilder();

                        skullBuilder.withSkull(value);
                    }

                    case "custom-model-data" -> itemBuilder.setCustomModelData(value);
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "hide-tool-tip" -> {
                        if (value.equalsIgnoreCase("true")) {
                            itemBuilder.hideToolTip();
                        }
                    }

                    case "trim" -> {
                        if (itemBuilder.isArmor()) { // only set trims to armor, move to fusion later maybe?
                            String[] split = value.split("!"); // trim:trim_pattern!trim_material

                            String trim = split[0];
                            String material = split[1];

                            itemBuilder.setTrim(trim.toLowerCase(), material.toLowerCase());
                        }
                    }

                    case "rgb", "color" -> itemBuilder.setColor(value);

                    default -> {
                        final Enchantment enchantment = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEnchantment(getEnchant(option));

                        if (enchantment != null) {
                            final Optional<Number> level = NumberUtils.tryParseInt(value);

                            itemBuilder.addEnchantment(getEnchant(option), level.map(Number::intValue).orElse(1));
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.hideToolTip();

                                break;
                            }
                        }

                        if (itemBuilder.isBanner() || itemBuilder.isShield()) {
                            final PatternBuilder builder = itemBuilder.asPatternBuilder();

                            builder.addPattern(value, option).build();
                        }
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.withType(ItemType.RED_TERRACOTTA).setDisplayName("<red>Error found!, Prize Name: " + section);

            if (MiscUtils.isLogging()) logger.warn("An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }
}