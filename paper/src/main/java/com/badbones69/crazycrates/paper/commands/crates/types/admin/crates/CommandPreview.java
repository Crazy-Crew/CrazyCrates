package com.badbones69.crazycrates.paper.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandPreview extends BaseCommand {

    @Command("preview")
    @Permission(value = "crazycrates.preview", def = PermissionDefault.OP)
    public void preview(Player player, @ArgName("crate") @Suggestion("crates") String crateName) {
        others(player, crateName, player);
    }

    @Command("preview-others")
    @Permission(value = "crazycrates.preview.others", def = PermissionDefault.OP)
    public void others(CommandSender sender, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("player") @Suggestion("players") Player player) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

            return;
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final String fancyName = crate.getCrateName();

        if (!crate.isPreviewEnabled()) {
            Messages.preview_disabled.sendMessage(sender, "{crate}", fancyName);

            return;
        }

        this.inventoryManager.openNewCratePreview(player, crate);
    }
}