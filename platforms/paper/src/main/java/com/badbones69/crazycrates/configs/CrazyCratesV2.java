package com.badbones69.crazycrates.configs;

import net.dehya.ruby.RubyCore;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class CrazyCratesV2 extends JavaPlugin implements RubyCore {

    private static CrazyCratesV2 plugin;

    public CrazyCratesV2() {
        super();

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (Exception e) {
            e.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }

        // Bind plugin variable on constructor build.
        plugin = this;
    }

    @Override
    public void onEnable() {

        // Enables the rest of the plugin after the initial steps.
        enable();
    }

    @Override
    public void enable() {

    }

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void disable() {

    }

    @Override
    public @NotNull Path getDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public @NotNull Boolean isFileModuleActivated() {
        return false;
    }

    public static CrazyCratesV2 getPlugin() {
        return plugin;
    }
}