package us.crazycrew.crazycrates.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.Messages;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.common.config.types.menus.MainMenuConfig;
import us.crazycrew.crazycrates.common.config.types.menus.PreviewMenuConfig;
import java.io.File;

public class ConfigManager {

    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager pluginConfig;
    private SettingsManager messages;
    private SettingsManager config;

    private SettingsManager mainMenuConfig;
    private SettingsManager previewMenuConfig;

    public void load() {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        // Create the plugin-config.yml file.
        File pluginConfigFile = new File(this.dataFolder, "plugin-config.yml");

        // Bind it to settings manager
        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile, builder)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        File configFile = new File(this.dataFolder, "config.yml");

        this.config = SettingsManagerBuilder
                .withYamlFile(configFile, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(Config.class))
                .create();

        File localeDir = new File(this.dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File messagesFile = new File(localeDir, this.pluginConfig.getProperty(PluginConfig.locale_file) + ".yml");

        this.messages = SettingsManagerBuilder
                .withYamlFile(messagesFile, builder)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();

        File mainMenuFile = new File(this.dataFolder, "/menus/crate-menu.yml");

        this.mainMenuConfig = SettingsManagerBuilder
                .withYamlFile(mainMenuFile, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(MainMenuConfig.class))
                .create();

        File previewMenuFile = new File(this.dataFolder, "/menus/preview-menu.yml");

        this.previewMenuConfig = SettingsManagerBuilder
                .withYamlFile(previewMenuFile, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(PreviewMenuConfig.class))
                .create();
    }

    public void reload() {
        // Reload plugin-config.yml
        this.pluginConfig.reload();

        // Reload config.yml
        this.config.reload();

        // Reload messages.yml
        this.messages.reload();

        // Reload crate-menu.yml
        this.mainMenuConfig.reload();

        // Reload preview-menu.yml
        this.previewMenuConfig.reload();
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

    @NotNull
    public SettingsManager getMainMenuConfig() {
        return this.mainMenuConfig;
    }

    @NotNull
    public SettingsManager getPreviewMenuConfig() {
        return this.previewMenuConfig;
    }
}