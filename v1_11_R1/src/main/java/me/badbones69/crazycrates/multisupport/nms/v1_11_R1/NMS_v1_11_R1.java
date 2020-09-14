package me.badbones69.crazycrates.multisupport.nms.v1_11_R1;

import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class NMS_v1_11_R1 implements NMSSupport {
    
    @Override
    public void openChest(Block block, boolean open) {
        Material type = block.getType();
        if (type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.ENDER_CHEST) {
            World world = ((CraftWorld) block.getWorld()).getHandle();
            BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
            if (block.getType() == Material.ENDER_CHEST) {
                TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
                world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
            } else {
                TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
                world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
            }
        }
    }
    
    @Override
    public void saveSchematic(Location[] locations, String owner, File file) {
    
    }
    
    //http://stackoverflow.com/questions/24101928/setting-block-data-from-schematic-in-bukkit
    @Override
    public void pasteSchematic(File f, Location loc) {
        loc = loc.subtract(2, 1, 2);
        try (FileInputStream fis = new FileInputStream(f)) {
            NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            byte[] blocks = nbt.getByteArray("Blocks");
            byte[] data = nbt.getByteArray("Data");
            //paste
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        int index = y * width * length + z * width + x;
                        final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
                        int b = blocks[index] & 0xFF;//make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted
                        final Block block = l.getBlock();
                        block.setType(Material.getMaterial(b));
                        block.setData(data[index]);
                        //you can check what type the block is here, like if(m.equals(Material.BEACON)) to check if it's a beacon
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Location> getLocations(File f, Location loc) {
        loc = loc.subtract(2, 1, 2);
        List<Location> locations = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(f)) {
            NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            //paste
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        final Location l = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ());
                        locations.add(l);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }
    
    @Override
    public List<Material> getQuadCrateBlacklistBlocks() {
        List<Material> blockList = new ArrayList<>();
        blockList.add(Material.SIGN);
        blockList.add(Material.WALL_SIGN);
        blockList.add(Material.STONE_BUTTON);
        blockList.add(Material.WOOD_BUTTON);
        return blockList;
    }
    
    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
    
}