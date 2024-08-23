package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.common.utils.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;

public class Tier {

    private final ItemBuilder item;
    private final int maxRange;
    private final String name;
    private final List<String> lore;
    private final String coloredName;
    private final int chance;
    private final int slot;

    public Tier(@NotNull final String tier, @NotNull final ConfigurationSection section) {
        this.name = tier;

        this.coloredName = section.getString("Name", "");

        this.lore = section.getStringList("Lore"); // this returns an empty list if not found anyway.

        this.item = new ItemBuilder().withType(section.getString("Item", "chest").toLowerCase()).setHidingItemFlags(section.getBoolean("HideItemFlags", false)).setCustomModelData(section.getInt("Custom-Model-Data", -1));

        this.chance = section.getInt("Chance");
        this.maxRange = section.getInt("MaxRange", 100);

        this.slot = section.getInt("Slot");
    }
    
    /**
     * @return name of the tier.
     */
    public @NotNull final String getName() {
        return this.name;
    }

    /**
     * @return colored name of the tier.
     */
    public @NotNull final String getColoredName() {
        return this.coloredName;
    }

    /**
     * @return the colored glass pane.
     */
    public @NotNull final ItemBuilder getItem() {
        return this.item;
    }

    /**
     * Get the total chance
     *
     * @return the total chance divided
     */
    public final String getTotalChance() {
        return StringUtil.formatDouble((double) getChance() / getMaxRange() * 100) + "%";
    }

    /**
     * Get the max range
     *
     * @return the max range of the prize.
     */
    public final int getMaxRange() {
        return this.maxRange;
    }

    /**
     * Get the chance
     *
     * @return the chance the prize has of being picked.
     */
    public final int getChance() {
        return this.chance;
    }

    /**
     * @return slot in the inventory.
     */
    public final int getSlot() {
        return this.slot;
    }

    /**
     * @return the tier item shown in the preview.
     */
    public @NotNull final ItemStack getTierItem(final @Nullable Player target) {
        if (target != null) this.item.setPlayer(target);

        return this.item.setDisplayName(this.coloredName).setDisplayLore(this.lore).addLorePlaceholder("%chance%", this.getTotalChance()).setPersistentString(PersistentKeys.crate_tier.getNamespacedKey(), this.name).getStack();
    }
}