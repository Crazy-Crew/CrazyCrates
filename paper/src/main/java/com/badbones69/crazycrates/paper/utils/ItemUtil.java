package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.SkullBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.custom.CustomBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.entity.Player;
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

public class ItemUtil {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final FusionPaper fusion = plugin.getFusion();

    private static final CrateManager crateManager = plugin.getCrateManager();

    public static void addItemModel(@NotNull final ItemBuilder builder, @NotNull final String namespace, @NotNull final String id) {
        final CustomBuilder custom = builder.asCustomBuilder();

        custom.setItemModel(namespace, id);

        custom.build();
    }

    public static void addCustomModel(@NotNull final ItemBuilder builder, @NotNull final String id) {
        final CustomBuilder custom = builder.asCustomBuilder();

        custom.setCustomModelData(id);

        custom.build();
    }

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

    @Deprecated(forRemoval = true)
    public static String getKey(@NotNull final PersistentDataContainerView container) {
        return container.get(ItemKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }

    public static @NotNull ItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemBuilder builder, @NotNull final Player player) {
        if (section.getBoolean("Glowing", false)) {
            builder.addEnchantGlint();
        }
        
        builder.setItemDamage(section.getInt("DisplayDamage", 0));

        builder.withDisplayLore(section.getStringList("Lore"));

        if (section.contains("Patterns")) {
            final PatternBuilder pattern = builder.asPatternBuilder();

            section.getStringList("Patterns").forEach(index -> {
                final String[] value = index.split(":");

                pattern.addPattern(value[0], value[0]);
            });

            pattern.build();
        }

        //builder.setHidingItemFlags(section.getBoolean("HideItemFlags", false) || !section.getStringList("Flags").isEmpty());

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        if (section.contains("Skull")) {
            builder.withSkull(section.getString("Skull", ""));
        }
        
        if (section.contains("Player") && builder.isPlayerHead()) {
            final SkullBuilder skull = builder.asSkullBuilder();

            skull.withName(section.getString("Player", "")).build();
        }

        final CustomBuilder custom = builder.asCustomBuilder();

        custom.setCustomModelData(section.getString("Custom-Model-Data", ""));

        final String model = section.getString("Model.Namespace", "");
        final String id = section.getString("Model.Id", "");

        custom.setItemModel(model, id).build();

        if (section.contains("DisplayTrim.Pattern") && section.contains("DisplayTrim.Material") && builder.isArmor()) {
            final String pattern = section.getString("DisplayTrim.Pattern", "sentry");
            final String material = section.getString("DisplayTrim.Material", "quartz");

            builder.setTrim(pattern, material);
        }
        
        if (section.contains("DisplayEnchantments")) {
            for (final String enchantment : section.getStringList("DisplayEnchantments")) {
                final String[] index = enchantment.split(":");

                builder.addEnchantment(index[0], StringUtils.tryParseInt(index[1]).orElse(1).intValue());
            }
        }
        
        return builder;
    }

    public static ItemBuilder convertItemStack(@NotNull final ItemStack itemStack) {
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
                itemBuilder.withBase64(item.getString("data", ""));
            }

            itemBuilder.withDisplayName(item.getString("name", ""));

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

            final CustomBuilder custom = itemBuilder.asCustomBuilder();

            custom.setCustomModelData(item.getString("custom-model-data", ""));

            final String model = item.getString("model.namespace", "");
            final String id = item.getString("model.id", "");

            custom.setItemModel(model, id).build();

            if (item.getBoolean("hide-tool-tip", false)) {
                itemBuilder.hideToolTip();
            }

            itemBuilder.hideComponents(item.getStringList("hidden-components"));

            itemBuilder.setUnbreakable(item.getBoolean("unbreakable-item", false));

            if (item.getBoolean("settings.glowing", false)) {
                itemBuilder.addEnchantGlint();
            }

            final String player = item.getString("settings.player", "");

            if (itemBuilder.isPlayerHead()) {
                itemBuilder.asSkullBuilder().withName(player).build();
            }

            itemBuilder.setItemDamage(item.getInt("settings.damage", 0));

            itemBuilder.withSkull(item.getString("settings.skull", ""));

            final String rgb = item.getString("settings.rgb", "");

            final String color = item.getString("settings.color", "");

            final String value = !color.isEmpty() ? color : !rgb.isEmpty() ? rgb : "";

            itemBuilder.setColor(value);

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
        ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE);

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> {
                        itemBuilder = ItemBuilder.from(value.toLowerCase());
                    }
                    case "data" -> itemBuilder.withBase64(value);
                    case "name" -> itemBuilder.withDisplayName(value);
                    case "mob" -> {
                        /*final EntityType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getEntity(value);

                        if (type != null) {
                            itemBuilder.setEntityType(type);
                        }*/
                    }
                    case "glowing" -> {
                        final boolean isGlowing = StringUtils.tryParseBoolean(value).orElse(false);

                        if (isGlowing) {
                            itemBuilder.addEnchantGlint();
                        }
                    }
                    case "amount" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setItemDamage(amount.map(Number::intValue).orElse(0));
                    }
                    case "lore" -> itemBuilder.withDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.asSkullBuilder().withName(value);
                    case "skull" -> itemBuilder.withSkull(value);
                    case "custom-model-data" -> itemBuilder.asCustomBuilder().setCustomModelData(value).build();
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "hide-tool-tip" -> {
                        final boolean isEnabled = StringUtils.tryParseBoolean(value).orElse(false);

                        if (isEnabled) {
                            itemBuilder.hideToolTip();
                        }
                    }
                    case "trim" -> { // trim-material, and trim-pattern are now combined i.e. trim:sentry;quartz
                        final String[] index = value.split(";");

                        itemBuilder.setTrim(index[0], index[1]);
                    }
                    case "rgb", "color" -> itemBuilder.setColor(value);
                    default -> {
                        if (com.ryderbelserion.fusion.paper.utils.ItemUtils.getEnchantment(option.toLowerCase()) != null) {
                            final Optional<Number> amount = StringUtils.tryParseInt(value);

                            itemBuilder.addEnchantment(option.toLowerCase(), amount.map(Number::intValue).orElse(1));

                            break;
                        }

                        try {
                            final PatternBuilder pattern = itemBuilder.asPatternBuilder();

                            pattern.addPattern(option, value);
                        } catch (final Exception ignored) {}
                    }
                }
            }
        } catch (final Exception exception) {
            itemBuilder = ItemBuilder.from(ItemType.RED_TERRACOTTA).withDisplayName("<red>Error found with Prize: %s".formatted(section));

            fusion.log(Level.ERROR, "An error has occurred with the prize %s".formatted(section), exception);
        }

        return itemBuilder;
    }
}