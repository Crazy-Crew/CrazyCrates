package com.ryderbelserion.crazycrates.loader;

import com.ryderbelserion.crazycrates.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Locale;

public class CrazyPlugin extends JavaPlugin {

    private final CrazyCrates crazyCrates;
    private final long startTime;

    public CrazyPlugin() {
        this.startTime = System.nanoTime();

        this.crazyCrates = new CrazyCrates(this);
    }

    @Override
    public void onLoad() {
        this.crazyCrates.onLoad();
    }

    @Override
    public void onEnable() {
        this.crazyCrates.onEnable();

        getComponentLogger().info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
    }

    @Override
    public void onDisable() {
        this.crazyCrates.onDisable();
    }
}