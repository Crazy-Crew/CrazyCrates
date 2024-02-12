package com.badbones69.crazycrates.api.events;

import com.badbones69.crazycrates.api.objects.other.CrateLocation;
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
    public KeyCheckEvent(Player player, CrateLocation crateLocation) {
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
    
    public Player getPlayer() {
        return this.player;
    }
    
    public CrateLocation getCrateLocation() {
        return this.crateLocation;
    }
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}