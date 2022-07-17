package com.badbones69.crazycrates.commands

import com.badbones69.crazycrates.listeners.MenuListener
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import org.bukkit.entity.Player

@Command(value = "crates", alias = ["crate", "cc"])
open class CrateBaseCommand : BaseCommand() {

    @Default
    @Permission("crazycrates.command.player.menu")
    fun Player.menu() {
        MenuListener.openGUI(this)
    }
}