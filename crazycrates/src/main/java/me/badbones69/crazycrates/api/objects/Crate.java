package me.badbones69.crazycrates.api.objects;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.controllers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Crate {
	
	private String name;
	private ItemStack key;
	private CrateType crateType;
	private FileConfiguration file;
	private ArrayList<Prize> prizes;
	private String crateInventoryName;
	private ArrayList<ItemStack> preview;
	private FileManager fileManager = FileManager.getInstance();
	
	/**
	 *
	 * @param name The name of the crate.
	 * @param crateType The crate type of the crate.
	 * @param key The key as an item stack.
	 * @param prizes The prizes that can be won.
	 * @param file The crate file.
	 */
	public Crate(String name, CrateType crateType, ItemStack key, ArrayList<Prize> prizes, FileConfiguration file) {
		this.key = key;
		this.file = file;
		this.name = name;
		this.prizes = prizes;
		this.crateType = crateType;
		this.preview = loadPreview();
		this.crateInventoryName = file != null ? Methods.color(file.getString("Crate.CrateName")) : "";
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System.
	 * @param player The player that will be winning the prize.
	 * @return The winning prize.
	 */
	public Prize pickPrize(Player player) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			useablePrizes.addAll(getPrizes());
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				useablePrizes.add(prize);
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		return prizes.get(new Random().nextInt(prizes.size()));
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System. Only used in the Cosmic Crate Type since it is the only one with tiers.
	 * @param player The player that will be winning the prize.
	 * @param tier The tier you wish the prize to be from.
	 * @return The winning prize based on the crate's tiers.
	 */
	public Prize pickPrize(Player player, String tier) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			useablePrizes.addAll(getPrizes());
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				useablePrizes.add(prize);
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		return prizes.get(new Random().nextInt(prizes.size()));
	}
	
	/**
	 * Picks a random prize based on BlackList Permissions and the Chance System. Spawns the display item at the location.
	 * @param player The player that will be winning the prize.
	 * @param location The location the prize will spawn at.
	 * @return The winning prize.
	 */
	public Prize pickPrize(Player player, Location location) {
		ArrayList<Prize> prizes = new ArrayList<>();
		ArrayList<Prize> useablePrizes = new ArrayList<>();
		// ================= Blacklist Check ================= //
		if(player.isOp()) {
			useablePrizes.addAll(getPrizes());
		}else {
			for(Prize prize : getPrizes()) {
				if(prize.hasBlacklistPermission(player)) {
					if(!prize.hasAltPrize()) {
						continue;
					}
				}
				useablePrizes.add(prize);
			}
		}
		// ================= Chance Check ================= //
		for(int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
			for(Prize prize : useablePrizes) {
				int max = prize.getMaxRange();
				int chance = prize.getChance();
				int num;
				for(int counter = 1; counter <= 1; counter++) {
					num = 1 + new Random().nextInt(max);
					if(num >= 1 && num <= chance) {
						prizes.add(prize);
					}
				}
			}
		}
		Prize prize = prizes.get(new Random().nextInt(prizes.size()));
		if(prize.useFireworks()) {
			Methods.fireWork(location);
		}
		return prize;
	}
	
	/**
	 *
	 * @return name The name of the crate.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the name of the inventory the crate will have.
	 * @return The name of the inventory for GUI based crate types.
	 */
	public String getCrateInventoryName() {
		return this.crateInventoryName;
	}
	
	/**
	 * Gets the inventory of a preview of prizes for the crate.
	 * @return The preview as an Inventory object.
	 */
	public Inventory getPreview() {
		int slots = 9;
		for(int size = file.getConfigurationSection("Crate.Prizes").getKeys(false).size(); size > 9 && slots < 54; size -= 9) {
			slots += 9;
		}
		Inventory inv = Bukkit.createInventory(null, slots, Methods.color(file.getString("Crate.Name")));
		for(ItemStack item : preview) {
			int nextSlot = inv.firstEmpty();
			if(nextSlot >= 0) {
				inv.setItem(nextSlot, item);
			}else {
				break;
			}
		}
		return inv;
	}
	
	/**
	 * Gets all the preview items.
	 * @return A list of all the preview items.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ItemStack> getPreviewItems() {
		return (ArrayList<ItemStack>) preview.clone();
	}
	
	/**
	 *
	 * @return The crate type of the crate.
	 */
	public CrateType getCrateType() {
		return this.crateType;
	}
	
	/**
	 *
	 * @return The key as an item stack.
	 */
	public ItemStack getKey() {
		return this.key.clone();
	}
	
	/**
	 *
	 * @param amount The amount of keys you want.
	 * @return The key as an item stack.
	 */
	public ItemStack getKey(int amount) {
		ItemStack key = this.key.clone();
		key.setAmount(amount);
		return key;
	}
	
	/**
	 *
	 * @return The crates file.
	 */
	public FileConfiguration getFile() {
		return this.file;
	}
	
	/**
	 *
	 * @return The prizes in the crate.
	 */
	public ArrayList<Prize> getPrizes() {
		return this.prizes;
	}
	
	/**
	 *
	 * @param name Name of the prize you want.
	 * @return The prize you asked for.
	 */
	public Prize getPrize(String name) {
		for(Prize prize : prizes) {
			if(prize.getName().equalsIgnoreCase(name)) {
				return prize;
			}
		}
		return null;
	}
	
	public void addEditorItem(String prize, ItemStack item) {
		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(item);
		String path = "Crate.Prizes." + prize;
		if(file.contains(path + ".Editor-Items")) {
			for(Object list : file.getList(path + ".Editor-Items")) {
				items.add((ItemStack) list);
			}
		}
		if(!file.contains(path + ".DisplayName")) file.set(path + ".DisplayName", "&7Auto Generated Prize #&6" + prize);
		if(!file.contains(path + ".DisplayItem")) file.set(path + ".DisplayItem", "STAINED_GLASS_PANE:14");
		if(!file.contains(path + ".DisplayAmount")) file.set(path + ".DisplayAmount", 1);
		if(!file.contains(path + ".Lore")) file.set(path + ".Lore", new ArrayList<>());
		if(!file.contains(path + ".MaxRange")) file.set(path + ".MaxRange", 100);
		if(!file.contains(path + ".Chance")) file.set(path + ".Chance", 50);
		if(!file.contains(path + ".Firework")) file.set(path + ".Firework", false);
		if(!file.contains(path + ".Glowing")) file.set(path + ".Glowing", false);
		if(!file.contains(path + ".Player")) file.set(path + ".Player", "");
		if(!file.contains(path + ".Unbreakable")) file.set(path + ".Unbreakable", false);
		if(!file.contains(path + ".Items")) file.set(path + ".Items", new ArrayList<>());
		file.set(path + ".Editor-Items", items);
		if(!file.contains(path + ".Commands")) file.set(path + ".Commands", new ArrayList<>());
		if(!file.contains(path + ".Messages")) file.set(path + ".Messages", new ArrayList<>());
		if(!file.contains(path + ".BlackListed-Permissions")) file.set(path + ".BlackListed-Permissions", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Toggle")) file.set(path + ".Alternative-Prize.Toggle", false);
		if(!file.contains(path + ".Alternative-Prize.Commands")) file.set(path + ".Alternative-Prize.Commands", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Items")) file.set(path + ".Alternative-Prize.Items", new ArrayList<>());
		if(!file.contains(path + ".Alternative-Prize.Messages")) file.set(path + ".Alternative-Prize.Messages", new ArrayList<>());
		fileManager.saveFile(fileManager.getFile(name));
	}
	
	/**
	 * Loads all the preview items and puts them into a list.
	 * @return A list of all the preview items that were created.
	 */
	private ArrayList<ItemStack> loadPreview() {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(Prize prize : getPrizes()) {
			items.add(prize.getDisplayItem());
		}
		return items;
	}
	
}