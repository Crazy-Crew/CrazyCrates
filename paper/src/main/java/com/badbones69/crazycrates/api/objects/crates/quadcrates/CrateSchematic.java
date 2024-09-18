package com.badbones69.crazycrates.api.objects.crates.quadcrates;

import java.io.File;

/**
 * A record holding the data required to load a schematic
 *
 * @author Ryder Belserion
 * @version 0.6
 * @since 0.1
 *
 * @param schematicName the name of the file
 * @param schematicFile the file object
 */
public record CrateSchematic(String schematicName, File schematicFile) {}