package com.badbones69.crazycrates.paper.support.structures.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.IOException;
import java.util.List;

public interface StructureControl {

    void pasteStructure(Location location);

    void removeStructure(Location location);

    void saveSchematic(Location[] locations);

    double getStructureX();

    double getStructureZ();

    List<Material> getBlackList();

    List<Block> getStructureBlocks(Location location) throws IOException;

    List<Block> getNearbyBlocks(Location location) throws IOException;

}