package com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class SessionManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    /**
     * Check if player is in session.
     *
     * @param player player to check.
     * @return true or false.
     */
    public final boolean inSession(@NotNull final Player player) {
        final List<QuadCrateManager> sessions = this.crateManager.getQuadSessions();

        if (sessions.isEmpty()) return false;

        final UUID uuid = player.getUniqueId();

        for (final QuadCrateManager session : sessions) {
            if (session.getPlayer().getUniqueId().equals(uuid)) return true;
        }

        return false;
    }

    /**
     * Get an ongoing session.
     *
     * @param player player to check.
     * @return crate session or null.
     */
    public @Nullable final QuadCrateManager getSession(@NotNull final Player player) {
        final UUID uuid = player.getUniqueId();

        final List<QuadCrateManager> sessions = this.crateManager.getQuadSessions();

        QuadCrateManager instance = null;

        if (sessions.isEmpty()) return instance;

        for (final QuadCrateManager session : sessions) {
            if (session.getPlayer().getUniqueId().equals(uuid)) {
                instance = session;

                break;
            }
        }

        return instance;
    }

    /**
     * End all crates.
     */
    public void endCrates() {
        final List<QuadCrateManager> sessions = this.crateManager.getQuadSessions();

        if (sessions.isEmpty()) return;

        sessions.forEach(session -> session.endCrate(true));

        this.crateManager.purgeQuadSessions();
    }
}