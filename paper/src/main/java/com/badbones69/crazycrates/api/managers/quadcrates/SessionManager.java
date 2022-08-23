package com.badbones69.crazycrates.api.managers.quadcrates;

import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import com.google.inject.Inject;
import org.bukkit.entity.Player;

public class SessionManager {

    @Inject private QuadCrateManager quadCrateManager;

    public boolean inSession(Player player) {
        for (QuadCrateManager quadCrate : quadCrateManager.getCrateSessions()) {
            if (quadCrate.getPlayer() == player) return true;
        }

        return false;
    }

    public QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrate : quadCrateManager.getCrateSessions()) {
            if (quadCrate.getPlayer() == player) return quadCrate;
        }

        return null;
    }

    public void endCrates() {
        quadCrateManager.getCrateSessions().forEach(session -> session.endCrateForce(false));
        quadCrateManager.getCrateSessions().clear();
    }
}