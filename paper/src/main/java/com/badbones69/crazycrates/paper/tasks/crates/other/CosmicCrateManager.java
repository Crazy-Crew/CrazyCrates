package com.badbones69.crazycrates.paper.tasks.crates.other;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.paper.utils.ItemUtil;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmicCrateManager extends AbstractCrateManager {

    private final Map<UUID, Map<Integer, Tier>> prizes = new HashMap<>();

    private final ConfigurationSection section;
    
    private ItemBuilder mysteryCrate;
    private ItemBuilder pickedCrate;
    private int totalPrizes;

    /**
     * Creates a cosmic crate manager instance.
     *
     * @param section the crate configuration.
     */
    public CosmicCrateManager(@NotNull final ConfigurationSection section) {
        this.section = section;
        
        final ConfigurationSection settings = section.getConfigurationSection("Crate-Type-Settings");
        
        if (settings == null) {
            return;
        }
        
        this.totalPrizes = settings.getInt("Total-Prize-Amount", 4);

        this.mysteryCrate = ItemBuilder.from(settings.getString("Mystery-Crate.Item", "chest").toLowerCase())
                .withDisplayName(settings.getString("Mystery-Crate.Name", "<bold><white>???</bold>"))
                //.setHidingItemFlags(file.getBoolean("Mystery-Crate.HideItemFlags", false)) //todo() this doesn't exist now.
                .withDisplayLore(settings.contains("Mystery-Crate.Lore") ? settings.getStringList("Mystery-Crate.Lore") : Collections.singletonList("<gray>You may choose 4 crates."))
                .setPersistentInteger(ItemKeys.cosmic_mystery_crate.getNamespacedKey(), 1);

        ItemUtil.addItemModel(this.mysteryCrate, settings.getString("Mystery-Crate.Model.Namespace", ""), settings.getString("Mystery-Crate.Model.Id", ""));
        ItemUtil.addCustomModel(this.mysteryCrate, settings.getString("Mystery-Crate.Custom-Model-Data", ""));

        this.pickedCrate = ItemBuilder.from(settings.getString("Picked-Crate.Item", "gray_stained_glass_pane").toLowerCase())
                .withDisplayName(settings.getString("Picked-Crate.Name", "<bold><white>???</white>"))
                //.setHidingItemFlags(file.getBoolean("Picked-Crate.HideItemFlags", false)) //todo() this doesn't exist now.
                .withDisplayLore(settings.contains("Picked-Crate.Lore") ? settings.getStringList("Picked-Crate.Lore") : Collections.singletonList("<gray>You have chosen #%slot%."))
                .setPersistentInteger(ItemKeys.cosmic_picked_crate.getNamespacedKey(), 1);

        ItemUtil.addItemModel(this.pickedCrate, settings.getString("Picked-Crate.Model.Namespace", ""), settings.getString("Picked-Crate.Model.Id", ""));
        ItemUtil.addCustomModel(this.pickedCrate, settings.getString("Picked-Crate.Custom-Model-Data", ""));
    }

    /**
     * @return crate file configuration.
     */
    public @NotNull final ConfigurationSection getSection() {
        return this.section;
    }

    /**
     * @return total prizes allowed to be won.
     */
    public final int getTotalPrizes() {
        return this.totalPrizes;
    }

    /**
     * @return mystery crate builder.
     */
    public @NotNull final ItemBuilder getMysteryCrate() {
        return this.mysteryCrate;
    }

    /**
     * @return picked crate builder.
     */
    public @NotNull final ItemBuilder getPickedCrate() {
        return this.pickedCrate;
    }

    /**
     * Get a tier from the item-stack's pdc
     *
     * @param itemStack the itemstack
     * @param crate the crate
     * @return the tier
     */
    public final Tier getTier(@NotNull final ItemStack itemStack, @NotNull final Crate crate) {
        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (container.has(ItemKeys.crate_tier.getNamespacedKey())) {
            return crate.getTier(container.get(ItemKeys.crate_tier.getNamespacedKey(), PersistentDataType.STRING));
        }

        // In case there is no tier.
        return PrizeManager.getTier(crate);
    }

    /**
     * Sets a tier to the item-builder's persistent data container.
     *
     * @param itemBuilder the ItemBuilder
     * @param name the name of the tier
     */
    public void setTier(@NotNull final ItemBuilder itemBuilder, @Nullable final String name) {
        if (name == null || name.isEmpty()) return;

        itemBuilder.setPersistentString(ItemKeys.crate_tier.getNamespacedKey(), name);
    }

    /**
     * Adds a single slot to the arraylist.
     * It also adds the player if not found.
     *
     * @param player player to add
     * @param slot slot to add.
     */
    public void addPickedPrize(@NotNull final Player player, final int slot, @NotNull final Tier tier) {
        final UUID uuid = player.getUniqueId();

        if (this.prizes.containsKey(uuid)) {
            this.prizes.get(uuid).put(slot, tier);

            return;
        }

        final Map<Integer, Tier> map = new HashMap<>();

        map.put(slot, tier);

        this.prizes.put(uuid, map);
    }

    /**
     * Removes a single slot from the arraylist.
     * It also removes the uuid if prizes arraylist is empty.
     *
     * @param player player to remove.
     * @param slot slot to remove.
     */
    public void removePickedPrize(@NotNull final Player player, final int slot) {
        final UUID uuid = player.getUniqueId();

        final Map<Integer, Tier> map = this.prizes.get(player.getUniqueId());

        map.entrySet().removeIf(value -> value.getKey() == slot);

        // If the map is empty, remove player uuid from map entirely to prevent a leak.
        if (map.isEmpty()) this.prizes.remove(uuid);
    }

    /**
     * Removes a player from the hashmap.
     *
     * @param player player to remove.
     */
    public void removePickedPlayer(@NotNull final Player player) {
        this.prizes.remove(player.getUniqueId());
    }

    /**
     * @return unmodifiable map of picked prizes.
     */
    public final Map<Integer, Tier> getPrizes(@NotNull final Player player) {
        return Collections.unmodifiableMap(this.prizes.getOrDefault(player.getUniqueId(), new HashMap<>()));
    }
}