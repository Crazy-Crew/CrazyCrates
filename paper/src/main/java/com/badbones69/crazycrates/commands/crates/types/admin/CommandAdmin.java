package com.badbones69.crazycrates.commands.crates.types.admin;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdmin extends BaseCommand {

    @Command("admin")
    @Permission(value = "crazycrates.admin", def = PermissionDefault.OP)
    public void admin(final Player player) {
        new CrateAdminMenu(player).open();
    }
}