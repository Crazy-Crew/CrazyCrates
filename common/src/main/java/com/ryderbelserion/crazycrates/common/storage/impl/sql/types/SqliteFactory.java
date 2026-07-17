package com.ryderbelserion.crazycrates.common.storage.impl.sql.types;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.sql.SqlFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NullMarked;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@NullMarked
public final class SqliteFactory extends SqlFactory {

    private final Path path;

    public SqliteFactory(final CrazyCratesPlugin plugin) {
        super(plugin, "SQLite");

        this.path = plugin.getDataPath().resolve("crazycrates.db");
    }

    @Override
    public void init() {
        if (!Files.exists(this.path)) {
            try {
                final Path parent = this.path.getParent();

                if (!Files.exists(parent)) {
                    Files.createDirectory(parent);
                }

                Files.createFile(this.path);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }

        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url());
        config.setMaximumPoolSize(5); // 5 is enough for flat file.
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");

        this.source = new HikariDataSource(config);

        super.init();
    }

    @Override
    public String url() {
        return "jdbc:sqlite:" + this.path.toFile().getAbsolutePath();
    }

    @Override
    public String addCrateLocation(final String crateName, final String worldName, final String id, final int x, final int y, final int z) {
        return "";
    }

    @Override
    public String addCrateLocation(final String crateName, final String worldName, final int x, final int y, final int z) {
        return "";
    }

    @Override
    public Optional<CrazyLocation> getCrateLocation(final String id) {
        return Optional.empty();
    }

    @Override
    public Map<CrateStatus, CrazyLocation> getCrateLocations() {
        return Map.of();
    }

    @Override
    public void removeCrateLocation(final String id) {

    }
}