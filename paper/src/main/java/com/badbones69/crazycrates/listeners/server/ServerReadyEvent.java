package com.badbones69.crazycrates.listeners.server;

import com.badbones69.crazycrates.support.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerReadyEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onServerReady(ServerLoadEvent event) {

        // Load PAPI Support
        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) new PlaceholderAPISupport().register();

    }
}