package com.badbones69.crazycrates.api.enums;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

public enum CustomFiles {

    messages("messages.yml"),
    config("config.yml");

    private final SettingsManager settingsManager;
    private final String fileName;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    CustomFiles(@NotNull final String fileName) {
        this.fileName = fileName;
        this.settingsManager = ConfigManager.getYamlManager().getFile(this.fileName);
    }

    /**
     * @return the file name
     */
    public @NotNull final String getFileName() {
        return this.fileName;
    }

    /**
     * @return the file configuration
     */
    public @NotNull final SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    /**
     * Save the file
     */
    public void save() {
        getSettingsManager().save();
    }

    /**
     * Reload the file
     */
    public void reload() {
        getSettingsManager().reload();
    }
}