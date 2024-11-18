package com.badbones69.crazycrates;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.IServer;
import java.util.UUID;

public class ExamplePlugin extends JavaPlugin implements Listener {

    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    @Override
    public void onEnable() {
        if (isPluginEnabled("CrazyCrates")) {
            Plugin instance = getServer().getPluginManager().getPlugin("CrazyCrates");

            if (instance != null) {
                getLogger().warning("The plugin: " + instance.getName() + " is enabled.");

                getServer().getPluginManager().registerEvents(this, this);
            }
        } else {
            getLogger().warning("Could not find CrazyCrates!");
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