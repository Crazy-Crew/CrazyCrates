package com.badbones69.crazycrates.api.events.player;

import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Prize;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPrizeEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Crate crate;
    private final Prize prize;
    private final String crateName;

    /**
     * Builds the event when called.
     * @param player - The player who triggered the event.
     * @param crate - The crate the player is opening.
     * @param crateName - The string name of the crate the player is opening.
     * @param prize - The prize the player won.
     */
    public PlayerPrizeEvent(Player player, Crate crate, String crateName, Prize prize) {
        this.player = player;
        this.crate = crate;
        this.prize = prize;
        this.crateName = crateName;
    }
    
    public HandlerList getHandlers() {
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