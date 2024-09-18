package com.badbones69.crazycrates.commands.crates.types.admin.keys.respin;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandRespinDeny extends BaseCommand {

    @Command("respin-deny")
    @Permission(value = "crazycrates.respin.deny", def = PermissionDefault.OP, description = "Allows the sender to deny the respin for another person.")
    public void deny(final CommandSender sender, @Suggestion("players") final Player target, @Suggestion("crates") final String crateName) {
        if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        if (!this.userManager.hasRespinPrize(target.getUniqueId(), crateName)) {
            //todo() they don't have a prize, so no accept.

            return;
        }

        this.userManager.removeRespinPrize(target.getUniqueId(), crate.getFileName());
    }
}