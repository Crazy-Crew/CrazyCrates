package com.badbones69.crazycrates.commands.subs.player;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.commands.CrateBaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;

public class CrateHelpCommand extends CrateBaseCommand {

    @SubCommand("help")
    @Permission("crazycrates.command.player.help")
    public void help(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }

    @SubCommand("ahelp")
    @Permission("crazycrates.command.admin.help")
    public void adminHelp(CommandSender sender) {
        sender.sendMessage(Messages.ADMIN_HELP.getMessage());
    }
}