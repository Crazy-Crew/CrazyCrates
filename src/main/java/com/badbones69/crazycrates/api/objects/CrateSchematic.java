package com.badbones69.crazycrates.api.objects;

import java.io.File;

public record CrateSchematic(String schematicName, File schematicFile) {

    /**
     * @return Returns the name of the schematic.
     */
    @Override
    public String schematicName() {
        return schematicName;
    }

    /**
     * @return Returns the file where the schematic is located.
     */
    @Override
    public File schematicFile() {
        return schematicFile;
    }

}