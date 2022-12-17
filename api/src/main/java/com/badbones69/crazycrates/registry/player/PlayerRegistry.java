package com.badbones69.crazycrates.registry.player;

import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.registry.KeyedRegistry;
import java.util.Locale;
import java.util.UUID;

/**
 * Manages player specific data.
 *
 * @author BillyGalbreath
 */
public abstract class PlayerRegistry extends KeyedRegistry<PlayerObject> {

    /**
     * Get the registered player by uuid.
     * <p>
     * Will return null if no player registered.
     *
     * @param uuid player uuid
     * @return registered player or null
     */
    @Nullable
    public PlayerObject get(UUID uuid) {
        return get(PlayerObject.createKey(uuid));
    }

    /**
     * Get the registered player by name.
     * <p>
     * Will return null if no player registered.
     *
     * @param name player name
     * @return registered player or null
     */
    @Nullable
    public PlayerObject get(String name) {
        String lowercaseName = name.toLowerCase(Locale.ROOT);

        for (PlayerObject playerObject : entries().values()) {
            if (playerObject.getName().toLowerCase(Locale.ROOT).equals(lowercaseName)) return playerObject;
        }

        return null;
    }
}