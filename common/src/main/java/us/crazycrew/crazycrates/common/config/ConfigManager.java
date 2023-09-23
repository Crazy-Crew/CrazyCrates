package us.crazycrew.crazycrates.common.config;

import ch.jalu.configme.SettingsManager;
import java.io.File;

public class ConfigManager {

    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager messages;
    private SettingsManager config;

    public void load() {

    }

    public void reload() {

    }

    private void createLocale() {

    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getMessages() {
        return this.messages;
    }
}