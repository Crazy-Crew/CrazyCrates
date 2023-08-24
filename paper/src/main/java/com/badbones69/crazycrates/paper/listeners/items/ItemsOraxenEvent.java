package com.badbones69.crazycrates.paper.listeners.items;

import com.badbones69.crazycrates.paper.CrazyCrates;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsOraxenEvent implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @EventHandler
    public void onItemsRegistered(OraxenItemsLoadedEvent event) {
        //This event is fired from Oraxen after all data are loaded
        //So, the plugin will update all items automatically when Oraxen is ready
        this.plugin.getStarter().getCrazyManager().loadCrates();
    }
}