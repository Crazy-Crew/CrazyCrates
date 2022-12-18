package com.badbones69.crazycrates.registry.player;

import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.util.LoggerUtil;

/**
 * Player event listener.
 *
 * @author BillyGalbreath
 */
public interface PlayerListener {

    /**
     * Fired when a player joins the server.
     *
     * @param player player that joined
     */
    default void onPlayerJoin(@Nullable PlayerObject player) {
        if (player == null) return;

        LoggerUtil.debug("<gold>" + player.getName() + "</gold> <red>has joined the server.</red>");
    }

    /**
     * Fired when a player leaves the server.
     *
     * @param player player that left
     */
    default void onPlayerQuit(@Nullable PlayerObject player) {
        if (player == null) return;

        LoggerUtil.debug("<gold>" + player.getName() + "</gold> <red>has left the server.</red>");
    }
}