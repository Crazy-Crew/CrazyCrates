package com.badbones69.crazycrates.api.enums;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public enum PersistentKeys {

    no_firework_damage("firework", PersistentDataType.BOOLEAN),
    cosmic_mystery_crate("cosmic_mystery_crate", PersistentDataType.INTEGER),
    cosmic_picked_crate("cosmic_picked_crate", PersistentDataType.INTEGER),
    preview_tier_button("preview_tier_button", PersistentDataType.STRING),
    main_menu_button("main_menu_button", PersistentDataType.STRING),
    selector_wand("selector_wand", PersistentDataType.STRING),
    back_button("back_button", PersistentDataType.STRING),
    next_button("next_button", PersistentDataType.STRING),
    crate_prize("crate_prize", PersistentDataType.STRING),
    crate_tier("crate_tier", PersistentDataType.STRING),
    crate_key("crate_key", PersistentDataType.STRING);

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final String NamespacedKey;
    private final PersistentDataType type;

    PersistentKeys(String NamespacedKey, PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }

    public PersistentDataType getType() {
        return this.type;
    }
}