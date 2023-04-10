package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandDebug extends CommandManager {

    @SubCommand("debug")
    @Permission(value = "crazycrates.command.admin.debug", def = PermissionDefault.OP)
    public void onDebug(CommandSender sender, @Suggestion("crates") String crateName) {
        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            crate.getPrizes().forEach(prize -> crazyManager.givePrize((Player) sender, prize, crate));
        } else {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
        }
    }
}