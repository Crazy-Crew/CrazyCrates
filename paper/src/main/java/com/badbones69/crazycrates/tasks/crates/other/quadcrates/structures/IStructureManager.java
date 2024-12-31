package com.badbones69.crazycrates.tasks.crates.other.quadcrates.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * A structure manager interface for managing and manipulating {@link Structure}.
 * Provides methods for loading, saving, pasting, and removing structures.
 *
 * @author Ryder Belserion
 * @version 0.1.0
 * @since 0.1.0
 */
public interface IStructureManager {

    /**
     * Loads the specified file as a structure.
     *
     * @param file the file to load as a structure
     * @since 0.1.0
     */
    void applyStructure(@Nullable final File file);

    /**
     * Gets the {@link StructureManager} from the current server context.
     *
     * @return the structure manager
     * @since 0.1.0
     */
    StructureManager getStructureManager();

    /**
     * Saves a structure defined by two locations to the specified file.
     *
     * @param file the file to save the structure to
     * @param one the first location defining the structure boundary
     * @param two the second location defining the structure boundary
     * @param includeEntities whether to include entities in the saved structure
     * @since 0.1.0
     */
    void saveStructure(@Nullable final File file, @Nullable final Location one, @Nullable final Location two, final boolean includeEntities);

    /**
     * Pastes the structure at the specified location.
     *
     * @param location the location to paste the structure
     * @param storeBlocks whether to store old blocks to restore later
     * @since 0.1.0
     */
    void pasteStructure(@Nullable final Location location, final boolean storeBlocks);

    /**
     * Removes the structure.
     *
     * @since 0.1.0
     */
    void removeStructure();

    /**
     * Gets the blocks of the structure at the specified location.
     *
     * @param location the location to check for structure blocks
     * @return a set of locations representing the structure's blocks
     * @since 0.1.0
     */
    Set<Location> getBlocks(@Nullable final Location location);

    /**
     * Gets the x coordinate of the structure.
     *
     * @return the x coordinate of the structure
     * @since 0.1.0
     */
    double getStructureX();

    /**
     * Gets the y coordinate of the structure.
     *
     * @return the y coordinate of the structure
     * @since 0.1.0
     */
    double getStructureY();

    /**
     * Gets the z coordinate of the structure.
     *
     * @return the z coordinate of the structure
     * @since 0.1.0
     */
    double getStructureZ();

    /**
     * Gets a set of nearby blocks to store and restore them after the structure is removed.
     *
     * @return a set of nearby block locations
     * @since 0.1.0
     */
    Set<Location> getNearbyBlocks();

    /**
     * Gets a list of materials that cannot be overridden when pasting a structure.
     *
     * @return a list of materials that are blacklisted from being overridden
     * @since 0.1.0
     */
    List<Material> getBlockBlacklist();

    /**
     * Creates the structure.
     *
     * @since 0.1.0
     */
    void createStructure();

    /**
     * Loads the structure file from the server files if not null.
     *
     * @return the structure file
     * @since 0.1.0
     */
    File getStructureFile();

}