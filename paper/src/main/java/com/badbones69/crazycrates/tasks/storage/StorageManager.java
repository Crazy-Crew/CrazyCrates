package com.badbones69.crazycrates.tasks.storage;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.storage.types.Sqlite;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class StorageManager {

    private static final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private static Sqlite crates;

    public static void open() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("Sqlite was not found on the classpath.");
        }

        crates = new Sqlite(new File(plugin.getDataFolder(), "crazycrates.db"));
    }

    public static void close() {
        crates.close();
    }

    public static Sqlite getData() {
        return crates;
    }
}