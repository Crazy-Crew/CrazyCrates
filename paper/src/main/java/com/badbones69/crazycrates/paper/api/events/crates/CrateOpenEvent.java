package com.badbones69.crazycrates.paper.api.events.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrateOpenEvent extends Event implements Cancellable {

    private final JavaPlugin plugin;
    private final Player player;
    private final Crate crate;
    private final FileConfiguration configuration;

    private boolean isCancelled;

    public CrateOpenEvent(JavaPlugin plugin, Player player, Crate crate, FileConfiguration configuration) {
        this.plugin = plugin;

        this.player = player;
        this.crate = crate;

        this.configuration = configuration;

        this.isCancelled = false;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Crate getCrate() {
        return this.crate;
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}