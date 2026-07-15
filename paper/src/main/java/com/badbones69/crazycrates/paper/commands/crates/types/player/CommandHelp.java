package com.badbones69.crazycrates.paper.commands.crates.types.player;

import us.crazycrew.crazycrates.api.config.impl.types.config.gui.GuiKeys;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission("crazycrates.gui")
    @Syntax("/crazycrates")
    public void gui(Player player) {
        if (this.pluginConfig.getProperty(GuiKeys.is_crate_menu_enabled)) {
            new CrateMainMenu(
                    player,
                    this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_name),
                    this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_rows)
            ).open();

            return;
        }

        help(player);
    }

    @Command("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    @Syntax("/crazycrates help")
    public void help(CommandSender sender) {
        if (sender.hasPermission("crazycrates.admin")) {
            Message.command_admin_help.sendMessage(sender);

            return;
        }

        Message.command_help.sendMessage(sender);
    }
}