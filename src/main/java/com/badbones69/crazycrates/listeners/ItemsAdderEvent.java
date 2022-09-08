package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderEvent implements Listener {
    @EventHandler
    public void onItemsRegistered(ItemsAdderLoadDataEvent event) {
        //This event is fired from ItemsAdder after all data are loaded
        //So, the plugin will update all items automatically when ItemsAdder is ready
        CrazyManager.getInstance().loadCrates();
    }
}
