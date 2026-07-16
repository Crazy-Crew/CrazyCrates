package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandTeleport extends BaseCommand {

    @Command("teleport")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.OP)
    @Syntax("/crazycrates teleport <crate_id>")
    public void teleport(final Player player, @ArgName("crate_id") @Optional @Suggestion("locations") final String id) {
        if (id == null || id.isBlank()) {
            Message.cannot_be_empty.sendMessage(player, "{value}", "crate location id");

            return;
        }

        final CommentedConfigurationNode locations = Files.locations.getConfiguration();

        final CommentedConfigurationNode section = locations.node("Locations");

        boolean isFailed = true;

        for (final Object object : section.childrenMap().keySet()) {
            final String asString = object.toString();

            if (!asString.equalsIgnoreCase(id)) continue;

            final CommentedConfigurationNode origin = section.node(asString);

            if (origin == null) continue;

            final World world = this.server.getWorld(origin.node("World").getString(""));

            if (world == null) continue;

            if (!origin.hasChild("X") || !origin.hasChild("Y") || !origin.hasChild("Z")) continue;

            if (player.teleport(new Location(world, origin.node("X").getInt(), origin.node("Y").getInt(), origin.node("Z").getInt()).add(.5, 0, .5))) {
                Message.crate_teleport_success.sendMessage(player, "{name}", asString);

                isFailed = false;
            }
        }

        if (isFailed) {
            Message.crate_teleport_failed.sendMessage(player, "{id}", id);
        }
    }
}