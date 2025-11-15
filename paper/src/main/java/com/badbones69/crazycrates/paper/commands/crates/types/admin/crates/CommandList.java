package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.ryderbelserion.fusion.core.api.utils.StringUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandList extends BaseCommand {

    @Command("list")
    @Permission(value = "crazycrates.list", def = PermissionDefault.OP)
    @Syntax("/crazycrates list")
    public void list(CommandSender sender) {
        final List<String> crates = new ArrayList<>();

        for (final CrateLocation crateLocation : this.crateManager.getCrateLocations()) {
            final Location location = crateLocation.getLocation();
            final Crate crate = crateLocation.getCrate();

            crates.add(Messages.crate_locations_format.getMessage(sender, Map.of(
                    "{crate_name}", crate.getCrateName(),
                    "{id}", crateLocation.getID(),
                    "{x}", String.valueOf(location.getBlockX()),
                    "{y}", String.valueOf(location.getBlockY()),
                    "{z}", String.valueOf(location.getBlockZ()),
                    "{world}", location.getWorld().getName()
            )));
        }

        final List<String> brokenCrates = crateManager.getBrokeCrates();
        final List<Crate> validCrates = crateManager.getUsableCrates();
        final List<CrateLocation> locations = crateManager.getCrateLocations();

        // this has to use sendRichMessage as it is a list.
        Messages.crate_locations.sendRichMessage(sender, Map.of(
                "{active_crates}", String.valueOf(validCrates.size()),
                "{broken_crates}", String.valueOf(brokenCrates.size()),
                "{active_locations}", String.valueOf(locations.size()),
                "{locations}", locations.isEmpty() ? "N/A" : StringUtils.toString(crates)
        ));
    }
}