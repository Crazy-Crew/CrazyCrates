package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.hooks.HeadDatabaseListener;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.items.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {

    private static @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private static @NotNull final CrateManager crateManager = plugin.getCrateManager();

    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public static boolean isSimilar(@NotNull final ItemStack itemStack, @NotNull final Crate crate) {
        return crateManager.isKeyFromCrate(itemStack, crate);
    }

    public static @NotNull String getKey(@NotNull final ItemMeta itemMeta) {
        return itemMeta.getPersistentDataContainer().getOrDefault(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING, "");
    }

    /**
     * Updates the builder object from a configuration section with a player attached.
     *
     * @param section the section in the config.
     * @param builder the current builder object.
     * @param player the player to set.
     * @return the builder object with updated data.
     */
    public static @NotNull ItemStackBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemStackBuilder builder, @NotNull final Player player) {
        return getItem(section, builder.setPlayer(player));
    }

    /**
     * Updates the builder object from a configuration section.
     *
     * @param section the section in the config.
     * @param builder the current builder object.
     * @return the builder object with updated data.
     */
    public static @NotNull ItemStackBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemStackBuilder builder) {
        builder.setGlowing(section.getBoolean("Glowing", false));
        
        builder.setDamage(section.getInt("DisplayDamage", 0));
        
        builder.setDisplayLore(section.getStringList("Lore"));

        builder.addPatterns(section.getStringList("Patterns"));

        //builder.addItemFlags(section.getStringList("Flags"));

        builder.setHiddenItemFlags(section.getBoolean("HideItemFlags", false));

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        if (section.contains("Skull")) {
            //builder.setSkull(section.getString("Skull", ""), HeadDatabaseListener.getHeads());
        }
        
        if (section.contains("Player") && builder.isPlayerHead()) {
            //builder.setPlayer(section.getString("Player", ""));
        }
        
        if (section.contains("DisplayTrim.Pattern") && builder.isArmor()) {
            builder.applyTrimPattern(section.getString("DisplayTrim.Pattern", "sentry"));
        }
        
        if (section.contains("DisplayTrim.Material") && builder.isArmor()) {
            builder.applyTrimMaterial(section.getString("DisplayTrim.Material", "quartz"));
        }
        
        if (section.contains("DisplayEnchantments")) {
            for (String ench : section.getStringList("DisplayEnchantments")) {
                String[] value = ench.split(":");

                builder.addEnchantment(value[0], Integer.parseInt(value[1]), true);
            }
        }
        
        return builder;
    }
}