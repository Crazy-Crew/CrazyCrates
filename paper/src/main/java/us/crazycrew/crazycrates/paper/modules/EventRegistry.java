package us.crazycrew.crazycrates.paper.modules;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import java.util.ArrayList;
import java.util.List;

public class EventRegistry {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

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