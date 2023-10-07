package us.crazycrew.crazycrates.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.Messages;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.io.File;

public class ConfigManager {

    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager pluginConfig;
    private SettingsManager messages;
    private SettingsManager config;

    public void load() {
        // Create the plugin-config.yml file.
        File pluginConfigFile = new File(this.dataFolder, "plugin-config.yml");

        // Bind it to settings manager
        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        File configFile = new File(this.dataFolder, "config.yml");

        this.config = SettingsManagerBuilder
                .withYamlFile(configFile)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(Config.class))
                .create();

        File localeDir = new File(this.dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File messagesFile = new File(localeDir, this.config.getProperty(PluginConfig.locale_file) + ".yml");

        this.messages = SettingsManagerBuilder
                .withYamlFile(messagesFile)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();
    }

    public void reload() {
        // Reload plugin-config.yml
        this.pluginConfig.reload();

        // Reload config.yml
        this.config.reload();

        // Reload messages.yml
        this.messages.reload();
    }

    @NotNull
    public SettingsManager getPluginConfig() {
        return this.pluginConfig;
    }

    @NotNull
    public SettingsManager getConfig() {
        return this.config;
    }

    @NotNull
    public SettingsManager getMessages() {
        return this.messages;
    }
}