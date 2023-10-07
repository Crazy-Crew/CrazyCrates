package com.badbones69.crazycrates.paper.api.enums;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum DataKeys {

    NO_FIREWORK_DAMAGE("crate_firework", Boolean.class);

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final String nameSpaceKey;

    DataKeys(String nameSpaceKey, Object dataType) {
        this.nameSpaceKey = nameSpaceKey;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this.plugin, this.nameSpaceKey);
    }

    public String getStringKey() {
        return this.nameSpaceKey;
    }
}