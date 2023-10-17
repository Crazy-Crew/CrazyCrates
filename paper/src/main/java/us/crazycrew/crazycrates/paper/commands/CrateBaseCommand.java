package us.crazycrew.crazycrates.paper.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import us.crazycrew.crazycrates.paper.crates.CrateManager;
import us.crazycrew.crazycrates.paper.crates.menus.GuiManager;

@Command("crazycrates")
public class CrateBaseCommand extends BaseCommand {

    private final CrazyHandler crazyHandler;
    private final CrateManager crateManager;
    private final GuiManager guiManager;

    public CrateBaseCommand(CrazyCrates plugin) {
        this.crazyHandler = plugin.getCrazyHandler();

        this.crateManager = this.crazyHandler.getCrateManager();
        this.guiManager = this.crazyHandler.getGuiManager();
    }

    @Default
    public void onCommand(Player player) {
        this.guiManager.getCrateMainMenu().getGui().open(player);

        //this.crateManager.getCrate("CrateExample").getPrizes().forEach(prize -> {
        //    player.getInventory().addItem(prize.getDisplayItem());
        //});
    }

    @SubCommand("reload")
    public void onReload(CommandSender sender) {
        this.crazyHandler.reload();

        sender.sendMessage("The reload has been completed.");
    }
}