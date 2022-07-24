package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.listeners.MenuListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;

@Command(value = "crates", alias = {"crazycrates", "crate", "cc"})
public class CrateBaseCommand extends BaseCommand {

    @Default
    @Permission("crazycrates.command.player.menu")
    public void menu(Player player) {
        MenuListener.openGUI(player);
    }
}