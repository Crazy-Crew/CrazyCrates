package com.badbones69.crazycrates.api.managers.quadcrates;

import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import org.bukkit.entity.Player;
import java.util.List;

public class SessionManager {

    private static final List<QuadCrateManager> sessions = QuadCrateManager.getCrateSessions();

    public static boolean inSession(Player player) {
        for (QuadCrateManager quadCrate : sessions) {
            if (quadCrate.getPlayer() == player) return true;
        }

        return false;
    }

    public static QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrate : sessions) {
            if (quadCrate.getPlayer() == player) return quadCrate;
        }

        return null;
    }

    public static void endCrates() {
        sessions.forEach(session -> session.endCrateForce(false));
        sessions.clear();
    }
}