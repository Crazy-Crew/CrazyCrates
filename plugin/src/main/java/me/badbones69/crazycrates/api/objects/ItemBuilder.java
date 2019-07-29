package me.badbones69.crazycrates.api.objects;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.multisupport.SkullCreator;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * The ItemBuilder is designed to make creating items easier by creating an easy to use Builder.
 * This will allow you to covert an existing ItemStack into an ItemBuilder to allow you to edit 
 * an existing ItemStack or make a new ItemStack from scratch.
 *
 * @author BadBones69
 *
 */
public class ItemBuilder {
	
	private Material material;
	private int damage;
	private String name;
	private List<String> lore;
	private int amount;
	private String crateName;
	private String player;
	private boolean isHash;
	private boolean isURL;
	private boolean isHead;
	private HashMap<Enchantment, Integer> enchantments;
	private boolean unbreakable;
	private boolean hideItemFlags;
	private boolean glowing;
	private ItemStack referenceItem;
	private boolean isMobEgg;
	private EntityType entityType;
	private HashMap<String, String> namePlaceholders;
	private HashMap<String, String> lorePlaceholders;
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	/**
	 * The initial starting point for making an item.
	 */
	public ItemBuilder() {
		this.material = Material.STONE;
		this.damage = 0;
		this.name = "";
		this.crateName = "";
		this.lore = new ArrayList<>();
		this.amount = 1;
		this.player = "";
		this.isHash = false;
		this.isURL = false;
		this.isHead = false;
		this.enchantments = new HashMap<>();
		this.unbreakable = false;
		this.hideItemFlags = false;
		this.glowing = false;
		this.entityType = EntityType.BAT;
		this.isMobEgg = false;
		this.namePlaceholders = new HashMap<>();
		this.lorePlaceholders = new HashMap<>();
	}
	
	/**
	 * Convert an ItemStack to an ItemBuilder to allow easier editing of the ItemStack.
	 * @param item The ItemStack you wish to convert into an ItemBuilder.
	 * @return The ItemStack as an ItemBuilder with all the info from the item.
	 */
	public static ItemBuilder convertItemStack(ItemStack item) {
		ItemBuilder itemBuilder = new ItemBuilder()
		.setReferenceItem(item)
		.setAmount(item.getAmount())
		.setMaterial(item.getType())
		.setEnchantments(new HashMap<>(item.getEnchantments()));
		if(item.hasItemMeta()) {
			ItemMeta itemMeta = item.getItemMeta();
			itemBuilder.setName(itemMeta.getDisplayName())
			.setLore(itemMeta.getLore());
			NBTItem nbt = new NBTItem(item);
			if(nbt.hasKey("Unbreakable")) {
				itemBuilder.setUnbreakable(nbt.getBoolean("Unbreakable"));
			}
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
				if(itemMeta instanceof org.bukkit.inventory.meta.Damageable) {
					itemBuilder.setDamage(((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage());
				}
			}else {
				itemBuilder.setDamage(item.getDurability());
			}
		}
		return itemBuilder;
	}
	
	/**
	 * Get the type of item as a Material the builder is set to.
	 * @return The type of material the builder is set to.
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Set the type of item the builder is set to.
	 * @param material The material you wish to set.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setMaterial(Material material) {
		this.material = material;
		this.isHead = material == (cc.useNewMaterial() ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM"));
		return this;
	}
	
	/**
	 * Set the type of item and its metadata in the builder.
	 * @param material The string must be in this form: %Material% or %Material%:%MetaData%
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setMaterial(String material) {
		if(material.contains(":")) {// Sets the durability.
			String[] b = material.split(":");
			material = b[0];
			this.damage = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(material);
		if(m != null) {// Sets the material.
			this.material = m;
			if(Version.getCurrentVersion().isNewer(Version.v1_8_R3) && Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
				if(m == Material.matchMaterial("MONSTER_EGG")) {
					this.entityType = EntityType.fromId(damage);
					this.damage = 0;
					this.isMobEgg = true;
				}
			}
		}
		this.isHead = this.material == (cc.useNewMaterial() ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM"));
		return this;
	}
	
	/**
	 * Get the damage of the item.
	 * @return The damage of the item as an int.
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * Set the items damage value.
	 * @param damage The damage value of the item.
	 */
	public ItemBuilder setDamage(int damage) {
		this.damage = damage;
		return this;
	}
	
	/**
	 * Get the name the of the item in the builder.
	 * @return The name as a string that is already been color converted.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the item in the builder. This will auto force color the name if it contains color code. (&a, &c, &7, etc...)
	 * @param name The name of the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setName(String name) {
		if(name != null) {
			this.name = color(name);
		}
		return this;
	}
	
	/**
	 * Set the placeholders for the name of the item.
	 * @param placeholders The placeholders that will be used.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setNamePlaceholders(HashMap<String, String> placeholders) {
		this.namePlaceholders = placeholders;
		return this;
	}
	
	/**
	 * Add a placeholder to the name of the item.
	 * @param placeholder The placeholder that will be replaced.
	 * @param argument The argument you wish to replace the placeholder with.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
		this.namePlaceholders.put(placeholder, argument);
		return this;
	}
	
	/**
	 * Remove a placeholder from the list.
	 * @param placeholder The placeholder you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeNamePlaceholder(String placeholder) {
		this.namePlaceholders.remove(placeholder);
		return this;
	}
	
	/**
	 * Get the item's name with all the placeholders added to it.
	 * @return The name with all the placeholders in it.
	 */
	public String getUpdatedName() {
		String newName = name;
		for(String placeholder : lorePlaceholders.keySet()) {
			newName = newName.replace(placeholder, lorePlaceholders.get(placeholder));
		}
		return newName;
	}
	
	/**
	 * Get the lore of the item in the builder.
	 * @return The lore of the item in the builder. This will already be color coded.
	 */
	public List<String> getLore() {
		return lore;
	}
	
	/**
	 * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
	 * @param lore The lore of the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setLore(List<String> lore) {
		if(lore != null) {
			this.lore.clear();
			for(String i : lore) {
				this.lore.add(color(i));
			}
		}
		return this;
	}
	
	/**
	 * Add a line to the current lore of the item. This will auto force color in the lore that contains color code. (&a, &c, &7, etc...)
	 * @param lore The new line you wish to add.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addLore(String lore) {
		if(lore != null) {
			this.lore.add(color(lore));
		}
		return this;
	}
	
	/**
	 * Set the placeholders that are in the lore of the item.
	 * @param placeholders The placeholders that you wish to use.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setLorePlaceholders(HashMap<String, String> placeholders) {
		this.lorePlaceholders = placeholders;
		return this;
	}
	
	/**
	 * Add a placeholder to the lore of the item.
	 * @param placeholder The placeholder you wish to replace.
	 * @param argument The argument that will replace the placeholder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
		this.lorePlaceholders.put(placeholder, argument);
		return this;
	}
	
	/**
	 * Remove a placeholder from the lore.
	 * @param placeholder The placeholder you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeLorePlaceholder(String placeholder) {
		this.lorePlaceholders.remove(placeholder);
		return this;
	}
	
	/**
	 * Get the lore with all the placeholders added to it.
	 * @return The lore with all placeholders in it.
	 */
	public List<String> getUpdatedLore() {
		List<String> newLore = new ArrayList<>();
		for(String i : lore) {
			for(String placeholder : lorePlaceholders.keySet()) {
				i = i.replace(placeholder, lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), lorePlaceholders.get(placeholder));
			}
			newLore.add(i);
		}
		return newLore;
	}
	
	/**
	 * Get the entity type of the mob egg.
	 * @return The EntityType of the mob egg.
	 */
	public EntityType getEntityType() {
		return entityType;
	}
	
	/**
	 * Sets the type of mob egg.
	 * @param entityType The entity type the mob egg will be.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setEntityType(EntityType entityType) {
		this.entityType = entityType;
		return this;
	}
	
	/**
	 * Check if the current item is a mob egg.
	 * @return True if it is and false if not.
	 */
	public boolean isMobEgg() {
		return isMobEgg;
	}
	
	/**
	 * The amount of the item stack in the builder.
	 * @return The amount that is set in the builder.
	 */
	public Integer getAmount() {
		return amount;
	}
	
	/**
	 * Get the amount of the item stack in the builder.
	 * @param amount The amount that is in the item stack.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}
	
	/**
	 * Get the name of the player being used as a head.
	 * @return The name of the player being used on the head.
	 */
	public String getPlayer() {
		return player;
	}
	
	/**
	 * Set the player that will be displayed on the head.
	 * @param player The player being displayed on the head.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setPlayer(String player) {
		this.player = player;
		if(player != null && player.length() > 16) {
			this.isHash = true;
			this.isURL = player.startsWith("http");
		}
		return this;
	}
	
	/**
	 * Check if the item is a player heads.
	 * @return True if it is a player head and false if not.
	 */
	public boolean isHead() {
		return isHead;
	}
	
	/**
	 * Check if the player name is a Base64.
	 * @return True if it is a Base64 and false if not.
	 */
	public boolean isHash() {
		return isHash;
	}
	
	/**
	 * Check if the hash is a url or a Base64.
	 * @return True if it is a url and false if it is a Base64.
	 */
	public boolean isURL() {
		return isURL;
	}
	
	public ItemBuilder setCrateName(String crateName) {
		this.crateName = crateName;
		return this;
	}
	
	public String getCrateName() {
		return crateName;
	}
	
	/**
	 * Get the enchantments that are on the item in the builder.
	 * @return The enchantments that are on the item in the builder.
	 */
	public HashMap<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}
	
	/**
	 * Set a list of enchantments that will go onto the item in the builder. These can have unsafe levels.
	 * It will also override any enchantments used in the "ItemBuilder#addEnchantment()" method.
	 * @param enchantments A list of enchantments that will go onto the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantments) {
		if(enchantments != null) {
			this.enchantments = enchantments;
		}
		return this;
	}
	
	/**
	 * Add an enchantment to the item in the builder.
	 * @param enchantment The enchantment you wish to add.
	 * @param level The level of the enchantment. This can be unsafe levels.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addEnchantments(Enchantment enchantment, Integer level) {
		this.enchantments.put(enchantment, level);
		return this;
	}
	
	/**
	 * Remove an enchantment from the item in the builder.
	 * @param enchantment The enchantment you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeEnchantments(Enchantment enchantment) {
		this.enchantments.remove(enchantment);
		return this;
	}
	
	/**
	 * Check if the item in the builder is unbreakable.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean isUnbreakable() {
		return unbreakable;
	}
	
	/**
	 * Set if the item in the builder to be unbreakable or not.
	 * @param unbreakable True will set it to be unbreakable and false will make it able to take damage.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}
	
	/**
	 * Set if the item should hide item flags or not
	 * @param hideItemFlags true the item will hide item flags. false will show them.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder hideItemFlags(boolean hideItemFlags) {
		this.hideItemFlags = hideItemFlags;
		return this;
	}
	
	/**
	 * Check if the item in the builder has hidden item flags.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean areItemFlagsHidden() {
		return hideItemFlags;
	}
	
	/**
	 * Check if the item in the builder is glowing.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean isGlowing() {
		return glowing;
	}
	
	/**
	 * Set if the item in the builder to be glowing or not.
	 * @param glowing True will set the item to have a glowing effect.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setGlowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}
	
	/**
	 * Builder the item from all the information that was given to the builder.
	 * @return The result of all the info that was given to the builder as an ItemStack.
	 */
	public ItemStack build() {
		ItemStack item = referenceItem != null ? referenceItem : new ItemStack(material, amount);
		if(item.getType() != Material.AIR) {
			if(isHead) {//Has to go 1st due to it removing all data when finished.
				if(isHash) {//Sauce: https://github.com/deanveloper/SkullCreator
					if(isURL) {
						SkullCreator.itemWithUrl(item, player);
					}else {
						SkullCreator.itemWithBase64(item, player);
					}
				}
			}
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(getUpdatedName());
			itemMeta.setLore(getUpdatedLore());
			if(Version.getCurrentVersion().isNewer(Version.v1_10_R1)) {
				itemMeta.setUnbreakable(unbreakable);
			}
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
				if(itemMeta instanceof org.bukkit.inventory.meta.Damageable) {
					((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(damage);
				}
			}else {
				item.setDurability((short) damage);
			}
			item.setItemMeta(itemMeta);
			hideFlags(item);
			item.addUnsafeEnchantments(enchantments);
			addGlow(item);
			NBTItem nbt = new NBTItem(item);
			if(isHead) {
				if(!isHash && player != null && !player.equals("")) {
					nbt.setString("SkullOwner", player);
				}
			}
			if(isMobEgg) {
				nbt.addCompound("EntityTag").setString("id", "minecraft:" + entityType.name());
			}
			if(Version.getCurrentVersion().isOlder(Version.v1_11_R1)) {
				if(unbreakable) {
					nbt.setBoolean("Unbreakable", true);
					nbt.setInteger("HideFlags", 4);
				}
			}
			if(!crateName.isEmpty()) {
				nbt.setString("CrazyCrates-Crate", crateName);
			}
			return nbt.getItem();
		}else {
			return item;
		}
	}
	
	/**
	 * Sets the converted item as a reference to try and save NBT tags and stuff.
	 * @param referenceItem The item that is being referenced.
	 * @return The ItemBuilder with updated info.
	 */
	private ItemBuilder setReferenceItem(ItemStack referenceItem) {
		this.referenceItem = referenceItem;
		return this;
	}
	
	private String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	private ItemStack hideFlags(ItemStack item) {
		if(hideItemFlags) {
			if(item != null && item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addItemFlags(ItemFlag.values());
				item.setItemMeta(itemMeta);
				return item;
			}
		}
		return item;
	}
	
	private ItemStack addGlow(ItemStack item) {
		if(glowing) {
			try {
				if(item != null) {
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasEnchants()) {
							return item;
						}
					}
					item.addUnsafeEnchantment(Enchantment.LUCK, 1);
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
				return item;
			}catch(NoClassDefFoundError e) {
				return item;
			}
		}
		return item;
	}
	
}