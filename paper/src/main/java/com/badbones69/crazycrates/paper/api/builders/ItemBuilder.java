package com.badbones69.crazycrates.paper.api.builders;

import com.ryderbelserion.fusion.paper.util.PaperMethods;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class ItemBuilder extends com.ryderbelserion.fusion.paper.builder.items.ItemBuilder<ItemBuilder> {

    public ItemBuilder(final ItemBuilder itemBuilder, final boolean createNewStack) {
        super(itemBuilder, createNewStack);
    }

    public ItemBuilder(final ItemType itemType, final int amount) {
        super(itemType, amount);
    }

    public ItemBuilder(final ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    public ItemBuilder(final ItemType itemType) {
        super(itemType, 1);
    }

    public ItemBuilder(final ItemStack itemStack) {
        super(itemStack, false);
    }

    public ItemBuilder() {}

    @Override
    public @NotNull ItemBuilder fromBase64(@NotNull String base64) {
        if (base64.isEmpty()) return this;

        return new ItemBuilder(PaperMethods.fromBase64(base64));
    }
}