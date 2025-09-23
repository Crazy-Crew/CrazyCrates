package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.core.files.enums.FileAction;
import com.ryderbelserion.fusion.core.files.interfaces.ICustomFile;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LegacyColorMigrator extends ICrateMigrator {

    public LegacyColorMigrator(final CommandSender sender) {
        super(sender, MigrationType.LEGACY_COLOR_ALL);
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            this.config.setProperty(ConfigKeys.command_prefix, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.command_prefix), true));
            this.config.setProperty(ConfigKeys.inventory_name, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.inventory_name), true));

            this.config.setProperty(ConfigKeys.menu_button_name, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.menu_button_name), true));
            this.config.setProperty(ConfigKeys.menu_button_lore, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.menu_button_lore), true));

            this.config.setProperty(ConfigKeys.next_button_name, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.next_button_name), true));
            this.config.setProperty(ConfigKeys.next_button_lore, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.next_button_lore), true));

            this.config.setProperty(ConfigKeys.back_button_name, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.back_button_name), true));
            this.config.setProperty(ConfigKeys.back_button_lore, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.back_button_lore), true));

            this.config.setProperty(ConfigKeys.filler_name, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.filler_name), true));
            this.config.setProperty(ConfigKeys.filler_lore, this.utils.convertLegacy(this.config.getProperty(ConfigKeys.filler_lore), true));

            success.add("<green>⤷ config.yml");

            this.config.save();
            this.config.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ config.yml");
        }

        try {
            for (final Messages message : Messages.values()) {
                message.migrate();
            }

            success.add("<green>⤷ messages.yml");

            this.messages.save();
            this.messages.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ messages.yml");
        }

        final Collection<ICustomFile<?, ?, ?, ?>> customFiles = this.fileManager.getFiles().values();

        customFiles.forEach(key -> { //todo() wtf? just list files in the crates folder, this is retarded lol
            try {
                if (!key.hasAction(FileAction.DYNAMIC_FILE) || !key.isLoaded() || key.getFileType() != com.ryderbelserion.fusion.core.files.enums.FileType.PAPER) return;

                final PaperCustomFile customFile = (PaperCustomFile) key;

                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) return;

                boolean isSave = false;

                if (section.contains("CrateName")) {
                    set(section, "Name", this.utils.convertLegacy(section.getString("CrateName", " "), true));
                    set(section, "CrateName", null);

                    isSave = true;
                } else if (section.contains("Name")) {
                    set(section, "Name", this.utils.convertLegacy(section.getString("Name", " "), true));

                    isSave = true;
                }

                if (section.contains("Preview-Name")) {
                    set(section, "Preview.Name", this.utils.convertLegacy(section.getString("Preview.Name", " "), true));
                    set(section, "Preview-Name", null);

                    isSave = true;
                } else if (section.contains("Preview.Name")) {
                    set(section, "Preview.Name", this.utils.convertLegacy(section.getString("Preview.Name", " "), true));

                    isSave = true;
                }

                if (section.contains("Preview.Glass.Name")) {
                    set(section, "Preview.Glass.Name", this.utils.convertLegacy(section.getString("Preview.Glass.Name", " "), true));

                    isSave = true;
                }

                if (section.contains("tier-preview.glass.name")) {
                    set(section, "tier-preview.glass.name", this.utils.convertLegacy(section.getString("tier-preview.glass.name", " "), true));

                    isSave = true;
                }

                if (section.contains("BroadCast")) {
                    set(section, "BroadCast", this.utils.convertLegacy(section.getString("BroadCast", " "), true));

                    isSave = true;
                }

                if (section.contains("Lore")) {
                    set(section, "Lore", this.utils.convertLegacy(section.getStringList("Lore"), true));

                    isSave = true;
                }

                if (section.contains("PhysicalKey")) {
                    set(section, "PhysicalKey.Name", this.utils.convertLegacy(section.getString("PhysicalKey.Name", " "), true));

                    set(section, "PhysicalKey.Lore", this.utils.convertLegacy(section.getStringList("PhysicalKey.Lore"), true));

                    isSave = true;
                }

                final ConfigurationSection tiers = section.getConfigurationSection("Tiers");

                if (tiers != null) {
                    for (final String value : tiers.getKeys(false)) {
                        final ConfigurationSection tierSection = tiers.getConfigurationSection(value);

                        if (tierSection == null) continue;

                        if (tierSection.contains("Name")) {
                            set(tierSection, "Name", this.utils.convertLegacy(tierSection.getString("Name", " "), true));

                            isSave = true;
                        }

                        if (tierSection.contains("Lore")) {
                            set(tierSection, "Lore", this.utils.convertLegacy(tierSection.getStringList("Name"), true));

                            isSave = true;
                        }
                    }
                }

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (final String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        if (prizeSection.contains("Lore")) {
                            set(prizeSection, "DisplayLore", this.utils.convertLegacy(prizeSection.getStringList("Lore"), true));
                            set(prizeSection, "Lore", null);

                            isSave = true;
                        } else if (prizeSection.contains("DisplayLore")) {
                            set(prizeSection, "DisplayLore", this.utils.convertLegacy(prizeSection.getStringList("DisplayLore"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("DisplayName")) {
                            set(prizeSection, "DisplayName", this.utils.convertLegacy(prizeSection.getString("DisplayName", " "), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Messages")) {
                            set(prizeSection, "Messages", this.utils.convertLegacy(prizeSection.getStringList("Messages"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Items")) {
                            set(prizeSection, "Items", this.utils.convertLegacy(prizeSection.getStringList("Items"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Alternative-Prize.Messages")) {
                            set(prizeSection, "Alternative-Prize.Messages", this.utils.convertLegacy(prizeSection.getStringList("Alternative-Prize.Messages"), true));

                            isSave = true;
                        }

                        if (prizeSection.contains("Alternative-Prize.Items")) {
                            set(prizeSection, "Alternative-Prize.Items", this.utils.convertLegacy(prizeSection.getStringList("Alternative-Prize.Items"), true));

                            isSave = true;
                        }
                    }
                }

                if (isSave) {
                    customFile.save();
                }

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + key.getFileName());
            }
        });

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
}