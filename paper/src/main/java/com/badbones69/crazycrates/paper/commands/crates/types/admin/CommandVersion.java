package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.spongepowered.configurate.BasicConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;
import java.util.List;
import java.util.Map;

public class CommandVersion extends BaseCommand {

    @Command("version")
    @Permission(value = "crazycrates.version", def = PermissionDefault.OP)
    @Syntax("/crazycrates version")
    public void version(final CommandSender sender) {
        final BasicConfigurationNode configuration = Files.version.getConfigurationNode();

        final String currentCommit = configuration.node("git", "current").getString("N/A");
        final String previousCommit = configuration.node("git", "previous").getString("");
        final String version = configuration.node("version").getString("N/A");

        final Map<String, String> placeholders = Map.of(
                "{version}", version,
                "{current_commit}", currentCommit,
                "{previous_commit}", previousCommit
        );

        sender.sendMessage(
                this.fusion.asComponent(StringUtils.toString(List.of(
                        "<bold><gold>━━━━━━━━━━━━━━━━━━━ Plugin Version ━━━━━━━━━━━━━━━━━━━</gold></bold>",
                        " <red>⤷ <gold>Version: <red>{version}",
                        "",
                        " ⤷ <gold>Current Commit: <red>{current_commit}",
                        " ⤷ <gold>Previous Commit: <red>{previous_commit}",
                        "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
                )), placeholders)
        );
    }
}