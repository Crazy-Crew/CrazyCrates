package com.badbones69.crazycrates.support.libs;

import com.badbones69.crazycrates.api.CrazyManager;

public enum PluginSupport {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    ITEMS_ADDER("ItemsAdder");
    
    private final String name;

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return crazyManager.getPlugin().getServer().getPluginManager().getPlugin(name) != null;
    }
}