package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;

public class CrateAdminMenu extends InventoryBuilder {

    public CrateAdminMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        for (Crate crate : this.plugin.getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                if (inventory.firstEmpty() >= 0) inventory.setItem(inventory.firstEmpty(), crate.getAdminKey());
            }
        }

        return this;
    }
}