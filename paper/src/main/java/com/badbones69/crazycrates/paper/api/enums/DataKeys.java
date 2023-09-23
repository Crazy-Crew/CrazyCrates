package com.badbones69.crazycrates.paper.api.enums;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKeys {

    NO_FIREWORK_DAMAGE("crate_firework", Boolean.class);

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final String namSpaceKey;

    DataKeys(String nameSpaceKey, Object dataType) {
        this.namSpaceKey = nameSpaceKey;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this.plugin, this.namSpaceKey);
    }

    public String getStringKey() {
        return this.namSpaceKey;
    }
}