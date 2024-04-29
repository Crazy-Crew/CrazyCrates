package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {

    private static final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private static final @NotNull CrateManager crateManager = plugin.getCrateManager();

    public static void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public static boolean isSimilar(ItemStack itemStack, Crate crate) {
        return crateManager.isKeyFromCrate(itemStack, crate);
    }

    public static String getKey(ItemMeta itemMeta) {
        return itemMeta.getPersistentDataContainer().get(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }
}