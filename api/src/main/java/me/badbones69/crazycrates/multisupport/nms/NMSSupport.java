package me.badbones69.crazycrates.multisupport.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.List;

public interface NMSSupport {
    
    void openChest(Block block, boolean open);
    
    void pasteSchematic(File file, Location loc);
    
    void saveSchematic(Location[] locations, String owner, File file);
    
    List<Location> getLocations(File file, Location loc);
    
    List<Material> getQuadCrateBlacklistBlocks();
    
    ItemStack getItemInMainHand(Player player);
    
}