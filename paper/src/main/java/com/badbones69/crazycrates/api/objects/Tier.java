package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.other.ItemBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import java.util.Collections;
import java.util.List;

public class Tier {

    private final ItemBuilder item;
    private final int maxRange;
    private final String name;
    private final List<String> lore;
    private final String coloredName;
    private final int chance;
    private final int slot;

    public Tier(String tier, ConfigurationSection section) {
        this.name = tier;

        this.coloredName = section.getString("Name", "");

        this.lore = section.getStringList("Lore").isEmpty() ? Collections.emptyList() : section.getStringList("Lore");

        this.item = new ItemBuilder().setMaterial(section.getString("Item", "CHEST"));

        this.chance = section.getInt("Chance");
        this.maxRange = section.getInt("MaxRange", 100);

        this.slot = section.getInt("Slot");
    }
    
    /**
     * @return name of the tier.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return colored name of the tier.
     */
    public String getColoredName() {
        return this.coloredName;
    }

    /**
     * @return the colored glass pane.
     */
    public ItemBuilder getItem() {
        return this.item;
    }
    
    /**
     * @return the chance of being picked.
     */
    public int getChance() {
        return this.chance;
    }
    
    /**
     * @return the range of max possible chances.
     */
    public int getMaxRange() {
        return this.maxRange;
    }

    /**
     * @return slot in the inventory.
     */
    public int getSlot() {
        return this.slot;
    }

    /**
     * @return the tier item shown in the preview.
     */
    public ItemStack getTierItem(Player target) {
        this.item.setTarget(target);

        this.item.setName(this.coloredName);

        this.item.setLore(this.lore);

        ItemMeta itemMeta = this.item.getItemMeta();

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        PersistentKeys key = PersistentKeys.preview_tier_button;

        //noinspection unchecked
        container.set(key.getNamespacedKey(), key.getType(), this.name);

        this.item.setItemMeta(itemMeta);

        return this.item.build();
    }
}