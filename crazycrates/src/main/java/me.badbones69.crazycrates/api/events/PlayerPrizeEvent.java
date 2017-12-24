package me.badbones69.crazycrates.api.events;

import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPrizeEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private CrateType crateType;
    private Prize prize;
    private String crateName;

    public PlayerPrizeEvent(Player player, CrateType crateType, String crateName, Prize prize)
    {
        this.player = player;
        this.crateType = crateType;
        this.prize = prize;
        this.crateName = crateName;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public Player getPlayer()
    {
        return player;
    }

    public CrateType getCrateType()
    {
        return crateType;
    }

    public String getCrateName()
    {
        return crateName;
    }

    public Prize getPrize()
    {
        return prize;
    }

}