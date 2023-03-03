package us.crazycrew.crazycrates.loader;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperConsole;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycore.paper.player.PaperPlayerRegistry;
import us.crazycrew.crazycrates.CrazyCrates;
import us.crazycrew.crazycrates.configurations.ConfigSettings;
import us.crazycrew.crazycrates.configurations.PluginSettings;
import us.crazycrew.crazycrates.configurations.migrations.PluginMigrationService;
import java.io.File;
import java.util.logging.LogManager;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/19/2023
 * Time: Unknown
 * Last Edited: 3/3/2023 @ 5:03 PM
 *
 * Description: The starter class that thanks to paper is run directly at server startup and allows us to pass variables through the plugin class.
 */
@SuppressWarnings("UnstableApiUsage")
public class CratesStarter implements PluginBootstrap {

    private PaperCore paperCore;

    private static SettingsManager pluginConfig;
    private static SettingsManager config;

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        this.paperCore = new PaperCore(context.getConfiguration().getName(), context.getDataDirectory());

        //ConfigMigrationService configMigrationService = new ConfigMigrationService();

        //configMigrationService.convert();

        pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile(), "plugin-settings.yml"))
                .configurationData(PluginSettings.class)
                .migrationService(new PluginMigrationService()).create();

        config = SettingsManagerBuilder
                .withYamlFile(new File(context.getDataDirectory().toFile(), "config.yml"))
                .configurationData(ConfigSettings.class)
                .create();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        // Create the player registry.
        this.paperCore.setPaperPlayerRegistry(new PaperPlayerRegistry());

        // Create the console instance.
        this.paperCore.setPaperConsole(new PaperConsole());

        // Set the project prefix.
        this.paperCore.setProjectPrefix(getPluginConfig().getProperty(PluginSettings.CONSOLE_PREFIX));

        // Set the logger name and create it.
        CrazyLogger.setName(this.paperCore.getProjectName());

        // Add the logger manager.
        LogManager.getLogManager().addLogger(CrazyLogger.getLogger());

        return new CrazyCrates(this.paperCore);
    }

    public static SettingsManager getPluginConfig() {
        return pluginConfig;
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public PaperCore getPaperCore() {
        return this.paperCore;
    }
}