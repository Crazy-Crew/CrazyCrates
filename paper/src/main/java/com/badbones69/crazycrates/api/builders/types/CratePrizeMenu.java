package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import org.jetbrains.annotations.NotNull;

public class CratePrizeMenu extends InventoryBuilder {

    public CratePrizeMenu(@NotNull final Crate crate, @NotNull final Player player, final int size, @NotNull final String title) {
        super(crate, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }
}