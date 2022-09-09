package com.badbones69.crazycrates.api.events;

import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PhysicalCrateKeyCheckEvent extends Event implements Cancellable {

    /**
     * A list of handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The player object.
     */
    private final Player player;

    /**
     * The location of the crate.
     */
    private final CrateLocation crateLocation;

    /**
     * If the event is cancelled or not.
     */
    private boolean isCancelled;
    
    /**
     * Used to be able to disabled CrazyCrates CrateControl.onCrateOpen# event listener to be able to implement a custom one.
     * This does not disable the preview opener and the menu opener.
     * @param player - Player being checked.
     * @param crateLocation - Location of the crate that is being used.
     */
    public PhysicalCrateKeyCheckEvent(Player player, CrateLocation crateLocation) {
        this.player = player;
        this.crateLocation = crateLocation;
        this.isCancelled = false;
    }

    /**
     * @return The list of handlers.
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return Get the player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Get the location of the crate.
     */
    public CrateLocation getCrateLocation() {
        return crateLocation;
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
}