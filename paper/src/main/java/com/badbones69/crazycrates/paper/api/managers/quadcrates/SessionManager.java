package com.badbones69.crazycrates.paper.api.managers.quadcrates;

import com.badbones69.crazycrates.paper.api.managers.QuadCrateManager;
import org.bukkit.entity.Player;

public class SessionManager {

    public boolean inSession(Player player) {
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
        QuadCrateManager.getCrateSessions().forEach(session -> session.endCrateForce(false));
        QuadCrateManager.getCrateSessions().clear();
    }
}