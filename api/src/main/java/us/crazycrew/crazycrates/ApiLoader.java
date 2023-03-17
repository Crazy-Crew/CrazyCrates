package us.crazycrew.crazycrates;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import us.crazycrew.crazycore.CrazyCore;
import us.crazycrew.crazycrates.configurations.CrateConfigurationBuilder;
import us.crazycrew.crazycrates.configurations.migrate.manual.MigrationService;
import java.io.File;

public class ApiLoader {

    private SettingsManager pluginConfig;
    private SettingsManager config;

    public void load() {
        MigrationService migrationService = new MigrationService();

        migrationService.convert();

        pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(CrazyCore.api().getDirectory().toFile(), "plugin-settings.yml"))
                .configurationData(CrateConfigurationBuilder.buildConfigurationData()).create();

        config = SettingsManagerBuilder
                .withYamlFile(new File(CrazyCore.api().getDirectory().toFile(), "config.yml"))
                .configurationData(CrateConfigurationBuilder.buildConfigurationData()).create();
    }

    public SettingsManager getPluginConfig() {
        return this.pluginConfig;
    }

    public SettingsManager getConfig() {
        return this.config;
    }
}