package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.hooks.HeadDatabaseListener;
import com.ryderbelserion.vital.util.DyeUtil;
import com.ryderbelserion.vital.util.ItemUtil;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static com.ryderbelserion.vital.util.ItemUtil.getEnchantment;

public class ItemBuilder {

    private String crateName;

    public ItemBuilder() {
        super();
    }

    public ItemBuilder(ItemBuilder itemBuilder) {
        this.crateName = itemBuilder.crateName;
    }

    /**
     * Adds the crate to the item meta pdc.
     *
     * @param crateName the name of the crate.
     * @return the ItemBuilder with updated information.
     */
    public ItemBuilder setCrateName(String crateName) {
        setString(PersistentKeys.crate_key.getNamespacedKey(), this.crateName = crateName);

        return this;
    }

    /**
     * @return the name of the crate.
     */
    public String getCrateName() {
        return "";
    }

    /**
     * Converts an item stack to an itembuilder
     *
     * @param player the player.
     * @param itemStack the itemstack.
     * @return the itembuilder.
     */
    public static ItemBuilder convertItemStack(Player player, ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(itemStack.getType()).setAmount(itemStack.getAmount());

        if (itemStack.hasItemMeta()) {
            itemStack.editMeta(itemMeta -> {
                if (itemMeta.hasEnchants()) {
                    itemMeta.getEnchants().forEach((enchantment, level) -> itemBuilder.addEnchantment(enchantment.translationKey(), level, true));
                }
            });
        }

        if (player != null) {
            itemBuilder.setTarget(player);
        }

        return itemBuilder;
    }

    /**
     * Converts an itemstack without a player.
     *
     * @param itemStack the itemstack.
     * @return the itembuilder.
     */
    public static ItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
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
                    case "name" -> itemBuilder.setDisplayName(value);
                    case "amount" -> {
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                    }
                    case "damage" -> {
                        try {
                            itemBuilder.setItemDamage(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setItemDamage(0);
                        }
                    }
                    case "lore" -> itemBuilder.setDisplayLore(Arrays.asList(value.split(",")));
                    case "hdb" -> itemBuilder.setSkull(value, HeadDatabaseListener.getHeads());
                    case "player" -> itemBuilder.setPlayer(value);
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "trim-pattern" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimPattern(value);
                    }
                    case "trim-material" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimMaterial(value);
                    }
                    default -> {
                        if (getEnchantment(option) != null) {
                            try {
                                itemBuilder.addEnchantment(option, Integer.parseInt(value), true);
                            } catch (NumberFormatException e) {
                                itemBuilder.addEnchantment(option, 1, true);
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
                            DyeColor color = DyeUtil.getDyeColor(value);

                            if (color != null) {
                                itemBuilder.addPattern(ItemUtil.getPatternType(option), color);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setDisplayName("<red>ERROR").setDisplayLore(Arrays.asList(
                    "<red>There is an error",
                    "<red>For : " + (placeHolder != null ? placeHolder : "")
            ));

            CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
            plugin.getLogger().log(Level.WARNING, "An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }

    public String getPlainDisplayName() {
        return "";
    }

    // Super methods
    
    public ItemBuilder getCompoundTag(ItemStack item, Player player) {
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        return this;
    }

    public ItemBuilder setMaterial(String material) {
        return this;
    }

    public ItemBuilder setNamePlaceholders(Map<String, String> placeholders) {
        return this;
    }

    public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
        return this;
    }

    public ItemBuilder removeNamePlaceholder(String placeholder) {
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        return this;
    }

    public ItemBuilder setLorePlaceholders(Map<String, String> placeholders) {
        return this;
    }

    public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
        return this;
    }

    public ItemBuilder removeLorePlaceholder(String placeholder) {
        return this;
    }

    public ItemBuilder setDisplayLore(List<String> displayLore) {
        return this;
    }

    public ItemBuilder setItemDamage(int itemDamage) {
        return this;
    }

    public ItemBuilder setCompoundTag(String tag) {
        return this;
    }

    public ItemBuilder addDisplayLore(String value) {
        return this;
    }

    public ItemBuilder setPatterns(Map<PatternType, DyeColor> types) {
        return this;
    }

    public ItemBuilder addPatterns(List<String> patterns) {
        return this;
    }

    public ItemBuilder addPattern(String pattern) {
        return this;
    }

    public ItemBuilder addPattern(PatternType pattern, DyeColor color) {
        return this;
    }

    public ItemBuilder setTrimMaterial(String material) {
        return this;
    }

    public ItemBuilder setTrimPattern(String pattern) {
        return this;
    }

    public ItemBuilder setPotionAmplifier(int potionAmplifier) {
        return this;
    }

    public ItemBuilder setPotionDuration(int potionDuration) {
        return this;
    }

    public ItemBuilder setGlowing(boolean isGlowing) {
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        return this;
    }

    public ItemBuilder setEffects(List<FireworkEffect.Builder> effects) {
        return this;
    }

    public ItemBuilder addEffect(FireworkEffect.Builder effect) {
        return this;
    }

    public ItemBuilder setFireworkPower(int power) {
        return this;
    }

    public ItemBuilder setEntityType(EntityType entityType) {
        return this;
    }

    public ItemBuilder addEnchantments(Map<String, Integer> enchantments, boolean ignoreLevelRestriction) {
        return this;
    }

    public ItemBuilder addEnchantment(String value, int level, boolean ignoreLevelRestriction) {
        return this;
    }

    public ItemBuilder removeEnchantment(String value) {
        return this;
    }

    public ItemBuilder setPlayer(String name) {
        return this;
    }

    public ItemBuilder setSkull(String skull, HeadDatabaseAPI api) {
        return this;
    }

    public ItemBuilder setTarget(Player target) {
        return this;
    }

    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        return this;
    }

    public ItemBuilder addItemFlags(List<String> flags) {
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        return this;
    }

    public ItemBuilder setString(NamespacedKey key, String value) {
        return this;
    }

    public ItemBuilder setDouble(NamespacedKey key, double value) {
        return this;
    }

    public ItemBuilder setInteger(NamespacedKey key, int value) {
        return this;
    }

    public ItemBuilder setBoolean(NamespacedKey key, boolean value) {
        return this;
    }

    public ItemBuilder setList(NamespacedKey key, List<String> values) {
        return this;
    }

    public ItemBuilder removeKey(NamespacedKey key) {
        return this;
    }
}