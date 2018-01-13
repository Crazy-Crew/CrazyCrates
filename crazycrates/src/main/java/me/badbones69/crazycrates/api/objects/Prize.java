package me.badbones69.crazycrates.api.objects;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Prize {
	
	private int chance;
	private String name;
	private String crate;
	private int maxRange;
	private boolean firework;
	private ItemStack displayItem;
	private ArrayList<String> messages;
	private ArrayList<String> commands;
	private ArrayList<ItemStack> items;
	private ArrayList<String> blackListPermissions;
	
	public Prize(String name, ItemStack displayItem, ArrayList<String> messages, ArrayList<String> commands,
	ArrayList<ItemStack> items, String crate, int chance, int maxRange, boolean firework, ArrayList<String> blackListPermissions) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = crate ;
		this.items = items != null ? items : new ArrayList<>();
		this.chance = chance;
		this.firework = firework;
		this.maxRange = maxRange;
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = displayItem != null ? displayItem : new ItemStack(Material.STONE);
		this.blackListPermissions = blackListPermissions != null ? blackListPermissions : new ArrayList<>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getDisplayItem() {
		return this.displayItem;
	}
	
	public ArrayList<String> getMessages() {
		return this.messages;
	}
	
	public ArrayList<String> getCommands() {
		return this.commands;
	}
	
	public ArrayList<ItemStack> getItems() {
		return this.items;
	}
	
	public String getCrate() {
		return this.crate;
	}
	
	public int getChance() {
		return this.chance;
	}
	
	public int getMaxRange() {
		return this.maxRange;
	}
	
	public boolean toggleFirework() {
		return this.firework;
	}
	
	public ArrayList<String> getBlackListedPermissions() {
		return blackListPermissions;
	}
	
}