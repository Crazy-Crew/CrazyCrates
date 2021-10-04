package com.badbones69.crazycrates.support.libs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    ORAXEN("Oraxen");
    
    private final String name;
    private Plugin plugin;
    
    private Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        if (plugin == null)
            plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
        return plugin != null;
    }
    
    public boolean isPluginEnabled() {
        if (plugin == null)
            plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
    
    public Plugin getPlugin() {return plugin;}
    
    public static void enableLoaded(PluginLoader pluginLoader) {
        for (Support support : Support.values()) {
            if (support.isPluginLoaded() && !support.isPluginEnabled()) {
                pluginLoader.enablePlugin(support.plugin);
            }
        }
    }
    
}