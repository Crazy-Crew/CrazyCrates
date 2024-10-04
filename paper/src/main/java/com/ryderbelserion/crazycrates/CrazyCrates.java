package com.ryderbelserion.crazycrates;

import com.ryderbelserion.crazycrates.common.plugin.AbstractCratesPlugin;
import com.ryderbelserion.crazycrates.common.plugin.logger.AbstractLogger;
import com.ryderbelserion.crazycrates.common.plugin.logger.PluginLogger;
import com.ryderbelserion.crazycrates.loader.CrazyPlugin;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import us.crazycrew.crazycrates.CrazyCratesApi;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class CrazyCrates extends AbstractCratesPlugin {

    private final CrazyPlugin plugin;

    private final PluginLogger logger;

    private final Server server;

    public CrazyCrates(final CrazyPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        this.logger = new AbstractLogger(this.plugin.getComponentLogger());
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
    public void reload() {

    }

    @Override
    protected void registerPlatformAPI(final CrazyCratesApi api) {
        this.server.getServicesManager().register(CrazyCratesApi.class, api, this.plugin, ServicePriority.Normal);
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
    public Path getDataDirectory() {
        return this.plugin.getDataPath();
    }

    @Override
    public PluginLogger getLogger() {
        return this.logger;
    }

    @Override
    public Collection<String> getPlayerList() {
        return this.server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        return this.server.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public UserManager getUserManager() {
        return null;
    }
}