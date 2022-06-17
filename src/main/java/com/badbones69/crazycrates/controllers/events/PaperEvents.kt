package com.badbones69.crazycrates.controllers.events

import com.badbones69.crazycrates.api.CrazyManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

class PaperEvents : Listener {

    private val crazyManager = CrazyManager.getInstance()

    @EventHandler
    fun onPlayerItemPickUp(e: PlayerAttemptPickupItemEvent): Unit = with(e) {
        if (crazyManager.isDisplayReward(item)) {
            isCancelled = true
            return@with
        }

        if (crazyManager.isInOpeningList(player)) isCancelled = true
    }
}