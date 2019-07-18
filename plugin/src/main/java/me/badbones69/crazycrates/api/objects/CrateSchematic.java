package me.badbones69.crazycrates.api.objects;

import java.io.File;

public class CrateSchematic {
	
	private String schematicName;
	private File schematicFile;
	
	public CrateSchematic(String schematicName, File schematicFile) {
		this.schematicName = schematicName;
		this.schematicFile = schematicFile;
	}
	
	public String getSchematicName() {
		return schematicName;
	}
	
	public File getSchematicFile() {
		return schematicFile;
	}
	
}