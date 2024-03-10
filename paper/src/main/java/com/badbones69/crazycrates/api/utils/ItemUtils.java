package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtils {

    public static void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public static boolean isKey(ItemStack itemStack) {
        if (itemStack == null) return false;

        if (itemStack.getType() == Material.AIR) return false;

        if (!itemStack.hasItemMeta()) return false;

        ItemMeta itemMeta = itemStack.getItemMeta();

        boolean asString = itemMeta.getAsString().contains("CrazyCrates-Crate");

        if (asString) {
            return true;
        }

        return itemMeta.getPersistentDataContainer().has(PersistentKeys.crate_key.getNamespacedKey());
    }

    public static String getKey(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }
}