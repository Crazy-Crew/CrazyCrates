package com.badbones69.crazycrates.paper.api.managers;

import us.crazycrew.crazycrates.paper.api.plugin.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Collections;

public class CosmicCrateManager extends CrateManager {
    
    private final FileConfiguration file;
    private final int totalPrizes;
    private final ItemBuilder mysteryCrate;
    private final ItemBuilder pickedCrate;
    
    public CosmicCrateManager(FileConfiguration file) {
        this.file = file;
        String path = "Crate.Crate-Type-Settings.";
        this.totalPrizes = file.getInt(path + "Total-Prize-Amount", 4);
        this.mysteryCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Mystery-Crate.Item", "CHEST"))
        .setName(file.getString(path + "Mystery-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Mystery-Crate.Lore") ? file.getStringList(path + "Mystery-Crate.Lore") : Collections.singletonList("&7You may choose 4 crates."));
        this.mysteryCrate.getNBTItem().setString("Cosmic-Mystery-Crate", "Mystery Crate");
        this.pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", Material.GLASS_PANE.toString()))
        .setName(file.getString(path + "Picked-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Collections.singletonList("&7You have chosen #{slot}."));
        this.pickedCrate.getNBTItem().setString("Cosmic-Picked-Crate", "Picked Crate");
    }
    
    public FileConfiguration getFile() {
        return this.file;
    }
    
    public int getTotalPrizes() {
        return this.totalPrizes;
    }
    
    public ItemBuilder getMysteryCrate() {
        return new ItemBuilder(this.mysteryCrate);
    }
    
    public ItemBuilder getPickedCrate() {
        return this.pickedCrate;
    }
}