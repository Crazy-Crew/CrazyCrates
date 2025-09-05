package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import com.badbones69.crazycrates.paper.tasks.menus.CrateAdminMenu;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdmin extends BaseCommand {

    @Command("admin")
    @Permission(value = "crazycrates.admin", def = PermissionDefault.OP)
    @Syntax("/crazycrates admin")
    public void admin(final Player player) {
        new CrateAdminMenu(player).open();
    }
}