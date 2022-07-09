package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.api.CrazyManager;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    HOLOGRAMS("Holograms"),
    DECENT_HOLOGRAMS("DecentHolograms");
    
    private final String name;
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return CrazyManager.getInstance().getPlugin().getServer().getPluginManager().getPlugin(name) != null;
    }
    
}