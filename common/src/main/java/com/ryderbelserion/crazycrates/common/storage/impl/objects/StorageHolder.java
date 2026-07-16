package com.ryderbelserion.crazycrates.common.storage.impl.objects;

import com.ryderbelserion.crazycrates.common.objects.crates.CrateLocation;
import com.ryderbelserion.crazycrates.common.objects.crates.CrateWorld;
import com.ryderbelserion.crazycrates.common.registry.CrateRegistry;
import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.api.storage.IStorageHolder;
import us.crazycrew.crazycrates.api.storage.enums.DataState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class StorageHolder extends IStorageHolder<CrateLocation, CrateWorld> {

    private final ConnectionFactory connectionFactory;
    private final CrateRegistry crateRegistry;

    public StorageHolder(final ConnectionFactory connectionFactory, final CrateRegistry crateRegistry) {
        this.connectionFactory = connectionFactory;
        this.crateRegistry = crateRegistry;
    }

    @Override
    public StorageHolder init() {
        this.connectionFactory.init();

        return this;
    }

    @Override
    public boolean tableExists(final String table) {
        try (final ResultSet resultSet = this.connectionFactory.getConnection().getMetaData().getTables(
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

    @Override
    public boolean hasWorld(final CrateWorld world) {
        if (this.crateRegistry.hasWorld(world.getIdentifier())) { // check cache first.
            return true;
        }

        return CompletableFuture.supplyAsync(() -> {
            boolean hasWorld = false;

            try (final Connection connection = this.connectionFactory.getConnection()) {
                try (final PreparedStatement statement =
                             connection.prepareStatement("select 1 from crate_worlds where world=?")) {
                    statement.setString(1, world.getIdentifier().toString());

                    final ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        hasWorld = true;

                        return hasWorld;
                    }
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }

            return hasWorld;
        }).join();
    }

    @Override
    public void addWorld(final CrateWorld world) {
        if (hasWorld(world)) {
            this.crateRegistry.addWorld(world); // add just in case it doesn't have it.

            return;
        }

        if (tableExists("crate_worlds")) { // table already exists.
            return;
        }

        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.connectionFactory.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("insert into crate_worlds(world, name) values(?, ?)")) {
                statement.setString(1, world.getIdentifier().toString());
                statement.setString(2, world.getWorldName());

                statement.executeUpdate();

                this.crateRegistry.addWorld(world);
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public DataState removeLocation(final UUID location) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = this.connectionFactory.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("delete from crate_locations where id=?")) {

                statement.setString(1, location.toString());
                statement.executeUpdate();

                return DataState.SUCCESS;
            } catch (final SQLException exception) {
                exception.printStackTrace();

                return DataState.FAILED;
            }
        }).join();
    }

    @Override
    public DataState addLocation(final UUID world, final UUID crate, final int x, final int y, final int z) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = this.connectionFactory.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("insert into crate_locations(id, world_id, crate_id, x, y, z) values(?, ?, ?, ?, ?, ?)")) {
                final UUID location = UUID.randomUUID();

                statement.setString(1, location.toString());
                statement.setString(2, world.toString());
                statement.setString(3, crate.toString());
                statement.setInt(4, x);
                statement.setInt(5, y);
                statement.setInt(6, z);

                statement.executeUpdate();

                this.crateRegistry.getWorld(world).ifPresent(value -> value.addLocation(crate, location, x, y, z));

                return DataState.SUCCESS;
            } catch (final SQLException exception) {
                exception.printStackTrace();

                return DataState.FAILED;
            }
        }).join();
    }

    @Override
    public void stop() {
        this.connectionFactory.stop();
    }
}