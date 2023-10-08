package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class PhysicalCrateKeyCheckEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final Player player;
    private final CrateLocation crateLocation;
    private boolean isCancelled;
    
    /**
     * Used to be able to disabled CrazyCrates CrateControl.onCrateOpen# event listener to be able to implement a custom one.
     * This does not disable the preview opener and the menu opener.
     *
     * @param uuid of the player being checked.
     * @param crateLocation that is being used.
     */
    public PhysicalCrateKeyCheckEvent(UUID uuid, CrateLocation crateLocation) {
        this.uuid = uuid;
        @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
        this.player = plugin.getServer().getPlayer(uuid);
        this.crateLocation = crateLocation;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public UUID getUuid() {
        return this.uuid;
    }
    
    public Player getPlayer() {
        return this.player;
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