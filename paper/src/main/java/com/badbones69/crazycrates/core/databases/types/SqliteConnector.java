package com.badbones69.crazycrates.core.databases.types;

import com.badbones69.crazycrates.core.databases.constants.UserSchema;
import com.badbones69.crazycrates.core.databases.interfaces.IConnector;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SqliteConnector implements IConnector {

    private final FusionCore fusion = FusionProvider.get();

    private HikariDataSource source;
    private Path path;

    @Override
    public IConnector init(@NotNull final Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (final IOException exception) {
           this.fusion.log("warn", "Failed to create the file requested!", exception);
        } finally {
            this.path = path;
        }

        start();

        return this;
    }

    @Override
    public void start() {
        final HikariConfig config = new HikariConfig();

        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");
        config.setConnectionTestQuery("PRAGMA journal_mode=WAL;");
        config.setMaximumPoolSize(10);
        config.setJdbcUrl(url());

        this.source = new HikariDataSource(config);

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = getConnection()) {
               if (connection == null) {
                   //todo() log anyway

                   return;
               }

               try (final PreparedStatement statement = connection.prepareStatement(UserSchema.create_users_table)) {
                   statement.executeUpdate();
               } catch (final SQLException exception) {
                   this.fusion.log("warn", "Failed to create users table!", exception);
               }

               try (final PreparedStatement statement = connection.prepareStatement(UserSchema.create_keys_table)) {
                   statement.executeUpdate();
               } catch (final SQLException exception) {
                   this.fusion.log("warn", "Failed to create keys table!", exception);
               }
            } catch (final SQLException exception) {
               this.fusion.log("warn", "Failed to execute prepared statement on initialization", exception);
           }
        });
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
            this.fusion.log("warn", "Failed to close the connection!", exception);
        }
    }

    @Override
    public boolean isRunning() {
        try {
            final Connection connection = getConnection();

            return connection != null && !connection.isClosed();
        } catch (final SQLException exception) {
            this.fusion.log("warn", "Connection is currently not running!", exception);

            return false;
        }
    }

    @Override
    public String url() {
        return String.format("jdbc:sqlite:%s", getPath().toString());
    }

    @Override
    public Connection getConnection() {
        try {
            return this.source.getConnection();
        } catch (final SQLException exception) {
            this.fusion.log("warn", "Connection is null!", exception);

            return null;
        }
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public boolean tableExists(@NotNull final Connection connection, @NotNull final String table) {
        try (ResultSet resultSet = connection.getMetaData().getTables(
                null,
                null,
                table,
                null
        )) {
            return resultSet.next();
        } catch (final SQLException exception) {
            this.fusion.log("Warn", "Table does not exist!", exception);

            return false;
        }
    }
}