package me.badbones69.crazycrates.api.managers;

import org.bukkit.configuration.file.FileConfiguration;

public class QuickCrateManager extends CrateManager {

    private final FileConfiguration file;
    private final int maxShiftOpen;

    public QuickCrateManager(FileConfiguration file) {
        this.file = file;
        String path = "Crate.Crate-Type-Settings.";
        maxShiftOpen = file.getInt(path + "Max-Keys-With-Shift", -1);
    }

    public FileConfiguration getFile() {
        return file;
    }

    public int getMaxShiftOpen() {
        return maxShiftOpen;
    }
}
