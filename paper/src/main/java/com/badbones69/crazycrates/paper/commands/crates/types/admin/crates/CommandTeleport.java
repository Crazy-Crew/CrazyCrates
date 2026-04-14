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

    @Command("teleport")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.OP)
    @Syntax("/crazycrates teleport <crate_id>")
    public void teleport(Player player, @ArgName("crate_id") @Optional @Suggestion("locations") String id) {
        if (id == null || id.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate location id");

            return;
        }

        final YamlConfiguration configuration = FileKeys.locations.getConfiguration();

        final ConfigurationSection section = configuration.getConfigurationSection("Locations");

        if (section == null) {
            configuration.set("Locations.Clear", null);

            FileKeys.locations.save();

            return;
        }

        for (final String name : section.getKeys(false)) {
            if (!name.equalsIgnoreCase(id)) continue;

            final ConfigurationSection origin = section.getConfigurationSection(name);

            if (origin == null) continue;

            final World world = this.server.getWorld(origin.getString("World", ""));

            if (world == null) continue;

            player.teleport(new Location(world, origin.getInt("Z"), origin.getInt("Y"), origin.getInt("Z")).add(.5, 0, .5));

            Messages.crate_teleported.sendMessage(player, "{name}", name);
        }

        Messages.crate_cannot_teleport.sendMessage(player, "{id}", id);
    }
}