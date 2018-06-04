package me.badbones69.crazycrates.api.objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Prize {
	
	private int chance;
	private String name;
	private String crate;
	private int maxRange;
	private boolean firework;
	private ItemStack displayItem;
	private List<String> tiers;
	private List<String> messages;
	private List<String> commands;
	private ArrayList<ItemStack> items;
	private Prize altPrize;
	private List<String> blackListPermissions;
	
	public Prize(String name, List<String> messages, List<String> commands, ArrayList<ItemStack> items) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = "";
		this.items = items != null ? items : new ArrayList<>();
		this.chance = 0;
		this.firework = false;
		this.maxRange = 100;
		this.tiers = tiers != null ? tiers : new ArrayList<>();
		this.tiers.replaceAll(String::toLowerCase);
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = displayItem != null ? displayItem : new ItemStack(Material.STONE);
		this.blackListPermissions = blackListPermissions != null ? blackListPermissions : new ArrayList<>();
		this.blackListPermissions.replaceAll(String::toLowerCase);
		this.altPrize = null;
	}
	
	public Prize(String name, ItemStack displayItem, List<String> messages, List<String> commands,
	ArrayList<ItemStack> items, String crate, int chance, int maxRange, boolean firework, List<String> blackListPermissions,
	List<String> tiers) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = crate;
		this.items = items != null ? items : new ArrayList<>();
		this.chance = chance;
		this.firework = firework;
		this.maxRange = maxRange;
		this.tiers = tiers != null ? tiers : new ArrayList<>();
		this.tiers.replaceAll(String::toLowerCase);
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = displayItem != null ? displayItem : new ItemStack(Material.STONE);
		this.blackListPermissions = blackListPermissions != null ? blackListPermissions : new ArrayList<>();
		this.blackListPermissions.replaceAll(String::toLowerCase);
		this.altPrize = null;
	}
	
	public Prize(String name, ItemStack displayItem, List<String> messages, List<String> commands,
	ArrayList<ItemStack> items, String crate, int chance, int maxRange, boolean firework, List<String> blackListPermissions,
	List<String> tiers, Prize altPrize) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = crate;
		this.items = items != null ? items : new ArrayList<>();
		this.chance = chance;
		this.firework = firework;
		this.maxRange = maxRange;
		this.tiers = tiers != null ? tiers : new ArrayList<>();
		this.tiers.replaceAll(String::toLowerCase);
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = displayItem != null ? displayItem : new ItemStack(Material.STONE);
		this.blackListPermissions = blackListPermissions != null ? blackListPermissions : new ArrayList<>();
		this.blackListPermissions.replaceAll(String::toLowerCase);
		this.altPrize = altPrize;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getDisplayItem() {
		return displayItem;
	}
	
	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
	}
	
	public List<String> getTiers() {
		return tiers;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	public String getCrate() {
		return crate;
	}
	
	public int getChance() {
		return chance;
	}
	
	public void setChance(int chance) {
		this.chance = chance;
	}
	
	public int getMaxRange() {
		return maxRange;
	}
	
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}
	
	public boolean useFireworks() {
		return firework;
	}
	
	public void setFirework(boolean firework) {
		this.firework = firework;
	}
	
	public List<String> getBlackListedPermissions() {
		return blackListPermissions;
	}
	
	public Prize getAltPrize() {
		return altPrize;
	}
	
	public Boolean hasAltPrize() {
		return altPrize != null;
	}
	
	public Boolean hasBlacklistPermission(Player player) {
		if(!player.isOp()) {
			for(String blackListPermission : blackListPermissions) {
				if(player.hasPermission(blackListPermission)) {
					return true;
				}
			}
		}
		return false;
	}
	
}