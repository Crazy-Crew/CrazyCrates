package us.crazycrew.crazycrates.configurations.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;

/**
 * Description: Migrate old values to new values.
 */
public class ConfigMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
        return false;
    }
}