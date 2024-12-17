package com.badbones69.crazycrates.data.types;

import com.badbones69.crazycrates.api.exception.CratesException;
import com.badbones69.crazycrates.data.interfaces.Connector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteConnector implements Connector {

    private HikariDataSource source;
    private File file;

    @Override
    public Connector init(final File file) {
        try {
            file.createNewFile();
        } catch (IOException exception) {
            throw new CratesException("Failed to create file " + file.getAbsolutePath(), exception);
        } finally {
            this.file = file;
        }

        start();

        return this;
    }

    @Override
    public void start() {
        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url());
        config.setMaximumPoolSize(5); // 5 is enough for flat file.
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");

        this.source = new HikariDataSource(config);
    }

    @Override
    public void stop() {
        if (!isRunning()) return;

        try {
            final Connection connection = getConnection();

            if (connection != null) {
                connection.close();
            }
        } catch (final SQLException exception) {
            throw new CratesException("Failed to close sqlite connection", exception);
        }
    }

    @Override
    public boolean isRunning() {
        try {
            final Connection connection = getConnection();

            return connection != null && !connection.isClosed();
        } catch (final SQLException exception) {
            return false;
        }
    }

    @Override
    public String url() {
        return "jdbc:sqlite:" + getFile().getAbsolutePath();
    }

    @Override
    public Connection getConnection() {
        try {
            return this.source.getConnection();
        } catch (final SQLException exception) {
            throw new CratesException("Failed to get sqlite connection", exception);
        }
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public boolean tableExists(final Connection connection, final String table) {
        try (final ResultSet resultSet = connection.getMetaData().getTables(
                null,
                null,
                table,
                null)) {
            return resultSet.next();
        } catch (final SQLException exception) {
            return false;
        }
    }
}