package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.config.ConfigManager;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import org.jetbrains.annotations.NotNull;

public enum DataFiles {

    locations("locations.yml"),
    data("data.yml");

    private final String fileName;
    private final YamlFile yamlFile;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    DataFiles(@NotNull final String fileName) {
        this.fileName = fileName;
        this.yamlFile = ConfigManager.getYamlManager().getStaticFile(this.fileName);
    }

    /**
     * @return {@link String}
     */
    public @NotNull final String getFileName() {
        return this.fileName;
    }

    /**
     * @return {@link YamlFile}
     */
    public @NotNull final YamlFile getYamlFile() {
        return this.yamlFile;
    }

    public void save() {
        ConfigManager.getYamlManager().saveStaticFile(getFileName());
    }
}