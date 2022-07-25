package com.badbones69.crazycrates.commands.subs.admin;


import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.commands.CrateBaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;

public class CrateReloadCommand extends CrateBaseCommand {

    private final FileManager fileManager = FileManager.getInstance();

    @SubCommand("reload")
    @Permission("crazycrates.command.admin.reload")
    public void reload(CommandSender sender) {
        fileManager.reloadAllFiles();

        // sender.sendMessage(Messages.RELOAD.getMessage());
    }
}