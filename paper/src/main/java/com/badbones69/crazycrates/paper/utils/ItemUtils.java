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

    private static final FusionPaper fusion = plugin.getFusion();

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

    public static void updateEnchantGlintState(@NotNull final ItemBuilder itemBuilder, @NotNull final String state) {
        final Optional<Boolean> legacy = StringUtils.tryParseBoolean(state);

        if (legacy.isPresent()) {
            final boolean isGlowing = legacy.get();

            if (isGlowing) {
                itemBuilder.addEnchantGlint();
            } else {
                itemBuilder.removeEnchantGlint();
            }

            return;
        }

        switch (state) {
            case "add_glow" -> itemBuilder.addEnchantGlint();
            case "remove_glow" -> itemBuilder.removeEnchantGlint();
            case "none", "" -> {}
        }
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
        ItemUtils.updateEnchantGlintState(builder, section.getString("Glowing", "add_glow"));
        
        builder.setItemDamage(section.getInt("DisplayDamage", 0));
        
        builder.withDisplayLore(section.getStringList("Lore"));

        if (section.contains("Patterns")) {
            final PatternBuilder patternBuilder = builder.asPatternBuilder();

            for (final String key : section.getStringList("Patterns")) {
                final String[] split = key.split(":");
                final String type = split[0];
                final String color = split[1];

                patternBuilder.addPattern(type, color);
            }

            patternBuilder.build();
        }

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        if (section.contains("Skull")) {
            builder.withSkull(section.getString("Skull", ""));
        }
        
        if (section.contains("Player")) {
            builder.asSkullBuilder().withName(section.getString("Player", "")).build();
        }

        final CustomBuilder customBuilder = builder.asCustomBuilder();

        customBuilder.setCustomModelData(section.getString("Custom-Model-Data", ""));

        customBuilder.setItemModel(section.getString("Model.Namespace", ""), section.getString("Model.Id", ""));

        customBuilder.build();

        builder.setTrim(section.getString("DisplayTrim.Pattern", ""), section.getString("DisplayTrim.Material", ""));
        
        if (section.contains("DisplayEnchantments")) {
            for (final String ench : section.getStringList("DisplayEnchantments")) {
                String[] value = ench.split(":");

                builder.addEnchantment(value[0], Integer.parseInt(value[1]));
            }
        }
        
        return builder;
    }

    public static ItemBuilder convertItemStack(@Nullable final Player player, @NotNull final ItemStack itemStack) {
        return ItemBuilder.from(itemStack);
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

                if (base64 != null && !base64.isEmpty()) {
                    itemBuilder.withBase64(base64);
                }
            }

            if (item.contains("name")) {
                itemBuilder.withDisplayName(item.getString("name", ""));
            }

            if (item.contains("lore")) {
                itemBuilder.withDisplayLore(item.getStringList("lore"));
            }

            if (item.isString("amount")) {
                final Optional<Number> integer = StringUtils.tryParseInt(item.getString("amount", "1"));

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

            itemBuilder.asCustomBuilder().setCustomModelData(item.getString("custom-model-data", "")).build();

            if (item.getBoolean("hide-tool-tip", false)) {
                itemBuilder.hideToolTip();
            }

            itemBuilder.hideComponents(item.getStringList("hidden-components"));

            itemBuilder.setUnbreakable(item.getBoolean("unbreakable-item", false));

            ItemUtils.updateEnchantGlintState(itemBuilder, item.getString("settings.glowing", "none"));

            final String player = item.getString("settings.player", null);

            if (player != null && !player.isEmpty() && itemBuilder.isPlayerHead()) {
                final SkullBuilder skullBuilder = itemBuilder.asSkullBuilder();

                skullBuilder.withName(player).build();
            }

            itemBuilder.setItemDamage(item.getInt("settings.damage", 0));

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
        ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE, 1);

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.withCustomItem(value.toLowerCase());
                    case "data" -> itemBuilder.withBase64(value);
                    case "name" -> itemBuilder.withDisplayName(value);
                    case "mob" -> {
                        final EntityType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(value);

                        if (type != null) {
                            itemBuilder.asSpawnerBuilder().withEntityType(type).build();
                        }
                    }
                    case "glowing" -> ItemUtils.updateEnchantGlintState(itemBuilder, value);
                    case "amount" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setItemDamage(amount.map(Number::intValue).orElse(0));
                    }
                    case "lore" -> itemBuilder.withDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.asSkullBuilder().withName(value).build();
                    case "skull" -> itemBuilder.withSkull(value);
                    case "custom-model-data" -> itemBuilder.asCustomBuilder().setCustomModelData(value).build();
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "hide-tool-tip" -> {
                        if (value.equalsIgnoreCase("true")) {
                            itemBuilder.hideToolTip();
                        }
                    }
                    case "trim" -> {
                        String[] split = value.split("!"); // trim:trim_pattern!trim_material

                        String trim = split[0];
                        String material = split[1];

                        itemBuilder.setTrim(trim.toLowerCase(), material.toLowerCase());
                    }

                    case "rgb", "color" -> itemBuilder.setColor(value);

                    default -> {
                        final Enchantment enchantment = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEnchantment(getEnchant(option));

                        if (enchantment != null) {
                            final Optional<Number> level = StringUtils.tryParseInt(value);

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
        } catch (final Exception exception) {
            itemBuilder.withType(ItemType.RED_TERRACOTTA).withDisplayName("<red>Error found!, Prize Name: " + section);

            fusion.log("error", "An error has occurred with the prize {}, {}", section, exception.getMessage());
        }

        return itemBuilder;
    }
}