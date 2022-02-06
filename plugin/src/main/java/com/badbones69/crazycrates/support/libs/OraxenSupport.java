package com.badbones69.crazycrates.support.libs;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenSupport {
    
    private static boolean loaded = false;
    
    /**
     * Loads the support
     */
    public static void load() {
        loaded = true;
    }
    
    /**
     * @return true if OraxenSupport is loaded
     */
    public static boolean isLoaded() {return loaded;}
    
    /**
     * Get an ItemStack from the Oraxen plugin.
     * @param name the name of the item in Oraxen config
     * @return ItemStack if Oraxen has the item or null.
     */
    public static ItemStack getItem(String name) {
        if (!loaded)
            return null;
        
        if (OraxenItems.exists(name))
            return OraxenItems.getItemById(name).build();
        return null;
    }
    
}
