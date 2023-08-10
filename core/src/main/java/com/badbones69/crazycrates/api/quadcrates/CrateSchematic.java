package com.badbones69.crazycrates.api.quadcrates;

import java.io.File;

/**
 * A constructor to build a schematic reference.
 *
 * @param schematicName the name of the schematic
 * @param schematicFile the schematic file
 */
public record CrateSchematic(String schematicName, File schematicFile) {

    /**
     * @return returns the name of the schematic.
     */
    @Override
    public String schematicName() {
        return schematicName;
    }

    /**
     * @return returns the file where the schematic is located.
     */
    @Override
    public File schematicFile() {
        return schematicFile;
    }
}