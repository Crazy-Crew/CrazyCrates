package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

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

        this.storageHolder.getCrateLocation(id).ifPresentOrElse(index -> {
            final World world = this.server.getWorld(index.getWorldName());

            if (world == null) {
                Message.crate_teleport_failed.sendMessage(player, "{id}", id);

                return;
            }

            final Location location = new Location(
                    world,
                    index.getX(),
                    index.getY(),
                    index.getZ()
            );

            if (player.teleport(location.add(.5, 1, .5))) {
                Message.crate_teleport_success.sendMessage(player, "{name}", index.getId());
            }
        }, () -> Message.crate_teleport_failed.sendMessage(player, "{id}", id));
    }
}