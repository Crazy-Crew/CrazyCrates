package com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface IStructureManager {

    void applyStructure(@Nullable final File file);

    StructureManager getStructureManager();

    void saveStructure(@Nullable final File file, @Nullable final Location one, @Nullable final Location two, final boolean includeEntities);

    void pasteStructure(@Nullable final Location location, final boolean storeBlocks);

    void removeStructure();

    Set<Location> getBlocks(@Nullable final Location location);

    double getStructureX();

    double getStructureY();

    double getStructureZ();

    Set<Location> getNearbyBlocks();

    List<Material> getBlockBlacklist();

    void createStructure();

    File getStructureFile();

}