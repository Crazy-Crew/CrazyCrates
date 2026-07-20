package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class CrateOpenEvent extends Event implements Cancellable {

    private final Player player;
    private final Crate crate;
    private final Location location;
    private final KeyType keyType;
    private final boolean checkHand;
    private final ConfigurationSection configuration;
    private final EventType eventType;
    private final boolean isSilent;

    private final int amount;

    private boolean isCancelled;

    public CrateOpenEvent(
            @NotNull final Player player,
            @NotNull final Location location,
            @NotNull final Crate crate,
            @NotNull final KeyType keyType,
            final boolean checkHand,
            @NotNull final ConfigurationSection configuration,
            final boolean isSilent,
            final EventType eventType,
            final int amount
    ) {
        this.player = player;
        this.location = location;
        this.crate = crate;

        this.keyType = keyType;
        this.checkHand = checkHand;

        this.configuration = configuration;
        this.eventType = eventType;

        this.amount = amount;

        this.isCancelled = false;
        this.isSilent = isSilent;
    }

    public CrateOpenEvent(
            @NotNull final Player player,
            @NotNull final Location location,
            @NotNull final Crate crate,
            @NotNull final KeyType keyType,
            final boolean checkHand,
            @NotNull final ConfigurationSection configuration,
            final boolean isSilent,
            final EventType eventType
    ) {
        this(player, location, crate, keyType, checkHand, configuration, isSilent, eventType, 1);
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    public @NotNull final KeyType getKeyType() {
        return this.keyType;
    }

    public final boolean isCheckHand() {
        return this.checkHand;
    }

    public @NotNull final ConfigurationSection getConfiguration() {
        return this.configuration;
    }

    public final boolean isSilent() {
        return this.isSilent;
    }

    public final EventType getEventType() {
        return this.eventType;
    }

    public final int getAmount() {
        return this.amount;
    }

    public @NotNull final Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}