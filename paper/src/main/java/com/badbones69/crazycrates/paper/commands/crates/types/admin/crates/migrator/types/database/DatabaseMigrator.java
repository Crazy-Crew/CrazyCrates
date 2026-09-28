package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.database;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.crazycrates.common.storage.impl.sql.types.SqliteFactory;
import com.ryderbelserion.fusion.core.api.enums.Level;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import us.crazycrew.crazycrates.api.config.impl.types.config.DatabaseKeys;
import us.crazycrew.crazycrates.api.enums.Files;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseMigrator extends ICrateMigrator {

    public DatabaseMigrator(final CommandSender sender) {
        super(sender, MigrationType.DATABASE_MIGRATION);
    }

    @Override
    public void run() {
        if (!this.pluginConfig.getProperty(DatabaseKeys.storage_type).equalsIgnoreCase("yaml")) {
            Message.command_migrate_error.sendMessage(this.sender, Map.of(
                    "{file}", "locations.yml",
                    "{type}", this.type.getName(),
                    "{reason}", "root.storage.type is not set to yaml!"
            ));

            return;
        }

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        if (Files.locations.exists()) {
            final StorageHolder holder = new StorageHolder(new SqliteFactory(this.platform)).init();

            this.storageHolder.getCrateLocations().forEach((status, values) -> values.forEach(location -> {
                final String id = location.getId();
                final int x = location.getX();
                final int y = location.getY();
                final int z = location.getZ();

                final String crateId = location.getCrateName();

                final Key key = location.getWorldKey();

                try {
                    this.fusion.log(Level.INFO, "<red>Migrating position id <yellow>%s</yellow> with crate id <yellow>%s</yellow> <light_purple>(<red>x:%s</red>,<red>y:%s</red>,<red>z:%s</red>,<red>world:%s</red>)</light_purple> to sqlite! <light_purple>(<red>Status: %s</red>)</light_purple></red>", id, crateId, x, y, z, key.asString(), status);

                    if (!holder.hasWorld(key)) {
                        holder.addWorld(key);
                    }

                    holder.addCrateLocation(
                            crateId,
                            key,
                            id,
                            x,
                            y,
                            z
                    );

                    success.add(id);
                } catch (final Exception exception) {
                    this.fusion.log(Level.WARNING, "<red>Failed to migrate position id <yellow>%s</yellow> <light_purple>(<red>Status: %s</red>)</light_purple></red>", id, status);

                    failed.add(id);
                }
            }));

            holder.stop(); // clean up after ourselves!
        }

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        final List<String> files = new ArrayList<>(failedCrates + convertedCrates);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedCrates, failedCrates);
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }
}