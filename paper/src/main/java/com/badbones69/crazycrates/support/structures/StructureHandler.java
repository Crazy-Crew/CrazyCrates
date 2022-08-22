package com.badbones69.crazycrates.support.structures;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureHandler {

    private final CrazyCrates plugin;
    private final File file;

    public StructureHandler(CrazyCrates plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    private final List<Block> structureBlocks = new ArrayList<>();

    private final List<Block> preStructureBlocks = new ArrayList<>();

    private StructureManager getStructureManager() {
        return plugin.getServer().getStructureManager();
    }

    private BlockVector getStructureSize() {
        try {
            return getStructureManager().loadStructure(file).getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pasteStructure(Location location) {
        try {
            getNearbyBlocks(location);

            getStructureManager().loadStructure(file).place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, new Random());

            getBlocks(true, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeStructure() {
        structureBlocks.forEach(block -> {
            block.getLocation().getBlock().setType(Material.AIR, false);
        });
    }

    public void saveStructure(Location location) {

    }

    public double getStructureX() {
        try {
            return getStructureSize().getX();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getStructureZ() {
        try {
            return getStructureSize().getZ();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void getBlocks(Boolean getStructureBlocks, Location location) {
        for (int x = 0; x < getStructureSize().getX(); x++) {
            for (int y = 0; y < getStructureSize().getY(); y++) {
                for (int z = 0; z < getStructureSize().getZ(); z++) {

                    Block relativeLocation = location.getBlock().getRelative(x, y, z);

                    if (getStructureBlocks) {
                        structureBlocks.add(relativeLocation);

                        structureBlocks.forEach(block -> block.getState().update());

                        return;
                    }

                    preStructureBlocks.add(relativeLocation);
                }
            }
        }
    }

    public List<Block> getNearbyBlocks(Location location) {
        getBlocks(false, location);

        return preStructureBlocks;
    }

    public List<Material> getBlockBlackList() {
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
}