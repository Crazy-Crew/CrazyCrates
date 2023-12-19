package com.badbones69.crazycrates.api.events;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class CrateOpenEvent extends Event implements Cancellable {

    private final Player player;
    private final Crate crate;
    private final KeyType keyType;
    private final boolean checkHand;
    private final FileConfiguration configuration;

    private boolean isCancelled;

    public CrateOpenEvent(Player player, Crate crate, KeyType keyType, boolean checkHand, FileConfiguration configuration) {
        this.player = player;
        this.crate = crate;

        this.keyType = keyType;
        this.checkHand = checkHand;

        this.configuration = configuration;

        this.isCancelled = false;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Crate getCrate() {
        return this.crate;
    }

    public KeyType getKeyType() {
        return this.keyType;
    }

    public boolean isCheckHand() {
        return this.checkHand;
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
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