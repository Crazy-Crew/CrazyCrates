package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.CratesAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandMigrate extends BaseCommand {

    public enum MigrationType {
        MOJANG_MAPPED("MojangMapped"),
        SPECIALIZED_CRATES("SpecializedCrates"),
        EXCELLENT_CRATES("ExcellentCrates");

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
    public void migrate(final CommandSender sender, final MigrationType type) {
        switch (type) {
            case MOJANG_MAPPED -> this.plugin.getInstance().getCrateFiles().forEach(name -> {
                final CustomFile customFile = this.fileManager.getCustomFile(name);

                if (customFile == null) {
                    sender.sendRichMessage(Messages.error_migrating.getMessage(sender, new HashMap<>() {{
                        put("{file}", name);
                        put("{type}", type.getName());
                        put("{reason}", "File was not loaded properly.");
                    }}));

                    return;
                }

                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection crate = configuration.getConfigurationSection("Crate");

                if (crate == null) {
                    sender.sendRichMessage(Messages.error_migrating.getMessage(sender, new HashMap<>() {{
                        put("{file}", name);
                        put("{type}", type.getName());
                        put("{reason}", "File could not be found in our data, likely invalid yml file that didn't load properly.");
                    }}));

                    return;
                }

                set(crate, "Item", crate.getString("Item", "diamond").toLowerCase());
                set(crate, "Preview.Glass.Item", crate.getString("Preview.Glass.Item", "gray_stained_glass_pane").toLowerCase());
                set(crate, "PhysicalKey.Item", crate.getString("PhysicalKey.Item", "lime_dye").toLowerCase());

                final ConfigurationSection prizes = configuration.getConfigurationSection("Prizes");

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

                        if (prize.contains("DisplayEnchantments")) {
                            List<String> enchants = new ArrayList<>() {{
                                prize.getStringList("DisplayEnchantments").forEach(enchant -> add(ItemUtils.getEnchant(enchant)));
                            }};

                            set(prize, "DisplayEnchantments", enchants);
                        }

                        if (prize.contains("Items")) {
                            set(prize, "Items", getItems(prize.getStringList("Items")));
                        }

                        if (prize.contains("Alternative-Prize.Items")) {
                            set(prize, "Items", getItems(prize.getStringList("Alternative-Prize.Items")));
                        }
                    });
                }

                customFile.save();

                sender.sendRichMessage(Messages.successfully_migrated.getMessage(sender, new HashMap<>() {{
                    put("{file}", name);
                    put("{type}", type.getName());
                }}));
            });

            case EXCELLENT_CRATES -> {
                if (!MiscUtils.isExcellentCratesEnabled()) {
                    sender.sendRichMessage(Messages.migration_plugin_not_enabled.getMessage(sender, "{name}", type.getName()));

                    return;
                }

                File dir = new File(this.plugin.getDataFolder(), "crates");

                if (!dir.exists()) dir.mkdirs();

                CratesAPI.getCrateManager().getCrates().forEach(crate -> {
                    final String name = crate.getName();

                    File crateFile = new File(dir, name);

                    if (!crateFile.exists()) {
                        try {
                            crateFile.createNewFile();
                        } catch (IOException e) {
                            this.plugin.getLogger().severe("Failed to create " + name + ".yml");
                        }
                    }

                    this.fileManager.addCustomFile(new CustomFile(dir).apply(name));

                    @Nullable CustomFile customFile = this.fileManager.getCustomFile(name);

                    if (customFile != null) {
                        final YamlConfiguration configuration = customFile.getConfiguration();

                        configuration.set("Crates.CrateType", "CSGO");

                        crate.getRewards().forEach(reward -> {
                            final String previewItem = ItemUtil.toBase64(reward.getPreview());

                            final ConfigurationSection section = configuration.getConfigurationSection("Crates");

                            if (section == null) return;

                            section.set("Prizes." + reward.getId() + ".DisplayData", previewItem);

                            ConfigurationSection prize = section.getConfigurationSection(reward.getId());

                            reward.getItems().forEach(itemStack -> {
                                final String toBase64 = ItemUtil.toBase64(itemStack);

                                if (prize != null) {
                                    if (prize.contains("Items")) {
                                        final List<String> list = prize.getStringList("Items");

                                        list.add("Data: " + toBase64);

                                        prize.set("Items", list);
                                    } else {
                                        prize.set("Items", new ArrayList<>() {{
                                            add("Data: " + toBase64);
                                        }});
                                    }
                                }
                            });
                        });

                        customFile.save();
                    }
                });
            }

            case SPECIALIZED_CRATES -> sender.sendRichMessage(Messages.migration_not_available.getMessage());
        }
    }

    private <T> void set(ConfigurationSection section, String path, T value) {
        section.set(path, value);
    }

    private List<String> getItems(List<String> items) {
        return new ArrayList<>() {{
            items.forEach(line -> {
                for (String option : line.split(", ")) {
                    String key = option.split(":")[0];
                    String value = option.replace(option + ":", "").replace(option, "");

                    switch (key.toLowerCase()) {
                        case "item", "trim-material", "trim-pattern" -> option = option.replace(value, value.toLowerCase());
                    }

                    add(ItemUtils.getEnchant(option));
                }
            });
        }};
    }
}