package com.badbones69.crazycrates.api.objects;

import org.bukkit.inventory.ItemStack;

public class Tier {
    
    private final String name;
    private final String coloredName;
    private final ItemBuilder colorGlass;
    private final Integer chance;
    private final Integer maxRange;
    
    public Tier(String name, String coloredName, ItemBuilder colorGlass, Integer chance, Integer maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.colorGlass = colorGlass.setName(coloredName);
        this.chance = chance;
        this.maxRange = maxRange;
    }
    
    public Tier(String name, String coloredName, String colorGlass, Integer chance, Integer maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.colorGlass = new ItemBuilder().setMaterial(colorGlass).setName(coloredName);
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
    public ItemBuilder getColorGlass() {
        return this.colorGlass;
    }
    
    /**
     * @return The chance of being picked.
     */
    public Integer getChance() {
        return this.chance;
    }
    
    /**
     * @return The range of max possible\ chances.
     */
    public Integer getMaxRange() {
        return this.maxRange;
    }
    
    public ItemStack getTierPane() {
        return this.colorGlass.build();
    }
}