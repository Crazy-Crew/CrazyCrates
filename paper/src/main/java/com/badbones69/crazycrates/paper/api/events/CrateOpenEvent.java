package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class CrateOpenEvent extends Event implements Cancellable {

    private final Player player;
    private final Crate crate;
    private final KeyType keyType;
    private final boolean checkHand;
    private final YamlConfiguration configuration;
    private final EventType eventType;
    private final boolean isSilent;

    private boolean isCancelled;

    public CrateOpenEvent(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, final boolean checkHand, @NotNull final YamlConfiguration configuration, boolean isSilent, final EventType eventType) {
        this.player = player;
        this.crate = crate;

        this.keyType = keyType;
        this.checkHand = checkHand;

        this.configuration = configuration;
        this.eventType = eventType;

        this.isCancelled = false;
        this.isSilent = isSilent;
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

    public @NotNull final YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public final boolean isSilent() {
        return this.isSilent;
    }

    public final EventType getEventType() {
        return this.eventType;
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