package us.crazycrew.crazycrates.api.crates.quadcrates;

import java.io.File;

/**
 * A class for crate schematics
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 *
 * @param schematicName the name of the file
 * @param schematicFile the file object
 */
public record CrateSchematic(String schematicName, File schematicFile) {}