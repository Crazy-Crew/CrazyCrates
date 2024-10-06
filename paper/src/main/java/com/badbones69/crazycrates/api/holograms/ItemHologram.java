package com.badbones69.crazycrates.api.holograms;

import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

public class ItemHologram extends TextHologram {

    private final ItemStack itemStack;

    public ItemHologram(final String value) {
        final Material material = ItemUtil.getMaterial(value);

        if (material == null) {
            this.itemStack = ItemStack.of(Material.STONE);

            return;
        }

        this.itemStack = ItemStack.of(material);
    }

    private ItemDisplay itemDisplay;

    @Override
    public ItemHologram create() {
        final Location location = getLocation();

        final World world = location.getWorld();

        this.itemDisplay = world.spawn(location.clone().add(0.0, getHeight(), 0.0), ItemDisplay.class, item -> {
            item.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);

            item.setItemStack(this.itemStack);

            item.setPersistent(true);
        });

        //todo() cache

        return this;
    }

    public ItemDisplay getItemDisplay() {
        return this.itemDisplay;
    }
}