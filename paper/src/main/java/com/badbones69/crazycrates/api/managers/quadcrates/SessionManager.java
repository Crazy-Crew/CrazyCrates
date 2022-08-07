package com.badbones69.crazycrates.api.managers.quadcrates;

import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import org.bukkit.entity.Player;

public class SessionManager {

    public boolean inSession(Player player) {
        for (QuadCrateManager loop : QuadCrateManager.getCrateSessions()) {
            if (loop.getPlayer() == player) return true;
        }

        return false;
    }

    public QuadCrateManager getSession(Player player) {
        for (QuadCrateManager loop : QuadCrateManager.getCrateSessions()) {
            if (loop.getPlayer() == player) return loop;
        }

        return null;
    }

    public void endCrates() {
        QuadCrateManager.getCrateSessions().forEach(session -> session.endCrateForce(false));
        QuadCrateManager.getCrateSessions().clear();
    }
}