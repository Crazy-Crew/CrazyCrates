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
import java.util.Map;
import java.util.UUID;

public class CosmicCrateManager extends AbstractCrate {

    private final ItemBuilder mysteryCrate;
    private final ItemBuilder pickedCrate;
    private final FileConfiguration file;
    private final int totalPrizes;

    // Picked items
    private final Map<UUID, ArrayList<Integer>> pickedPrizes = new HashMap<>();
    
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
    
    public FileConfiguration getFile() {
        return this.file;
    }
    
    public int getTotalPrizes() {
        return this.totalPrizes;
    }
    
    public ItemBuilder getMysteryCrate() {
        return this.mysteryCrate;
    }

    public ItemBuilder getPickedCrate() {
        return this.pickedCrate;
    }

    public void addPickedPrize(Player player, ArrayList<Integer> prizes) {
        this.pickedPrizes.put(player.getUniqueId(), prizes);
    }

    public void removePickedPrize(Player player) {
        this.pickedPrizes.remove(player.getUniqueId());
    }

    public Map<UUID, ArrayList<Integer>> getPickedPrizes() {
        return Collections.unmodifiableMap(this.pickedPrizes);
    }
}