package com.badbones69.crazycrates.controllers.events

import com.badbones69.crazycrates.api.CrazyManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class SpigotEvents : Listener {

    private val crazyManager = CrazyManager.getInstance()

    @EventHandler
    fun onPlayerItemPickUp(e: EntityPickupItemEvent): Unit = with(e) {
        if (crazyManager.isDisplayReward(item)) {
            isCancelled = true
            return@with
        }

        if (entity !is Player) return@with

        if (crazyManager.isInOpeningList(entity as Player)) isCancelled = true
    }
}