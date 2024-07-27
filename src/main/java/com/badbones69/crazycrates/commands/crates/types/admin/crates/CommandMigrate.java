package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.core.util.FileUtil;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandMigrate extends BaseCommand {

    public enum MigrationType {
        MOJANG_MAPPED_SINGLE("MojangMappedSingle"),
        MOJANG_MAPPED_ALL("MojangMappedAll"),

        //MIGRATE_OLD_COMMANDS("MigrateOldCommands"),

        CRATES_DEPRECATED_ALL("CratesDeprecated"),

        EXCELLENT_CRATES("ExcellentCrates"),

        SPECIALIZED_CRATES("SpecializedCrates");

        private final String name;

        MigrationType(String name) {
            this.name = name;
        }

        public final String getName() {
            return this.name;
        }
    }

    @Command("migrate")
    @Permission(value = "crazycrates.migrate", def = PermissionDefault.OP)
    public void migrate(final CommandSender sender, @ArgName("migration_type") final MigrationType type, @ArgName("crate") @Optional @Suggestion("crates") final String crateName) {
        switch (type) {
            case MOJANG_MAPPED_ALL -> this.plugin.getFileManager().getCustomFiles().forEach(customFile -> migrate(sender, customFile, "", type));

            case MOJANG_MAPPED_SINGLE -> {
                if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
                    Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

                    return;
                }

                final CustomFile file = this.fileManager.getCustomFile(crateName);

                if (file == null) {
                    Messages.error_migrating.sendMessage(sender, new HashMap<>() {{
                        put("{file}", crateName);
                        put("{type}", type.getName());
                        put("{reason}", "File was not loaded properly.");
                    }});

                    return;
                }

                migrate(sender, file, crateName, type);

                Messages.successfully_migrated.sendMessage(sender, new HashMap<>() {{
                    put("{file}", crateName);
                    put("{type}", type.getName());
                }});
            }

            case CRATES_DEPRECATED_ALL -> {
                this.plugin.getFileManager().getCustomFiles().forEach(file -> {
                    final YamlConfiguration configuration = file.getConfiguration();

                    final ConfigurationSection prizes = configuration.getConfigurationSection("Crate.Prizes");

                    if (prizes != null) {
                        prizes.getKeys(false).forEach(value -> {
                            if (configuration.contains("Crate.Prizes." + value + ".Lore")) {
                                configuration.set("Crate.Prizes." + value + ".DisplayLore", configuration.getStringList("Crate.Prizes." + value + ".Lore"));

                                configuration.set("Crate.Prizes." + value + ".Lore", null);
                            }

                            if (configuration.contains("Crate.Prizes." + value + ".Patterns")) {
                                configuration.set("Crate.Prizes." + value + ".DisplayPatterns", configuration.getStringList("Crate.Prizes." + value + ".Patterns"));

                                configuration.set("Crate.Prizes." + value + ".Patterns", null);
                            }
                        });

                        file.save();
                    }
                });

                Messages.successfully_migrated.sendMessage(sender, new HashMap<>() {{
                    put("{file}", crateName);
                    put("{type}", type.getName());
                }});

                this.plugin.getFileManager().init();
            }

            /*case MIGRATE_OLD_COMMANDS -> {
                final File oldDirectory = new File(this.plugin.getDataFolder(), "old");

                final List<String> files = FileUtil.getFiles(oldDirectory, ".yml");

                files.forEach(key -> {
                    final @Nullable CustomFile customFile = this.plugin.getFileManager().getCustomFile(key);

                    if (customFile != null) {
                        final File file = new File(oldDirectory, key + ".yml");

                        final YamlConfiguration root  = YamlConfiguration.loadConfiguration(file);

                        final ConfigurationSection prizes = root.getConfigurationSection("Crate.Prizes");

                        if (prizes != null) {
                            final YamlConfiguration configuration = customFile.getConfiguration();

                            prizes.getKeys(false).forEach(value -> {
                                List<String> commands = prizes.getStringList(value + ".Commands");

                                if (!commands.isEmpty()) {
                                    configuration.set("Crate.Prizes." + value + ".Commands", commands);
                                }
                            });

                            customFile.save();
                        }
                    }
                });
            }*/

            case SPECIALIZED_CRATES, EXCELLENT_CRATES -> Messages.migration_not_available.sendMessage(sender);
        }
    }

    private void migrate(final CommandSender sender, final CustomFile file, final String crateName, final MigrationType type) {
        final YamlConfiguration configuration = file.getConfiguration();

        final ConfigurationSection crate = configuration.getConfigurationSection("Crate");

        if (crate == null) {
            Messages.error_migrating.sendMessage(sender, new HashMap<>() {{
                put("{file}", crateName.isEmpty() ? file.getStrippedName() : crateName);
                put("{type}", type.getName());
                put("{reason}", "File could not be found in our data, likely invalid yml file that didn't load properly.");
            }});

            return;
        }

        set(crate, "Item", crate.getString("Item", "diamond").toLowerCase());
        set(crate, "Preview.Glass.Item", crate.getString("Preview.Glass.Item", "gray_stained_glass_pane").toLowerCase());
        set(crate, "PhysicalKey.Item", crate.getString("PhysicalKey.Item", "lime_dye").toLowerCase());

        final ConfigurationSection prizes = crate.getConfigurationSection("Prizes");

        if (prizes != null) {
            prizes.getKeys(false).forEach(key -> {
                final ConfigurationSection prize = prizes.getConfigurationSection(key);

                if (prize == null) return;

                if (prize.contains("DisplayItem")) {
                    set(prize, "DisplayItem", prize.getString("DisplayItem", "red_terracotta").toLowerCase());
                }

                if (prize.contains("DisplayTrim")) {
                    set(prize, "DisplayTrim.Material", prize.getString("DisplayTrim.Material", "quartz").toLowerCase());
                    set(prize, "DisplayTrim.Pattern", prize.getString("DisplayTrim.Pattern", "sentry").toLowerCase());
                }

                if (prize.contains("Editor-Items")) {
                    final List<?> items = prize.getList("Editor-Items");

                    if (items != null) {
                        items.forEach(item -> {
                            final org.bukkit.inventory.ItemStack itemStack = (org.bukkit.inventory.ItemStack) item;

                            final String asBase64 = ItemUtil.toBase64(itemStack);

                            if (prize.contains("Items")) {
                                final List<String> list = prize.getStringList("Items");

                                list.add("Data: " + asBase64);

                                prize.set("Items", list);
                            } else {
                                prize.set("Items", new ArrayList<>() {{
                                    add("Data: " + asBase64);
                                }});
                            }
                        });

                        prize.set("Editor-Items", null);
                    }
                }

                if (prize.contains("DisplayEnchantments")) {
                    List<String> enchants = new ArrayList<>() {{
                        prize.getStringList("DisplayEnchantments").forEach(enchant -> add(ItemUtils.getEnchant(enchant)));
                    }};

                    set(prize, "DisplayEnchantments", enchants);
                }
            });
        }

        file.save();
        file.reload();
    }

    private <T> void set(ConfigurationSection section, String path, T value) {
        section.set(path, value);
    }
}