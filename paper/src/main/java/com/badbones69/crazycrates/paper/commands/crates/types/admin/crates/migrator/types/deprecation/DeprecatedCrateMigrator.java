package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        final List<Path> paths = this.fusion.getFiles(getCratesDirectory(), ".yml");

        for (final Path path : paths) {
            final Optional<PaperCustomFile> optional = this.fileManager.getPaperFile(path);

            if (optional.isEmpty()) continue;

            final PaperCustomFile customFile = optional.get();

            if (!customFile.isLoaded()) continue;

            final YamlConfiguration configuration = customFile.getConfiguration();

            final ConfigurationSection section = configuration.getConfigurationSection("Crate");

            if (section == null) continue;

            try {
                boolean isSave = false;

                if (section.contains("CrateName")) {
                    set(section, "Name", section.getString("CrateName", " "));
                    set(section, "CrateName", null);

                    isSave = true;
                }

                final ConfigurationSection displaySection = section.contains("Preview.Display") ? section.getConfigurationSection("Preview.Display") : section.createSection("Preview.Display");

                if (displaySection != null) {
                    if (section.contains("Glowing") && section.isBoolean("Glowing")) {
                        final boolean isGlowing  = section.getBoolean("Glowing", false);

                        if (isGlowing) {
                            set(displaySection, "Glowing", "add_glow");
                        } else {
                            set(displaySection, "Glowing", "remove_glow");
                        }

                        set(section, "Glowing", null);

                        isSave = true;
                    }

                    if (section.contains("Item")) {
                        set(displaySection, "Item", section.getString("Item", "chest"));
                        set(section, "Item", null);

                        isSave = true;
                    }

                    if (section.contains("Custom-Model-Data")) {
                        set(displaySection, "Custom-Model-Data", section.getInt("Custom-Model-Data", -1));
                        set(section, "Custom-Model-Data", null);

                        isSave = true;
                    }

                    if (section.contains("Model.Namespace") && section.contains("Model.Id")) {
                        set(displaySection, "Model.Namespace", section.getString("Model.Namespace", ""));
                        set(displaySection, "Model.Id", section.getString("Model.Id", ""));

                        set(section, "Model", null);

                        isSave = true;
                    }

                    if (section.contains("Slot")) {
                        set(displaySection, "Slot", section.getInt("Slot", -1));
                        set(section, "Slot", null);

                        isSave = true;
                    }

                    if (section.contains("Name")) {
                        set(displaySection, "Name", section.getString("Name", " "));
                        set(section, "Name", null);

                        isSave = true;
                    }

                    if (section.contains("Lore")) {
                        set(displaySection, "Lore", section.getStringList("Lore"));
                        set(section, "Lore", null);

                        isSave = true;
                    }

                    if (section.contains("InGUI")) {
                        set(displaySection, "Toggle", section.getBoolean("InGUI", false));
                        set(section, "InGUI", null);

                        isSave = true;
                    }
                }

                if (section.contains("PhysicalKey.Glowing") && section.isBoolean("PhysicalKey.Glowing")) {
                    final boolean isGlowing  = section.getBoolean("PhysicalKey.Glowing", false);

                    if (isGlowing) {
                        set(section, "PhysicalKey.Glowing", "add_glow");
                    } else {
                        set(section, "PhysicalKey.Glowing", "remove_glow");
                    }

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

                        if (prizeSection.contains("Glowing") && prizeSection.isBoolean("Glowing")) {
                            final boolean isGlowing  = section.getBoolean("Glowing", false);

                            if (isGlowing) {
                                set(section, "Glowing", "add_glow");
                            } else {
                                set(section, "Glowing", "remove_glow");
                            }

                            isSave = true;
                        }
                    }
                }

                if (isSave) {
                    customFile.save();
                    customFile.load();
                }

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + customFile.getFileName());
            }
        }

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        final List<String> files = new ArrayList<>(failedCrates + convertedCrates);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedCrates, failedCrates);

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, T value) {
        section.set(path, value);
    }
}