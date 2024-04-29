package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandList extends BaseCommand {

    @Command("list")
    @Permission(value = "crazycrates.list", def = PermissionDefault.OP)
    public void list(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokeCrates;

        this.crateManager.getUsableCrates().forEach(crate -> crates.append("<green>").append(crate.getName()).append("<dark_gray>, "));

        StringBuilder brokeCratesBuilder = new StringBuilder();

        this.crateManager.getBrokeCrates().forEach(crate -> brokeCratesBuilder.append("<red>").append(crate).append(".yml<dark_gray>,"));

        brokeCrates = brokeCratesBuilder.toString();

        sender.sendRichMessage("<bold><yellow>Crates:</bold><white> " + crates);

        if (!brokeCrates.isEmpty()) sender.sendRichMessage("<bold><gold>Broken Crates:</bold><white> " + brokeCrates.substring(0, brokeCrates.length() - 2));

        sender.sendRichMessage("<bold><yellow>All Crate Locations:</bold>");
        sender.sendRichMessage("<red>[ID]<dark_gray>, <red>[Crate]<dark_gray>, <red>[World]<dark_gray>, <red>[X]<dark_gray>, <red>[Y]<dark_gray>, <red>[Z]");
        int line = 1;

        for (CrateLocation loc : this.crateManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendRichMessage("<dark_gray>[<blue>" + line + "<dark_gray>]: " + "<red>" + loc.getID() + "<dark_gray>, <red>" + crate.getName() + "<dark_gray>, <red>" + world + "<dark_gray>, <red>" + x + "<dark_gray>, <red>" + y + "<dark_gray>, <red>" + z);

            line++;
        }
    }
}