package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.api.enums.FileType;
import com.ryderbelserion.fusion.paper.files.LegacyCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MojangMappedMigratorSingle extends ICrateMigrator {

    public MojangMappedMigratorSingle(final CommandSender sender, final String crateName) {
        super(sender, MigrationType.MOJANG_MAPPED_SINGLE, crateName);
    }

    @Override
    public void run() {
        if (this.crateName == null || this.crateName.isEmpty() || this.crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(this.sender, "{value}", "crate name");

            return;
        }

        final LegacyCustomFile customFile = this.plugin.getFileManager().getFile(this.crateName, FileType.YAML);

        if (customFile == null) {
            Messages.error_migrating.sendMessage(this.sender, new HashMap<>() {{
                put("{file}", crateName);
                put("{type}", type.getName());
                put("{reason}", "File was not loaded properly.");
            }});

            return;
        }

        if (!customFile.isDynamic()) {
            Messages.error_migrating.sendMessage(this.sender, new HashMap<>() {{
                put("{file}", crateName);
                put("{type}", type.getName());
                put("{reason}", "File requested is not a crate config file.");
            }});

            return;
        }

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            migrate(customFile, this.crateName);

            success.add("<green>⤷ " + this.crateName);
        } catch (Exception exception) {
            failed.add("<red>⤷ " + this.crateName);
        }

        final Crate crate = this.crateManager.getCrateFromName(this.crateName);

        this.crateManager.reloadCrate(crate);

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