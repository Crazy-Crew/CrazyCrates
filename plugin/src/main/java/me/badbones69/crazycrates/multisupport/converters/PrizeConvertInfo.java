package me.badbones69.crazycrates.multisupport.converters;

import me.badbones69.crazycrates.api.objects.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class PrizeConvertInfo {
	
	private String crate;
	private String name;
	private ItemBuilder displayItem;
	private List<String> displayEnchantments;
	private int maxRange;
	private int chance;
	private boolean firework;
	private List<String> items;
	private List<String> commands;
	private List<String> messages;
	
	public PrizeConvertInfo() {
		this.crate = "Converted Crate";
		this.name = "1";
		this.displayItem = new ItemBuilder();
		this.displayEnchantments = new ArrayList<>();
		this.maxRange = 100;
		this.chance = 50;
		this.firework = true;
		this.items = new ArrayList<>();
		this.commands = new ArrayList<>();
		this.messages = new ArrayList<>();
	}
	
	public String getCrate() {
		return crate;
	}
	
	public PrizeConvertInfo setCrate(String crate) {
		this.crate = crate;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public PrizeConvertInfo setName(String name) {
		this.name = name;
		return this;
	}
	
	public ItemBuilder getDisplayItem() {
		return displayItem;
	}
	
	public PrizeConvertInfo setDisplayItem(ItemBuilder displayItem) {
		this.displayItem = displayItem;
		return this;
	}
	
	public List<String> getDisplayEnchantments() {
		return displayEnchantments;
	}
	
	public PrizeConvertInfo setDisplayEnchantments(List<String> displayEnchantments) {
		this.displayEnchantments = displayEnchantments;
		return this;
	}
	
	public PrizeConvertInfo addDisplayEnchantments(String displayEnchantment) {
		this.displayEnchantments.add(displayEnchantment);
		return this;
	}
	
	public int getMaxRange() {
		return maxRange;
	}
	
	public PrizeConvertInfo setMaxRange(int maxRange) {
		this.maxRange = maxRange;
		return this;
	}
	
	public int getChance() {
		return chance;
	}
	
	public PrizeConvertInfo setChance(int chance) {
		this.chance = chance;
		return this;
	}
	
	public boolean isFirework() {
		return firework;
	}
	
	public PrizeConvertInfo setFirework(boolean firework) {
		this.firework = firework;
		return this;
	}
	
	public List<String> getItems() {
		return items;
	}
	
	public PrizeConvertInfo setItems(List<String> items) {
		this.items = items;
		return this;
	}
	
	public PrizeConvertInfo addItem(String item) {
		this.items.add(item);
		return this;
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	public PrizeConvertInfo setCommands(List<String> commands) {
		this.commands = commands;
		return this;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public PrizeConvertInfo setMessages(List<String> messages) {
		this.messages = messages;
		return this;
	}
	
}