package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class CratePrizeMenu extends InventoryBuilder {

    public CratePrizeMenu(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate) {
        super(player, title, size, crate);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {}
}