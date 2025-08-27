package com.badbones69.crazycrates.paper.api.builders;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.paper.api.builders.items.legacy.ItemBuilder;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class LegacyItemBuilder extends ItemBuilder<LegacyItemBuilder> {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    public LegacyItemBuilder(final CrazyCrates plugin, final LegacyItemBuilder itemBuilder, final boolean createNewStack) {
        super(plugin, itemBuilder, createNewStack);
    }

    public LegacyItemBuilder(final CrazyCrates plugin, final ItemType itemType, final int amount) {
        super(plugin, itemType, amount);
    }

    public LegacyItemBuilder(final CrazyCrates plugin, final LegacyItemBuilder itemBuilder) {
        super(plugin, itemBuilder);
    }

    public LegacyItemBuilder(final CrazyCrates plugin, final ItemType itemType) {
        super(plugin, itemType, 1);
    }

    public LegacyItemBuilder(final CrazyCrates plugin, final ItemStack itemStack) {
        super(plugin, itemStack, false);
    }

    public LegacyItemBuilder(final CrazyCrates plugin) {
        super(plugin);
    }

    @Override
    public @NotNull LegacyItemBuilder fromBase64(@NotNull final String base64) {
        if (base64.isEmpty()) return this;

        return new LegacyItemBuilder(this.plugin, ItemUtils.fromBase64(base64));
    }
}