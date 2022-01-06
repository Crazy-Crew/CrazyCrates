package me.badbones69.crazycrates.multisupport.libs;

import org.bukkit.Bukkit;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays");
    
    private String name;
    
    private Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }
    
}