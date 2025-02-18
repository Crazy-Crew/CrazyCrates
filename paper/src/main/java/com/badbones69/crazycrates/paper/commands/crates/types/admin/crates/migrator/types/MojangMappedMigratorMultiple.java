package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.paper.files.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MojangMappedMigratorMultiple extends ICrateMigrator {

    public MojangMappedMigratorMultiple(final CommandSender sender) {
        super(sender, MigrationType.MOJANG_MAPPED_ALL);
    }

    @Override
    public void run() {
        final Collection<CustomFile> customFiles = this.plugin.getFileManager().getFiles().values();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(customFile -> {
            try {
                if (!customFile.isDynamic()) return;

                migrate(customFile, "");

                success.add("<green>⤷ " + customFile.getEffectiveName());
            } catch (Exception exception) {
                failed.add("<red>⤷ " + customFile.getEffectiveName());
            }
        });

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