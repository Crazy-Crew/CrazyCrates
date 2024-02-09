package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

public class Tier {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    private final ItemBuilder item;
    private final int maxRange;
    private final String name;
    private final String coloredName;
    private final int chance;
    private final int slot;

    public Tier(String tier, ConfigurationSection section) {
        this.name = tier;

        this.coloredName = section.getString("Name", "");

        List<String> lore = section.getStringList("Lore");

        this.item = new ItemBuilder()
                .setMaterial(section.getString("Item", "CHEST"))
                .setName(coloredName)
                .setLore(!lore.isEmpty() ? lore : Collections.emptyList());

        this.chance = section.getInt("Chance");
        this.maxRange = section.getInt("MaxRange");

        this.slot = section.getInt("Slot");
    }
    
    /**
     * @return Name of the tier.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Colored name of the tier.
     */
    public String getColoredName() {
        return this.coloredName;
    }

    /**
     * @return The colored glass pane.
     */
    public ItemBuilder getItem() {
        return this.item;
    }
    
    /**
     * @return The chance of being picked.
     */
    public int getChance() {
        return this.chance;
    }
    
    /**
     * @return The range of max possible chances.
     */
    public int getMaxRange() {
        return this.maxRange;
    }

    /**
     * @return slot in the inventory.
     */
    public int getSlot() {
        return this.slot;
    }

    /**
     * @return The tier item shown in the preview.
     */
    public ItemStack getTierItem() {
        ItemMeta itemMeta = this.item.getItemMeta();

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        PersistentKeys key = PersistentKeys.preview_tier_button;

        //noinspection unchecked
        container.set(key.getNamespacedKey(), key.getType(), this.name);

        this.item.setItemMeta(itemMeta);

        return this.item.build();
    }
}