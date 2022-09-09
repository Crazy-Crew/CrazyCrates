package com.badbones69.crazycrates.api.managers;

import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Collections;

public class CosmicCrateManager extends CrateManager {

    /**
     * The blank file configuration.
     */
    private final FileConfiguration file;

    /**
     * The blank amount of prizes.
     */
    private final int totalPrizes;

    /**
     * The blank mysteryCrate.
     */
    private final ItemBuilder mysteryCrate;

    /**
     * The blank pickedCrate.
     */
    private final ItemBuilder pickedCrate;

    /**
     * Starts building everything for the Cosmic Crate.
     * @param file - Where we retrieve all our values.
     */
    public CosmicCrateManager(FileConfiguration file) {
        this.file = file;
        String path = "Crate.Crate-Type-Settings.";
        totalPrizes = file.getInt(path + "Total-Prize-Amount", 4);
        mysteryCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Mystery-Crate.Item", "CHEST"))
        .setName(file.getString(path + "Mystery-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Mystery-Crate.Lore") ? file.getStringList(path + "Mystery-Crate.Lore") : Collections.singletonList("&7You may choose 4 crates."));
        mysteryCrate.getNBTItem().setString("Cosmic-Mystery-Crate", "Mystery Crate");
        pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", Material.GLASS_PANE.toString()))
        .setName(file.getString(path + "Picked-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Collections.singletonList("&7You have chosen #%slot%."));
        pickedCrate.getNBTItem().setString("Cosmic-Picked-Crate", "Picked Crate");
    }

    /**
     * @return The file configuration.
     */
    public FileConfiguration getFile() {
        return file;
    }

    /**
     * @return All the available prizes.
     */
    public int getTotalPrizes() {
        return totalPrizes;
    }

    /**
     * @return The mystery crate item.
     */
    public ItemBuilder getMysteryCrate() {
        return new ItemBuilder(mysteryCrate);
    }

    /**
     * @return The crate the player picked.
     */
    public ItemBuilder getPickedCrate() {
        return pickedCrate;
    }
}