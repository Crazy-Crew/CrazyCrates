package com.badbones69.crazycrates.commands.v2.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CommandPreview extends BaseCommand {

    @Command("preview")
    @Permission(value = "crazycrates.preview", def = PermissionDefault.OP)
    public void onPreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendRichMessage(Messages.not_a_crate.getMessage(sender, "{crate}", crateName));

            return;
        }

        if (!crate.isPreviewEnabled()) {
            sender.sendRichMessage(Messages.preview_disabled.getMessage(sender, "{crate}", crate.getName()));

            return;
        }

        this.inventoryManager.addViewer(player);
        this.inventoryManager.openNewCratePreview(player, crate);
    }
}