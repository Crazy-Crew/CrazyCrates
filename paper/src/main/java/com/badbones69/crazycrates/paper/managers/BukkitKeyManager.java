package com.badbones69.crazycrates.paper.managers;

import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BukkitKeyManager extends us.crazycrew.crazycrates.api.KeyManager<ItemStack> {

    @Override
    public boolean isMatchingKey(@NotNull final ItemStack item, @NotNull final ItemStack comparing) {
        final String first_key = getKey(item);

        if (first_key.isBlank()) return false;

        final String second_key = getKey(comparing);

        if (second_key.isBlank()) return false;

        return first_key.equals(second_key);
    }

    @Override
    public final boolean isKey(@NotNull final ItemStack item) {
        return item.getPersistentDataContainer().has(ItemKeys.crate_key.getNamespacedKey());
    }

    @Override
    public final String getKey(@NotNull final ItemStack item) {
        return isKey(item) ? item.getPersistentDataContainer().get(ItemKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING) : "";
    }
}