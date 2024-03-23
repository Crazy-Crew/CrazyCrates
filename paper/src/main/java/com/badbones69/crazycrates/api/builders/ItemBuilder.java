package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.support.PluginSupport;
import com.badbones69.crazycrates.support.SkullCreator;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ryderbelserion.cluster.utils.DyeUtils;
import io.th0rgal.oraxen.api.OraxenItems;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.nbt.TagParser;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ItemBuilder {

    @NotNull
    private final CrazyCratesPaper plugin = CrazyCratesPaper.get();

    // Items
    private Material material = Material.STONE;
    private ItemStack itemStack = null;
    private int itemAmount = 1;

    // NBT Data
    private String itemData = "";

    // Display
    private String displayName = "";
    private List<String> displayLore = new ArrayList<>();
    private int itemDamage = 0;

    // Model Data
    private boolean hasCustomModelData = false;
    private int customModelData = 0;
    private String customMaterial = "";

    // Potions
    private boolean isPotion = false;
    private Color potionColor = Color.RED;
    private PotionEffectType potionType = null;
    private int potionDuration = -1;
    private int potionAmplifier = 1;

    // Player Heads
    private boolean isHash = false;
    private boolean isURL = false;
    private boolean isHead = false;

    private String player = "";

    // Arrows
    private boolean isTippedArrow = false;

    // Armor
    private boolean isLeatherArmor = false;
    private boolean isArmor = false;

    // Trims
    private TrimMaterial trimMaterial = null;
    private TrimPattern trimPattern = null;
    private Color armorColor = Color.RED;

    // Banners
    private boolean isBanner = false;
    private List<Pattern> patterns = new ArrayList<>();

    // Shields
    private boolean isShield = false;

    // Maps
    private boolean isMap = false;
    private Color mapColor = Color.RED;

    // Fireworks
    private boolean isFirework = false;
    private boolean isFireworkStar = false;
    private Color fireworkColor = Color.RED;
    private List<Color> fireworkColors = new ArrayList<>();
    private int fireworkPower = 1;

    // Enchantments or ItemFlags
    private boolean isUnbreakable = false;

    private boolean hideItemFlags = false;
    private List<ItemFlag> itemFlags = new ArrayList<>();

    private boolean isGlowing = false;

    private boolean isSpawner = false;
    private EntityType entityType = EntityType.BAT;

    // Crates
    private String crateName = "";
    private Player target = null;

    // Placeholders
    private Map<String, String> namePlaceholders = new HashMap<>();
    private Map<String, String> lorePlaceholders = new HashMap<>();

    /**
     * Deduplicate an item builder.
     *
     * @param itemBuilder the item builder to deduplicate.
     */
    public ItemBuilder(ItemBuilder itemBuilder) {
        this.target = itemBuilder.target;

        this.material = itemBuilder.material;
        this.itemStack = itemBuilder.itemStack;

        this.customMaterial = itemBuilder.customMaterial;

        this.itemAmount = itemBuilder.itemAmount;
        this.itemData = itemBuilder.itemData;

        this.displayName = itemBuilder.displayName;
        this.displayLore = itemBuilder.displayLore;
        this.itemDamage = itemBuilder.itemDamage;

        this.hasCustomModelData = itemBuilder.hasCustomModelData;
        this.customModelData = itemBuilder.customModelData;

        this.isPotion = itemBuilder.isPotion;
        this.potionColor = itemBuilder.potionColor;
        this.potionType = itemBuilder.potionType;
        this.potionDuration = itemBuilder.potionDuration;
        this.potionAmplifier = itemBuilder.potionAmplifier;

        this.isHead = itemBuilder.isHead;
        this.isHash = itemBuilder.isHash;
        this.isURL = itemBuilder.isURL;
        this.player = itemBuilder.player;

        this.isTippedArrow = itemBuilder.isTippedArrow;

        this.isLeatherArmor = itemBuilder.isLeatherArmor;
        this.isArmor = itemBuilder.isArmor;

        this.trimMaterial = itemBuilder.trimMaterial;
        this.trimPattern = itemBuilder.trimPattern;
        this.armorColor = itemBuilder.armorColor;

        this.isBanner = itemBuilder.isBanner;
        this.patterns = itemBuilder.patterns;

        this.isShield = itemBuilder.isShield;

        this.isMap = itemBuilder.isMap;
        this.mapColor = itemBuilder.mapColor;

        this.isFirework = itemBuilder.isFirework;
        this.isFireworkStar = itemBuilder.isFireworkStar;
        this.fireworkColor = itemBuilder.fireworkColor;
        this.fireworkColors = itemBuilder.fireworkColors;
        this.fireworkPower = itemBuilder.fireworkPower;

        this.isUnbreakable = itemBuilder.isUnbreakable;

        this.hideItemFlags = itemBuilder.hideItemFlags;
        this.itemFlags = itemBuilder.itemFlags;

        this.isGlowing = itemBuilder.isGlowing;

        this.isSpawner = itemBuilder.isSpawner;
        this.entityType = itemBuilder.entityType;

        this.namePlaceholders = new HashMap<>(itemBuilder.namePlaceholders);
        this.lorePlaceholders = new HashMap<>(itemBuilder.lorePlaceholders);

        this.crateName = itemBuilder.crateName;
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;

        this.material = itemStack.getType();

        switch (this.material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> this.isLeatherArmor = true;
            case POTION, SPLASH_POTION, LINGERING_POTION -> this.isPotion = true;
            case FIREWORK_STAR -> this.isFireworkStar = true;
            case TIPPED_ARROW -> this.isTippedArrow = true;
            case FIREWORK_ROCKET -> this.isFirework = true;
            case FILLED_MAP -> this.isMap = true;
            case PLAYER_HEAD -> this.isHead = true;
            case SPAWNER -> this.isSpawner = true;
            case SHIELD -> this.isShield = true;
        }

        this.itemStack.editMeta(itemMeta -> {
            if (itemMeta.hasDisplayName()) this.displayName = itemMeta.getDisplayName();

            if (itemMeta.hasLore()) this.displayLore = itemMeta.getLore();
        });

        String name = this.material.name();

        this.isArmor = name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");

        this.isBanner = name.endsWith("BANNER");
    }

    public ItemBuilder(ItemStack itemStack, Player target) {
        this.target = target;

        this.itemStack = itemStack;

        this.material = itemStack.getType();

        switch (this.material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> this.isLeatherArmor = true;
            case POTION, SPLASH_POTION, LINGERING_POTION -> this.isPotion = true;
            case FIREWORK_STAR -> this.isFireworkStar = true;
            case TIPPED_ARROW -> this.isTippedArrow = true;
            case FIREWORK_ROCKET -> this.isFirework = true;
            case FILLED_MAP -> this.isMap = true;
            case PLAYER_HEAD -> this.isHead = true;
            case SPAWNER -> this.isSpawner = true;
            case SHIELD -> this.isShield = true;
        }

        this.itemStack.editMeta(itemMeta -> {
            if (itemMeta.hasDisplayName()) this.displayName = itemMeta.getDisplayName();

            if (itemMeta.hasLore()) this.displayLore = itemMeta.getLore();
        });

        String name = this.material.name();

        this.isArmor = name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");

        this.isBanner = name.endsWith("BANNER");
    }

    public ItemBuilder() {}

    private String parse(String message) {
        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled() && this.target != null) {
            return MsgUtils.color(PlaceholderAPI.setPlaceholders(this.target, message));
        }

        return MsgUtils.color(message);
    }

    /**
     * Builder the item from all the information that was given to the builder.
     *
     * @return the result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {
        // Check if oraxen is enabled.
        if (PluginSupport.ORAXEN.isPluginEnabled()) {
            // Get the item.
            io.th0rgal.oraxen.items.ItemBuilder oraxenItem = OraxenItems.getItemById(this.customMaterial);

            if (oraxenItem != null) {
                // If the item isn't null, we don't need to re-build.
                if (this.itemStack != null) {
                    this.material = this.itemStack.getType();

                    return this.itemStack;
                }

                // This is just here in case it is null for whatever reason.
                this.itemStack = oraxenItem.build();

                this.material = this.itemStack.getType();

                return this.itemStack;
            }
        }

        if (this.isHead) { // Has to go 1st due to it removing all data when finished.
            if (this.isHash) { // Sauce: https://github.com/deanveloper/SkullCreator
                if (this.isURL) {
                    this.itemStack = SkullCreator.itemWithUrl(this.itemStack, this.player);
                } else {
                    this.itemStack = SkullCreator.itemWithBase64(this.itemStack, this.player);
                }
            }
        }

        if (this.itemStack.getType() != Material.AIR) {
            // If item data is not empty. We ignore all other options and simply return.
            if (!this.itemData.isEmpty()) {
                net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItemStack());

                try {
                    nmsItem.setTag(TagParser.parseTag(this.itemData));
                } catch (CommandSyntaxException exception) {
                    this.plugin.getLogger().log(Level.WARNING, "Failed to set nms tag.", exception);
                }

                return CraftItemStack.asBukkitCopy(nmsItem);
            }

            getItemStack().setAmount(this.itemAmount);

            getItemStack().editMeta(itemMeta -> {
                // If the item is able to be damaged.
                if (itemMeta instanceof Damageable damageable) {
                    if (this.itemDamage >= 1) {
                        if (this.itemDamage >= this.material.getMaxDurability()) {
                            damageable.setDamage(this.material.getMaxDurability());
                        } else {
                            damageable.setDamage(this.itemDamage);
                        }
                    }
                }

                if (this.isArmor) {
                    if (this.trimPattern != null && this.trimMaterial != null) {
                        ArmorMeta armorMeta = (ArmorMeta) itemMeta;

                        armorMeta.setTrim(new ArmorTrim(this.trimMaterial, this.trimPattern));
                    }
                }

                if (this.isMap) {
                    MapMeta mapMeta = (MapMeta) itemMeta;

                    mapMeta.setScaling(true);

                    if (this.mapColor != null) mapMeta.setColor(this.mapColor);
                }

                // Check if is potion and apply potion related settings.
                if (this.isPotion || this.isTippedArrow) {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;

                    // Single potion effect.
                    if (this.potionType != null) {
                        PotionEffect effect = new PotionEffect(this.potionType, this.potionDuration, this.potionAmplifier);

                        potionMeta.addCustomEffect(effect, true);

                        potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(effect.getType().getName())));
                    }

                    if (this.potionColor != null) {
                        potionMeta.setColor(this.potionColor);
                    }
                }

                if (this.isLeatherArmor && this.armorColor != null) {
                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;

                    leatherArmorMeta.setColor(this.armorColor);
                }

                if (this.isBanner && !this.patterns.isEmpty()) {
                    BannerMeta bannerMeta = (BannerMeta) itemMeta;
                    bannerMeta.setPatterns(this.patterns);
                }

                if (this.isShield && !this.patterns.isEmpty()) {
                    BlockStateMeta shieldMeta = (BlockStateMeta) itemMeta;

                    Banner banner = (Banner) shieldMeta.getBlockState();
                    banner.setPatterns(this.patterns);
                    banner.update();

                    shieldMeta.setBlockState(banner);
                }

                // If the item has model data.
                if (this.hasCustomModelData) {
                    itemMeta.setCustomModelData(this.customModelData);
                }

                this.itemFlags.forEach(itemMeta::addItemFlags);

                if (this.hideItemFlags) {
                    itemMeta.addItemFlags(ItemFlag.values());
                }

                itemMeta.setUnbreakable(this.isUnbreakable);

                if (this.isGlowing) {
                    if (!itemMeta.hasEnchants()) {
                        itemMeta.addEnchant(Enchantment.LUCK, 1, false);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                }

                itemMeta.setDisplayName(getUpdatedName());
                itemMeta.setLore(getUpdatedLore());

                if (!this.crateName.isBlank() || !this.crateName.isEmpty()) {
                    PersistentDataContainer container = itemMeta.getPersistentDataContainer();

                    container.set(PersistentKeys.crate_key.getNamespacedKey(), PersistentDataType.STRING, crateName);
                }
            });
        } else {
            if (MiscUtils.isLogging()) {
                Logger logger = this.plugin.getLogger();

                logger.warning("Material cannot be of type AIR or null, If you see this.");
                logger.warning("in your console but do not have any invalid items. You can");
                logger.warning("ignore this as we use AIR for some niche cases internally.");
            }
        }

        return getItemStack();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ItemMeta getItemMeta() {
        return this.itemStack.getItemMeta();
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    /**
     * Set a target player if using PlaceholderAPI
     *
     * @param target the target to set.
     * @return the ItemBuilder with updated data.
     */
    public ItemBuilder setTarget(Player target) {
        this.target = target;

        return this;
    }

    /**
     * Get the item's name with all the placeholders added to it.
     *
     * @return The name with all the placeholders in it.
     */
    public String getUpdatedName() {
        String newName = this.displayName;

        for (String placeholder : this.namePlaceholders.keySet()) {
            newName = newName.replace(placeholder, this.namePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.namePlaceholders.get(placeholder));
        }

        return parse(newName);
    }

    /**
     * @return the name of the item.
     */
    public String getName() {
        return this.displayName;
    }

    /**
     * @return the material of the item.
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Sets the material.
     *
     * @param material the material to use.
     * @return the ItemBuilder with updated data.
     */
    public ItemBuilder setMaterial(Material material) {
        this.material = material;

        if (this.itemStack == null) {
            // If item stack is null, we create new item stack based on material.
            this.itemStack = new ItemStack(this.material);
        } else {
            // Get old item meta.
            ItemMeta itemMeta = this.itemStack.getItemMeta();

            // Create new itemstack.
            ItemStack newItemStack = new ItemStack(this.material);
            // Set old item meta to new itemstack.
            newItemStack.setItemMeta(itemMeta);

            // Overwrite old item stack with new item stack.
            this.itemStack = newItemStack;
        }

        this.isHead = material == Material.PLAYER_HEAD;

        return this;
    }

    /**
     * Set trim material
     *
     * @param trimMaterial pattern to set.
     */
    public void setTrimMaterial(TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    /**
     * Set trim pattern
     *
     * @param trimPattern pattern to set.
     */
    public void setTrimPattern(TrimPattern trimPattern) {
        this.trimPattern = trimPattern;
    }

    /**
     * Set the type of item and its metadata in the builder.
     *
     * @param type the string must be in this form: %Material% or %Material%:%MetaData%
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(String type) {
        if (type == null || type.isEmpty()) {
            List.of(
                    "Material cannot be null or empty, Output: " + type + ".",
                    "Please take a screenshot of this before asking for support."
            ).forEach(line -> {
                if (MiscUtils.isLogging()) this.plugin.getLogger().warning(line);
            });

            this.itemStack = new ItemStack(Material.STONE);
            this.itemStack.editMeta(itemMeta -> itemMeta.setDisplayName(parse("&cAn error has occurred with the item builder.")));

            this.material = this.itemStack.getType();

            return this;
        }

        this.customMaterial = type;

        String metaData;

        if (type.contains(":")) {
            String[] section = type.split(":");

            type = section[0];
            metaData = section[1];

            if (metaData.contains("#")) {
                String modelData = metaData.split("#")[1];

                if (isInt(modelData)) {
                    this.hasCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }

            metaData = metaData.replace("#" + this.customModelData, "");

            if (isInt(metaData)) {
                this.itemDamage = Integer.parseInt(metaData);
            } else {
                this.potionType = getPotionType(PotionEffectType.getByName(metaData)).getEffectType();

                this.potionColor = DyeUtils.getColor(metaData);
                this.armorColor = DyeUtils.getColor(metaData);
                this.mapColor = DyeUtils.getColor(metaData);
                this.fireworkColor = DyeUtils.getColor(metaData);
            }
        } else if (type.contains("#")) {
            String[] section = type.split("#");
            type = section[0];

            String modelData = section[1];

            if (isInt(modelData)) {
                this.hasCustomModelData = true;
                this.customModelData = Integer.parseInt(modelData);
            }
        }

        Material material = Material.matchMaterial(type);

        if (material != null) {
            this.itemStack = new ItemStack(material);

            this.material = this.itemStack.getType();
        } else {
            if (PluginSupport.ORAXEN.isPluginEnabled()) {
                io.th0rgal.oraxen.items.ItemBuilder oraxenItem = OraxenItems.getItemById(this.customMaterial);

                if (oraxenItem != null) {
                    this.itemStack = oraxenItem.build();

                    this.material = this.itemStack.getType();

                    return this;
                }
            }
        }

        switch (this.material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> this.isLeatherArmor = true;
            case POTION, SPLASH_POTION, LINGERING_POTION -> this.isPotion = true;
            case FIREWORK_STAR -> this.isFireworkStar = true;
            case TIPPED_ARROW -> this.isTippedArrow = true;
            case FIREWORK_ROCKET -> this.isFirework = true;
            case FILLED_MAP -> this.isMap = true;
            case PLAYER_HEAD -> this.isHead = true;
            case SPAWNER -> this.isSpawner = true;
            case SHIELD -> this.isShield = true;
        }

        String name = this.material.name();

        this.isArmor = name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");

        this.isBanner = name.endsWith("BANNER");

        return this;
    }

    /**
     * Sets the crate name
     *
     * @param crateName the crate name to set.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setCrateName(String crateName) {
        this.crateName = crateName;

        return this;
    }

    /**
     * @param damage the damage value of the item.
     */
    public void setDamage(int damage) {
        this.itemDamage = damage;
    }

    /**
     * Get the damage to the item.
     *
     * @return the damage to the item as an int.
     */
    public int getDamage() {
        return this.itemDamage;
    }

    /**
     * @param itemName the name of the item.
     * @return the ItemBuilder with an updated name.
     */
    public ItemBuilder setName(String itemName) {
        if (itemName != null) {
            this.displayName = itemName;
        }

        return this;
    }

    /**
     * @param placeholders the placeholders that will be used.
     * @return the ItemBuilder with updated placeholders.
     */
    public ItemBuilder setNamePlaceholders(Map<String, String> placeholders) {
        this.namePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the name of the item.
     *
     * @param placeholder the placeholder that will be replaced.
     * @param argument the argument you wish to replace the placeholder with.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
        this.namePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Remove a placeholder from the list.
     *
     * @param placeholder the placeholder you wish to remove.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder removeNamePlaceholder(String placeholder) {
        this.namePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
     *
     * @param lore the lore of the item in the builder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setLore(List<String> lore) {
        if (lore != null) {
            this.displayLore.clear();

            for (String line : lore) {
                this.displayLore.add(MsgUtils.color(line));
            }
        }

        return this;
    }

    /**
     * Set the lore of the item with papi support in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
     *
     * @param player the player viewing the button.
     * @param lore the lore of the item in the builder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setLore(Player player, List<String> lore) {
        if (lore != null) {
            this.displayLore.clear();

            for (String line : lore) {
                this.displayLore.add(PlaceholderAPI.setPlaceholders(player, MsgUtils.color(line)));
            }
        }

        return this;
    }

    /**
     * Add a line to the current lore of the item. This will auto force color in the lore that contains color code. (&a, &c, &7, etc...)
     *
     * @param lore the new line you wish to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addLore(String lore) {
        if (lore != null) this.displayLore.add(MsgUtils.color(lore));

        return this;
    }

    /**
     * Set the placeholders that are in the lore of the item.
     *
     * @param placeholders the placeholders that you wish to use.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setLorePlaceholders(Map<String, String> placeholders) {
        this.lorePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the lore of the item.
     *
     * @param placeholder the placeholder you wish to replace.
     * @param argument the argument that will replace the placeholder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
        this.lorePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Get the lore with all the placeholders added to it.
     *
     * @return the lore with all placeholders in it.
     */
    public List<String> getUpdatedLore() {
        List<String> newLore = new ArrayList<>();

        for (String item : this.displayLore) {
            for (String placeholder : this.lorePlaceholders.keySet()) {
                item = item.replace(placeholder, this.lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.lorePlaceholders.get(placeholder));
            }

            newLore.add(parse(item));
        }

        return newLore;
    }

    /**
     * Remove a placeholder from the lore.
     *
     * @param placeholder the placeholder you wish to remove.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder removeLorePlaceholder(String placeholder) {
        this.lorePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * @param entityType the entity type the mob spawn egg will be.
     * @return the ItemBuilder with an updated mob spawn egg.
     */
    public ItemBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    /**
     * @param patterns the list of Patterns to add.
     * @return the ItemBuilder with updated patterns.
     */
    public ItemBuilder addPatterns(List<String> patterns) {
        patterns.forEach(this::addPatterns);

        return this;
    }

    /**
     * @param pattern a pattern to add.
     * @return the ItemBuilder with an updated pattern.
     */
    public ItemBuilder addPattern(Pattern pattern) {
        this.patterns.add(pattern);

        return this;
    }

    /**
     * @param patterns set a list of Patterns.
     * @return the ItemBuilder with an updated list of patterns.
     */
    public ItemBuilder setPattern(List<Pattern> patterns) {
        this.patterns = patterns;

        return this;
    }

    /**
     * @param amount the amount of the item stack.
     * @return the ItemBuilder with an updated item count.
     */
    public ItemBuilder setAmount(Integer amount) {
        this.itemAmount = amount;

        return this;
    }

    /**
     * Set the player that will be displayed on the head.
     *
     * @param playerName the player being displayed on the head.
     * @return the ItemBuilder with an updated Player Name.
     */
    public ItemBuilder setPlayerName(String playerName) {
        this.player = playerName;

        if (this.player != null && this.player.length() > 16) {
            this.isHash = true;
            this.isURL = this.player.startsWith("http");
        }

        return this;
    }

    /**
     * Adds multiple enchantments to an item.
     *
     * @param enchantments the map of enchantments, String/Integer
     * @param unsafeEnchantments if the enchantments are higher than the vanilla defaults.
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments, boolean unsafeEnchantments) {
        enchantments.forEach((enchantment, level) -> addEnchantment(enchantment, level, unsafeEnchantments));

        return this;
    }

    /**
     * Adds a single enchantment to an item.
     *
     * @param enchantment the type of enchantment.
     * @param level the level of the enchantment.
     * @param unsafeEnchantments if the enchantment is higher than the vanilla defaults.
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean unsafeEnchantments) {
        getItemStack().editMeta(itemMeta -> itemMeta.addEnchant(enchantment, level, unsafeEnchantments));

        return this;
    }

    /**
     * Removes an enchantment.
     *
     * @param enchantment the enchantment to remove.
     * @return the ItemBuilder with updated data.
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        getItemStack().editMeta(itemMeta -> itemMeta.removeEnchant(enchantment));

        return this;
    }

    /**
     * Set the flags that will be on the item in the builder.
     *
     * @param flagStrings the flag names as string you wish to add to the item in the builder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setFlagsFromStrings(List<String> flagStrings) {
        this.itemFlags.clear();

        for (String flagString : flagStrings) {
            ItemFlag flag = getFlag(flagString);

            if (flag != null) this.itemFlags.add(flag);
        }

        return this;
    }

    /**
     * Adds a list of item flags to an item.
     *
     * @param flagStrings list of items to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());

                addItemFlag(itemFlag);
            } catch (Exception ignored) {}
        }

        return this;
    }

    /**
     * Add a flag to the item in the builder.
     *
     * @param flagString the name of the flag you wish to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addFlags(String flagString) {
        ItemFlag flag = getFlag(flagString);

        if (flag != null) this.itemFlags.add(flag);

        return this;
    }

    /**
     * Adds an ItemFlag to a map which is added to an item.
     *
     * @param itemFlag the flag to add.
     * @return the ItemBuilder with an updated ItemFlag.
     */
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) this.itemFlags.add(itemFlag);

        return this;
    }

    /**
     * Adds multiple ItemFlags in a list to a map which get added to an item.
     *
     * @param itemFlags the list of flags to add.
     * @return the ItemBuilder with a list of ItemFlags.
     */
    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    /**
     * @param hideItemFlags hide item flags based on a boolean.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;

        return this;
    }

    /**
     * @param unbreakable sets the item to be unbreakable.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.isUnbreakable = unbreakable;

        return this;
    }

    /**
     * @param glow sets whether to make an item to glow or not.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setGlow(boolean glow) {
        this.isGlowing = glow;

        return this;
    }

    /**
     * It will override any enchantments used in ItemBuilder.addEnchantment() below.
     *
     * @param enchantments a list of enchantments to add to the item.
     * @return the ItemBuilder with a list of updated enchantments.
     */
    public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantments, boolean ignoreRestriction) {
        if (enchantments != null) getItemStack().editMeta(meta -> enchantments.forEach((enchantment, amount) -> meta.addEnchant(enchantment, amount, ignoreRestriction)));

        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment the enchantment you wish to add.
     * @param level the level of the enchantment ( Unsafe levels included )
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder addEnchantments(Enchantment enchantment, int level, boolean ignoreRestriction) {
        getItemStack().editMeta(meta -> meta.addEnchant(enchantment, level, ignoreRestriction));

        return this;
    }

    /**
     * Remove an enchantment from the item.
     *
     * @param enchantment the enchantment you wish to remove.
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder removeEnchantments(Enchantment enchantment) {
        getItemStack().editMeta(meta -> {
            meta.removeEnchant(enchantment);
        });

        return this;
    }

    /**
     * Convert an ItemStack to an ItemBuilder to allow easier editing of the ItemStack.
     *
     * @param item the ItemStack you wish to convert into an ItemBuilder.
     * @return the ItemStack as an ItemBuilder with all the info from the item.
     */
    public static ItemBuilder convertItemStack(ItemStack item) {
        return new ItemBuilder(item).setAmount(item.getAmount()).setEnchantments(new HashMap<>(item.getEnchantments()), true);
    }

    public static ItemBuilder convertItemStack(ItemStack item, Player player) {
        return new ItemBuilder(item).setTarget(player).setAmount(item.getAmount()).setEnchantments(new HashMap<>(item.getEnchantments()), true);
    }

    /**
     * Converts a String to an ItemBuilder.
     *
     * @param itemString the string you wish to convert.
     * @return the string as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a string to an ItemBuilder with a placeholder for errors.
     *
     * @param itemString the string you wish to convert.
     * @param placeHolder the placeholder to use if there is an error.
     * @return the string as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString, String placeHolder) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.setMaterial(value);
                    case "name" -> itemBuilder.setName(value);
                    case "amount" -> {
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                    }
                    case "damage" -> {
                        try {
                            itemBuilder.setDamage(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setDamage(0);
                        }
                    }
                    case "lore" -> itemBuilder.setLore(Arrays.asList(value.split(",")));
                    case "player" -> itemBuilder.setPlayerName(value);
                    case "unbreakable-item" -> {
                        if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);
                    }
                    case "trim-pattern" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimPattern(Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    case "trim-material" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimMaterial(Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    default -> {
                        Enchantment enchantment = getEnchantment(option);

                        if (enchantment != null) {
                            try {
                                itemBuilder.addEnchantments(enchantment, Integer.parseInt(value), true);
                            } catch (NumberFormatException e) {
                                itemBuilder.addEnchantments(enchantment, 1, true);
                            }

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);
                                break;
                            }
                        }

                        try {
                            for (PatternType pattern : PatternType.values()) {
                                if (option.equalsIgnoreCase(pattern.name()) || value.equalsIgnoreCase(pattern.getIdentifier())) {
                                    DyeColor color = DyeUtils.getDyeColor(value);
                                    if (color != null) itemBuilder.addPattern(new Pattern(color, pattern));
                                    break;
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor : &c" + (placeHolder != null ? placeHolder : "")));

            CrazyCratesPaper plugin = CrazyCratesPaper.get();
            plugin.getLogger().log(Level.WARNING, "An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders.
     *
     * @param itemStrings the list of Strings.
     * @return the list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders with a placeholder for errors.
     *
     * @param itemStrings the list of strings.
     * @param placeholder the placeholder for errors.
     * @return the list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String placeholder) {
        return itemStrings.stream().map(itemString -> convertString(itemString, placeholder)).collect(Collectors.toList());
    }

    /**
     * Get the PotionEffect from a PotionEffectType.
     *
     * @param type the type of the potion effect.
     * @return the potion type.
     */
    private PotionType getPotionType(PotionEffectType type) {
        if (type != null) {
            if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                return PotionType.FIRE_RESISTANCE;
            } else if (type.equals(PotionEffectType.HARM)) {
                return PotionType.INSTANT_DAMAGE;
            } else if (type.equals(PotionEffectType.HEAL)) {
                return PotionType.INSTANT_HEAL;
            } else if (type.equals(PotionEffectType.INVISIBILITY)) {
                return PotionType.INVISIBILITY;
            } else if (type.equals(PotionEffectType.JUMP)) {
                return PotionType.JUMP;
            } else if (type.equals(PotionEffectType.getByName("LUCK"))) {
                return PotionType.valueOf("LUCK");
            } else if (type.equals(PotionEffectType.NIGHT_VISION)) {
                return PotionType.NIGHT_VISION;
            } else if (type.equals(PotionEffectType.POISON)) {
                return PotionType.POISON;
            } else if (type.equals(PotionEffectType.REGENERATION)) {
                return PotionType.REGEN;
            } else if (type.equals(PotionEffectType.SLOW)) {
                return PotionType.SLOWNESS;
            } else if (type.equals(PotionEffectType.SPEED)) {
                return PotionType.SPEED;
            } else if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
                return PotionType.STRENGTH;
            } else if (type.equals(PotionEffectType.WATER_BREATHING)) {
                return PotionType.WATER_BREATHING;
            } else if (type.equals(PotionEffectType.WEAKNESS)) {
                return PotionType.WEAKNESS;
            }
        }

        return null;
    }

    /**
     * Get the enchantment from a string.
     *
     * @param enchantmentName the string of the enchantment.
     * @return the enchantment from the string.
     */
    private static Enchantment getEnchantment(String enchantmentName) {
        enchantmentName = stripEnchantmentName(enchantmentName);

        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) return enchantment;

                Map<String, String> enchantments = getEnchantmentList();

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                        stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) return enchantment;
            } catch (Exception ignore) {}
        }

        return null;
    }

    /**
     * Strip extra characters from an enchantment name.
     *
     * @param enchantmentName the enchantment name.
     * @return the stripped enchantment name.
     */
    private static String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }

    /**
     * Get the list of enchantments and their in-Game names.
     *
     * @return the hashmap of enchantments and their in-game names.
     */
    private static Map<String, String> getEnchantmentList() {
        Map<String, String> enchantments = new HashMap<>();

        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        enchantments.put("SWEEPING_EDGE", "Sweeping_Edge");
        enchantments.put("RIPTIDE", "Riptide");
        enchantments.put("CHANNELING", "Channeling");
        enchantments.put("IMPALING", "Impaling");
        enchantments.put("LOYALTY", "Loyalty");

        return enchantments;
    }

    /**
     * Check if the parameter is an integer.
     *
     * @param value string to check.
     * @return true if integer or false if not.
     */
    private boolean isInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * Get an item flag from string.
     *
     * @param flagString string to check.
     * @return item flag or null.
     */
    private ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }

        return null;
    }

    /**
     * Add patterns to the item.
     *
     * @param stringPattern the pattern you wish to add.
     */
    private void addPatterns(String stringPattern) {
        try {
            String[] split = stringPattern.split(":");

            for (PatternType pattern : PatternType.values()) {

                if (split[0].equalsIgnoreCase(pattern.name()) || split[0].equalsIgnoreCase(pattern.getIdentifier())) {
                    DyeColor color = DyeUtils.getDyeColor(split[1]);

                    if (color != null) addPattern(new Pattern(color, pattern));

                    break;
                }
            }
        } catch (Exception ignored) {}
    }
}