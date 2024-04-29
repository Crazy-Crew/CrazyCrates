package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.ryderbelserion.vital.items.ParentItemBuilder;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder extends ParentItemBuilder {

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

    public static ItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
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
    public ItemBuilder setTag(String tag) {
        super.setTag(tag);

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
    public ItemBuilder setTexture(String texture) {
        super.setTexture(texture);

        return this;
    }

    @Override
    public ItemBuilder setUUID(UUID uuid) {
        super.setUUID(uuid);

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