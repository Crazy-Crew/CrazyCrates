package com.badbones69.crazycrates.api.crateoptions;

import com.badbones69.crazycrates.api.objects.CrateOptions;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class WheelOptions implements CrateOptions {

    private ItemBuilder selectorItem;

    public WheelOptions(FileConfiguration file) {
        if (file.contains("Crate.Crate-Type-Options.Selector.Item")) {
            selectorItem = new ItemBuilder().setMaterial(file.getString("Crate.Crate-Type-Options.Selector.Item"));
        } else {
            selectorItem = new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE);
        }
    }

    /**
     * Get a copy of the ItemBuilder set as the selector.
     */
    public ItemBuilder getSelectorItem() {
        return new ItemBuilder(selectorItem);
    }

}