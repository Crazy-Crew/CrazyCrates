package com.badbones69.crazycrates.paper.api.events;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPrizeEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Crate crate;
    private final Prize prize;
    private final String crateName;
    
    public PlayerPrizeEvent(Player player, Crate crate, String crateName, Prize prize) {
        this.player = player;
        this.crate = crate;
        this.prize = prize;
        this.crateName = crateName;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Crate getCrate() {
        return crate;
    }
    
    public String getCrateName() {
        return crateName;
    }
    
    public Prize getPrize() {
        return prize;
    }
}