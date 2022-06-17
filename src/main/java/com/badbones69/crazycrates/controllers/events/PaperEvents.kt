package com.badbones69.crazycrates.controllers.events

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.enums.CrateType
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

        if (crazyManager.isInOpeningList(player)) {
            // DrBot Start
            if (crazyManager.getOpeningCrate(player).crateType.equals(CrateType.QUICK_CRATE)) return@with
            // DrBot End
            isCancelled = true
        }
    }
}