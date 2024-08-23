package com.badbones69.crazycrates.api.builders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends com.ryderbelserion.vital.paper.api.builders.items.ItemBuilder<ItemBuilder> {

    public ItemBuilder(final ItemBuilder itemBuilder, final boolean createNewStack) {
        super(itemBuilder, createNewStack);
    }

    public ItemBuilder(final Material material, final int amount) {
        super(material, amount);
    }

    public ItemBuilder(final ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    public ItemBuilder(final Material material) {
        super(material, 1);
    }

    public ItemBuilder(final ItemStack itemStack) {
        super(itemStack, false);
    }

    public ItemBuilder() {}
}