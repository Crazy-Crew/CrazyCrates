package com.badbones69.crazycrates.api.events.player;

import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerReceiveKeyEvent extends Event implements Cancellable {

    /**
     * A list of handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The player object.
     */
    private final Player player;

    /**
     * The crate object.
     */
    private final Crate crate;

    /**
     * The reason for getting a key.
     */
    private final KeyReceiveReason reason;

    /**
     * The amount of keys.
     */
    private final int amount;

    /**
     * If the event is cancelled or not.
     */
    private boolean isCancelled;

    /**
     * Builds the event when called.
     * @param player - The player who is receiving the key.
     * @param crate - The crate of the key the player is receiving.
     * @param reason - The reason supplied for why they have the key.
     * @param amount - The amount of keys they are given.
     */
    public PlayerReceiveKeyEvent(Player player, Crate crate, KeyReceiveReason reason, int amount) {
        this.player = player;
        this.crate = crate;
        this.reason = reason;
        this.amount = amount;
        isCancelled = false;
    }

    /**
     * @return Get the player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Get the crate object.
     */
    public Crate getCrate() {
        return crate;
    }

    /**
     * @return Get the reason for why they were given a key.
     */
    public KeyReceiveReason getReason() {
        return reason;
    }

    /**
     * @return True if the event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * @param cancel - True if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    /**
     * @return The list of handlers.
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return The amount of keys.
     */
    public int getAmount() {
        return amount;
    }
    
    public enum KeyReceiveReason {
        /**
         * Given a key from the /crates give command.
         */
        GIVE_COMMAND,
        /**
         * Received a key from the /crates giveall command.
         */
        GIVE_ALL_COMMAND,
        /**
         * Received when a player has come online after being given a key while offline.
         */
        OFFLINE_PLAYER,
        /**
         * Received a key as a refund from a crate that failed.
         */
        REFUND,
        /**
         * Received a key from the /crates transfer command.
         */
        TRANSFER
    }
}