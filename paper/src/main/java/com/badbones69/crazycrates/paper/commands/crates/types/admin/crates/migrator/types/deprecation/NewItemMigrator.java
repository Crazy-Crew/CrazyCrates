package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.files.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewItemMigrator extends ICrateMigrator {

    public NewItemMigrator(final CommandSender sender) {
        super(sender, MigrationType.NEW_ITEM_FORMAT);
    }

    @Override
    public void run() {
        final Collection<CustomFile> customFiles = this.plugin.getFileManager().getFiles().values();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(customFile -> {
            try {
                if (!customFile.isDynamic()) return;

                final YamlConfiguration configuration = customFile.getConfiguration();

                if (configuration == null) return;

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) return;

                boolean isSave = false;

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        if (prizeSection.isList("Items")) {
                            final List<String> items = new ArrayList<>() {{
                                addAll(prizeSection.getStringList("Items"));
                            }};

                            prizeSection.set("Items", null);

                            items.forEach(item -> prizeSection.set("Items." + MiscUtils.randomUUID() + ".data", item));

                            isSave = true;
                        }
                    }
                }

                if (isSave) {
                    customFile.save();
                }

                success.add("<green>⤷ " + customFile.getEffectiveName());
            } catch (Exception exception) {
                failed.add("<red>⤷ " + customFile.getEffectiveName());
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