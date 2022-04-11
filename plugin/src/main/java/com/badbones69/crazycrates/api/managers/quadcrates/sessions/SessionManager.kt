package com.badbones69.crazycrates.api.managers.quadcrates.sessions

import com.badbones69.crazycrates.api.managers.QuadCrateManager
import org.bukkit.entity.Player

object SessionManager {

    fun inSession(player: Player): Boolean {
        QuadCrateManager.getCrateSessions().forEach {
            if (it.player == player) return true
        }
        return false;
    }

    fun getSession(player: Player): QuadCrateManager? {
        QuadCrateManager.getCrateSessions().forEach {
            if (it.player == player) return it
        }
        return null
    }

    fun endAllCrates() {
        QuadCrateManager.getCrateSessions().forEach { session -> session.endCrateForce(false) }
        QuadCrateManager.getCrateSessions().clear()
    }
}