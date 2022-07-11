package com.badbones69.crazycrates.support.structures;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.support.structures.interfaces.StructureControl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.StructureManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureHandler implements StructureControl {


    private final File file;

    public StructureHandler(File file) {
        this.file = file;
    }

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private final ArrayList<Block> structureBlocks = new ArrayList<>();

    private final ArrayList<Block> preStructureBlocks = new ArrayList<>();

    StructureManager structureManager = null;

    @Override
    public void pasteStructure(Location location) {
        try {

            structureManager = crazyManager.getPlugin().getServer().getStructureManager();

            getNearbyBlocks(location);

            structureManager.loadStructure(file).place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, new Random());

            getStructureBlocks(location);
        } catch (Exception e) {
            crazyManager.getPlugin().getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void removeStructure(Location location) {
        structureBlocks.forEach(block -> block.getLocation().getBlock().setType(Material.AIR));
    }

    @Override
    public void saveSchematic(Location[] locations) {
        // Not available for use.
    }

    @Override
    public double getStructureX() {
        try {
            return structureManager.loadStructure(file).getSize().getX();
        } catch (Exception e) {
            crazyManager.getPlugin().getLogger().warning(e.getMessage());
        }

        return 0;
    }

    @Override
    public double getStructureZ() {
        try {
            return structureManager.loadStructure(file).getSize().getZ();
        } catch (Exception e) {
            crazyManager.getPlugin().getLogger().warning(e.getMessage());
        }

        return 0;
    }

    @Override
    public List<Material> getBlackList() {
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
    public List<Block> getStructureBlocks(Location location) throws IOException {

        for (int x = 0; x < structureManager.loadStructure(file).getSize().getX(); x++) {
            for (int y = 0; y < structureManager.loadStructure(file).getSize().getY(); y++) {
                for (int z = 0; z < structureManager.loadStructure(file).getSize().getZ(); z++) {
                    structureBlocks.add(location.getBlock().getRelative(x, y, z));
                }
            }
        }

        return structureBlocks;
    }

    @Override
    public List<Block> getNearbyBlocks(Location location) throws IOException {

        for (int x = 0; x < structureManager.loadStructure(file).getSize().getX(); x++) {
            for (int y = 0; y < structureManager.loadStructure(file).getSize().getY(); y++) {
                for (int z = 0; z < structureManager.loadStructure(file).getSize().getZ(); z++) {
                    preStructureBlocks.add(location.getBlock().getRelative(x, y, z));
                }
            }
        }

        return preStructureBlocks;
    }
}