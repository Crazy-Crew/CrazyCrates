package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.CrazyCratesOld;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class PlayerReceiveKeyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final Player player;
    private final Crate crate;
    private final KeyReceiveReason reason;
    private final int amount;
    private boolean isCancelled;
    
    public PlayerReceiveKeyEvent(UUID uuid, Crate crate, KeyReceiveReason reason, int amount) {
        this.uuid = uuid;
        @NotNull CrazyCratesOld plugin = JavaPlugin.getPlugin(CrazyCratesOld.class);
        this.player = plugin.getServer().getPlayer(uuid);
        this.crate = crate;
        this.reason = reason;
        this.amount = amount;
        isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    public UUID getUuid() {
        return this.uuid;
    }

    public Player getPlayer() {
        return this.player;
    }
    
    public Crate getCrate() {
        return crate;
    }
    
    public KeyReceiveReason getReason() {
        return reason;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public enum KeyReceiveReason {
        // Received a key from the /cc give command.
        GIVE_COMMAND,
        // Received a key from the /cc giveall command.
        GIVE_ALL_COMMAND,
        // Received when player has come online after being given a key while offline.
        OFFLINE_PLAYER,
        // Received a key as a refund from a crate that failed.
        REFUND,
        // Received a key from the /cc transfer command.
        TRANSFER
    }
}