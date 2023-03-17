package us.crazycrew.crazycrates.configurations;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import us.crazycrew.crazycrates.configurations.sections.ConfigSettings;
import us.crazycrew.crazycrates.configurations.sections.PluginSettings;

public class CrateConfigurationBuilder {

    /**
     * Private constructor to prevent instantiation
     */
    private CrateConfigurationBuilder() {}

    /**
     * Builds the core configuration data.
     *
     * @return configuration data object containing the main crate configurations.
     */
    public static ConfigurationData buildConfigurationData() {
        return ConfigurationDataBuilder.createConfiguration(
                PluginSettings.class,
                ConfigSettings.class
        );
    }
}