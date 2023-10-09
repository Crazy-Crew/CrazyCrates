package us.crazycrew.crazycrates.paper.api.enums;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;

@SuppressWarnings("rawtypes")
public enum PersistentKeys {

    no_firework_damage("firework", PersistentDataType.BOOLEAN);

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