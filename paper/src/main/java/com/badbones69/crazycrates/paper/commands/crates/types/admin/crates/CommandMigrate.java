package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation.NewItemMigrator;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.MojangMappedMigratorMultiple;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.MojangMappedMigratorSingle;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation.DeprecatedCrateMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation.LegacyColorMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.deprecation.WeightMigrator;
import com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.migrator.types.plugins.ExcellentCratesMigrator;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandMigrate extends BaseCommand {

    @Command("migrate")
    @Permission(value = "crazycrates.migrate", def = PermissionDefault.OP)
    @Flag(flag = "mt", longFlag = "migration_type", argument = String.class, suggestion = "migrators")
    @Flag(flag = "c", longFlag = "crate", argument = String.class, suggestion = "crates")
    @Flag(flag = "d", longFlag = "data")
    @Syntax("/crazycrates migrate -mt <migration_type> [-c/--crate] <crate_name> [-d/--data]")
    public void migrate(final CommandSender sender, Flags flags) {
        final boolean hasFlag = flags.hasFlag("mt");

        if (!hasFlag) {
            Messages.lacking_flag.sendMessage(sender, new HashMap<>() {{
                put("{flag}", "-mt");
                put("{usage}", "/crazycrates migrate -mt <migration_type>");
            }});

            return;
        }

        final MigrationType type = MigrationType.fromName(flags.getFlagValue("mt").orElse(null));

        if (type == null) {
            Messages.migration_not_available.sendMessage(sender);

            return;
        }

        switch (type) {
            case MOJANG_MAPPED_ALL -> new MojangMappedMigratorMultiple(sender).run();
            case MOJANG_MAPPED_SINGLE -> {
                final boolean hasCrateFlag = flags.hasFlag("c");

                if (!hasCrateFlag) {
                    Messages.lacking_flag.sendMessage(sender, new HashMap<>() {{
                        put("{flag}", "-c");
                        put("{usage}", "/crazycrates migrate -mt <migration_type> -c <crate>");
                    }});

                    return;
                }

                final String crateName = flags.getFlagValue("c").orElse(null);

                if (crateName == null || crateName.isBlank() || crateName.equalsIgnoreCase("Menu")) {
                    Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

                    return;
                }

                new MojangMappedMigratorSingle(sender, crateName).run();
            }

            case NEW_ITEM_FORMAT -> new NewItemMigrator(sender).run();

            case WEIGHT_MIGRATION -> new WeightMigrator(sender).run();

            case LEGACY_COLOR_ALL -> new LegacyColorMigrator(sender).run();

            case CRATES_DEPRECATED_ALL -> new DeprecatedCrateMigrator(sender).run();

            case SPECIALIZED_CRATES -> sender.sendRichMessage(Messages.migration_not_available.getMessage(sender));

            case EXCELLENT_CRATES -> {
                if (!MiscUtils.isExcellentCratesEnabled()) {
                    Messages.migration_plugin_not_enabled.sendMessage(sender, "{name}", type.getName());

                    return;
                }

                new ExcellentCratesMigrator(sender, flags.hasFlag("d")).run();
            }
        }
    }
}