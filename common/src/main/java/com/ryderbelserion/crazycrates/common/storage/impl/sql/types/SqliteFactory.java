package com.ryderbelserion.crazycrates.common.storage.impl.sql.types;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.impl.sql.SqlFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    public void addWorld(final Key worldKey) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("insert into crate_worlds(world_id) values(?)")) {
                statement.setString(1, worldKey.asString());

                statement.executeUpdate();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void removeWorld(final Key worldKey) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("delete from crate_worlds where world_id=?")) {
                statement.setString(1, worldKey.asString());

                statement.executeUpdate();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public boolean hasWorld(final Key worldKey) {
        return CompletableFuture.supplyAsync(() -> {
            boolean hasWorld = false;

            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("select 1 from crate_worlds where world_id=?")) {
                statement.setString(1, worldKey.asString());

                return statement.executeQuery().next();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }

            return hasWorld;
        }).join();
    }

    @Override
    public void removeCrateLocation(final String id) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("delete from crate_locations where id=?")) {

                statement.setString(1, id);

                statement.executeUpdate();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public Optional<CrazyLocation> getCrateLocation(final String id) {
        final CompletableFuture<Optional<CrazyLocation>> object = CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("select 1 from crate_locations where id=?")
            ) {
                statement.setString(1, id);

                final ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    final Key key = this.plugin.asWorld(resultSet.getString("world_id"));

                    if (key == null) {
                        return Optional.empty();
                    }

                    return Optional.of(new CrazyLocation(
                            resultSet.getString("crate_id"),
                            key,
                            id,
                            resultSet.getInt("x"),
                            resultSet.getInt("y"),
                            resultSet.getInt("z")
                    ));
                }

                return Optional.empty();
            } catch (final Exception exception) {
                exception.printStackTrace();

                return Optional.empty();
            }
        });

        return object.join();
    }

    @Override
    public Map<CrateStatus, List<CrazyLocation>> getCrateLocations() {
        final Map<CrateStatus, List<CrazyLocation>> locations = new ConcurrentHashMap<>();

        locations.putIfAbsent(CrateStatus.failed, new CopyOnWriteArrayList<>());
        locations.putIfAbsent(CrateStatus.success, new CopyOnWriteArrayList<>());
        locations.putIfAbsent(CrateStatus.unavailable, new CopyOnWriteArrayList<>());

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                   connection.prepareStatement("select * from crate_locations")
           ) {
               final ResultSet resultSet = statement.executeQuery();

               while (resultSet.next()) {
                   final Key key = this.plugin.asWorld(resultSet.getString("world_id"));

                   final String crateId = resultSet.getString("crate_id");
                   final String id = resultSet.getString("id");
                   final int x = resultSet.getInt("x");
                   final int y = resultSet.getInt("y");
                   final int z = resultSet.getInt("z");

                   if (key == null) {
                       locations.get(CrateStatus.unavailable).add(new CrazyLocation(
                               crateId,
                               Key.key("minecraft:unknown"),
                               id,
                               x,
                               y,
                               z
                       ));

                       continue;
                   }

                   final CrazyLocation location = new CrazyLocation(
                           crateId,
                           key,
                           id,
                           x,
                           y,
                           z
                   );

                   if (!this.plugin.isCrateAvailable(crateId)) {
                       locations.get(CrateStatus.unavailable).add(location);

                       continue;
                   }

                   locations.get(CrateStatus.success).add(location);
               }
           } catch (final Exception exception) {
               exception.printStackTrace();
           }
        }).join();

        return locations;
    }

    @Override
    public String addCrateLocation(final String crateId, final Key key, final int x, final int y, final int z) {
        return addCrateLocation(crateId, key, UUID.randomUUID().toString(), x, y, z);
    }

    @Override
    public String addCrateLocation(final String crateId, final Key key, final String id, final int x, final int y, final int z) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = this.source.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("insert into crate_locations(id, world_id, crate_id, x, y, z) values(?, ?, ?, ?, ?, ?)")
            ) {
                statement.setString(1, id);
                statement.setString(2, key.asString());
                statement.setString(3, crateId);
                statement.setInt(4, x);
                statement.setInt(5, y);
                statement.setInt(6, z);

                statement.executeUpdate();

                return id;
            } catch (final SQLException exception) {
                exception.printStackTrace();

                return id;
            }
        }).join();
    }
}