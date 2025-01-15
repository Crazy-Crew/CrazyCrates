package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KeyCheckEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final CrateLocation crateLocation;
    private boolean isCancelled;
    
    /**
     * Used to be able to disabled CrazyCrates CrateControl.onCrateOpen# event listener to be able to implement a custom one.
     * This does not disable the preview opener and the menu opener.
     *
     * @param player player being checked.
     * @param crateLocation crate location that is being used.
     */
    public KeyCheckEvent(@NotNull final Player player, @NotNull final CrateLocation crateLocation) {
        this.player = player;
        this.crateLocation = crateLocation;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public @NotNull final Player getPlayer() {
        return this.player;
    }
    
    public @NotNull final CrateLocation getCrateLocation() {
        return this.crateLocation;
    }
    
    @Override
    public final boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}