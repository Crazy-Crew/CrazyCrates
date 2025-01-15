package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerReceiveKeyEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final Crate crate;
    private final KeyReceiveReason reason;
    private final int amount;
    private boolean isCancelled;
    
    public PlayerReceiveKeyEvent(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyReceiveReason reason, final int amount) {
        this.player = player;
        this.offlinePlayer = null;
        this.crate = crate;
        this.reason = reason;
        this.amount = amount;
        this.isCancelled = false;
    }

    public PlayerReceiveKeyEvent(@NotNull final OfflinePlayer player, @NotNull final Crate crate, @NotNull final KeyReceiveReason reason, final int amount) {
        this.player = null;
        this.offlinePlayer = player;
        this.crate = crate;
        this.reason = reason;
        this.amount = amount;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public @Nullable final Player getPlayer() {
        return this.player;
    }

    public @Nullable OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public Crate getCrate() {
        return this.crate;
    }
    
    public KeyReceiveReason getReason() {
        return this.reason;
    }
    
    @Override
    public final boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public final int getAmount() {
        return this.amount;
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