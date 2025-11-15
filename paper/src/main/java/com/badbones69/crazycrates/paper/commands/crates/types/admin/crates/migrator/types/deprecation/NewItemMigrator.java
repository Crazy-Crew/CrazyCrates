package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.core.enums.Comments;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.core.api.interfaces.files.ICustomFile;
import com.ryderbelserion.fusion.core.api.utils.StringUtils;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;
import java.io.File;
import java.util.*;

public class NewItemMigrator extends ICrateMigrator {

    public NewItemMigrator(final CommandSender sender) {
        super(sender, MigrationType.NEW_ITEM_FORMAT);
    }

    @Override
    public void run() {
        final Collection<ICustomFile<? extends ICustomFile<?>>> customFiles = this.fileManager.getCustomFiles().values();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(key -> {
            try {
                if (key.isStatic() || !key.isLoaded() || key.getFileType() != FileType.PAPER) return;

                final PaperCustomFile customFile = (PaperCustomFile) key;

                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection section = configuration.getConfigurationSection("Crate");

                if (section == null) return;

                boolean isSave = false;

                final ConfigurationSection prizes = section.getConfigurationSection("Prizes");

                if (prizes != null) {
                    for (final String value : prizes.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                        if (prizeSection == null) continue;

                        if (prizeSection.isList("Items")) {
                            final List<String> items = prizeSection.getStringList("Items");

                            prizeSection.set("Items", null);

                            items.forEach(item -> {
                                final Map<String, String> patterns = new HashMap<>();
                                final Map<String, Integer> enchantments = new HashMap<>();
                                final String uuid = MiscUtils.randomUUID();

                                for (final String split : item.split(", ")) {
                                    final String option = split.split(":")[0];
                                    final String type = split.replace(option + ":", "").replace(option, "");

                                    switch (option.toLowerCase()) {
                                        case "item" -> {
                                            prizeSection.set("Items." + uuid + ".material", type);
                                            prizeSection.setComments("Items." + uuid + ".Material", Comments.material.getComments());
                                        }
                                        case "data" -> {
                                            prizeSection.set("Items." + uuid + ".data", type);
                                            prizeSection.setComments("Items." + uuid + ".data", Comments.base64.getComments());
                                        }
                                        case "name" -> {
                                            prizeSection.set("Items." + uuid + ".name", type);
                                            prizeSection.setComments("Items." + uuid + ".name", Comments.name.getComments());
                                        }
                                        case "mob" -> {
                                            prizeSection.set("Items." + uuid + ".settings.mob.type", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.mob.type", Comments.mob_type.getComments());
                                        }
                                        case "glowing" -> {
                                            prizeSection.set("Items." + uuid + ".settings.glowing", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.glowing", Comments.glowing.getComments());
                                        }
                                        case "amount" -> {
                                            prizeSection.set("Items." + uuid + ".amount", type);
                                            prizeSection.setComments("Items." + uuid + ".amount", Comments.amount.getComments());
                                        }
                                        case "damage" -> {
                                            prizeSection.set("Items." + uuid + ".settings.damage", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.damage", Comments.damage.getComments());
                                        }
                                        case "lore" -> {
                                            prizeSection.set("Items." + uuid + ".lore", List.of(type.split(",")));
                                            prizeSection.setComments("Items." + uuid + ".lore", Comments.lore.getComments());
                                        }
                                        case "player" -> {
                                            prizeSection.set("Items." + uuid + ".settings.player", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.player", Comments.player.getComments());
                                        }
                                        case "skull" -> {
                                            prizeSection.set("Items." + uuid + ".settings.skull", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.skull", Comments.skull.getComments());
                                        }
                                        case "custom-model-data" -> {
                                            prizeSection.set("Items." + uuid + ".custom-model-data", type);
                                            prizeSection.setComments("Items." + uuid + ".custom-model-data", Comments.custom_model_data.getComments());
                                        }
                                        case "unbreakable-item" -> {
                                            prizeSection.set("Items." + uuid + ".unbreakable-item", type);
                                            prizeSection.setComments("Items." + uuid + ".unbreakable-item", Comments.unbreakable.getComments());
                                        }
                                        case "hide-tool-tip" -> {
                                            prizeSection.set("Items." + uuid + ".hide-tool-tip", type);
                                            prizeSection.setComments("Items." + uuid + ".hide-tool-tip", Comments.hide_tool_tip.getComments());
                                        }
                                        case "trim-pattern" -> {
                                            prizeSection.set("Items." + uuid + ".settings.trim.pattern", type);
                                            prizeSection.setComments("Items." + uuid + ".trim.pattern", Comments.trim_pattern.getComments());
                                        }
                                        case "trim-material" -> {
                                            prizeSection.set("Items." + uuid + ".settings.trim.material", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.trim.material", Comments.trim_material.getComments());
                                        }
                                        case "rgb" -> {
                                            prizeSection.set("Items." + uuid + ".settings.rgb", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.rgb", Comments.rgb.getComments());
                                        }
                                        case "color" -> {
                                            prizeSection.set("Items." + uuid + ".settings.color", type);
                                            prizeSection.setComments("Items." + uuid + ".settings.color", Comments.color.getComments());
                                        }
                                        default -> {
                                            final String placeholder = option.toLowerCase();

                                            try {
                                                final PotionEffectType effect = ItemUtils.getPotionEffect(placeholder);

                                                if (effect != null) {
                                                    final ConfigurationSection potionsSection = prizeSection.createSection("Items." + uuid + ".settings.potions");

                                                    final ConfigurationSection potionSection = potionsSection.createSection(placeholder);

                                                    potionSection.set("duration", 60);
                                                    potionSection.set("level", 1);

                                                    potionSection.set("style.icon", true);
                                                    potionSection.set("style.ambient", true);
                                                    potionSection.set("style.particles", true);

                                                    prizeSection.set("Items." + uuid + ".settings.potions", Comments.potions.getComments());
                                                }
                                            } catch (final Exception ignored) {}

                                            if (ItemUtils.getEnchantment(placeholder) != null) {
                                                enchantments.put(option.toLowerCase(), StringUtils.tryParseInt(value).map(Number::intValue).orElse(1));

                                                final ConfigurationSection enchantmentSection = prizeSection.createSection("Items." + uuid + ".enchantments");

                                                prizeSection.setComments("Items." + uuid + ".enchantments", Comments.enchantments.getComments());

                                                enchantments.forEach(enchantmentSection::set);

                                                break;
                                            }

                                            if (!prizeSection.contains("Items." + uuid + ".hide-tool-tip")) {
                                                for (ItemFlag itemFlag : ItemFlag.values()) {
                                                    if (itemFlag.name().equalsIgnoreCase(option)) {
                                                        prizeSection.set("Items." + uuid + ".hide-tool-tip", true);
                                                        prizeSection.setComments("Items." + uuid + ".hide-tool-tip", Comments.hide_tool_tip.getComments());

                                                        break;
                                                    }
                                                }
                                            }

                                            try {
                                                final PatternType patternType = ItemUtils.getPatternType(placeholder);

                                                if (patternType != null) {
                                                    patterns.put(placeholder, type);

                                                    final ConfigurationSection patternsSection = prizeSection.createSection("Items." + uuid + ".settings.patterns");

                                                    prizeSection.setComments("Items." + uuid + ".settings.patterns", Comments.patterns.getComments());

                                                    patterns.forEach(patternsSection::set);
                                                }
                                            } catch (final Exception ignored) {}
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

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (final Exception exception) {
                failed.add("<red>⤷ " + key.getFileName());
            }
        });

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        final List<String> files = new ArrayList<>(failedCrates + convertedCrates);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedCrates, failedCrates);

        this.fileManager.init(new ArrayList<>());

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