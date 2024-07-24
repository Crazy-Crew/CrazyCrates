package com.badbones69.crazycrates.api.builders.types.settings;

import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class CrateMenu extends InventoryBuilder {

    public CrateMenu(final Player player) {
        super(player, "", 54);
    }

    public CrateMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();
        final Player player = getPlayer();

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CrateMenu)) return;

        event.setCancelled(true);
    }

    @Override
    public void run(InventoryDragEvent event) {
        final Inventory inventory = event.getView().getTopInventory();

        if (!(inventory.getHolder(false) instanceof CrateMenu)) return;

        event.setCancelled(true);
    }
}