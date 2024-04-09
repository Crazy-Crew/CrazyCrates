package us.crazycrew.crazycrates.platform.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import com.ryderbelserion.vital.api.ServerProvider;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.logging.Logger;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigMigration extends PlainMigrationService {

    private static final Logger logger = ServerProvider.get().getLogger();

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateConfig(reader, configurationData);
    }

    private boolean migrateConfig(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        String oldPath = "Settings.Enable-Crate-Menu";

        if (reader.getString(oldPath) == null) {
            return false;
        }

        return moveProperty(oldPrefix, ConfigKeys.command_prefix, reader, configurationData);
    }

    private <T> boolean moveProperty(String oldProperty, Property<T> newProperty, PropertyReader reader, ConfigurationData data) {
        if (!reader.contains(newProperty.getPath())) {
            String key = reader.getString(oldProperty);


            if (key != null) {
                PropertyValue<T> propertyKey = data.
                data.setValue(newProperty, key);
            }

            return true;
        }

        return false;
    }


    /**
     * Checks for an old property path and moves it to a new path if it is present and the new path is not yet set.
     *
     * @param oldProperty The old property (create a temporary {@link Property} object with the path)
     * @param newProperty The new property to move the value to
     * @param reader The property reader
     * @param configData Configuration data
     * @param <T> The type of the property
     * @return True if a migration has been done, false otherwise
     */
    protected static <T> boolean moveProperty(Property<T> oldProperty, Property<T> newProperty, PropertyReader reader, ConfigurationData configData) {
        PropertyValue<T> oldPropertyValue = oldProperty.determineValue(reader);

        if (oldPropertyValue.isValidInResource()) {
            if (reader.contains(newProperty.getPath())) {
                logger.info("Detected deprecated property " + oldProperty.getPath());
            } else {
                logger.info("Renaming " + oldProperty.getPath() + " to " + newProperty.getPath());

                configData.setValue(newProperty, oldPropertyValue.getValue());
            }

            return true;
        }

        return false;
    }
}