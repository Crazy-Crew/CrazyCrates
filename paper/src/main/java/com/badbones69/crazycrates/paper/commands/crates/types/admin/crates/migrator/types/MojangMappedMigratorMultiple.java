package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MojangMappedMigratorMultiple extends ICrateMigrator {

    public MojangMappedMigratorMultiple(final CommandSender sender) {
        super(sender, MigrationType.MOJANG_MAPPED_ALL);
    }

    @Override
    public void run() {
        final List<Path> paths = this.fusion.getFiles(this.dataPath.resolve("crates"), ".yml");

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        for (final Path path : paths) {
            try {
                if (!this.fileManager.hasFile(path)) continue;

                final Optional<PaperCustomFile> optional = this.fileManager.getPaperFile(path);

                if (optional.isEmpty()) continue;

                final PaperCustomFile customFile = optional.get();

                migrate(customFile, "");

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + path.getFileName().toString());
            }
        }

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }

    @Override
    public final File getCratesDirectory() {
        return new File(this.plugin.getDataFolder(), "crates");
    }
}