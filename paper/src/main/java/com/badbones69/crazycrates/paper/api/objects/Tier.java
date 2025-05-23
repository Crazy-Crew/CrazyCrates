package com.badbones69.crazycrates.paper.api.objects;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.utils.NumberUtils;
import com.ryderbelserion.fusion.paper.api.builders.items.ItemBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;

public class Tier {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final ItemBuilder item;
    private final String name;
    private final List<String> lore;
    private final String coloredName;
    private final double weight;
    private final int slot;

    public Tier(@NotNull final String tier, @NotNull final ConfigurationSection section) {
        this.name = tier;

        this.coloredName = section.getString("Name", "");

        this.lore = section.getStringList("Lore"); // this returns an empty list if not found anyway.

        this.item = ItemBuilder.from(section.getString("Item", "chest").toLowerCase()).setCustomModelData(section.getString("Custom-Model-Data", "")).setItemModel(section.getString("Model.Namespace", ""), section.getString("Model.Id", ""));

        if (section.getBoolean("HideItemFlags", false)) {
            this.item.hideToolTip();
        }

        this.weight = section.getDouble("Weight", -1);

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
    public final double getWeight() {
        if (this.weight == -1 && MiscUtils.isLogging()) {
            this.logger.warn("Cannot fetch the weight as the option is not present for this tier: {}", this.name);
        }

        return this.weight;
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
    public @NotNull final ItemStack getTierItem(@Nullable final Player target, @NotNull final Crate crate) {
        return this.item.setDisplayName(this.coloredName).withDisplayLore(this.lore).addPlaceholder("%chance%", NumberUtils.format(crate.getTierChance(getWeight()))).setPersistentString(ItemKeys.crate_tier.getNamespacedKey(), this.name).asItemStack(target == null ? Audience.empty() : target);
    }
}