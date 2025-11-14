package com.badbones69.crazycrates.core.databases;

import com.badbones69.crazycrates.core.Server;
import com.badbones69.crazycrates.core.databases.interfaces.IConnector;
import com.badbones69.crazycrates.core.databases.types.SqliteConnector;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public class DataManager {

    private final FusionCore fusion = FusionProvider.get();

    private final Path path = this.fusion.getPath();

    private IConnector connector;

    private final Server plugin;

    public DataManager(@NotNull final Server plugin) {
        this.plugin = plugin;
    }

    public final DataManager init() {
        this.connector = new SqliteConnector().init(this.path.resolve("users.db"));

        /*final String uuid = UUID.randomUUID().toString();

        CompletableFuture.runAsync(() -> {
            final Connection connection = this.connector.getConnection();

            try (final PreparedStatement statement = connection.prepareStatement("insert into users(uuid) values (?)")) {
                statement.setString(1, uuid);

                this.fusion.log("warn", "This is user insertion!");

                statement.executeUpdate();
            } catch (final SQLException exception) {
                this.fusion.log("warn", "Failed to insert user!", exception);
            }

            try (final PreparedStatement statement = connection.prepareStatement("insert into keys(crate, uuid, amount) values (?, ?, ?)")) {
                statement.setString(1, "CrateExample");
                statement.setString(2, uuid);
                statement.setInt(3, 30);

                statement.executeUpdate();

                this.fusion.log("warn", "This is one insertion!");
            } catch (final SQLException exception) {
                this.fusion.log("warn", "Failed to insert key data!", exception);
            }

            try (final PreparedStatement statement = connection.prepareStatement("insert into keys(crate, uuid, amount) values (?, ?, ?)")) {
                statement.setString(1, "CosmicCrate");
                statement.setString(2, uuid);
                statement.setInt(3, 15);

                this.fusion.log("warn", "This is two insertion!");

                statement.executeUpdate();
            } catch (final SQLException exception) {
                this.fusion.log("warn", "Failed to insert key data!", exception);
            }
        });*/

        return this;
    }

    public @NotNull final IConnector getConnector() {
        return this.connector;
    }
}