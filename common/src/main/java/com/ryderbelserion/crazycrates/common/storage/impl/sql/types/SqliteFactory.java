package com.ryderbelserion.crazycrates.common.storage.impl.sql.types;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.sql.SqlFactory;
import org.jspecify.annotations.NullMarked;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@NullMarked
public final class SqliteFactory extends SqlFactory {

    public SqliteFactory(final CrazyCratesPlugin plugin) {
        super(plugin, "SQLite");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
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