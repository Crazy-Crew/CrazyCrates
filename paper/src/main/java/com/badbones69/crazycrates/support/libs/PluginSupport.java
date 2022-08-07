package com.badbones69.crazycrates.support.libs;

import com.badbones69.crazycrates.CrazyCrates;

public enum PluginSupport {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms");
    
    private final String name;

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded(CrazyCrates plugin) {
        return plugin.getServer().getPluginManager().getPlugin(name) != null;
    }
}