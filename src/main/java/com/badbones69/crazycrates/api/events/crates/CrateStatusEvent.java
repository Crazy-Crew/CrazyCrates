package com.badbones69.crazycrates.api.events.crates;

import com.badbones69.crazycrates.api.enums.crates.CrateStatus;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateStatusEvent extends Event {

    private final Player player;
    private final Crate crate;

    public CrateStatusEvent(@NotNull final Crate crate, @NotNull final Player player) {
        this.status = CrateStatus.opened;
        this.player = player;
        this.crate = crate;
    }

    private Inventory inventory;
    private CrateStatus status;
    private Location location;

    public void setInventory(@Nullable final Inventory inventory) {
        this.inventory = inventory;
    }

    public @Nullable final Inventory getInventory() {
        return this.inventory;
    }

    public void setLocation(@Nullable final Location location) {
        this.location = location != null ? location : this.player.getLocation();
    }

    public final Location getLocation() {
        return this.location;
    }

    public final CrateStatusEvent setStatus(@NotNull final CrateStatus status) {
        this.status = status;

        return this;
    }

    public final CrateStatus getStatus() {
        return this.status;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final Crate getCrate() {
        return this.crate;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}