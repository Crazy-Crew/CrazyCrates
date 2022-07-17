package com.badbones69.crazycrates.commands

import com.badbones69.crazycrates.api.enums.settings.Messages
import com.badbones69.crazycrates.support.utils.KeyUtils
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import org.bukkit.entity.Player

@Command(value = "keys", alias = ["key"])
open class KeysBaseCommand : BaseCommand() {

    private val keyUtils = KeyUtils.getKeyUtils()

    @Default
    @Permission("crazycrates.command.player.key")
    fun view(player: Player) {
        keyUtils.checkKeys(player, Messages.PERSONAL_HEADER.messageNoPrefix, null)
    }
}