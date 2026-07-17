package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.List;

public class CommandFix extends BaseCommand {

    @Command("fix")
    @Permission(value = "crazycrates.fix", def = PermissionDefault.OP)
    @Syntax("/crazycrates fix")
    public void fix(final CommandSender sender) {
        final List<CrazyLocation> locations = this.crateManager.getBrokenLocations();

        int amount = 0;

        for (final CrazyLocation index : locations) {
            final World world = this.server.getWorld(index.getWorldName());
            final String id = index.getId();

            if (world == null) {
                this.crateManager.removeBrokenCrateLocation(index);

                this.storageHolder.removeCrateLocation(id);

                continue;
            }

            final Crate crate = this.crateManager.getCrateFromName(index.getCrateName());

            if (crate == null) {
                this.crateManager.removeBrokenCrateLocation(index);

                this.storageHolder.removeCrateLocation(id);

                continue;
            }

            final CrateLocation location = new CrateLocation(id, crate, new Location(
                    world,
                    index.getX(),
                    index.getY(),
                    index.getZ()
            ));

            this.crateManager.addLocation(location);

            final HologramManager hologramManager = this.crateManager.getHolograms();

            if (hologramManager != null && crate.getHologram().isEnabled()) {
                hologramManager.createHologram(location.getLocation(), crate, id);
            }

            this.crateManager.removeBrokenCrateLocation(index);

            amount++;
        }

        if (this.fusion.isVerbose()) {
            this.logger.warn("Fixed {} broken crate locations.", amount);

            if (this.crateManager.getBrokenLocations().isEmpty()) {
                this.logger.warn("All broken crate locations have been fixed.");
            }
        }
    }
}