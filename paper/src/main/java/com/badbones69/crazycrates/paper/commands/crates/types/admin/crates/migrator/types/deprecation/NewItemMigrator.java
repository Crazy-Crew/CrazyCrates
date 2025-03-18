package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.util.StringUtils;
import com.ryderbelserion.fusion.paper.files.CustomFile;
import com.ryderbelserion.fusion.paper.util.PaperMethods;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import java.io.File;
import java.util.*;

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

                            items.forEach(item -> {
                                for (final String key : item.split(", ")) {
                                    final String option = key.split(":")[0];
                                    final String type = key.replace(option + ":", "").replace(option, "");

                                    final String uuid = MiscUtils.randomUUID();

                                    switch (option.toLowerCase()) {
                                        case "item" -> prizeSection.set("Items." + uuid + ".material", type);
                                        case "data" -> prizeSection.set("Items." + uuid + ".data", type);
                                        case "name" -> prizeSection.set("Items." + uuid + ".name", type);
                                        case "mob" -> prizeSection.set("Items." + uuid + ".settings.mob.type", type);
                                        case "glowing" -> prizeSection.set("Items." + uuid + ".settings.glowing", type);
                                        case "amount" -> prizeSection.set("Items." + uuid + ".amount", type);
                                        case "damage" -> prizeSection.set("Items." + uuid + ".settings.damage", type);
                                        case "lore" -> prizeSection.set("Items." + uuid + ".lore", List.of(type.split(",")));
                                        case "player" -> prizeSection.set("Items." + uuid + ".settings.player", type);
                                        case "skull" -> prizeSection.set("Items." + uuid + ".settings.skull", type);
                                        case "custom-model-data" -> prizeSection.set("Items." + uuid + ".custom-model-data", type);
                                        case "unbreakable-item" -> prizeSection.set("Items." + uuid + ".unbreakable-item", type);
                                        case "hide-tool-tip" -> prizeSection.set("Items." + uuid + ".hide-tool-tip", type);
                                        case "trim-pattern" -> prizeSection.set("Items." + uuid + ".settings.trim.pattern", type);
                                        case "trim-material" -> prizeSection.set("Items." + uuid + ".settings.trim.material", type);
                                        case "rgb" -> prizeSection.set("Items." + uuid + ".settings.rgb", type);
                                        case "color" -> prizeSection.set("Items." + uuid + ".settings.color", type);
                                        default -> {
                                            final String toString = option.toLowerCase();

                                            if (PaperMethods.getEnchantment(toString) != null) {
                                                final Map<String, Integer> enchantments = new HashMap<>();

                                                enchantments.put(toString, StringUtils.tryParseInt(value).map(Number::intValue).orElse(1));

                                                final ConfigurationSection enchantmentSection = prizeSection.createSection("Items." + uuid + ".enchantments");

                                                enchantments.forEach(enchantmentSection::set);

                                                break;
                                            }

                                            if (!prizeSection.contains("Items." + uuid + ".hide-tool-tip")) {
                                                for (ItemFlag itemFlag : ItemFlag.values()) {
                                                    if (itemFlag.name().equalsIgnoreCase(option)) {
                                                        prizeSection.set("Items." + uuid + ".hide-tool-tip", true);

                                                        break;
                                                    }
                                                }
                                            }

                                            try {
                                                final PatternType patternType = PaperMethods.getPatternType(toString);

                                                if (patternType != null) {
                                                    final Map<String, String> patterns = new HashMap<>();

                                                    patterns.put(toString, value);

                                                    final ConfigurationSection patternsSection = prizeSection.createSection("Items." + uuid + ".settings.patterns");

                                                    patterns.forEach(patternsSection::set);
                                                }
                                            } catch (Exception ignored) {}
                                        }
                                    }
                                }
                            });

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