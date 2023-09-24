package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPrizeEvent extends Event implements Cancellable {
    
    private final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Crate crate;
    private final Prize prize;
    private final String crateName;

    private boolean isCancelled;
    
    public PlayerPrizeEvent(Player player, Crate crate, String crateName, Prize prize) {
        this.player = player;
        this.crate = crate;
        this.prize = prize;
        this.crateName = crateName;

        this.isCancelled = false;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Crate getCrate() {
        return this.crate;
    }
    
    public String getCrateName() {
        return this.crateName;
    }
    
    public Prize getPrize() {
        return this.prize;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return this.handlers;
    }
}