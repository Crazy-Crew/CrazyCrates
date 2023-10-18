package us.crazycrew.crazycrates.paper.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import us.crazycrew.crazycrates.paper.crates.CrateManager;
import us.crazycrew.crazycrates.paper.crates.menus.types.CrateMainMenu;

@Command("crazycrates")
public class CrateBaseCommand extends BaseCommand {

    private final CrazyCrates plugin;

    private final CrazyHandler crazyHandler;
    private final CrateManager crateManager;

    public CrateBaseCommand(CrazyCrates plugin) {
        this.plugin = plugin;

        this.crazyHandler = plugin.getCrazyHandler();

        this.crateManager = this.crazyHandler.getCrateManager();
    }

    @Default
    @Permission(value = "crazycrates.crate-menu", def = PermissionDefault.TRUE)
    public void onCommand(Player player) {
        CrateMainMenu crateMainMenu = new CrateMainMenu(this.plugin, player);

        crateMainMenu.create();

        crateMainMenu.getGui().open(player);
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        if (sender.hasPermission(new org.bukkit.permissions.Permission("crazycrates.admin-access", PermissionDefault.NOT_OP))) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.ADMIN_HELP.getMessage());
            return;
        }

        //TODO() Update message enum.
        //sender.sendMessage(Messages.HELP.getMessage());
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        this.crazyHandler.reload();

        sender.sendMessage("The reload has been completed.");
    }
}