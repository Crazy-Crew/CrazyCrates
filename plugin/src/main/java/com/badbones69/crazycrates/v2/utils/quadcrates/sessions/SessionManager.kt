package com.badbones69.crazycrates.v2.utils.quadcrates.sessions

import com.badbones69.crazycrates.v2.crates.sessions.QuadCrateSession
import org.bukkit.entity.Player

object SessionManager {

    fun inSession(player: Player): Boolean {
        QuadCrateSession.getCrateSessions().forEach {
            if (it.player == player) return true
        }
        return false;
    }

    fun getSession(player: Player): QuadCrateSession? {
        QuadCrateSession.getCrateSessions().forEach {
            if (it.player == player) return it
        }
        return null
    }

    fun endAllCrates() {
        QuadCrateSession.getCrateSessions().forEach { session -> session.endCrateForce(false) }
        QuadCrateSession.getCrateSessions().clear()
    }
}