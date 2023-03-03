package us.crazycrew.crazycrates.configurations.migrations;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.simpleyaml.configuration.file.YamlConfiguration;
import us.crazycrew.crazycore.CrazyCore;
import us.crazycrew.crazycore.CrazyLogger;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Date: 3/1/2023
 * Time: 12:41 PM
 * Last Edited: 3/1/2023 @ 12:42 PM
 *
 * Description: Migrate old values to new values.
 */
public class PluginMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
        //return moveProperty(oldPrefix, newPrefix, reader, configurationData);
        return false;
    }

    private boolean convert(PropertyReader reader, String oldValue, String newFile, boolean cascade) {
        if (reader.contains(oldValue)) {
            Path nFile = CrazyCore.api().getDirectory().resolve(newFile);

            YamlConfiguration yamlNewFile = null;

            try {
                yamlNewFile = YamlConfiguration.loadConfiguration(nFile.toFile());
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            CrazyLogger.info("Starting the config migration process...");
            CrazyLogger.info("Found old config value (" + oldValue + ")");

            if (!nFile.toFile().exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    nFile.toFile().createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            if (yamlNewFile == null) return false;

            for (String child : reader.getChildKeys(oldValue)) {
                if (cascade) {
                    for (String doubleChild : reader.getChildKeys(child)) {
                        yamlNewFile.set(doubleChild, reader.getObject(doubleChild));
                    }
                } else {
                    yamlNewFile.set(child, reader.getObject(child));
                }
            }

            try {
                yamlNewFile.save(nFile.toFile());

                CrazyLogger.info("The migration process is complete!");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return true;
        }

        return false;
    }
}