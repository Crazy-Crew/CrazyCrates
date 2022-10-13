package me.badbones69.crazycrates.multisupport.nms.v1_18_R2;

import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.entity.TileEntityEnderChest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NMS_v1_18_R2 implements NMSSupport {

    @Override
    public void openChest(Block block, boolean open) {
        Material type = block.getType();
        if (type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.ENDER_CHEST) {
            World world = ((CraftWorld) block.getWorld()).getHandle();
            BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
            if (block.getType() == Material.ENDER_CHEST) {
                TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getBlockEntity(position, false);
                world.a(position, tileChest.q(), 1, open ? 1 : 0);
            } else {
                TileEntityChest tileChest = (TileEntityChest) world.getBlockEntity(position, false);
                world.a(position, tileChest.q(), 1, open ? 1 : 0);
            }
        }
    }

    @Override
    public void saveSchematic(Location[] locations, String owner, File file) {
        try {
            StructureService.createAndSaveAny(locations, owner, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //https://www.spigotmc.org/threads/pasting-schematics-in-1-13.333643/#post-3312204
    @Override
    public void pasteSchematic(File f, Location loc) {
        try {
            Location[] locations = StructureService.normalizeEdges(loc, StructureService.getOtherEdge(f, loc));
            int[] dimensions = StructureService.getDimensions(locations);
            //Math.floor allows it to round a double to the lowest whole number
            StructureService.loadAndInsertAny(f, loc.subtract(Math.floor(dimensions[0] / 2), 1, Math.floor(dimensions[2] / 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Location> getLocations(File f, Location loc) {
        try {
            Location[] locations = StructureService.normalizeEdges(loc, StructureService.getOtherEdge(f, loc));
            int[] dimensions = StructureService.getDimensions(locations);
            return StructureService.getSingleStructureLocations(f, loc.subtract(Math.floor(dimensions[0] / 2), 1, Math.floor(dimensions[2] / 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Material> getQuadCrateBlacklistBlocks() {
        List<Material> blockList = new ArrayList<>();
        blockList.add(Material.ACACIA_SIGN);
        blockList.add(Material.BIRCH_SIGN);
        blockList.add(Material.DARK_OAK_SIGN);
        blockList.add(Material.JUNGLE_SIGN);
        blockList.add(Material.OAK_SIGN);
        blockList.add(Material.SPRUCE_SIGN);
        blockList.add(Material.ACACIA_WALL_SIGN);
        blockList.add(Material.BIRCH_WALL_SIGN);
        blockList.add(Material.DARK_OAK_WALL_SIGN);
        blockList.add(Material.JUNGLE_WALL_SIGN);
        blockList.add(Material.OAK_WALL_SIGN);
        blockList.add(Material.SPRUCE_WALL_SIGN);
        blockList.add(Material.STONE_BUTTON);
        blockList.add(Material.BIRCH_BUTTON);
        blockList.add(Material.ACACIA_BUTTON);
        blockList.add(Material.DARK_OAK_BUTTON);
        blockList.add(Material.JUNGLE_BUTTON);
        blockList.add(Material.OAK_BUTTON);
        blockList.add(Material.SPRUCE_BUTTON);
        blockList.add(Material.STONE_BUTTON);
        return blockList;
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
}