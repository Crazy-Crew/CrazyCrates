package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import java.util.HashMap;
import java.util.Map;

public class CommandSet extends BaseCommand {

    @Command("set")
    @Permission(value = "crazycrates.set", def = PermissionDefault.OP)
    public void set(Player player, @Suggestion("crates") String crateName) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate name"));

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            player.sendRichMessage(Messages.cannot_set_type.getMessage(player));

            return;
        }

        final Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            player.sendRichMessage(Messages.must_be_looking_at_block.getMessage(player));

            return;
        }

        if (this.crateManager.isCrateLocation(block.getLocation())) {
            player.sendRichMessage(Messages.physical_crate_already_exists.getMessage(player, new HashMap<>() {{
                put("{crate}", crate.getName());

                final CrateLocation crateLocation = crateManager.getCrateLocation(block.getLocation());

                put("{id}", crateLocation != null ? crateLocation.getID() : "N/A");
            }}));

            return;
        }

        this.crateManager.addCrateLocation(block.getLocation(), crate);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{prefix}", MsgUtils.getPrefix());

        player.sendRichMessage(Messages.created_physical_crate.getMessage(player, placeholders));
    }
}