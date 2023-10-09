package com.badbones69.crazycrates.paper.api.objects;

import us.crazycrew.crazycrates.paper.api.plugin.builder.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class Tier {
    
    private final String name;
    private final String coloredName;
    private final ItemBuilder glassColor;
    private final int chance;
    private final int maxRange;
    
    public Tier(String name, String coloredName, ItemBuilder colorGlass, int chance, int maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.glassColor = colorGlass.setName(coloredName);
        this.chance = chance;
        this.maxRange = maxRange;
    }
    
    public Tier(String name, String coloredName, String colorGlass, int chance, int maxRange) {
        this.name = name;
        this.coloredName = coloredName;
        this.glassColor = new ItemBuilder().setMaterial(colorGlass).setName(coloredName);
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
    public ItemBuilder getGlassColor() {
        return this.glassColor;
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
    
    public ItemStack getTierPane() {
        return this.glassColor.build();
    }
}