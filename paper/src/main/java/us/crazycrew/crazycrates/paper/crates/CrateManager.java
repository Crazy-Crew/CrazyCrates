package us.crazycrew.crazycrates.paper.crates;

import com.ryderbelserion.cluster.api.adventure.FancyLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.config.CrateConfig;
import us.crazycrew.crazycrates.paper.crates.object.Crate;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CrateManager {

    private final CrazyCrates plugin;

    public CrateManager(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    private final HashSet<Crate> crates = new HashSet<>();

    public void load() {
        File cratesDirectory = new File(this.plugin.getDataFolder(), "crates");

        // If for some reason, the crates folder doesn't exist, We return.
        if (!cratesDirectory.exists()) return;

        if (getCratesList() == null) {
            FancyLogger.error("Could not read from crates directory! " + cratesDirectory.getAbsolutePath());
            return;
        }

        for (File file : getCratesList()) {
            FancyLogger.info("Loading crate: " + file.getName());

            CrateConfig config = new CrateConfig(file);

            try {
                config.load();
            } catch (IOException exception) {
                FancyLogger.error("Could not load crate file: " + file.getName(), exception);
                continue;
            } catch (InvalidConfigurationException exception) {
                FancyLogger.error("Crate file has invalid yaml syntax: " + file.getName(), exception);
                continue;
            }

            if (!config.isEnabled()) {
                if (this.plugin.isLogging()) FancyLogger.warn(config.getFile().getName() + " is currently disabled. Config Option: " + config.isEnabled());
                return;
            }

            Crate crate = new Crate(this.plugin, config);

            this.crates.add(crate);
        }
    }

    public void unload() {
        this.crates.forEach(crate -> {
            //TODO() Stop everything.
        });

        this.crates.clear();
    }

    public Set<Crate> getCrates() {
        return Collections.unmodifiableSet(this.crates);
    }

    public Crate getCrate(String crateName) {
        for (Crate crate : getCrates()) {
            if (crateName.equalsIgnoreCase(crate.getFileName())) return crate;
        }

        return null;
    }

    public File[] getCratesList() {
        File cratesDirectory = new File(this.plugin.getDataFolder(), "crates");

        return cratesDirectory.listFiles((dir, name) -> name.endsWith(".yml"));
    }
}