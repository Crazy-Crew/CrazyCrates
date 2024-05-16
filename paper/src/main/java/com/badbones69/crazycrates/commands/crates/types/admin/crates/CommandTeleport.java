package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.DataFiles;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import java.util.Objects;

public class CommandTeleport extends BaseCommand {

    private @NotNull final YamlFile locations = DataFiles.locations.getYamlFile();

    @Command("teleport")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.OP)
    public void teleport(Player player, @Suggestion("locations") String id) {
        if (id.isEmpty() || id.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate location id"));

            return;
        }

       final ConfigurationSection section = this.locations.getConfigurationSection("Locations");

        if (section == null) {
            this.locations.set("Locations.Clear", null);

            DataFiles.locations.save();

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

                player.sendRichMessage(MsgUtils.getPrefix("<gray>You have been teleported to <gold>" + name + "."));

                return;
            }
        }

        player.sendRichMessage(MsgUtils.getPrefix("<red>There is no location called <gold>" + id + "."));
    }
}