package com.badbones69.crazycrates.tasks.crates.other;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import org.bukkit.persistence.PersistentDataType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmicCrateManager extends AbstractCrateManager {

    private final ItemBuilder mysteryCrate;
    private final ItemBuilder pickedCrate;
    private final FileConfiguration file;
    private final int totalPrizes;

    private final Map<UUID, Map<Integer, Tier>> prizes = new HashMap<>();

    /**
     * Creates a cosmic crate manager instance.
     *
     * @param file the crate configuration.
     */
    public CosmicCrateManager(FileConfiguration file) {
        this.file = file;

        String path = "Crate.Crate-Type-Settings.";

        this.totalPrizes = file.getInt(path + "Total-Prize-Amount", 4);

        this.mysteryCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Mystery-Crate.Item", "CHEST"))
        .setName(file.getString(path + "Mystery-Crate.Name", "<bold><white>???</bold>"))
        .hideItemFlags(file.getBoolean(path + "Mystery-Crate.HideItemFlags", false))
        .setLore(file.contains(path + "Mystery-Crate.Lore") ? file.getStringList(path + "Mystery-Crate.Lore") : Collections.singletonList("<gray>You may choose 4 crates."));

        ItemMeta mysteryItemMeta = this.mysteryCrate.getItemMeta();

        PersistentDataContainer mysteryData = mysteryItemMeta.getPersistentDataContainer();

        PersistentKeys mysteryCrate = PersistentKeys.cosmic_mystery_crate;

        mysteryData.set(mysteryCrate.getNamespacedKey(), mysteryCrate.getType(), 1);

        this.mysteryCrate.setItemMeta(mysteryItemMeta);

        this.pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", Material.GLASS_PANE.toString()))
        .setName(file.getString(path + "Picked-Crate.Name", "<bold><white>???</white>"))
        .hideItemFlags(file.getBoolean(path + "Picked-Crate.HideItemFlags", false))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Collections.singletonList("<gray>You have chosen #%slot%."));

        ItemMeta pickedCrateMeta = this.pickedCrate.getItemMeta();

        PersistentDataContainer pickedCrateData = pickedCrateMeta.getPersistentDataContainer();

        PersistentKeys pickedCrate = PersistentKeys.cosmic_picked_crate;

        pickedCrateData.set(pickedCrate.getNamespacedKey(), pickedCrate.getType(), 1);

        this.pickedCrate.setItemMeta(pickedCrateMeta);
    }

    /**
     * @return crate file configuration.
     */
    public FileConfiguration getFile() {
        return this.file;
    }

    /**
     * @return total prizes allowed to be won.
     */
    public int getTotalPrizes() {
        return this.totalPrizes;
    }

    /**
     * @return mystery crate builder.
     */
    public ItemBuilder getMysteryCrate() {
        return this.mysteryCrate;
    }

    /**
     * @return picked crate builder.
     */
    public ItemBuilder getPickedCrate() {
        return this.pickedCrate;
    }

    public Tier getTier(ItemStack itemStack, Crate crate) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(PersistentKeys.crate_tier.getNamespacedKey())) {
                return crate.getTier(container.get(PersistentKeys.crate_tier.getNamespacedKey(), PersistentDataType.STRING));
            }

            // In case there is no tier.
            return PrizeManager.getTier(crate);
        }

        // In case there is no item meta.
        return PrizeManager.getTier(crate);
    }

    public ItemMeta setTier(ItemMeta itemMeta, String name) {
        itemMeta.getPersistentDataContainer().set(PersistentKeys.crate_tier.getNamespacedKey(), PersistentDataType.STRING, name);

        return itemMeta;
    }

    /**
     * Adds a single slot to the arraylist.
     * It also adds the player if not found.
     *
     * @param player player to add
     * @param slot slot to add.
     */
    public void addPickedPrize(Player player, int slot, Tier tier) {
        UUID uuid = player.getUniqueId();

        if (this.prizes.containsKey(uuid)) {
            this.prizes.get(uuid).put(slot, tier);

            return;
        }

        this.prizes.put(uuid, new HashMap<>() {{
            put(slot, tier);
        }});
    }

    /**
     * Removes a single slot from the arraylist.
     * It also removes the uuid if prizes arraylist is empty.
     *
     * @param player player to remove.
     * @param slot slot to remove.
     */
    public void removePickedPrize(Player player, int slot) {
        UUID uuid = player.getUniqueId();

        Map<Integer, Tier> map = this.prizes.get(player.getUniqueId());

        map.entrySet().removeIf(value -> value.getKey() == slot);

        // If the map is empty, remove player uuid from map entirely to prevent a leak.
        if (map.isEmpty()) this.prizes.remove(uuid);
    }

    /**
     * Removes a player from the hashmap.
     *
     * @param player player to remove.
     */
    public void removePickedPlayer(Player player) {
        this.prizes.remove(player.getUniqueId());
    }

    /**
     * @return unmodifiable map of picked prizes.
     */
    public Map<Integer, Tier> getPrizes(Player player) {
        return Collections.unmodifiableMap(this.prizes.getOrDefault(player.getUniqueId(), new HashMap<>()));
    }
}