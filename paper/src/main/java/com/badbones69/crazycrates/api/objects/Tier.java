package com.badbones69.crazycrates.api.objects;

import org.bukkit.inventory.ItemStack;

public class Tier {
    
    private final String name;
    private final String coloredName;
    private final ItemBuilder item;
    private final int chance;
    private final int maxRange;
    
    public Tier(String name, String coloredName, String item, int chance, int maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.item = new ItemBuilder().setMaterial(item).setName(coloredName);
        this.chance = chance;
        this.maxRange = maxRange;
    }
    
    /**
     * @return Name of the tier.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return The colored name of the tier.
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
     * @return The range of max possible\ chances.
     */
    public int getMaxRange() {
        return this.maxRange;
    }
    
    public ItemStack getTierItem() {
        return this.item.build();
    }
}