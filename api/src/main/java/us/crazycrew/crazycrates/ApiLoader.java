package us.crazycrew.crazycrates;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import us.crazycrew.crazycore.CrazyCore;
import us.crazycrew.crazycrates.configurations.ConfigSettings;
import us.crazycrew.crazycrates.configurations.PluginSettings;
import us.crazycrew.crazycrates.configurations.migrate.manual.MigrationService;
import java.io.File;

public class ApiLoader {

    private static SettingsManager pluginConfig;
    private static SettingsManager config;

    public static void load() {
        MigrationService migrationService = new MigrationService();

        migrationService.convert();

        pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(CrazyCore.api().getDirectory().toFile(), "plugin-settings.yml"))
                .configurationData(PluginSettings.class).create();

        config = SettingsManagerBuilder
                .withYamlFile(new File(CrazyCore.api().getDirectory().toFile(), "config.yml"))
                .configurationData(ConfigSettings.class).create();
    }

    public static SettingsManager getPluginConfig() {
        return pluginConfig;
    }

    public static SettingsManager getConfig() {
        return config;
    }
}