package me.badbones69.crazycrates.multisupport.nms;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface NMSSupport {

    public void openChest(Block b, Location location, Boolean open);

    public List<Location> pasteSchematic(File f, Location loc);

    public List<Location> getLocations(File f, Location loc);

    public List<Material> getQuadCreateBlocks();

}
