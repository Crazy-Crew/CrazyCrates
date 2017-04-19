package me.badbones69.crazycrates.api;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Prize {
	
	private int chance;
	private String name;
	private int maxRange;
	private boolean firework;
	private ItemStack displayItem;
	private ArrayList<String> messages;
	private ArrayList<String> commands;
	private ArrayList<ItemStack> items;
	
	public Prize(String name, ItemStack displayItem, ArrayList<String> messages, ArrayList<String> commands,
			ArrayList<ItemStack> items, int chance, int maxRange, boolean firework){
		this.name = name;
		this.items = items;
		this.chance = chance;
		this.firework = firework;
		this.maxRange = maxRange;
		this.messages = messages;
		this.commands = commands;
		this.displayItem = displayItem;
	}
	
	public String getName(){
		return this.name;
	}
	
	public ItemStack getDisplayItem(){
		return this.displayItem;
	}
	
	public ArrayList<String> getMessages(){
		return this.messages;
	}
	
	public ArrayList<String> getCommands(){
		return this.commands;
	}
	
	public ArrayList<ItemStack> getItems(){
		return this.items;
	}
	
	public int getChance(){
		return this.chance;
	}
	
	public int getMaxRange(){
		return this.maxRange;
	}
	
	public boolean toggleFirework(){
		return this.firework;
	}
	
}