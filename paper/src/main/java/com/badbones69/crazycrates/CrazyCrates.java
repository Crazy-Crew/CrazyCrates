package com.badbones69.crazycrates;

import com.badbones69.crazycrates.listeners.server.ServerReadyEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

public class CrazyCrates extends JavaPlugin implements Listener {

    private static CrazyCrates plugin;

    private Starter starter;

    private boolean pluginEnabled = false;

    public final Path DATA_DIRECTORY = getDataFolder().toPath().resolve("data");
    public final Path MENU_DIRECTORY = getDataFolder().toPath().resolve("menus");
    public final Path LOCALE_DIRECTORY = getDataFolder().toPath().resolve("locale");
    public final Path PLUGIN_DIRECTORY = getDataFolder().toPath();

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            starter.run();

            if (!getDataFolder().exists()) getDataFolder().mkdirs();
        } catch (Exception exception) {
            exception.printStackTrace();

            pluginEnabled = false;

            return;
        }

        enable();

        pluginEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;
    }

    private void enable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new ServerReadyEvent(), this);
    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }
}