package com.badbones69.crazycrates.api.modules;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import java.util.ArrayList;
import java.util.List;

public class EventRegistry {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        if (this.listeners.contains(listener)) return;

        this.listeners.add(listener);
        this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
    }

    public void removeListener(Listener listener) {
        if (!this.listeners.contains(listener)) return;

        this.listeners.remove(listener);
        HandlerList.unregisterAll(listener);
    }
}