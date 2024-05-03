package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.hooks.HeadDatabaseListener;
import com.ryderbelserion.vital.items.AbstractItemHandler;
import com.ryderbelserion.vital.util.DyeUtil;
import com.ryderbelserion.vital.util.ItemUtil;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

public class ItemBuilder extends AbstractItemHandler {

    private String crateName;

    public ItemBuilder() {
        super();
    }

    public ItemBuilder(ItemBuilder itemBuilder) {
        super(itemBuilder);

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
        return getString(PersistentKeys.crate_key.getNamespacedKey());
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
        return PlainTextComponentSerializer.plainText().serialize(getDisplayName());
    }

    // Super methods
    @Override
    public ItemBuilder getCompoundTag(ItemStack item, Player player) {
        super.getCompoundTag(item, player);

        return this;
    }

    @Override
    public ItemBuilder setMaterial(Material material) {
        super.setMaterial(material);

        return this;
    }

    @Override
    public ItemBuilder setMaterial(String material) {
        super.setMaterial(material);

        return this;
    }

    @Override
    public ItemBuilder setNamePlaceholders(Map<String, String> placeholders) {
        super.setNamePlaceholders(placeholders);

        return this;
    }

    @Override
    public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
        super.addNamePlaceholder(placeholder, argument);

        return this;
    }

    @Override
    public ItemBuilder removeNamePlaceholder(String placeholder) {
        super.removeNamePlaceholder(placeholder);

        return this;
    }

    @Override
    public ItemBuilder setDisplayName(String displayName) {
        super.setDisplayName(displayName);

        return this;
    }

    @Override
    public ItemBuilder setLorePlaceholders(Map<String, String> placeholders) {
        super.setLorePlaceholders(placeholders);

        return this;
    }

    @Override
    public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
        super.addLorePlaceholder(placeholder, argument);

        return this;
    }

    @Override
    public ItemBuilder removeLorePlaceholder(String placeholder) {
        super.removeLorePlaceholder(placeholder);

        return this;
    }

    @Override
    public ItemBuilder setDisplayLore(List<String> displayLore) {
        super.setDisplayLore(displayLore);

        return this;
    }

    @Override
    public ItemBuilder setItemDamage(int itemDamage) {
        super.setItemDamage(itemDamage);

        return this;
    }

    @Override
    public ItemBuilder setCompoundTag(String tag) {
        super.setCompoundTag(tag);

        return this;
    }

    @Override
    public ItemBuilder addDisplayLore(String value) {
        super.addDisplayLore(value);

        return this;
    }

    @Override
    public ItemBuilder setPatterns(Map<PatternType, DyeColor> types) {
        super.setPatterns(types);

        return this;
    }

    @Override
    public ItemBuilder addPatterns(List<String> patterns) {
        super.addPatterns(patterns);

        return this;
    }

    @Override
    public ItemBuilder addPattern(String pattern) {
        super.addPattern(pattern);

        return this;
    }

    @Override
    public ItemBuilder addPattern(PatternType pattern, DyeColor color) {
        super.addPattern(pattern, color);

        return this;
    }

    @Override
    public ItemBuilder setTrimMaterial(String material) {
        super.setTrimMaterial(material);

        return this;
    }

    @Override
    public ItemBuilder setTrimPattern(String pattern) {
        super.setTrimPattern(pattern);

        return this;
    }

    @Override
    public ItemBuilder setPotionAmplifier(int potionAmplifier) {
        super.setPotionAmplifier(potionAmplifier);

        return this;
    }

    @Override
    public ItemBuilder setPotionDuration(int potionDuration) {
        super.setPotionDuration(potionDuration);

        return this;
    }

    @Override
    public ItemBuilder setGlowing(boolean isGlowing) {
        super.setGlowing(isGlowing);

        return this;
    }

    @Override
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        super.setUnbreakable(unbreakable);

        return this;
    }

    @Override
    public ItemBuilder setAmount(int amount) {
        super.setAmount(amount);

        return this;
    }

    @Override
    public ItemBuilder setEffects(List<FireworkEffect.Builder> effects) {
        super.setEffects(effects);

        return this;
    }

    @Override
    public ItemBuilder addEffect(FireworkEffect.Builder effect) {
        super.addEffect(effect);

        return this;
    }

    @Override
    public ItemBuilder setFireworkPower(int power) {
        super.setFireworkPower(power);

        return this;
    }

    @Override
    public ItemBuilder setEntityType(EntityType entityType) {
        super.setEntityType(entityType);

        return this;
    }

    @Override
    public ItemBuilder addEnchantments(Map<String, Integer> enchantments, boolean ignoreLevelRestriction) {
        super.addEnchantments(enchantments, ignoreLevelRestriction);

        return this;
    }

    @Override
    public ItemBuilder addEnchantment(String value, int level, boolean ignoreLevelRestriction) {
        super.addEnchantment(value, level, ignoreLevelRestriction);

        return this;
    }

    @Override
    public ItemBuilder removeEnchantment(String value) {
        super.removeEnchantment(value);

        return this;
    }

    @Override
    public ItemBuilder setPlayer(String name) {
        super.setPlayer(name);

        return this;
    }

    @Override
    public ItemBuilder setSkull(String skull, HeadDatabaseAPI api) {
        super.setSkull(skull, api);

        return this;
    }

    @Override
    public ItemBuilder setTarget(Player target) {
        super.setTarget(target);

        return this;
    }

    @Override
    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        super.hideItemFlags(hideItemFlags);

        return this;
    }

    @Override
    public ItemBuilder addItemFlags(List<String> flags) {
        super.addItemFlags(flags);

        return this;
    }

    @Override
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        super.addItemFlag(itemFlag);

        return this;
    }

    @Override
    public ItemBuilder setString(NamespacedKey key, String value) {
        super.setString(key, value);

        return this;
    }

    @Override
    public ItemBuilder setDouble(NamespacedKey key, double value) {
        super.setDouble(key, value);

        return this;
    }

    @Override
    public ItemBuilder setInteger(NamespacedKey key, int value) {
        super.setInteger(key, value);

        return this;
    }

    @Override
    public ItemBuilder setBoolean(NamespacedKey key, boolean value) {
        super.setBoolean(key, value);

        return this;
    }

    @Override
    public ItemBuilder setList(NamespacedKey key, List<String> values) {
        super.setList(key, values);

        return this;
    }

    @Override
    public ItemBuilder removeKey(NamespacedKey key) {
        super.removeKey(key);

        return this;
    }
}