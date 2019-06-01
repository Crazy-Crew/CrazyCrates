package me.badbones69.crazycrates.multisupport.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.util.List;

public interface NMSSupport {
	
	public void openChest(Block b, Location location, Boolean open);
	
	public void rotateChest(Block block, Byte direction);
	
	public List<Location> pasteSchematic(File f, Location loc);
	
	public List<Location> getLocations(File f, Location loc);
	
	public List<Material> getQuadCrateBlacklistBlocks();
	
}