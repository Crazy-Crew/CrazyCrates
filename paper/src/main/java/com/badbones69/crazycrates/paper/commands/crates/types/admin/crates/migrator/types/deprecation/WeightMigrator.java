package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.utils.MiscUtils;
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

public class WeightMigrator extends ICrateMigrator {

    public WeightMigrator(final CommandSender sender) {
        super(sender, MigrationType.WEIGHT_MIGRATION);
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
                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) continue;

                boolean isSave = false;

                if (section.contains("Tiers")) {
                    final ConfigurationSection tiers = section.getConfigurationSection("Tiers");

                    if (tiers != null) {
                        tiers.getKeys(false).forEach(pair -> {
                            final ConfigurationSection tier = tiers.getConfigurationSection(pair);

                            if (tier != null) {
                                final int chance = tier.getInt("Chance");
                                int maxRange = tier.getInt("MaxRange");

                                if (maxRange > 100000) {
                                    maxRange = 100000;
                                }

                                tier.set("Weight", MiscUtils.calculateWeight(chance, maxRange));
                            }
                        });

                        isSave = true;
                    }
                }

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (final String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        int chance = 10;
                        int maxRange = 100;

                        if (prizeSection.contains("Chance")) {
                            chance = prizeSection.getInt("Chance", 10);

                            isSave = true;
                        }

                        if (prizeSection.contains("MaxRange")) {
                            maxRange = prizeSection.getInt("MaxRange", 100);

                            isSave = true;
                        }

                        if (maxRange > 100000) {
                            maxRange = 100000;
                        }

                        prizeSection.set("Weight", MiscUtils.calculateWeight(chance, maxRange));
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