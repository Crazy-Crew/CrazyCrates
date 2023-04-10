package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdminHelp extends CommandManager {

    @SubCommand("admin-help")
    @Permission(value = "crazycrates.command.admin.help", def = PermissionDefault.OP)
    public void onAdminHelp(CommandSender sender) {
        sender.sendMessage(Messages.ADMIN_HELP.getMessage());
    }
}