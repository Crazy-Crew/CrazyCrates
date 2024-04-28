package com.badbones69.crazycrates.commands.v2.types.admin.crates;

import com.badbones69.crazycrates.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandAddItem extends BaseCommand {

    @Command("additem")
    @Permission(value = "crazycrates.additem", def = PermissionDefault.OP)
    public void add(Player player, @Suggestion("crates") String crateName) {
        //todo() turn this into a gui.
    }
}