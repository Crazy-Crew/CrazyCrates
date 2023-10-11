package us.crazycrew.crazycrates.paper.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.CrateManager;

@Command("crazycrates")
public class CrateBaseCommand extends BaseCommand {

    private final CrateManager crateManager;

    public CrateBaseCommand(CrazyCrates plugin) {
        this.crateManager = plugin.getCrazyHandler().getCrateManager();
    }

    @Default
    public void onCommand(Player player) {
        this.crateManager.getCrate("CrateExample").getPrizes().forEach(prize -> {
            player.getInventory().addItem(prize.getDisplayItem());
        });
    }
}