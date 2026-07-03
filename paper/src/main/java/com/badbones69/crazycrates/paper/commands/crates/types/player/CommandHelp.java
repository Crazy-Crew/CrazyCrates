package com.badbones69.crazycrates.paper.commands.crates.types.player;

import us.crazycrew.crazycrates.api.config.types.plugin.types.GuiConfig;
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
        final GuiConfig guiConfig = this.pluginConfig.getGuiConfig();

        if (guiConfig.isCrateMenuEnabled()) {
            new CrateMainMenu(
                    player,
                    guiConfig.getCrateMenuName(),
                    guiConfig.getCrateMenuRows()
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