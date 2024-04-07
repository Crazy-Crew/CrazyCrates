package com.badbones69.crazycrates.support;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    CMI("CMI");
    
    private final String name;

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    /**
     * @param name the name of the plugin.
     */
    PluginSupport(String name) {
        this.name = name;
    }

    /**
     * @return name of the plugin.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Checks if plugin is enabled.
     *
     * @return true or false.
     */
    public boolean isPluginEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }
}