package us.crazycrew.crazycrates.paper.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MigrationService {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public void migrate() {
        File input = new File(this.plugin.getDataFolder(), "config.yml");

        CompletableFuture<YamlConfiguration> configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input));

        YamlConfiguration config = configuration.join();

        if (config.getString("Settings.Prefix") == null) return;

        ConfigManager configManager = new ConfigManager(this.plugin.getDataFolder());
        configManager.load();

        SettingsManager pluginConfig = configManager.getPluginConfig();

        String oldPrefix = config.getString("Settings.Prefix", "&8[&bCrazyCrates&8]: ");
        boolean oldMetrics = config.getBoolean("Settings.Toggle-Metrics");

        pluginConfig.setProperty(PluginConfig.command_prefix, oldPrefix);
        pluginConfig.setProperty(PluginConfig.toggle_metrics, oldMetrics);

        config.set("Settings.Prefix", null);
        config.set("Settings.Toggle-Metrics", null);

        try {
            pluginConfig.save();

            config.save(input);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save config.yml or plugin-config.yml", exception);
        }
    }
}