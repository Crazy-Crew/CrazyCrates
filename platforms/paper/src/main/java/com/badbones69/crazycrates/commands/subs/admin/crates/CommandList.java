package com.badbones69.crazycrates.commands.subs.admin.crates;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandList extends CommandManager {

    @SubCommand("list")
    @Permission(value = "crazycrates.command.admin.list", def = PermissionDefault.OP)
    public void onAdminList(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokeCrates;

        crazyManager.getCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokeCratesBuilder = new StringBuilder();

        crazyManager.getBrokeCrates().forEach(crate -> brokeCratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokeCrates = brokeCratesBuilder.toString();

        sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));

        if (brokeCrates.length() > 0) sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokeCrates.substring(0, brokeCrates.length() - 2)));

        sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
        sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;

        for (CrateLocation loc : crazyManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
            line++;
        }
    }
}