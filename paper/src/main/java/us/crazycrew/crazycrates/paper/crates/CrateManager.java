package us.crazycrew.crazycrates.paper.crates;

import org.bukkit.configuration.InvalidConfigurationException;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.config.CrateConfig;
import us.crazycrew.crazycrates.paper.crates.object.Crate;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

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
            this.plugin.getLogger().severe("Could not read from crates directory! " + cratesDirectory.getAbsolutePath());
            return;
        }

        for (File file : getCratesList()) {
            this.plugin.getLogger().info("Loading crate: " + file.getName());

            CrateConfig config = new CrateConfig(file);

            try {
                config.load();
            } catch (IOException exception) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not load crate file: " + file.getName(), exception);
                continue;
            } catch (InvalidConfigurationException exception) {
                this.plugin.getLogger().log(Level.SEVERE, "Crate file has invalid yaml syntax: " + file.getName(), exception);
                continue;
            }

            if (!config.isEnabled()) {
                if (this.plugin.isLogging()) this.plugin.getLogger().warning(config.getFile().getName() + " is currently disabled. Config Option: " + config.isEnabled());
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