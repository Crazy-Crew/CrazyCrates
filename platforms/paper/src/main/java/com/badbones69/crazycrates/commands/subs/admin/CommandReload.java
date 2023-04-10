package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends CommandManager {

    @SubCommand("reload")
    @Permission(value = "crazycrates.command.admin.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        fileManager.reloadAllFiles();
        fileManager.setup();

        plugin.cleanFiles();
        crazyManager.loadCrates();

        sender.sendMessage(Messages.RELOAD.getMessage());
    }
}
