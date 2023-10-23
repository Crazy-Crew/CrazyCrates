package com.badbones69.crazycrates.paper.support.libraries;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    CMI("CMI"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    ORAXEN("Oraxen"),
    ITEMS_ADDER("ItemsAdder");
    
    private final String name;

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public boolean isPluginEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }
}