package com.badbones69.crazycrates;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.IServer;
import java.util.UUID;

public class ExamplePlugin extends JavaPlugin implements Listener {

    public boolean isPluginEnabled(@NotNull final String pluginName, @NotNull final PluginManager server) {
        return server.isPluginEnabled(pluginName);
    }

    @Override
    public void onEnable() {
        final PluginManager server = getServer().getPluginManager();
        final ComponentLogger logger = getComponentLogger();

        if (isPluginEnabled("CrazyCrates", server)) {
            final Plugin instance = server.getPlugin("CrazyCrates");

            if (instance != null) {
                logger.warn("{} is enabled.", "The plugin: " + instance.getName());

                server.registerEvents(this, this);
            }
        } else {
            logger.warn("Could not find CrazyCrates, so the API did not load.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final IServer service = CratesProvider.get();
        final UserManager userManager = service.getUserManager();

        userManager.addKeys(uuid, "CrateExample", KeyType.virtual_key, 5);
        userManager.addKeys(uuid, "QuadCrateExample", KeyType.physical_key, 5);
        userManager.addKeys(uuid, "Fuckyou", KeyType.virtual_key, 5);
        userManager.addOpenedCrate(uuid, "CrateExample");
    }
}