package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandDebug extends BaseCommand {

    @Command("debug")
    @Permission(value = "crazycrates.debug", def = PermissionDefault.OP)
    public void debug(Player player, @Suggestion("crates") String crateName) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate name"));

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        crate.getPrizes().forEach(prize -> PrizeManager.givePrize(player, crate, prize));
    }
}