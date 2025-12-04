package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates.editor;

import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.Map;

public class CommandSet extends BaseCommand {

    @Command("set")
    @Permission(value = "crazycrates.set", def = PermissionDefault.OP)
    @Syntax("/crazycrates set <crate_name>")
    public void set(Player player, @ArgName("crate") @Suggestion("crates") String crateName) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            Messages.cannot_set_type.sendMessage(player);

            return;
        }

        final Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            Messages.must_be_looking_at_block.sendMessage(player);

            return;
        }

        final Location location = block.getLocation();

        if (this.crateManager.isCrateLocation(location)) {
            final CrateLocation crateLocation = crateManager.getCrateLocation(location);

            Messages.physical_crate_already_exists.sendMessage(player, Map.of(
                    "{id}", crateLocation != null ? crateLocation.getID() : "N/A",
                    "{crate}", crateLocation != null ? crateLocation.getCrate().getCrateName() : "N/A"
            ));

            return;
        }

        this.crateManager.addCrateLocation(location, crate);

        // this has to use sendRichMessage as it is a list.
        Messages.created_physical_crate.sendRichMessage(player, Map.of(
                "{crate}", crate.getCrateName()
        ));
    }
}