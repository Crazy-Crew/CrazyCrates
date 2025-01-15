package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandDebug extends BaseCommand {

    @Command("debug")
    @Permission(value = "crazycrates.debug", def = PermissionDefault.OP)
    public void debug(CommandSender sender, @ArgName("crate") @Suggestion("crates") String crateName, @Optional @Suggestion("players") Player target) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        if (sender instanceof Player player && target == null) {
            crate.getPrizes().forEach(prize -> PrizeManager.givePrize(player, crate, prize));

            return;
        }

        crate.getPrizes().forEach(prize -> PrizeManager.givePrize(target, crate, prize));
    }
}