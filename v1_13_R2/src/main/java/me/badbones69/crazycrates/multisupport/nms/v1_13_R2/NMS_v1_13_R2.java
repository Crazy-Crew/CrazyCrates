package me.badbones69.crazycrates.multisupport.nms.v1_13_R2;

import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class NMS_v1_13_R2 implements NMSSupport {
	
	@Override
	public void openChest(Block b, Location location, Boolean open) {
		World world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		if(b.getType() == Material.ENDER_CHEST) {
			TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
		}else {
			TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
		}
	}
	
	//Disabled till can be fixed.
	//http://stackoverflow.com/questions/24101928/setting-block-data-from-schematic-in-bukkit
	@Override
	public List<Location> pasteSchematic(File f, Location loc) {
		loc = loc.subtract(2, 1, 2);
		List<Location> locations = new ArrayList<>();
		try {
			FileInputStream fis = new FileInputStream(f);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
			short width = nbt.getShort("Width");
			short height = nbt.getShort("Height");
			short length = nbt.getShort("Length");
			byte[] blocks = nbt.getByteArray("Blocks");
			byte[] data = nbt.getByteArray("Data");
			fis.close();
			//paste
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					for(int z = 0; z < length; ++z) {
						int index = y * width * length + z * width + x;
						final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
						int b = blocks[index] & 0xFF;//make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted
						final Block block = l.getBlock();
						//Material m = Material.getMaterial(b);
						//block.setType(m);
						//you can check what type the block is here, like if(m.equals(Material.BEACON)) to check if it's a beacon
						locations.add(l);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	@Override
	public List<Location> getLocations(File f, Location loc) {
		loc = loc.subtract(2, 1, 2);
		List<Location> locations = new ArrayList<>();
		try {
			FileInputStream fis = new FileInputStream(f);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
			short width = nbt.getShort("Width");
			short height = nbt.getShort("Height");
			short length = nbt.getShort("Length");
			fis.close();
			//paste
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					for(int z = 0; z < length; ++z) {
						final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
						locations.add(l);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	@Override
	public List<Material> getQuadCreateBlocks() {
		List<Material> blockList = new ArrayList<>();
		blockList.add(Material.SIGN);
		blockList.add(Material.WALL_SIGN);
		blockList.add(Material.STONE_BUTTON);
		blockList.add(Material.BIRCH_BUTTON);
		blockList.add(Material.ACACIA_BUTTON);
		blockList.add(Material.DARK_OAK_BUTTON);
		blockList.add(Material.JUNGLE_BUTTON);
		blockList.add(Material.OAK_BUTTON);
		blockList.add(Material.SPRUCE_BUTTON);
		blockList.add(Material.STONE_BUTTON);
		return null;
	}
	
}