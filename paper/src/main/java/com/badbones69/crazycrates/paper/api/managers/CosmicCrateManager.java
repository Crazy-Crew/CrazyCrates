package com.badbones69.crazycrates.paper.api.managers;

import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CosmicCrateManager extends AbstractCrate {

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

        mysteryData.set(mysteryCrate.getNamespacedKey(plugin), mysteryCrate.getType(), 1);

        this.mysteryCrate.setItemMeta(mysteryItemMeta);

        this.pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", Material.GLASS_PANE.toString()))
        .setName(file.getString(path + "Picked-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Collections.singletonList("&7You have chosen #%slot%."));

        ItemMeta pickedCrateMeta = this.pickedCrate.getItemMeta();

        PersistentDataContainer pickedCrateData = pickedCrateMeta.getPersistentDataContainer();

        PersistentKeys pickedCrate = PersistentKeys.cosmic_picked_crate;

        pickedCrateData.set(pickedCrate.getNamespacedKey(plugin), pickedCrate.getType(), 1);

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
    /**
     * Removes a single slot from the arraylist
     * It also removes the uuid if prizes arraylist is empty.
     *
     * @param player to remove
     * @param slot to remove
     */
    }

    /**
     * Adds a list of prizes to the hashmap.
     *
     * @param player to add
     * @param prizes list
     */
    }

    /**
     * @return unmodifiable list
     */
    public List<Integer> getPickedPrizes(Player player) {
        return Collections.unmodifiableList(this.pickedPrizes.getOrDefault(player.getUniqueId(), new ArrayList<>()));
    }
}