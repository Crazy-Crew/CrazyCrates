package com.badbones69.crazycrates.tasks.storage.types;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Sqlite {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private Connection connection;
    private File file;

    public Sqlite(final File file) {
        start(file);

        open();
    }

    public void open() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + this.file)) {
            this.connection = connection;
        } catch (SQLException exception) {
            this.plugin.getLogger().warning("Failed to create connection!");
        }
    }

    public void close() {
        if (getConnection() == null) return;

        try {
            getConnection().close();
        } catch (SQLException e) {
            this.plugin.getLogger().warning("Failed to close connection!");
        }
    }

    public final boolean isOpen() {
        if (getConnection() == null) return false;

        try {
            return getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public final Connection getConnection() {
        return this.connection;
    }

    private void start(File file) {
        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException exception) {
            this.plugin.getLogger().severe("Failed to create " + file.getName());
        } finally {
            this.file = file;
        }
    }
}