package us.crazycrew.crazycrates.crates.quadcrates;

import java.io.File;

public record CrateSchematic(String getSchematicName, File getSchematicFile) {

    @Override
    public String getSchematicName() {
        return this.getSchematicName;
    }

    @Override
    public File getSchematicFile() {
        return this.getSchematicFile;
    }
}