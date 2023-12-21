package com.badbones69.crazycrates.api.enums;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import com.badbones69.crazycrates.CrazyCrates;

@SuppressWarnings("rawtypes")
public enum PersistentKeys {

    no_firework_damage("firework", PersistentDataType.BOOLEAN),
    cosmic_mystery_crate("cosmic_mystery_crate", PersistentDataType.INTEGER),
    cosmic_picked_crate("cosmic_picked_crate", PersistentDataType.INTEGER),
    crate_prize("item", PersistentDataType.INTEGER);

    private final String NamespacedKey;
    private final PersistentDataType type;

    PersistentKeys(String NamespacedKey, PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public NamespacedKey getNamespacedKey(CrazyCrates plugin) {
        return new NamespacedKey(plugin, plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }

    public PersistentDataType getType() {
        return this.type;
    }
}