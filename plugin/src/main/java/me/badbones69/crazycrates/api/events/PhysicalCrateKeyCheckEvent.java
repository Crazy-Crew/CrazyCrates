package me.badbones69.crazycrates.api.events;

import me.badbones69.crazycrates.api.objects.CrateLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PhysicalCrateKeyCheckEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private CrateLocation crateLocation;
    private boolean isCancelled;
    
    /**
     * Used to be able to disabled CrazyCrates CrateControl.onCrateOpen# event listener to be able to implement a custom one.
     * This does not disable the preview opener and the menu opener.
     * @param player Player being checked.
     * @param crateLocation Location of the crate that is being used.
     */
    public PhysicalCrateKeyCheckEvent(Player player, CrateLocation crateLocation) {
        this.player = player;
        this.crateLocation = crateLocation;
        this.isCancelled = false;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public CrateLocation getCrateLocation() {
        return crateLocation;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    
}