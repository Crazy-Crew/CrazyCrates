package us.crazycrew.crazycrates.loader;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperConsole;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycore.paper.player.PaperPlayerRegistry;
import us.crazycrew.crazycrates.ApiLoader;
import us.crazycrew.crazycrates.CrazyCrates;
import us.crazycrew.crazycrates.configurations.PluginSettings;
import java.util.logging.LogManager;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: The starter class that thanks to paper is run directly at server startup and allows us to pass variables through the plugin class.
 */
public class CrazyStarter implements PluginBootstrap {

    private PaperCore paperCore;

    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        this.paperCore = new PaperCore(context.getConfiguration().getName(), context.getDataDirectory());

        // Load specific shit.
        ApiLoader.load();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        // Create the player registry.
        this.paperCore.setPaperPlayerRegistry(new PaperPlayerRegistry());

        // Create the console instance.
        this.paperCore.setPaperConsole(new PaperConsole());

        // Set the project prefix.
        this.paperCore.setProjectPrefix(ApiLoader.getPluginConfig().getProperty(PluginSettings.CONSOLE_PREFIX));

        // Set the logger name and create it.
        CrazyLogger.setName(this.paperCore.getProjectName());

        // Add the logger manager.
        LogManager.getLogManager().addLogger(CrazyLogger.getLogger());

        return new CrazyCrates(this.paperCore, context);
    }

    public PaperCore getPaperCore() {
        return this.paperCore;
    }
}