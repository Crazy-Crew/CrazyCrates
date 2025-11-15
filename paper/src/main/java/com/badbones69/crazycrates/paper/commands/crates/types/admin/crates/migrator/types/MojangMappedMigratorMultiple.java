package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        final List<Path> paths = this.fusion.getFiles(getCratesDirectory(), ".yml");

        for (final Path path : paths) {
            final Optional<PaperCustomFile> optional = this.fileManager.getPaperFile(path);

            if (optional.isEmpty()) continue;

            final PaperCustomFile customFile = optional.get();

            if (!customFile.isLoaded()) continue;

            try {
                migrate(customFile, "");

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + customFile.getFileName());
            }
        }

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

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