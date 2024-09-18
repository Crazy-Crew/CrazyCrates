package com.badbones69.crazycrates.commands.crates.types.player;

import com.badbones69.crazycrates.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import com.badbones69.crazycrates.managers.config.impl.ConfigKeys;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission("crazycrates.gui")
    public void gui(Player player) {
        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            new CrateMainMenu(
                    player,
                    this.config.getProperty(ConfigKeys.inventory_name),
                    this.config.getProperty(ConfigKeys.inventory_rows)
            ).open();

            return;
        }

        help(player);
    }

    @Command("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    public void help(CommandSender sender) {
        if (sender.hasPermission("crazycrates.admin")) {
            // this has to use sendRichMessage as it is a list.
            Messages.admin_help.sendRichMessage(sender);

            return;
        }

        // this has to use sendRichMessage as it is a list.
        Messages.help.sendRichMessage(sender);
    }
}