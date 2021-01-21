package me.badbones69.crazycrates.api.events;

import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreCrateOpenEvent extends Event implements Cancellable {

    private Player player;
    private Crate crate;
    private KeyType keyType;
    private boolean isCancelled;
    private HandlerList handlers = new HandlerList();

    public PreCrateOpenEvent(Player player, Crate crate, KeyType keyType) {
        this.player = player;
        this.crate = crate;
        this.keyType = keyType;
        isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Crate getCrate() {
        return crate;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
