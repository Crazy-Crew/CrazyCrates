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

public class CommandRespinRemove extends BaseCommand {

    @Command("respin-remove")
    @Permission(value = "crazycrates.respin.remove", def = PermissionDefault.OP, description = "Allows the sender to remove a respin from another person.")
    public void remove(final CommandSender sender, @Suggestion("players") final Player target, @Suggestion("crates") final String crateName, @Suggestion("numbers") final int amount) {
        if (crateName == null || crateName.isEmpty() || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        if (amount <= 0) {
            Messages.not_a_number.sendMessage(sender, "{number}", String.valueOf(amount));

            return;
        }

        final Crate crate = getCrate(sender, crateName, false);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        this.userManager.removeRespinCrate(target.getUniqueId(), crate.getFileName(), amount);

        //todo() send message that they added a prize.
    }
}