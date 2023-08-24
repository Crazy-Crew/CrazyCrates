package com.badbones69.crazycrates.paper.listeners.items;

import com.badbones69.crazycrates.paper.CrazyCrates;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Kakumi
 *
 * https://github.com/Crazy-Crew/CrazyCrates/pull/446
 */
public class ItemsAdderEvent implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @EventHandler
    public void onItemsRegistered(ItemsAdderLoadDataEvent event) {
        //This event is fired from ItemsAdder after all data are loaded
        //So, the plugin will update all items automatically when ItemsAdder is ready
        this.plugin.getStarter().getCrazyManager().loadCrates();
    }
}