package com.badbones69.crazycrates.tasks.crates.other.quadcrates;

import org.bukkit.entity.Player;

public class SessionManager {

    /**
     * Check if player is in session.
     *
     * @param player player to check.
     * @return true or false.
     */
    public boolean inSession(Player player) {
        if (QuadCrateManager.getCrateSessions().isEmpty()) return false;

        for (QuadCrateManager quadCrateManager : QuadCrateManager.getCrateSessions()) {
            if (quadCrateManager.getPlayer().getUniqueId() == player.getUniqueId()) return true;
        }

        return false;
    }

    /**
     * Get an ongoing session.
     *
     * @param player player to check.
     * @return crate session or null.
     */
    public QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrateManager : QuadCrateManager.getCrateSessions()) {
            if (quadCrateManager.getPlayer().getUniqueId() == player.getUniqueId()) return quadCrateManager;
        }

        return null;
    }

    /**
     * End all crates.
     */
    public static void endCrates() {
        if (!QuadCrateManager.getCrateSessions().isEmpty()) {
            QuadCrateManager.getCrateSessions().forEach(session -> session.endCrate(true));
            QuadCrateManager.getCrateSessions().clear();
        }
    }
}