package us.crazycrew.crazycrates.paper.api.plugin.migration;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class MigrationService {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private SettingsManager pluginConfig;

    public void migrate() {
        copyPluginConfig();

        copyConfig();

        copyMessages();
    }

    private void copyPluginConfig() {
        File input = new File(this.plugin.getDataFolder(),"config.yml");

        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        // Check if this exists, and if not we return.
        if (file.getString("Settings.Prefix") == null) return;

        // Fetch the values I want to migrate
        String oldPrefix = file.getString("Settings.Prefix");
        boolean oldMetrics = file.getBoolean("Settings.Toggle-Metrics");

        // Create the plugin-config.yml file.
        File pluginConfigFile = new File(this.plugin.getDataFolder(), "plugin-config.yml");

        // Bind it to settings manager
        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        this.pluginConfig.setProperty(PluginConfig.toggle_metrics, oldMetrics);

        if (oldPrefix != null) {
            this.pluginConfig.setProperty(PluginConfig.command_prefix, oldPrefix);
        }

        file.set("Settings.Prefix", null);
        file.set("Settings.Toggle-Metrics", null);

        try {
            this.pluginConfig.save();

            file.save(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void copyConfig() {

    }

    private void copyMessages() {

    }
}