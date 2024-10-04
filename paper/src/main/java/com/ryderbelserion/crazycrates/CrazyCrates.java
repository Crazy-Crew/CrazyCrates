package com.ryderbelserion.crazycrates;

import com.ryderbelserion.crazycrates.common.plugin.AbstractCratesPlugin;
import com.ryderbelserion.crazycrates.common.plugin.logger.PluginLogger;
import com.ryderbelserion.crazycrates.loader.CrazyPlugin;
import org.bukkit.plugin.ServicePriority;
import us.crazycrew.crazycrates.CrazyCratesApi;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CrazyCrates extends AbstractCratesPlugin {

    private final CrazyPlugin plugin;

    public CrazyCrates(final CrazyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void registerPlatformAPI(final CrazyCratesApi api) {
        this.plugin.getServer().getServicesManager().register(CrazyCratesApi.class, api, this.plugin, ServicePriority.Normal);
    }

    @Override
    protected void registerListeners() {

    }

    @Override
    protected void registerCommands() {

    }

    @Override
    protected void setupManagers() {

    }

    @Override
    public void reload() {

    }

    @Override
    public Path getDataDirectory() {
        return null;
    }

    @Override
    public PluginLogger getLogger() {
        return null;
    }

    @Override
    public Collection<String> getPlayerList() {
        return List.of();
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        return List.of();
    }

    @Override
    public UserManager getUserManager() {
        return null;
    }
}