package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    @EventHandler(ignoreCancelled = true)
    public void onItemsRegistered(ItemsAdderLoadDataEvent event) {
        // This event is fired from ItemsAdder after all data are loaded
        // So, the plugin will update all items automatically when ItemsAdder is ready
        crazyManager.loadCrates();
    }
}