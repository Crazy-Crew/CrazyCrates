package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.List;

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

            final boolean isEnabled = section.getBoolean("toggle", false);



            final String inventory = section.getString("inventory.name", "<bold><gradient:#e91e63:blue>CrazyCrates</gradient></bold>");
            final int rows = section.getInt("inventory.rows", 5);

            migrateItem(section, "menu");
            migrateItem(section, "next");
            migrateItem(section, "back");

            migrateItem(section, "filler");
        }, () -> {

        });
    }

    public void migrateItem(@NonNull final ConfigurationSection section, @NonNull final String path) {
        final String item = section.getString("inventory.buttons.%s.item".formatted(path), "compass");

        final int row = section.getInt("inventory.butttons.%s.placement.row".formatted(path), -1);
        final int column = section.getInt("inventory.buttons.%s.placement.column".formatted(path), 5);

        final List<String> commands = section.getStringList("inventory.buttons.%s.overrides.list".formatted(path));

        final int modelData = section.getInt("inventory.buttons.%s.custom-model-data".formatted(path), -1);

        final String modelNamespace = section.getString("inventory.buttons.%s.model.namespace".formatted(path), "");
        final String modelId = section.getString("inventory.buttons.%s.model.id".formatted(path), "");

        final String name = section.getString("inventory.buttons.%s.name".formatted(path), "");
        final List<String> lore = section.getStringList("inventory.buttons.%s.lore".formatted(path));

        switch (path) {
            case "filler" -> {
                final boolean isEnabled = section.getBoolean("inventory.buttons.%s.toggle", false);
            }
        }
    }

    public void add(final CommentedConfigurationNode node, final String value) {

    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }
}