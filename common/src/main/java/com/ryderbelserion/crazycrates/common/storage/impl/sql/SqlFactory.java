package com.ryderbelserion.crazycrates.common.storage.impl.sql;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.impl.file.FlatFactory;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public abstract class SqlFactory extends FlatFactory {

    protected final String create_crate_worlds_table = "create table if not exists crate_worlds(" +
            "world varchar(36) primary key, " +
            "name varchar(36) not null)";

    protected final String create_crate_locations_table = "create table if not exists crate_locations(" +
            "id varchar(16) primary key, " +
            "world_id varchar(36) not null, " +
            "crate_id varchar(36) not null, " +
            "x bigint not null, " +
            "y bigint not null, " +
            "z bigint not null, " +
            "foreign key(world_id) references crate_worlds(world) on delete cascade)";

    protected HikariDataSource source;

    public SqlFactory(final CrazyCratesPlugin plugin, final String impl) {
        super(plugin, impl);
    }

    public abstract String url();

    @Override
    public void init() {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = getConnection()) {
                if (connection == null) return;

                try (final Statement statement = connection.createStatement()) {
                    statement.addBatch(this.create_crate_locations_table);
                    statement.addBatch(this.create_crate_worlds_table);

                    statement.executeBatch();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        try {
            final Connection connection = getConnection();

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void save() {

    }

    public Connection getConnection() throws SQLException {
        if (this.source.isClosed()) {
            throw new FusionException("Failed to get connection from pool. (Source returned closed)");
        }

        return this.source.getConnection();
    }

    public boolean tableExists(final String table) {
        try (final ResultSet resultSet = getConnection().getMetaData().getTables(
                null,
                null,
                table,
                null
        )) {
            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();

            return false;
        }
    }
}