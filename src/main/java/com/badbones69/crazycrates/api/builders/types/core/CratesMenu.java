package com.badbones69.crazycrates.api.builders.types.core;

import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CratesMenu extends InventoryBuilder {

    public CratesMenu(@NotNull final Player player) {
        super(player, "Crates Menu", 54);
    }

    public CratesMenu() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        this.plugin.getCrateManager().getCrates().forEach(crate -> {
            if (crate.getCrateType() == CrateType.menu) return;

            final ItemBuilder itemBuilder = new ItemBuilder().withType(Material.CHEST);

            itemBuilder.setPersistentString(PersistentKeys.crate_key.getNamespacedKey(), crate.getName());

            inventory.setItem(inventory.firstEmpty(), itemBuilder.getStack());
        });

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        if (!(inventory.getHolder(false) instanceof CratesMenu)) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || !itemStack.hasItemMeta()) return;

        final ItemMeta itemMeta = itemStack.getItemMeta();

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        final String crateName = container.get(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }
}