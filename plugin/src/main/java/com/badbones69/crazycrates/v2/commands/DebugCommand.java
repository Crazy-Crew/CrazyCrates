package com.badbones69.crazycrates.v2.commands;

import com.badbones69.crazycrates.v2.crates.sessions.QuadCrateSession;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;

@Command("debug")
public class DebugCommand extends BaseCommand {

    @Default
    public void executor(Player player, String structureName) {
        //new QuadCrateSession(player, player.getLocation(), structureName).startCrate();
    }
}