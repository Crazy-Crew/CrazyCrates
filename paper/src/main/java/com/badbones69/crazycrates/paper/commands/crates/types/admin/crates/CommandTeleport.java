package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Objects;

public class CommandTeleport extends BaseCommand {

    private final YamlConfiguration locations = FileKeys.locations.getConfiguration();

    @Command("teleport")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.OP)
    @Syntax("/crazycrates teleport <crate_id>")
    public void teleport(Player player, @ArgName("crate_id") @Optional @Suggestion("locations") String id) {
        if (id == null || id.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate location id");

            return;
        }

       final ConfigurationSection section = this.locations.getConfigurationSection("Locations");

        if (section == null) {
            this.locations.set("Locations.Clear", null);

            FileKeys.locations.save();

            return;
        }

        for (final String name : section.getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                final World world = this.server.getWorld(Objects.requireNonNull(this.locations.getString("Locations." + name + ".World")));

                final int x = this.locations.getInt("Locations." + name + ".X");
                final int y = this.locations.getInt("Locations." + name + ".Y");
                final int z = this.locations.getInt("Locations." + name + ".Z");

                final Location loc = new Location(world, x, y, z);

                player.teleport(loc.add(.5, 0, .5));

                Messages.crate_teleported.sendMessage(player, "{name}", name);

                return;
            }
        }

        Messages.crate_cannot_teleport.sendMessage(player, "{id}", id);
    }
}