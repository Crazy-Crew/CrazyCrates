package me.badbones69.crazycrates.multisupport;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenSupport {
    private static boolean loaded = false;
    
    public static void load() {
        loaded = true;
    }
    
    public static boolean isLoaded() { return loaded; }
    
    public static ItemStack getItem(String name) {
        if(!loaded)
            return null;
        
        if(OraxenItems.exists(name))
            return OraxenItems.getItemById(name).build();
        return null;
    }
}
