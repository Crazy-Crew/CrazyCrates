package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CratePrizeMenu extends InventoryBuilder {

    public CratePrizeMenu(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate) {
        super(player, title, size, crate);
    }

    public CratePrizeMenu() {}

    @Override
    public InventoryBuilder build() {
        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu)) return;

        event.setCancelled(true);
    }

    @Override
    public void run(InventoryDragEvent event) {
        final Inventory inventory = event.getView().getTopInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu)) return;

        event.setCancelled(true);
    }
}