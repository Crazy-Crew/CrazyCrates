package com.badbones69.crazycrates.managers.v2;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.exception.CratesException;
import com.badbones69.crazycrates.managers.data.DataManager;
import com.badbones69.crazycrates.managers.data.interfaces.Connector;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.sql.*;
import java.util.UUID;

public class BukkitUserManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final DataManager dataManager = this.plugin.getDataManager();

    private final Connector connector = this.dataManager.getConnector();

    public void createUser(final UUID uuid) {
        try (final Connection connection = this.connector.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement("insert into " +
                    "users(user_id, total_crates_opened) values (?, ?)")) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, 0);

                statement.executeUpdate();
            }

            try (final PreparedStatement statement = connection.prepareStatement("insert into " +
                    "crates(user_id, crate_name, amount, times_opened, current_respins) values (?, ?, ?, ?, ?)")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, null);
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);

                statement.executeUpdate();
            }
        } catch (final SQLException exception) {
            throw new CratesException("Failed to create user " + uuid, exception);
        }
    }

    public void removeUser(final UUID uuid) {
        try (final Connection connection = this.connector.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement("delete from users where user_id = ?")) {
                statement.setString(1, uuid.toString());

                statement.executeUpdate();
            }
        } catch (final SQLException exception) {
            throw new CratesException("Failed to remove user " + uuid, exception);
        }
    }

    public int getVirtualKeys(final UUID uuid, final String crateName) {
        return getKeys(uuid, crateName, KeyType.virtual_key);
    }

    public int getKeys(final UUID uuid, final String crateName, final KeyType keyType) {
        int amount = 0;

        //todo() fetch crate from cache.

        switch (keyType) {
            case physical_key -> {

            }

            case virtual_key -> {
                try (final Connection connection = this.connector.getConnection()) {
                    try (final PreparedStatement statement = connection.prepareStatement("select amount from crates where user_id = ? and crate_name = ?")) {
                        statement.setString(1, uuid.toString());
                        statement.setString(2, crateName);

                        final ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            amount = resultSet.getInt("amount");
                        }
                    }
                } catch (final SQLException exception) {
                    throw new CratesException("Failed to get the virtual keys for " + uuid, exception);
                }
            }
        }

        return amount;
    }

    public void addKeys(final UUID uuid, final String crateName, final int amount, final KeyType keyType) {
        //todo() fetch crate from cache.

        switch (keyType) {
            case physical_key -> {
                final Player player = this.plugin.getServer().getPlayer(uuid);

                if (player == null) return;

                //todo() add keys to inventory
            }

            case virtual_key -> {
                final int keys = getVirtualKeys(uuid, crateName);

                try (final Connection connection = this.connector.getConnection()) {
                    try (final PreparedStatement statement = connection.prepareStatement("update crates set amount = ? where user_id = ? and crate_name = ?")) {
                        statement.setString(1, uuid.toString());
                        statement.setString(2, crateName);

                        statement.setInt(3, Math.max(keys, amount));

                        statement.executeUpdate();
                    }
                } catch (final SQLException exception) {
                    throw new CratesException("Failed to add the virtual keys for " + uuid, exception);
                }
            }
        }
    }

    public void setKeys(final UUID uuid, final String crateName, final int amount, final KeyType keyType) {
        //todo() fetch crate from cache.

        switch (keyType) {
            case physical_key -> addKeys(uuid, crateName, amount, keyType);

            case virtual_key -> {
                try (final Connection connection = this.connector.getConnection()) {
                    try (final PreparedStatement statement = connection.prepareStatement("insert into " +
                            "crates(user_id, crate_name, amount, times_opened, current_respins) values (?, ?, ?, ?, ?)")) {
                        statement.setString(1, uuid.toString());
                        statement.setString(2, crateName);
                        statement.setInt(3, Math.max(1, amount)); // always set 1 key
                        statement.setInt(4, 0);
                        statement.setInt(5, 0);

                        statement.executeUpdate();
                    }
                } catch (final SQLException exception) {
                    throw new CratesException("Failed to set the virtual keys for " + uuid, exception);
                }
            }
        }
    }
}