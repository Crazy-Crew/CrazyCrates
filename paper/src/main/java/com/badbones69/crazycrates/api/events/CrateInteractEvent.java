package com.badbones69.crazycrates.api.events;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateInteractEvent extends Event implements Cancellable {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final CrateLocation crateLocation;
    private final EquipmentSlot equipmentSlot;
    private final Location location;
    private final Player player;
    private final Action action;

    private boolean isCancelled;

    private final boolean isVanilla;

    public CrateInteractEvent(@NotNull final Location location, @NotNull final EquipmentSlot equipmentSlot, @NotNull final Player player, @NotNull final Action action) {
        this.crateLocation = this.crateManager.getCrateLocation(location);

        this.equipmentSlot = equipmentSlot;
        this.location = location;
        this.player = player;
        this.action = action;

        this.isCancelled = false;
        this.isVanilla = false;

        this.event = null;
    }

    private final PlayerInteractEvent event;

    public CrateInteractEvent(@NotNull final PlayerInteractEvent event, @NotNull final Location location) {
        this.crateLocation = this.crateManager.getCrateLocation(location);
        this.location = location;

        this.equipmentSlot = event.getHand();
        this.player = event.getPlayer();
        this.action = event.getAction();
        this.event = event;

        this.isCancelled = false;
        this.isVanilla = true;
    }

    public @Nullable CrateLocation getCrateLocation() {
        return this.crateLocation;
    }

    public @NotNull EquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public @Nullable PlayerInteractEvent getEvent() {
        return this.event;
    }

    public @NotNull Location getLocation() {
        return this.location;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull Action getAction() {
        return this.action;
    }

    public boolean isKey() {
        return this.crateManager.isKey(this.player, this.equipmentSlot);
    }

    public boolean isVanilla() {
        return this.isVanilla;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
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