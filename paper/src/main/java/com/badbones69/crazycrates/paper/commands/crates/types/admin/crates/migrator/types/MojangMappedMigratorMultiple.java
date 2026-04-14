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

        for (final Path path : this.fusion.getFilesByPath(this.dataPath.resolve("crates"), ".yml")) {
            final Optional<PaperCustomFile> origin = this.fileManager.getPaperFile(path);

            final String fileName = path.getFileName().toString();

            if (origin.isEmpty()) {
                failed.add("<red>⤷ " + fileName);

                continue;
            }

            final PaperCustomFile customFile = origin.get();

            try {
                if (migrate(customFile)) {
                    success.add("<green>⤷ " + customFile.getFileName());
                } else {
                    failed.add("<red>⤷ " + fileName);
                }
            } catch (Exception exception) {
                failed.add("<red>⤷ " + fileName);
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