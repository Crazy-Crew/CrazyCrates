package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.badbones69.crazycrates.paper.api.managers.ItemManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.custom.CustomBuilder;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CratePlatform platform = plugin.getPlatform();

    private static final ItemManager itemManager = platform.getItemManager();

    public static List<ItemBuilder> convertNodes(@NotNull final CommentedConfigurationNode configuration) {
        final List<ItemBuilder> builders = new ArrayList<>();

        for (final CommentedConfigurationNode node : configuration.childrenMap().values()) {
            builders.add(convertNode(node));
        }

        return builders;
    }

    public static ItemBuilder convertNode(@NotNull final CommentedConfigurationNode configuration) {
        ItemBuilder builder = ItemBuilder.from(configuration.node("material").getString("stone"));

        if (configuration.hasChild("key")) {
            final String key = configuration.node("key").getString("");

            if (!key.isBlank()) {
                final ItemBuilder custom = itemManager.getItem(key);

                final ItemType itemType = custom.getType();

                if (itemType != ItemType.AIR) {
                    builder = custom;
                }
            }
        }

        if (configuration.hasChild("data")) { //todo() write migrator
            final String item = configuration.node("data").getString("");

            if (!item.isBlank()) {
                builder.withBase64(item);
            }
        }

        if (configuration.hasChild("name")) {
            builder.withDisplayName(configuration.node("name").getString(""));
        }

        if (configuration.hasChild("lore")) {
            builder.withDisplayLore(StringUtils.getStringList(configuration.node("lore")));
        }

        builder.setAmount(configuration.node("amount").getInt(1));

        final CustomBuilder custom = builder.asCustomBuilder();

        if (configuration.hasChild("custom-model-data")) {
            custom.setCustomModelData(configuration.node("custom-model-data").getString(""));
        }

        custom.build();

        if (configuration.hasChild("hide-tool-tip")) {
            builder.hideToolTip();
        }

        if (configuration.hasChild("hidden-components")) {
            builder.hideComponents(StringUtils.getStringList(configuration.node("hidden-components")));
        }

        if (configuration.hasChild("unbreakable-item")) {
            builder.setUnbreakable(configuration.node("unbreakable-item").getBoolean(false));
        }

        final CommentedConfigurationNode settings = configuration.node("settings");

        if (settings.hasChild("glowing")) {
            if (settings.node("glowing").getBoolean(false)) {
                builder.addEnchantGlint();
            } else {
                builder.removeEnchantGlint();
            }
        }

        if (settings.hasChild("player")) {
            final String player = settings.node("player").getString("");

            if (!player.isBlank() && builder.isPlayerHead()) {
                builder.asSkullBuilder().withName(player).build();
            }
        }

        builder.setItemDamage(settings.node("damage").getInt(0));

        builder.withSkull(settings.node("skull").getString(""));

        builder.setColor(settings.hasChild("rgb") ?
                settings.node("rgb").getString("") :
                settings.node("color").getString("")
        );

        builder.setTrim(
                settings.node("trim", "pattern").getString(""),
                settings.node("trim", "material").getString("")
        );

        if (builder.isPotion() && configuration.hasChild("potions")) {
            final CommentedConfigurationNode potion = configuration.node("potions");

            final PotionBuilder potionBuilder = builder.asPotionBuilder();

            for (final Map.Entry<Object, CommentedConfigurationNode> child : potion.childrenMap().entrySet()) {
                final PotionEffectType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getPotionEffect(child.getKey().toString());

                if (type == null) {
                    continue;
                }

                final CommentedConfigurationNode section = child.getValue();

                potionBuilder.withPotionEffect(
                        type,
                        section.node("duration").getInt(10) * 20,
                        section.node("level").getInt(1),
                        section.node("style", "icon").getBoolean(false),
                        section.node("style", "ambient").getBoolean(false),
                        section.node("style", "particles").getBoolean(false)
                );
            }

            potionBuilder.setColor(settings.hasChild("rgb") ?
                    settings.node("rgb").getString("") :
                    settings.node("color").getString("")).build();
        }

        if (configuration.hasChild("enchantments")) {
            final CommentedConfigurationNode enchantments = configuration.node("enchantments");

            for (final Object key : enchantments.childrenMap().keySet()) {
                builder.addEnchantment(key.toString(), enchantments.node(key).getInt(1));
            }
        }

        if (configuration.hasChild("patterns")) {
            final PatternBuilder patternBuilder = builder.asPatternBuilder();

            final CommentedConfigurationNode patterns = settings.node("patterns");

            for (final Object key : patterns.childrenMap().keySet()) {
                patternBuilder.addPattern(key.toString(), patterns.node(key).getString("white"));
            }

            patternBuilder.build();
        }

        return builder;
    }
}