package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandList extends BaseCommand {

    @Command("list")
    @Permission(value = "crazycrates.list", def = PermissionDefault.OP)
    @Syntax("/crazycrates list")
    public void list(CommandSender sender) {
        final List<String> crates = new ArrayList<>();

        for (final CrateLocation crateLocation : this.crateManager.getCrateLocations()) {
            final Location location = crateLocation.getLocation();
            final Crate crate = crateLocation.getCrate();

            crates.add(Messages.crate_locations_format.getMessage(sender, new HashMap<>() {{
                put("{crate_name}", crate.getCrateName());
                put("{id}", crateLocation.getID());
                put("{x}", String.valueOf(location.getBlockX()));
                put("{y}", String.valueOf(location.getBlockY()));
                put("{z}", String.valueOf(location.getBlockZ()));
                put("{world}", location.getWorld().getName());
            }}));
        }

        // this has to use sendRichMessage as it is a list.
        Messages.crate_locations.sendRichMessage(sender, new HashMap<>() {{
            put("{active_crates}", String.valueOf(crateManager.getUsableCrates().size()));
            put("{broken_crates}", String.valueOf(crateManager.getBrokeCrates().size()));
            put("{active_locations}", String.valueOf(crateManager.getCrateLocations().size()));
            put("{locations}", crateManager.getCrateLocations().isEmpty() ? "N/A" : utils.toString(crates));
        }});
    }
}