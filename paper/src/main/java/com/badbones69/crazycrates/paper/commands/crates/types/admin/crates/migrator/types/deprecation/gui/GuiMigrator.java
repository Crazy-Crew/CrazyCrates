package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation.gui;

import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GuiMigrator extends ICrateMigrator {

    public GuiMigrator(final CommandSender sender) {
        super(sender, MigrationType.GUI_MIGRATION);
    }

    @Override
    public void run() {
        final Path config = this.dataPath.resolve("config.yml");

        this.fileManager.addPaperFile(config).getPaperFile(config).ifPresentOrElse(customFile -> {
            final YamlConfiguration configuration = customFile.getConfiguration();

            if (!configuration.contains("gui")) {
                return;
            }

            final ConfigurationSection section = configuration.getConfigurationSection("gui");

            if (section == null) {
                return;
            }

            final YamlCustomFile file = FileKeys.crate_gui.getYamlCustomFile();

            final CommentedConfigurationNode node = file.getConfiguration();

            if (section.contains("toggle")) {
                set(node, section.getBoolean("toggle", false), "gui", "toggle");
            }

            if (section.contains("inventory.name")) {
                set(node, section.getString("inventory.name", "<bold><gradient:#e91e63:blue>CrazyCrates</gradient></bold>"), "gui", "name");
            }

            if (section.contains("inventory.rows")) {
                set(node, section.getInt("inventory.rows", 5), "gui", "rows");
            }

            migrateItem(node, section, "menu");
            migrateItem(node, section, "next");
            migrateItem(node, section, "back");

            migrateItem(node, section, "filler");
        }, () -> this.fusion.log(Level.WARNING, "Failed to migrate the config.yml to crate-gui.yml"));
    }

    public void set(@NonNull final CommentedConfigurationNode node, @NonNull final Object value, @NonNull final Object... path) {
        try {
            node.node(path).set(value);
        } catch (final SerializationException exception) {
            exception.printStackTrace();
        }
    }

    public void migrateItem(@NonNull final CommentedConfigurationNode parent, @NonNull final ConfigurationSection section, @NonNull final String path) {
        final ConfigurationSection button = section.getConfigurationSection("inventory.buttons.%s".formatted(path));

        if (button == null) {
            return;
        }

        final CommentedConfigurationNode item = parent.node(path);

        switch (path) {
            case "filler" -> {
                if (button.contains("toggle")) {
                    set(item, button.getBoolean("toggle", false), "toggle");
                }
            }
        }

        set(item, path, "key");

        final Path folder = this.dataPath.resolve("buttons").resolve("%s.yml".formatted(path));

        if (Files.notExists(folder)) {
            try {
                Files.createFile(folder);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }

        this.fileManager.addFile(folder, FileType.YAML).getYamlFile(folder).ifPresentOrElse(customFile -> {
            final CommentedConfigurationNode node = customFile.getConfiguration();

            boolean isSave = false;

            final int slot = getSlotFromColumn(button.getInt("placement.row", 5), button.getInt("placement.column", 5));

            if (slot >= 0) {
                set(item, slot);
            }

            if (button.contains("item")) {
                set(node, button.getString("item", "compass"));

                isSave = true;
            }

            if (button.contains("overrides.list")) {
                set(node, button.getStringList("overrides.list"));

                isSave = true;
            }

            if (button.contains("item")) {
                set(node, button.getString("item", "compass"));

                isSave = true;
            }

            if (button.contains("custom-model-data")) {
                set(node, button.getInt("custom-model-data", -1));

                isSave = true;
            }

            if (button.contains("model.namespace")) {
                set(node, button.getString("model.namespace", ""));

                isSave = true;
            }

            if (button.contains("model.id")) {
                set(node, button.getString("model.id", ""));

                isSave = true;
            }

            if (button.contains("name")) {
                set(node, button.getString("name", ""));

                isSave = true;
            }

            if (button.contains("lore")) {
                set(node, button.getStringList("lore"));

                isSave = true;
            }

            if (isSave) {
                customFile.save();
            }
        }, () -> this.fusion.log(Level.WARNING, "Failed to add file %s!", folder));
    }

    public int getSlotFromColumn(final int row, final int column) {
        return (column + (row - 1) * 9) - 1;
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }
}