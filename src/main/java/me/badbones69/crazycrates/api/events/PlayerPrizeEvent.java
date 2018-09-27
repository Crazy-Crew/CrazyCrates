package me.badbones69.crazycrates.api.events;

import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPrizeEvent extends Event {
	
	private Player player;
	private Crate crate;
	private Prize prize;
	private String crateName;
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public PlayerPrizeEvent(Player player, Crate crate, String crateName, Prize prize) {
		this.player = player;
		this.crate = crate;
		this.prize = prize;
		this.crateName = crateName;
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