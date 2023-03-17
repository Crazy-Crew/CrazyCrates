package us.crazycrew.crazycrates;

import com.badbones69.crazycrates.support.libraries.UpdateChecker;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycrates.commands.Permissions;

public class CrazyCrates extends JavaPlugin {

    private final PaperCore paperCore;

    private final PluginProviderContext context;

    private static CrazyCrates plugin;

    public CrazyCrates(PaperCore paperCore, PluginProviderContext context) {
        this.paperCore = paperCore;

        this.context = context;

        plugin = this;
    }

    @Override
    @NotNull
    public java.util.logging.Logger getLogger() {
        return CrazyLogger.getLogger();
    }

    @Override
    public void onEnable() {
        getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UpdateChecker updateChecker = new UpdateChecker(17599, context);

            try {
                if (!updateChecker.update()) return;

                String name = context.getConfiguration().getName();
                String version = updateChecker.getNewVersion();
                String current = context.getConfiguration().getVersion();
                String download = updateChecker.getResourcePage();

                String updateMessage = """
                        %s has a new update available. New version: %s
                        Current Version: %s
                        Download: %s
                        """;

                CrazyLogger.info(String.format(updateMessage, name, version, current, download));
            } catch (Exception exception) {
                CrazyLogger.info(exception.getMessage());
            }
        });

        Permissions.register(this.getServer().getPluginManager());
    }

    @Override
    public void onDisable() {

    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public PaperCore getCrazyCore() {
        return this.paperCore;
    }
}