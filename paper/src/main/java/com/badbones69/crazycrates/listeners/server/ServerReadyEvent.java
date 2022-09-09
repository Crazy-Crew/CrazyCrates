package com.badbones69.crazycrates.listeners.server;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.support.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerReadyEvent implements Listener {

    private final CrazyManager crazyManager;

    public ServerReadyEvent(CrazyManager crazyManager) {
        this.crazyManager = crazyManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerReady(ServerLoadEvent event) {

        // Load PAPI Support
        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) new PlaceholderAPISupport(crazyManager).register();

    }
}