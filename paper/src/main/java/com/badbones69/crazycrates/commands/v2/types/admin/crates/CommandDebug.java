package com.badbones69.crazycrates.commands.v2.types.admin.crates;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandDebug extends BaseCommand {

    @Command("debug")
    @Permission(value = "crazycrates.debug", def = PermissionDefault.OP)
    public void debug(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }


        crate.getPrizes().forEach(prize -> PrizeManager.givePrize(player, crate, prize));
    }
}