package com.badbones69.crazycrates.support.structures.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.List;

interface StructureControl {

    void pasteStructure(Location location);

    void removeStructure(Location location);

    void saveSchematic(Location[] locations);

    double getStructureX();

    double getStructureZ();

    List<Material> getBlackList();

    List<Block> getStructureBlocks(Location location);

    List<Block> getNearbyBlocks(Location location);

}