package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prize {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    private List<ItemStack> items = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();
    private ItemBuilder displayItem = new ItemBuilder();
    private List<String> commands = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private boolean firework = false;
    private String crateName = "";
    private String prizeName = "";
    private ItemStack itemStack;
    private int maxRange = 100;
    private int chance = 0;

    private List<Tier> tiers;
    private List<ItemBuilder> itemBuilders;
    private Prize alternativePrize;

    public Prize(ConfigurationSection section, String crateName) {
        this.crateName = crateName;

        List<?> list = section.getList("Editor-Items") == null ? section.getList("Editor-Items") : Collections.emptyList();

        if (list != null) {
            for (Object key : list) {
                this.items.add((ItemStack) key);
            }
        }

        this.displayItem = display(section);

        this.prizeName = section.getString("DisplayName", "&4Name not found.exe!");
        this.maxRange = section.getInt("MaxRange", 100);
        this.chance = section.getInt("Chance", 100);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.getStringList("Messages");
        this.commands = section.getStringList("Commands");

        this.permissions = section.getStringList("BlackListed-Permissions");
        this.permissions.replaceAll(String::toLowerCase);
    }
    
    /**
     * @param tiers The tiers the prize is in.
     * @param alternativePrize The alternative prize that is won if the player has a blacklist permission.
     */
    public Prize(List<ItemBuilder> itemBuilders, List<Tier> tiers, Prize alternativePrize) {
        this.itemBuilders = itemBuilders != null ? itemBuilders : new ArrayList<>();

        this.tiers = tiers != null ? tiers : new ArrayList<>();

        this.alternativePrize = alternativePrize;
    }

    /**
     * Create a new prize.
     * This option is used only for Alternative Prizes.
     * @param name The name of the prize.
     * @param messages The messages it sends to the player that wins it.
     * @param commands The commands that run when the prize is won.
     * @param items The ItemStacks that are given to the player that wins.
     */
    public Prize(String name, List<String> messages, List<String> commands, List<ItemStack> items, List<ItemBuilder> itemBuilders) {
        this.prizeName = name != null ? name : "&4No name Found!";

        this.crateName = "";

        this.items = items != null ? items : new ArrayList<>();

        this.itemBuilders = itemBuilders != null ? itemBuilders : new ArrayList<>();

        this.chance = 0;

        this.firework = false;

        this.tiers = new ArrayList<>();

        this.messages = messages != null ? messages : new ArrayList<>();
        this.commands = commands != null ? commands : new ArrayList<>();

        this.displayItem = new ItemBuilder();

        this.permissions = new ArrayList<>();

        this.alternativePrize = null;
    }
    
    /**
     * @return returns the name of the prize.
     */
    public String getPrizeName() {
        return this.prizeName;
    }
    
    /**
     * @return returns the display item that is shown for the preview and the winning prize.
     */
    public ItemStack getDisplayItem() {
        if (this.itemStack == null) {
            this.itemStack = this.displayItem.build();

            ItemMeta itemMeta = this.itemStack.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            container.set(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, this.prizeName);

            this.itemStack.setItemMeta(itemMeta);
        }

        return this.itemStack.clone();
    }

    /**
     * Sets the new item stack.
     *
     * @param itemStack new item
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * @return returns the ItemBuilder of the display item.
     */
    public ItemBuilder getDisplayItemBuilder() {
        return this.displayItem;
    }
    
    /**
     * @return returns the list of tiers the prize is in.
     */
    public List<Tier> getTiers() {
        return this.tiers;
    }

    public List<String> getTiersList() {
        List<String> tiers = new ArrayList<>();

        getTiers().forEach(tier -> tiers.add(tier.getName()));

        return tiers;
    }
    
    /**
     * @return returns the messages sent to the player.
     */
    public List<String> getMessages() {
        return this.messages;
    }
    
    /**
     * @return returns the commands that are run when the player wins.
     */
    public List<String> getCommands() {
        return this.commands;
    }
    
    /**
     * @return returns the Editor ItemStacks that are given to the player that wins.
     */
    public List<ItemStack> getItems() {
        return this.items;
    }
    
    /**
     * @return returns the ItemBuilders for all the custom items made from the Items: option.
     */
    public List<ItemBuilder> getItemBuilders() {
        return this.itemBuilders;
    }
    
    /**
     * @return returns the name of the crate the prize is in.
     */
    public String getCrateName() {
        return this.crateName;
    }
    
    /**
     * @return returns the chance the prize has of being picked.
     */
    public int getChance() {
        return this.chance;
    }
    
    /**
     * @return returns the max range of the prize.
     */
    public int getMaxRange() {
        return this.maxRange;
    }
    
    /**
     * @return returns true if a firework explosion is played and false if not.
     */
    public boolean useFireworks() {
        return this.firework;
    }
    
    /**
     * @return returns the alternative prize the player wins if they have a blacklist permission.
     */
    public Prize getAlternativePrize() {
        return this.alternativePrize;
    }
    
    /**
     * @return returns true if the prize doesn't have an alternative prize and false if it does.
     */
    public boolean hasAlternativePrize() {
        return this.alternativePrize == null;
    }
    
    /**
     * @return Returns true if they prize has blacklist permissions and false if not.
     */
    public boolean hasPermission(Player player) {
        if (player.isOp()) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning(player.getName() + " is opped so we do not check for blacklist permissions.");

            return false;
        }

        for (String permission : this.permissions) {
            if (player.hasPermission(permission)) return true;
        }

        return false;
    }

    private ItemBuilder display(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder();

        try {
            String material = section.getString("DisplayItem", "RED_TERRACOTTA");
            int amount = section.getInt("DisplayAmount", 1);
            List<String> lore = section.getStringList("Lore");
            boolean isGlowing = section.getBoolean("Glowing", false);
            boolean isUnbreakable = section.getBoolean("Unbreakable", false);
            boolean hideItemFlags = section.getBoolean("HideItemFlags", false);
            List<String> itemFlags = section.getStringList("Flags");
            List<String> patterns = section.getStringList("Patterns");
            String playerName = section.getString("Player", "");

            builder.setMaterial(material)
                    .setAmount(amount)
                    .setName(this.prizeName)
                    .setLore(lore)
                    .setGlow(isGlowing)
                    .setUnbreakable(isUnbreakable)
                    .hideItemFlags(hideItemFlags)
                    .addItemFlags(itemFlags)
                    .addPatterns(patterns)
                    .setPlayerName(playerName);

            NamespacedKey cratePrize = PersistentKeys.crate_prize.getNamespacedKey();

            ItemMeta itemMeta = builder.getItemMeta();

            // Check to make sure the container has the correct variable.
            Bukkit.getLogger().warning("Section Name: " + section.getName());

            itemMeta.getPersistentDataContainer().set(cratePrize, PersistentDataType.STRING, section.getName());
            builder.setItemMeta(itemMeta);

            int displayDamage = section.getInt("DisplayDamage", builder.getMaterial().getMaxDurability());
            builder.setDamage(displayDamage);

            if (section.contains("DisplayTrim.Pattern")) {
                NamespacedKey key = null;

                String trimPattern = section.getString("DisplayTrim.Pattern");

                if (trimPattern != null) {
                    key = NamespacedKey.fromString(trimPattern);
                }

                if (key != null) {
                    TrimPattern registry = Registry.TRIM_PATTERN.get(key);
                    builder.setTrimPattern(registry);
                }
            }

            if (section.contains("DisplayTrim.Material")) {
                NamespacedKey key = null;

                String trimMaterial = section.getString("DisplayTrim.Material");

                if (trimMaterial != null) {
                    key = NamespacedKey.fromString(trimMaterial);
                }

                if (key != null) {
                    TrimPattern registry = Registry.TRIM_PATTERN.get(key);
                    builder.setTrimPattern(registry);
                }
            }

            if (section.contains("DisplayEnchantments")) {
                for (String name : section.getStringList("DisplayEnchantments")) {
                    Enchantment enchantment = MiscUtils.getEnchantment(name.split(":")[0]);

                    if (enchantment != null) {
                        builder.addEnchantments(enchantment, Integer.parseInt(name.split(":")[1]));
                    }
                }
            }

            return builder;
        } catch (Exception exception) {
            List<String> list = new ArrayList<>() {{
               add("&cThere was an error with one of your prizes!");
               add("&cThe reward in question is labeled: &e" + section.getName() + " &cin crate: &e" + crateName);
               add("&cName of the reward is " + section.getString("DisplayName"));
               add("&cIf you are confused, Stop by our discord for support!");
            }};

            return new ItemBuilder().setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(list);
        }
    }
}