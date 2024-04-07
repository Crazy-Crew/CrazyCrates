package com.badbones69.crazycrates.support;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class StructureHandler {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final File file;

    public StructureHandler(File file) {
        this.file = file;
    }

    private final List<Location> structureBlocks = new ArrayList<>();
    private final List<Location> preStructureBlocks = new ArrayList<>();

    private StructureManager getStructureManager() {
        return this.plugin.getServer().getStructureManager();
    }

    private BlockVector getStructureSize() {
        try {
            return getStructureManager().loadStructure(this.file).getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pasteStructure(Location location) {
        try {
            getBlocks(location);

            getStructureManager().loadStructure(this.file).place(location.subtract(2, 0.0, 2), false, StructureRotation.NONE, Mirror.NONE, 0, 1F, ThreadLocalRandom.current());

            getStructureBlocks(location);
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to paste structure using " + this.file.getName(), exception);
        }
    }

    public void removeStructure() {
        this.structureBlocks.forEach(block -> {
            Location location = block.toBlockLocation();

            location.getBlock().setType(Material.AIR, true);
        });
    }

    private void getStructureBlocks(Location location) {
        for (int x = 0; x < getStructureX(); x++) {
            for (int y = 0; y < getStructureY(); y++) {
                for (int z = 0; z < getStructureZ(); z++) {
                    Block relativeLocation = location.getBlock().getRelative(x, y, z);

                    List<Location> relativeBlocks = new ArrayList<>();

                    relativeBlocks.add(relativeLocation.getLocation());
                    this.structureBlocks.addAll(relativeBlocks);

                    this.structureBlocks.forEach(block -> {
                        Location blockLoc = block.toBlockLocation();

                        blockLoc.getBlock().getState().update();
                    });
                }
            }
        }
    }

    public List<Location> getBlocks(Location location) {
        for (int x = 0; x < getStructureX(); x++) {
            for (int y = 0; y < getStructureY(); y++) {
                for (int z = 0; z < getStructureZ(); z++) {
                    Block relativeLocation = location.getBlock().getRelative(x, y, z).getLocation().subtract(2, 0.0, 2).getBlock();

                    this.preStructureBlocks.add(relativeLocation.getLocation());
                }
            }
        }

        return getNearbyBlocks();
    }

    public double getStructureX() {
        try {
            return getStructureSize().getX();
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to get structure size x");
        }

        return 0;
    }

    public double getStructureY() {
        try {
            return getStructureSize().getY();
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to get structure size y");
        }

        return 0;
    }

    public double getStructureZ() {
        try {
            return getStructureSize().getZ();
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to get structure size z");
        }

        return 0;
    }

    public List<Location> getNearbyBlocks() {
        return Collections.unmodifiableList(this.preStructureBlocks);
    }

    public List<Material> getBlockBlackList() {
        return Lists.newArrayList(Material.ACACIA_SIGN, Material.BIRCH_SIGN, Material.DARK_OAK_SIGN, Material.JUNGLE_SIGN, Material.OAK_SIGN,
                Material.SPRUCE_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.OAK_WALL_SIGN,
                Material.SPRUCE_WALL_SIGN,Material.STONE_BUTTON,Material.BIRCH_BUTTON,Material.ACACIA_BUTTON,Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.SPRUCE_BUTTON);
    }
}