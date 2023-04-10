package com.badbones69.crazycrates.commands.subs.admin.crates;

import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.types.CrateType;
import com.badbones69.crazycrates.listeners.PreviewListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandPreview extends CommandManager {

    @SubCommand("preview")
    @Permission(value = "crazycrates.command.admin.preview", def = PermissionDefault.OP)
    public void onAdminCratePreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {

        Crate crate = null;

        for (Crate crates : crazyManager.getCrates()) {
            if (crates.getCrateType() != CrateType.MENU) {
                if (crates.getName().equalsIgnoreCase(crateName)) crate = crates;
            }
        }

        if (crate != null) {
            if (!crate.isPreviewEnabled()) {
                sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                return;
            }

            if (crate.getCrateType() != CrateType.MENU) {
                PreviewListener.setPlayerInMenu(player, false);
                PreviewListener.openNewPreview(player, crate);
            }
        }
    }
}