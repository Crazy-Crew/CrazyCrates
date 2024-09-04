package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Objects;

public class CommandTeleport extends BaseCommand {

    private @NotNull final YamlConfiguration locations = Files.locations.getConfiguration();

    @Command("teleport")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.OP)
    public void teleport(Player player, @ArgName("crate_id") @Optional @Suggestion("locations") String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate location id");

            return;
        }

       final ConfigurationSection section = this.locations.getConfigurationSection("Locations");

        if (section == null) {
            this.locations.set("Locations.Clear", null);

            Files.locations.save();

            return;
        }

        for (final String name : section.getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                final World world = this.plugin.getServer().getWorld(Objects.requireNonNull(this.locations.getString("Locations." + name + ".World")));

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