package com.badbones69.crazycrates.tasks.crates.other;

import com.badbones69.crazycrates.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CosmicCrateManager extends AbstractCrateManager {

    private final ItemBuilder mysteryCrate;
    private final ItemBuilder pickedCrate;
    private final FileConfiguration file;
    private final int totalPrizes;

    private final Map<UUID, ArrayList<Integer>> pickedPrizes = new HashMap<>();

    /**
     * Creates a cosmic crate manager instance
     *
     * @param plugin instance
     * @param file crate configuration
     */
    public CosmicCrateManager(CrazyCrates plugin, FileConfiguration file) {
        this.file = file;

        String path = "Crate.Crate-Type-Settings.";

        this.totalPrizes = file.getInt(path + "Total-Prize-Amount", 4);

        this.mysteryCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Mystery-Crate.Item", "CHEST"))
        .setName(file.getString(path + "Mystery-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Mystery-Crate.Lore") ? file.getStringList(path + "Mystery-Crate.Lore") : Collections.singletonList("&7You may choose 4 crates."));

        ItemMeta mysteryItemMeta = this.mysteryCrate.getItemMeta();

        PersistentDataContainer mysteryData = mysteryItemMeta.getPersistentDataContainer();

        PersistentKeys mysteryCrate = PersistentKeys.cosmic_mystery_crate;

        mysteryData.set(mysteryCrate.getNamespacedKey(), mysteryCrate.getType(), 1);

        this.mysteryCrate.setItemMeta(mysteryItemMeta);

        this.pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", Material.GLASS_PANE.toString()))
        .setName(file.getString(path + "Picked-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Collections.singletonList("&7You have chosen #%slot%."));

        ItemMeta pickedCrateMeta = this.pickedCrate.getItemMeta();

        PersistentDataContainer pickedCrateData = pickedCrateMeta.getPersistentDataContainer();

        PersistentKeys pickedCrate = PersistentKeys.cosmic_picked_crate;

        pickedCrateData.set(pickedCrate.getNamespacedKey(), pickedCrate.getType(), 1);

        this.pickedCrate.setItemMeta(pickedCrateMeta);
    }

    /**
     * @return crate file configuration
     */
    public FileConfiguration getFile() {
        return this.file;
    }

    /**
     * @return total prizes allowed to be won
     */
    public int getTotalPrizes() {
        return this.totalPrizes;
    }

    /**
     * @return mystery crate builder
     */
    public ItemBuilder getMysteryCrate() {
        return this.mysteryCrate;
    }

    /**
     * @return picked crate builder
     */
    public ItemBuilder getPickedCrate() {
        return this.pickedCrate;
    }

    /**
     * Adds a single slot to the arraylist
     * It also adds the player if not found.
     *
     * @param player to add
     * @param slot to add
     */
    public void addPickedPrize(Player player, int slot) {
        if (this.pickedPrizes.containsKey(player.getUniqueId())) {
            // Adds a player uuid with a default arraylist to the map if not found otherwise adds the slot.
            this.pickedPrizes.get(player.getUniqueId()).add(slot);
            return;
        }

        this.pickedPrizes.put(player.getUniqueId(), new ArrayList<>());
        this.pickedPrizes.get(player.getUniqueId()).add(slot);
    }

    /**
     * Removes a single slot from the arraylist
     * It also removes the uuid if prizes arraylist is empty.
     *
     * @param player to remove
     * @param slot to remove
     */
    public void removePickedPrize(Player player, int slot) {
        // Get prizes.
        ArrayList<Integer> prizes = this.pickedPrizes.get(player.getUniqueId());

        //prizes.removeIf(value -> value == slot);
        prizes.removeIf(value -> value == slot);

        // If the arraylist is empty, remove player uuid from list entirely to prevent a leak.
        if (prizes.isEmpty()) this.pickedPrizes.remove(player.getUniqueId());
    }

    /**
     * Removes a player from the hashmap.
     *
     * @param player to remove
     */
    public void removePickedPlayer(Player player) {
        this.pickedPrizes.remove(player.getUniqueId());
    }

    /**
     * Adds a list of prizes to the hashmap.
     *
     * @param player to add
     * @param prizes list
     */
    public void addPickedPrizes(Player player, ArrayList<Integer> prizes) {
        this.pickedPrizes.put(player.getUniqueId(), prizes);
    }

    /**
     * @return unmodifiable list
     */
    public List<Integer> getPickedPrizes(Player player) {
        return Collections.unmodifiableList(this.pickedPrizes.getOrDefault(player.getUniqueId(), new ArrayList<>()));
    }
}