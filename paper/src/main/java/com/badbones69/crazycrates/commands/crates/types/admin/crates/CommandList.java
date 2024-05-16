package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.common.util.StringUtil;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandList extends BaseCommand {

    @Command("list")
    @Permission(value = "crazycrates.list", def = PermissionDefault.OP)
    public void list(CommandSender sender) {
        final List<String> crates = new ArrayList<>();

        for (final CrateLocation crateLocation : this.crateManager.getCrateLocations()) {
            final Crate crate = crateLocation.getCrate();

            Location location = crateLocation.getLocation();

            final String world = location.getWorld().getName();
            final int x = location.getBlockX();
            final int y = location.getBlockY();
            final int z = location.getBlockZ();

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{crate_name}", crate.getName());
            placeholders.put("{id}", crateLocation.getID());
            placeholders.put("{x}", String.valueOf(x));
            placeholders.put("{y}", String.valueOf(y));
            placeholders.put("{z}", String.valueOf(z));
            placeholders.put("{world}", world);

            crates.add(Messages.crate_locations_format.getMessage(placeholders));
        }

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{active_crates}", String.valueOf(this.crateManager.getUsableCrates().size()));
        placeholders.put("{broken_crates}", String.valueOf(this.crateManager.getBrokeCrates().size()));
        placeholders.put("{active_locations}", String.valueOf(this.crateManager.getCrateLocations().size()));

        placeholders.put("{locations}", this.crateManager.getCrateLocations().isEmpty() ? "N/A" : StringUtils.chomp(StringUtil.convertList(crates)));

        sender.sendRichMessage(Messages.crate_locations.getMessage(sender, placeholders));
    }
}