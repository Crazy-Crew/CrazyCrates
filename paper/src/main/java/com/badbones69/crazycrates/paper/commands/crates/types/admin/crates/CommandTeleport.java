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

        String value = "";

        for (final String name : section.getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                value = name;

                break;
            }
        }

        Location validLocation = null;

        if (!value.isBlank()) {
            final ConfigurationSection location = this.locations.getConfigurationSection("Locations." + value);

            if (location != null) {
                final String worldName = location.getString("World", "");

                if (!worldName.isBlank()) {
                    final World world = this.server.getWorld(worldName);

                    if (world != null) {
                        final int x = location.getInt("X");
                        final int y = location.getInt("Y");
                        final int z = location.getInt("Z");

                        validLocation = new Location(world, x, y, z);
                    }
                }
            }
        }

        if (validLocation == null) {
            Messages.crate_cannot_teleport.sendMessage(player, "{id}", id);

            return;
        }

        player.teleport(validLocation.add(0.5, 0, 0.5));

        Messages.crate_teleported.sendMessage(player, "{name}", value);
    }
}