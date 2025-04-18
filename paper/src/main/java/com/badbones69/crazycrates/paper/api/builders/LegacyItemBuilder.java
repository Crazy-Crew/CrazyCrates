package com.badbones69.crazycrates.paper.api.builders;

import com.ryderbelserion.fusion.paper.api.builder.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class LegacyItemBuilder extends ItemBuilder<LegacyItemBuilder> {

    public LegacyItemBuilder(final LegacyItemBuilder itemBuilder, final boolean createNewStack) {
        super(itemBuilder, createNewStack);
    }

    public LegacyItemBuilder(final ItemType itemType, final int amount) {
        super(itemType, amount);
    }

    public LegacyItemBuilder(final LegacyItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    public LegacyItemBuilder(final ItemType itemType) {
        super(itemType, 1);
    }

    public LegacyItemBuilder(final ItemStack itemStack) {
        super(itemStack, false);
    }

    public LegacyItemBuilder() {}

    @Override
    public @NotNull LegacyItemBuilder fromBase64(@NotNull final String base64) {
        if (base64.isEmpty()) return this;

        return new LegacyItemBuilder(ItemUtils.fromBase64(base64));
    }
}