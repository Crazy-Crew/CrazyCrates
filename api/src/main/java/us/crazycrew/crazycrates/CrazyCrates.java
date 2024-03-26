package us.crazycrew.crazycrates;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.Server;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import java.util.logging.Logger;

public class CrazyCrates implements ICrazyCrates {

    private final Server server;

    public CrazyCrates(Server server) {
        // Create server object.
        this.server = server;

        // Register legacy provider
        CrazyCratesService.register(this);

        // Register provider.
        CrazyCratesProvider.register(this);
    }

    public void reload() {
        ConfigManager.reload();
    }

    public void disable() {
        // Unregister legacy provider.
        CrazyCratesService.unregister();

        // Unregister provider.
        CrazyCratesProvider.unregister();
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return getServer().getUserManager();
    }

    @NotNull
    public Server getServer() {
        return this.server;
    }

    public Logger getLogger() {
        return getServer().getLogger();
    }
}