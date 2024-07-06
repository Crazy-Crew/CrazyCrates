package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandMigrate extends BaseCommand {

    public enum MigrationType {
        MOJANG_MAPPED,
        SPECIALIZED_CRATES
    }

    @Command("migrate")
    @Permission(value = "crazycrates.migrate", def = PermissionDefault.OP)
    public void migrate(final CommandSender sender, @Suggestion("crates") final String crateName, final MigrationType type) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            sender.sendRichMessage(Messages.cannot_be_empty.getMessage(sender, "{value}", "crate name"));

            return;
        }

        final CustomFile file = this.fileManager.getCustomFile(crateName);

        if (file == null) {
            sender.sendRichMessage(Messages.error_migrating.getMessage(sender, new HashMap<>() {{
                put("{file}", crateName);
                put("{type}", String.valueOf(type));
                put("{reason}", "File was not loaded properly.");
            }}));

            return;
        }

        switch (type) {
            case MOJANG_MAPPED -> {
                final YamlConfiguration configuration = file.getConfiguration();

                final ConfigurationSection crate = configuration.getConfigurationSection("Crate");

                if (crate == null) {
                    sender.sendRichMessage(Messages.error_migrating.getMessage(sender, new HashMap<>() {{
                        put("{file}", crateName);
                        put("{type}", String.valueOf(type));
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

                file.save();
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