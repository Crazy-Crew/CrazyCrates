package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandTeleport extends CommandManager {

    @SubCommand("tp")
    @Permission(value = "crazycrates.command.admin.teleport", def = PermissionDefault.OP)
    public void onAdminTeleport(Player player, @Suggestion("locations") String id) {

        if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
            FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
            FileManager.Files.LOCATIONS.saveFile();
        }

        for (String name : FileManager.Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                World W = plugin.getServer().getWorld(FileManager.Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));

                int X = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
                int Y = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
                int Z = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");

                Location loc = new Location(W, X, Y, Z);

                player.teleport(loc.add(.5, 0, .5));
                player.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));

                return;
            }
        }

        player.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + id + "&c."));
    }
}