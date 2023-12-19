package com.badbones69.crazycrates.managers.crates.other.quadcrates;

import org.bukkit.entity.Player;

public class SessionManager {

    public boolean inSession(Player player) {
        if (QuadCrateManager.getCrateSessions().isEmpty()) return false;

        for (QuadCrateManager quadCrateManager : QuadCrateManager.getCrateSessions()) {
            if (quadCrateManager.getPlayer() == player) return true;
        }

        return false;
    }

    public QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrateManager : QuadCrateManager.getCrateSessions()) {
            if (quadCrateManager.getPlayer() == player) return quadCrateManager;
        }

        return null;
    }

    public static void endCrates() {
        if (!QuadCrateManager.getCrateSessions().isEmpty()) {
            QuadCrateManager.getCrateSessions().forEach(session -> session.endCrateForce(false));
            QuadCrateManager.getCrateSessions().clear();
        }
    }
}