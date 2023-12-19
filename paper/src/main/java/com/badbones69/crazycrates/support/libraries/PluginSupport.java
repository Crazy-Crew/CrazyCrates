package com.badbones69.crazycrates.support.libraries;

import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    CMI("CMI"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    ORAXEN("Oraxen"),
    ITEMS_ADDER("ItemsAdder");
    
    private final String name;

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

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