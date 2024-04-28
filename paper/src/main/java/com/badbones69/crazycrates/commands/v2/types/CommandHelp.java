package com.badbones69.crazycrates.commands.v2.types;

import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission("crazycrates.gui")
    public void gui(Player player) {
        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            player.openInventory(new CrateMainMenu(
                    player,
                    this.config.getProperty(ConfigKeys.inventory_size),
                    this.config.getProperty(ConfigKeys.inventory_name)
            ).build().getInventory());

            return;
        }

        help(player);
    }

    @Command("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    public void help(CommandSender sender) {
        String message = sender.hasPermission("crazycrates.admin") ? Messages.admin_help.getMessage(sender) : Messages.help.getMessage(sender);

        sender.sendMessage(message);
    }
}