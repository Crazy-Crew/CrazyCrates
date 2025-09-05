package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.editor;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;

public class CommandEditor extends BaseCommand {

    @Command("editor")
    @Flag(flag = "c", longFlag = "crate", argument = String.class, suggestion = "crates")
    @Flag(flag = "e", longFlag = "exit")
    @Permission(value = "crazycrates.editor", def = PermissionDefault.OP)
    @Syntax("/crazycrates editor [-c/--crate] [-e/--exit]")
    public void editor(final Player player, final Flags flags) {
        if (flags.hasFlag("e")) {
            this.crateManager.removeEditorCrate(player);

            Messages.editor_exit.sendMessage(player, "{reason}", "you asked.");

            return;
        }

        if (!flags.hasFlag("c")) {
            Messages.lacking_flag.sendMessage(player, new HashMap<>() {{
                put("{flag}", "-c");
                put("{usage}", "/crazycrates editor -c <crate_name>");
            }});

            return;
        }

        final String crateName = flags.getFlagValue("c").orElse("");

        if (crateName.isEmpty()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

            return;
        }

        if (this.crateManager.hasEditorCrate(player)) {
            Messages.editor_already_in.sendMessage(player);

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            Messages.cannot_set_type.sendMessage(player);

            return;
        }

        this.crateManager.addEditorCrate(player, crate);

        Messages.editor_enter.sendMessage(player, "{crate}", crateName);
    }
}