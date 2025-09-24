package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeprecatedCrateMigrator extends ICrateMigrator {

    public DeprecatedCrateMigrator(final CommandSender sender) {
        super(sender, MigrationType.CRATES_DEPRECATED_ALL);
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

                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) continue;

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

                if (section.contains("Preview.ChestLines")) {
                    set(section, "Preview.Rows", section.getInt("Preview.ChestLines"));
                    set(section, "Preview.ChestLines", null);

                    isSave = true;
                }

                if (section.contains("Settings.Border.Glass-Border.Toggle")) {
                    set(section, "Animation.Glass-Frame.Toggle", section.getBoolean("Settings.Border.Glass-Border.Toggle", true));
                    set(section, "Animation.Glass-Frame.Random.Toggle", true);
                    set(section, "Animation.Glass-Frame.Random.Items", section.createSection("Animation.Glass-Frame.Random.Items"));
                    set(section, "Animation.Glass-Frame.Items", section.createSection("Animation.Glass-Frame.Items"));

                    set(section, "Settings.Border.Glass-Border", null);

                    isSave = true;
                }

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (final String value : prizes.getKeys(false)) {
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

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + path.getFileName().toString());
            }
        }

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);

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