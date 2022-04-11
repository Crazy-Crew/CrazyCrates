package com.badbones69.crazycrates.commands.v2

import com.badbones69.crazycrates.getPlugin
import com.badbones69.crazycrates.api.managers.quadcrates.StructureHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

object TestCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        val player = sender as Player

        val file = File("${getPlugin().dataFolder}/schematics/classic.nbt")

        StructureHandler(file).pasteStructure(player.location)

        return false
    }
}