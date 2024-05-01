package com.badbones69.crazycrates.api.hooks;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabaseListener implements Listener {

    private static HeadDatabaseAPI api;

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
        api = new HeadDatabaseAPI();
    }

    public static HeadDatabaseAPI getHeads() {
        if (api == null) {
            return null;
        }

        return api;
    }
}