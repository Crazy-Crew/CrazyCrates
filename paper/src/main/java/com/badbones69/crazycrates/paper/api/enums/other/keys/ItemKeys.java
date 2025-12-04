package com.badbones69.crazycrates.paper.api.enums.other.keys;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import com.badbones69.crazycrates.paper.CrazyCrates;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public enum ItemKeys {

    no_firework_damage("firework", PersistentDataType.BOOLEAN),
    cosmic_mystery_crate("cosmic_mystery_crate", PersistentDataType.INTEGER),
    cosmic_picked_crate("cosmic_picked_crate", PersistentDataType.INTEGER),
    main_menu_button("main_menu_button", PersistentDataType.STRING),
    selector_wand("selector_wand", PersistentDataType.STRING),
    back_button("back_button", PersistentDataType.STRING),
    next_button("next_button", PersistentDataType.STRING),
    crate_prize("crate_prize", PersistentDataType.STRING),
    crate_tier("crate_tier", PersistentDataType.STRING),
    crate_key("crate_key", PersistentDataType.STRING),
    crate_command("crate_command", PersistentDataType.STRING);

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final String NamespacedKey;
    private final PersistentDataType type;

    ItemKeys(@NotNull final String NamespacedKey, @NotNull final PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public @NotNull final NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }

    public @NotNull final PersistentDataType getType() {
        return this.type;
    }
}