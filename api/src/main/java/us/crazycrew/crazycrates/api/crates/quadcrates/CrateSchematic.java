package us.crazycrew.crazycrates.api.crates.quadcrates;

import java.io.File;

/**
 * A class for crate schematics
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 */
public record CrateSchematic(String getSchematicName, File getSchematicFile) {}