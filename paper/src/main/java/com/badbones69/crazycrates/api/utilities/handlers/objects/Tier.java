package com.badbones69.crazycrates.api.utilities.handlers.objects;

import org.bukkit.inventory.ItemStack;

public class Tier {

    /**
     * The name of the tier.
     */
    private final String name;

    /**
     * The name of the colored glass.
     */
    private final String coloredName;

    /**
     * The unbuilt colored glass.
     */
    private final ItemBuilder colorGlass;

    /**
     * The chance to win the item.
     */

    private final int chance;
    /**
     * The max range.
     */
    private final int maxRange;
    
    public Tier(String name, String coloredName, ItemBuilder colorGlass, int chance, int maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.colorGlass = colorGlass.setName(coloredName);
        this.chance = chance;
        this.maxRange = maxRange;
    }
    
    public Tier(String name, String coloredName, String colorGlass, int chance, int maxRange) {
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
        return name;
    }
    
    /**
     * @return The colored name of the tier.
     */
    public String getColoredName() {
        return coloredName;
    }
    
    /**
     * @return The colored glass pane.
     */
    public ItemBuilder getColorGlass() {
        return colorGlass;
    }
    
    /**
     * @return The chance of being picked.
     */
    public int getChance() {
        return chance;
    }
    
    /**
     * @return The range of max possible\ chances.
     */
    public int getMaxRange() {
        return maxRange;
    }

    /**
     * @return The tier glass.
     */
    public ItemStack getTierPane() {
        return getColorGlass().build();
    }
}