package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.ryderbelserion.fusion.core.util.StringUtils;
import com.ryderbelserion.fusion.paper.builder.items.modern.ItemBuilder;
import com.ryderbelserion.fusion.paper.builder.items.modern.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builder.items.modern.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builder.items.modern.types.SkullBuilder;
import com.ryderbelserion.fusion.paper.builder.items.modern.types.SpawnerBuilder;
import com.ryderbelserion.fusion.paper.util.PaperMethods;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;
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

    public static String getEnchant(String enchant) {
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

    public static String getPotion(String potion) {
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

        builder.setCustomModelData(section.getInt("Custom-Model-Data", -1));
        
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

    public static LegacyItemBuilder convertItemStack(Player player, ItemStack itemStack) {
        LegacyItemBuilder itemBuilder = new LegacyItemBuilder(itemStack);

        if (player != null) {
            itemBuilder.setPlayer(player);
        }

        return itemBuilder;
    }

    public static LegacyItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
    }

    public static List<ItemBuilder> convertConfigurationSection(final ConfigurationSection section) {
        final List<ItemBuilder> cache = new ArrayList<>();

        if (section == null) return cache;

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(key);

            if (item == null) continue;

            final String base64 = item.getString("data", null);

            final ItemBuilder itemBuilder = ItemBuilder.from(item.getString("material", "stone"));

            if (base64 != null && !base64.isEmpty()) {
                itemBuilder.withBase64(base64);
            }

            itemBuilder.setDisplayName(item.getString("name", ""))
                    .withDisplayLore(item.getStringList("lore"))
                    .setAmount(item.getInt("amount", 1));

            final ConfigurationSection enchantments = item.getConfigurationSection("enchantments");

            if (enchantments != null) {
                for (final String enchantment : enchantments.getKeys(false)) {
                    final int level = enchantments.getInt(enchantment);

                    itemBuilder.addEnchantment(enchantment, level);
                }
            }

            itemBuilder.setCustomModelData(item.getInt("custom-model-data", -1));

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

                spawnerBuilder.withEntityType(PaperMethods.getEntity(mobType)).build();
            }

            itemBuilder.setTrim(item.getString("settings.trim.pattern", ""), item.getString("settings.trim.material", ""), false);

            final ConfigurationSection potions = item.getConfigurationSection("settings.potions");

            if (potions != null) {
                for (final String potion : potions.getKeys(false)) {
                    final PotionEffectType type = PaperMethods.getPotionEffect(potion);

                    if (type != null) {
                        final int duration = potions.getInt(potion + ".duration", 60);
                        final int level = potions.getInt(potion + ".level", 1);

                        final boolean icon = potions.getBoolean(potion + ".style.icon", false);
                        final boolean ambient = potions.getBoolean(potion + ".style.ambient", false);
                        final boolean particles = potions.getBoolean(potion + ".style.particles", false);

                        final PotionBuilder potionBuilder = itemBuilder.asPotionBuilder();

                        potionBuilder.withPotionEffect(type, duration, level, ambient, particles, icon).build();
                    }
                }
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

    public static List<LegacyItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    public static List<LegacyItemBuilder> convertStringList(List<String> itemStrings, String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    public static LegacyItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    public static LegacyItemBuilder convertString(String itemString, String section) {
        LegacyItemBuilder itemBuilder = new LegacyItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.withType(value.toLowerCase());
                    case "data" -> itemBuilder = itemBuilder.fromBase64(value);
                    case "name" -> itemBuilder.setDisplayName(value);
                    case "mob" -> {
                        final EntityType type = PaperMethods.getEntity(value);

                        if (type != null) {
                            itemBuilder.setEntityType(type);
                        }
                    }
                    case "glowing" -> itemBuilder.setGlowing(StringUtils.tryParseBoolean(value).orElse(false));
                    case "amount" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setDamage(amount.map(Number::intValue).orElse(1));
                    }
                    case "lore" -> itemBuilder.setDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.setPlayer(value);
                    case "skull" -> itemBuilder.setSkull(value);
                    case "custom-model-data" -> itemBuilder.setCustomModelData(StringUtils.tryParseInt(value).orElse(-1).intValue());
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "hide-tool-tip" -> itemBuilder.setHidingItemFlags(value.equalsIgnoreCase("true"));
                    case "trim-pattern" -> itemBuilder.applyTrimPattern(value);
                    case "trim-material" -> itemBuilder.applyTrimMaterial(value);
                    case "rgb" -> {
                        final @Nullable Color color = PaperMethods.getRGB(value);

                        if (color != null) {
                            itemBuilder.setColor(color);
                        }
                    }
                    case "color" -> {
                        final @Nullable Color color = PaperMethods.getColor(value);

                        itemBuilder.setColor(color);
                    }
                    default -> {
                        if (PaperMethods.getEnchantment(option.toLowerCase()) != null) {
                            final Optional<Number> amount = StringUtils.tryParseInt(value);

                            itemBuilder.addEnchantment(option.toLowerCase(), amount.map(Number::intValue).orElse(1), true);

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.setHidingItemFlags(true);

                                break;
                            }
                        }

                        try {
                            DyeColor color = PaperMethods.getDyeColor(value);

                            PatternType patternType = PaperMethods.getPatternType(option.toLowerCase());

                            if (patternType != null) {
                                itemBuilder.addPattern(patternType, color);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.withType(ItemType.RED_TERRACOTTA).setDisplayName("<red>Error found!, Prize Name: " + section);

            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }
}