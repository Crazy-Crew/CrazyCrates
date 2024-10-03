package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.vital.paper.api.files.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeprecatedCrateMigrator extends ICrateMigrator {

    public DeprecatedCrateMigrator(final CommandSender sender, final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        final Collection<CustomFile> customFiles = this.plugin.getVital().getFileManager().getCustomFiles().values();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(customFile -> {
            try {
                final YamlConfiguration configuration = customFile.getConfiguration();

                if (configuration == null) return;

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) return;

                boolean isSave = false;

                if (section.contains("CrateName")) {
                    set(section, "Name", section.getString("CrateName", " "));
                    set(section, "CrateName", null);

                    isSave = true;
                }

                if (section.contains("Preview-Name")) {
                    set(section, "Preview.Name", section.getString("Preview-Name", " "));
                    set(section, "Preview-Name", null);

                    isSave = true;
                }

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        if (prizeSection.contains("Lore")) {
                            set(prizeSection, "DisplayLore", prizeSection.getStringList("Lore"));
                            set(prizeSection, "Lore", null);

                            isSave = true;
                        }

                        if (prizeSection.contains("Patterns")) {
                            set(prizeSection, "DisplayPatterns", prizeSection.getStringList("Patterns"));
                            set(prizeSection, "Patterns", null);

                            isSave = true;
                        }
                    }
                }

                if (isSave) {
                    customFile.save();
                }

                success.add("<green>⤷ " + customFile.getCleanName());
            } catch (Exception exception) {
                failed.add("<red>⤷ " + customFile.getCleanName());
            }
        });

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);

        this.fileManager.init();

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();
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