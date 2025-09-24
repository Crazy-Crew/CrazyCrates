package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builders.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builders.types.SkullBuilder;
import com.ryderbelserion.fusion.paper.builders.types.SpawnerBuilder;
import com.ryderbelserion.fusion.paper.builders.types.custom.CustomBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final FusionPaper fusion = plugin.getFusion();

    private static final CrateManager crateManager = plugin.getCrateManager();

    private static final StringUtils utils = fusion.getStringUtils();

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

    public static @NotNull ItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemBuilder builder) {
        if (section.contains("Glowing")) {
            builder.addEnchantGlint(section.getBoolean("Glowing", false));
        }
        
        builder.setItemDamage(section.getInt("DisplayDamage", -1));
        
        builder.withDisplayLore(MiscUtils.replacePlaceholders(section.getStringList("Lore")));

        //builder.addPatterns(section.getStringList("Patterns")); //todo() yo

        //builder.setHidingItemFlags(section.getBoolean("HideItemFlags", false) || !section.getStringList("Flags").isEmpty());

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        //if (section.contains("Skull")) {
        //    builder.setSkull(section.getString("Skull", ""));
        //}
        
        //if (section.contains("Player") && builder.isPlayerHead()) {
        //    builder.setPlayer(section.getString("Player", ""));
        //}

        if (section.contains("Custom-Model-Data")) {
            builder.asCustomBuilder().setCustomModelData(section.getString("Custom-Model-Data", "")).build();
        }

        if (section.contains("Model.Namespace") && section.contains("Model.Id")) {
            builder.asCustomBuilder().setItemModel(section.getString("Model.Namespace", ""), section.getString("Model.Id", ""));
        }

        if (section.contains("DisplayTrim.Pattern") && section.contains("DisplayTrim.Material") && builder.isArmor()) {
            builder.setTrim(section.getString("DisplayTrim.Pattern", "sentry"), section.getString("DisplayTrim.Material", "quartz"));
        }
        
        if (section.contains("DisplayEnchantments")) {
            for (final String ench : section.getStringList("DisplayEnchantments")) {
                String[] value = ench.split(":");

                builder.addEnchantment(value[0], Integer.parseInt(value[1]));
            }
        }
        
        return builder;
    }

    public static List<ItemBuilder> convertConfigurationSection(final ConfigurationSection section) {
        final List<ItemBuilder> cache = new ArrayList<>();

        if (section == null) return cache;

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(key);

            if (item == null) continue;

            final ItemBuilder itemBuilder = new ItemBuilder(item.getString("material", "stone"));

            itemBuilder.withBase64(item.getString("data", ""));

            itemBuilder.withDisplayName(MiscUtils.replacePlaceholders(item.getString("name", "")));

            if (item.contains("lore")) {
                itemBuilder.withDisplayLore(MiscUtils.replacePlaceholders(item.getStringList("lore")));
            }

            if (item.isString("amount")) {
                final Optional<Number> integer = fusion.getStringUtils().tryParseInt(item.getString("amount", "1"));

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

            if (item.contains("custom-model-data")) {
                final CustomBuilder customBuilder = itemBuilder.asCustomBuilder();

                customBuilder.setCustomModelData(item.getString("custom-model-data", "")).build();
            }

            if (item.getBoolean("hide-tool-tip", false)) {
                itemBuilder.hideToolTip();
            }

            itemBuilder.hideComponents(item.getStringList("hidden-components"));

            if (item.contains("unbreakable-item")) {
                itemBuilder.setUnbreakable(item.getBoolean("unbreakable-item", false));
            }

            if (item.contains("settings.glowing")) {
                itemBuilder.addEnchantGlint(item.getBoolean("settings.glowing", false));
            }

            final String player = item.getString("settings.player", null);

            if (player != null && !player.isEmpty() && itemBuilder.isPlayerHead()) {
                final SkullBuilder skullBuilder = itemBuilder.asSkullBuilder();

                skullBuilder.withName(player).build();
            }

            itemBuilder.setItemDamage(item.getInt("settings.damage", -1));

            itemBuilder.withSkull(item.getString("settings.skull", ""));

            final String rgb = item.getString("settings.rgb", "");

            final String color = item.getString("settings.color", "");

            final String value = !color.isEmpty() ? color : !rgb.isEmpty() ? rgb : "";

            itemBuilder.setColor(value);

            final String mobType = item.getString("settings.mob.type", null);

            if (mobType != null && !mobType.isEmpty()) {
                final SpawnerBuilder spawnerBuilder = itemBuilder.asSpawnerBuilder();

                spawnerBuilder.withEntityType(com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(mobType)).build();
            }

            itemBuilder.setTrim(item.getString("settings.trim.pattern", ""), item.getString("settings.trim.material", ""));

            if (itemBuilder.isPotion()) {
                final ConfigurationSection potions = item.getConfigurationSection("settings.potions");

                final PotionBuilder potionBuilder = itemBuilder.asPotionBuilder();

                if (potions != null) {
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
                }

                potionBuilder.setColor(value).build();
            }

            final ConfigurationSection patterns = item.getConfigurationSection("settings.patterns");

            if (patterns != null) {
                final PatternBuilder patternBuilder = itemBuilder.asPatternBuilder();

                for (final String pattern : patterns.getKeys(false)) {
                    final String patternColor = patterns.getString(pattern, "white");

                    patternBuilder.addPattern(pattern, patternColor);
                }

                patternBuilder.build();
            }

            cache.add(itemBuilder);
        }

        return cache;
    }

    public static List<ItemBuilder> convertStringList(@NotNull final List<String> itemStrings, @NotNull final String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    public static ItemBuilder convertString(@NotNull final String itemString, @NotNull final String section) {
        return new ItemBuilder(ItemType.RED_TERRACOTTA, consumer -> {
            try {
                for (final String content : itemString.split(", ")) {
                    String option = content.split(":")[0];
                    String value = option.replace(option + ":", "").replace(option, "");

                    switch (option.toLowerCase()) {
                        case "item" -> consumer.withCustomItem(value.toLowerCase());
                        case "data" -> consumer.withBase64(value);
                        case "name" -> consumer.withDisplayName(MiscUtils.replacePlaceholders(value));
                        case "glowing" -> consumer.addEnchantGlint(utils.tryParseBoolean(value).orElse(false));
                        case "amount" -> consumer.setAmount(utils.tryParseInt(value).map(Number::intValue).orElse(1));
                        case "damage" -> consumer.setItemDamage(utils.tryParseInt(value).map(Number::intValue).orElse(-1));
                        case "lore" -> //noinspection unchecked
                                consumer.withDisplayLore(MiscUtils.replacePlaceholders(List.of(value.split(","))));
                        case "player" -> {
                            if (consumer.isPlayerHead()) {
                                consumer.asSkullBuilder().withName(value).build();
                            } else {
                                fusion.log("warn", "The item type supplied for {} is not a player_head", section);
                            }
                        }

                        case "skull" -> {
                            if (consumer.isPlayerHead()) {
                                consumer.asSkullBuilder().withSkull(value).build();
                            } else {
                                fusion.log("warn", "The item type supplied for {} is not a player_head", section);
                            }
                        }

                        case "mob" -> {
                            if (consumer.isSpawner()) {
                                final EntityType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(value);

                                if (type != null) {
                                    consumer.asSpawnerBuilder().withEntityType(type).build();
                                }
                            } else {
                                fusion.log("warn", "The item type supplied for {} is not mob_spawner", section);
                            }
                        }

                        case "custom-model-data" -> consumer.asCustomBuilder().setCustomModelData(value);
                        case "unbreakable-item" -> consumer.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                        case "hide-tool-tip" -> {
                            if (value.equalsIgnoreCase("true")) {
                                consumer.hideToolTip();
                            }
                        }

                        case "trim" -> {
                            if (consumer.isArmor()) { // only set trims to armor, move to fusion later maybe?
                                final String[] split = value.split("!"); // trim:trim_pattern!trim_material

                                final String trim = split[0];
                                final String material = split[1];

                                consumer.setTrim(trim.toLowerCase(), material.toLowerCase());
                            }
                        }

                        case "rgb", "color" -> consumer.setColor(value);

                        default -> {
                            if (com.ryderbelserion.fusion.paper.utils.ItemUtils.getEnchantment(option.toLowerCase()) != null) {
                                consumer.addEnchantment(option.toLowerCase(), utils.tryParseInt(value).map(Number::intValue).orElse(1));

                                break;
                            }

                            for (final ItemFlag itemFlag : ItemFlag.values()) {
                                if (itemFlag.name().equalsIgnoreCase(option)) {
                                    consumer.hideToolTip();

                                    break;
                                }
                            }

                            try {
                                consumer.asPatternBuilder().addPattern(option.toLowerCase(), value).build();
                            } catch (final Exception ignored) {}
                        }
                    }
                }
            } catch (final Exception exception) {
                consumer.withType(ItemType.RED_TERRACOTTA).withDisplayName(String.format("<red>Error found! Prize Name: %s", section));

                fusion.log("error", "An error has occurred with the prize {}, {}", section, exception.getCause());
            }
        });
    }
}