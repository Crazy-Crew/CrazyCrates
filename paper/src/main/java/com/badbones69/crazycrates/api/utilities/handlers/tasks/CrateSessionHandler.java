package com.badbones69.crazycrates.api.utilities.handlers.tasks;

import com.badbones69.crazycrates.api.managers.QuadCrateManager;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import java.util.List;

public class CrateSessionHandler {

    /**
     * Where you store and fetch active crate sessions.
     */
    private final List<QuadCrateManager> crateSessions = Lists.newArrayList();

    /**
     * Check if the player is in a session.
     * @param player - The player in the session.
     * @return True if the player is or false if not.
     */
    public boolean inSession(Player player) {
        for (QuadCrateManager quadCrate : crateSessions) {
            if (quadCrate.getPlayer() == player) return true;
        }

        return false;
    }

    public void addSession(QuadCrateManager quadCrateManager) {
        crateSessions.add(quadCrateManager);
    }

    public void removeSession(QuadCrateManager quadCrateManager) {
        crateSessions.remove(quadCrateManager);
    }

    /**
     * Get the player in the session.
     * @param player - The player in the session.
     * @return The quadcrate session or null if nothing.
     */
    public QuadCrateManager getSession(Player player) {
        for (QuadCrateManager quadCrate : crateSessions) {
            if (quadCrate.getPlayer() == player) return quadCrate;
        }

        return null;
    }

    /**
     * End all crates forcefully & clear the array list.
     */
    public void endSessions() {
        crateSessions.forEach(session -> session.endCrateForce(false));

        crateSessions.clear();
    }

    /**
     * @return All active crate sessions.
     */
    public List<QuadCrateManager> getCrateSessions() {
        return crateSessions;
    }
}