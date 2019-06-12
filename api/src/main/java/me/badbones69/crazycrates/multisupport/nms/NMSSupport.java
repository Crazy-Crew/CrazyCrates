package me.badbones69.crazycrates.multisupport.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.util.List;

public interface NMSSupport {
	
	void openChest(Block block, Boolean open);
	
	void pasteSchematic(File f, Location loc);
	
	List<Location> getLocations(File f, Location loc);
	
	List<Material> getQuadCrateBlacklistBlocks();
	
}