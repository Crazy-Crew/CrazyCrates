package com.badbones69.crazycrates.tasks.storage;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.storage.types.Sqlite;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class StorageManager {

    private static CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private static Sqlite locationData;

    public static void load() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("SQLITE was not found on the classpath.");
        }

        locationData = new Sqlite(new File(plugin.getDataFolder(), "locations.db"));

        plugin.getLogger().warning("Status: " + locationData.isOpen());
    }

    public static Sqlite getLocationData() {
        return locationData;
    }
}