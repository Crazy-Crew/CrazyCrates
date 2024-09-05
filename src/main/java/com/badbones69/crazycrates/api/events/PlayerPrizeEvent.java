package com.badbones69.crazycrates.api.events;

import com.badbones69.crazycrates.api.events.crates.CrateStatusEvent;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerPrizeEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();

    private final CrateStatusEvent event;
    private final String crateName;
    private final Player player;
    private final Crate crate;
    private final Prize prize;

    private boolean isCancelled;

    @Deprecated(since = "3.7.4", forRemoval = true)
    public PlayerPrizeEvent(@NotNull final Player player, @NotNull final Crate crate, @NotNull final String crateName, @Nullable final Prize prize) {
        this(player, crate, prize);
    }

    public PlayerPrizeEvent(@NotNull final Player player, @NotNull final Crate crate, @Nullable final Prize prize) {
        this(player, null, crate, prize);
    }

    @ApiStatus.Internal
    public PlayerPrizeEvent(@NotNull final Player player, @Nullable final CrateStatusEvent event, @NotNull final Crate crate, @Nullable final Prize prize) {
        this.player = player;
        this.crate = crate;
        this.crateName = this.crate.getFileName();
        this.prize = prize;

        this.isCancelled = false;

        this.event = event;
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
    
    public @NotNull final Crate getCrate() {
        return this.crate;
    }
    
    public @NotNull final String getCrateName() {
        return this.crateName;
    }
    
    public @Nullable final Prize getPrize() {
        return this.prize;
    }

    public @Nullable final CrateStatusEvent getEvent() {
        return this.event;
    }

    @Override
    public final boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }
}