package me.badbones69.crazycrates.api.objects;

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
	private ItemBuilder displayItem;
	private List<Tier> tiers;
	private List<String> messages;
	private List<String> commands;
	private ArrayList<ItemStack> items;
	private Prize altPrize;
	private List<String> blackListPermissions;
	
	/**
	 * Create a new prize.
	 * This option is used only for Alternative Prizes.
	 * @param name The name of the prize.
	 * @param messages The messages it sends to the player that wins it.
	 * @param commands The commands that run when the prize is won.
	 * @param items The ItemStacks that are given to the player that wins.
	 */
	public Prize(String name, List<String> messages, List<String> commands, ArrayList<ItemStack> items) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = "";
		this.items = items != null ? items : new ArrayList<>();
		this.chance = 0;
		this.firework = false;
		this.maxRange = 100;
		this.tiers = new ArrayList<>();
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = new ItemBuilder();
		this.blackListPermissions = new ArrayList<>();
		this.altPrize = null;
	}
	
	/**
	 *
	 * @param name The name of the prize.
	 * @param displayItem The display item that is shown in the preview and for what the player wins.
	 * @param messages The messages it sends to the player that wins it.
	 * @param commands The commands that run when the prize is won.
	 * @param items The ItemStacks that are given to the player that wins.
	 * @param crate The name of the crate the prize belongs to.
	 * @param chance The chance the prize has of winning.
	 * @param maxRange The max range the prize has.
	 * @param firework Set if the prize plays an exploding firework if won.
	 * @param blackListPermissions The list of black permissions that the prize has.
	 * @param tiers The tiers the prize is in.
	 * @param altPrize The alternative prize that is won if the player has a blacklist permission.
	 */
	public Prize(String name, ItemBuilder displayItem, List<String> messages, List<String> commands,
	ArrayList<ItemStack> items, String crate, int chance, int maxRange, boolean firework, List<String> blackListPermissions,
	List<Tier> tiers, Prize altPrize) {
		this.name = name != null ? name : "&4No name Found!";
		this.crate = crate;
		this.items = items != null ? items : new ArrayList<>();
		this.chance = chance;
		this.firework = firework;
		this.maxRange = maxRange;
		this.tiers = tiers != null ? tiers : new ArrayList<>();
		this.messages = messages != null ? messages : new ArrayList<>();
		this.commands = commands != null ? commands : new ArrayList<>();
		this.displayItem = displayItem != null ? displayItem : new ItemBuilder();
		this.blackListPermissions = blackListPermissions != null ? blackListPermissions : new ArrayList<>();
		this.blackListPermissions.replaceAll(String :: toLowerCase);
		this.altPrize = altPrize;
	}
	
	/**
	 * @return Returns the name of the prize.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Returns the display item that is shown for the preview and the winning prize.
	 */
	public ItemStack getDisplayItem() {
		return displayItem.build();
	}
	
	/**
	 * @return Returns the ItemBuilder of the display item.
	 */
	public ItemBuilder getDisplayItemBuilder() {
		return displayItem;
	}
	
	/**
	 * @return Returns the list of tiers the prize is in.
	 */
	public List<Tier> getTiers() {
		return tiers;
	}
	
	/**
	 * @return Returns the messages sent to the player.
	 */
	public List<String> getMessages() {
		return messages;
	}
	
	/**
	 * @return Returns the commands that are ran when the player wins.
	 */
	public List<String> getCommands() {
		return commands;
	}
	
	/**
	 * @return Returns the ItemStacks that are given to the player that wins.
	 */
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	/**
	 * @return Returns the name of the crate the prize is in.
	 */
	public String getCrate() {
		return crate;
	}
	
	/**
	 * @return Returns the chance the prize has of being picked.
	 */
	public int getChance() {
		return chance;
	}
	
	/**
	 * @return Returns the max range of the prize.
	 */
	public int getMaxRange() {
		return maxRange;
	}
	
	/**
	 * @return Returns true if a firework explosion is played and false if not.
	 */
	public boolean useFireworks() {
		return firework;
	}
	
	/**
	 * @return Returns the list of blacklist permissions on the prize.
	 */
	public List<String> getBlackListedPermissions() {
		return blackListPermissions;
	}
	
	/**
	 * @return Returns the alternative prize the player wins if they have a blacklist permission.
	 */
	public Prize getAltPrize() {
		return altPrize;
	}
	
	/**
	 * @return Returns true if the prize has an alternative prize and false if not.
	 */
	public boolean hasAltPrize() {
		return altPrize != null;
	}
	
	/**
	 * @return Returns true if they prize has blacklist permissions and false if not.
	 */
	public boolean hasBlacklistPermission(Player player) {
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