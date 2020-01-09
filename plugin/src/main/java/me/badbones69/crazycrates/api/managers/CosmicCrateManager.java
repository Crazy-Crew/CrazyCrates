package me.badbones69.crazycrates.api.managers;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class CosmicCrateManager extends CrateManager {
    
    private FileConfiguration file;
    private ItemBuilder mysteryCrate;
    private ItemBuilder pickedCrate;
    
    public CosmicCrateManager(FileConfiguration file) {
        this.file = file;
        CrazyCrates cc = CrazyCrates.getInstance();
        String path = "Crate.Crate-Type-Settings.";
        mysteryCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Mystery-Crate.Item", "CHEST"))
        .setName(file.getString(path + "Mystery-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Mystery-Crate.Lore") ? file.getStringList(path + "Mystery-Crate.Lore") : Arrays.asList("&7You may choose 4 crates."));
        mysteryCrate.getNBTItem().setString("Cosmic-Mystery-Crate", "Mystery Crate");
        pickedCrate = new ItemBuilder()
        .setMaterial(file.getString(path + "Picked-Crate.Item", cc.useNewMaterial() ? "GLASS_PANE" : "THIN_GLASS"))
        .setName(file.getString(path + "Picked-Crate.Name", "&f&l???"))
        .setLore(file.contains(path + "Picked-Crate.Lore") ? file.getStringList(path + "Picked-Crate.Lore") : Arrays.asList("&7You have chosen #%slot%."));
        pickedCrate.getNBTItem().setString("Cosmic-Picked-Crate", "Picked Crate");
    }
    
    public FileConfiguration getFile() {
        return file;
    }
    
    public ItemBuilder getMysteryCrate() {
        return mysteryCrate.clone();
    }
    
    public ItemBuilder getPickedCrate() {
        return pickedCrate;
    }
    
}