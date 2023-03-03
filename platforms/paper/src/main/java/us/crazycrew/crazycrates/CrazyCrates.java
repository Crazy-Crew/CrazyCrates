package us.crazycrew.crazycrates;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperCore;
import us.crazycrew.crazycrates.commands.Permissions;

public class CrazyCrates extends JavaPlugin {

    private final PaperCore paperCore;

    private static CrazyCrates plugin;

    public CrazyCrates(PaperCore paperCore) {
        this.paperCore = paperCore;

        plugin = this;
    }

    @Override
    @NotNull
    public java.util.logging.Logger getLogger() {
        return CrazyLogger.getLogger();
    }

    @Override
    public void onEnable() {
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