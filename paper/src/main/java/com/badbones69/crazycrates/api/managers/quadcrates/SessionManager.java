package com.badbones69.crazycrates.api.managers.quadcrates;

import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import org.bukkit.entity.Player;
import java.util.List;

public class SessionManager {

    private static final List<QuadCrateManager> sessions = QuadCrateManager.getCrateSessions();

    /**
     * Check if the player is in a session.
     * @param player - The player in the session.
     * @return True if the player is or false if not.
     */
    public static boolean inSession(Player player) {
        for (QuadCrateManager quadCrate : sessions) {
            if (quadCrate.getPlayer() == player) return true;
        }

        return false;
    }

    /**
     * Get the player in the session.
     * @param player - The player in the session.
     * @return The quadcrate session or null if nothing.
     */
    public static QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrate : sessions) {
            if (quadCrate.getPlayer() == player) return quadCrate;
        }

        return null;
    }

    /**
     * End all crates forcefully & clear the array list.
     */
    public static void endCrates() {
        sessions.forEach(session -> session.endCrateForce(false));
        sessions.clear();
    }
}