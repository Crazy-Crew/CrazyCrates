package com.ryderbelserion.crazycrates.loader;

import com.ryderbelserion.crazycrates.CrazyCrates;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyPlugin extends JavaPlugin {

    private final CrazyCrates crazyCrates;

    public CrazyPlugin() {
        this.crazyCrates = new CrazyCrates(this);
    }

    @Override
    public void onLoad() {
        this.crazyCrates.load();
    }

    @Override
    public void onEnable() {
        this.crazyCrates.enable();
    }

    @Override
    public void onDisable() {
        this.crazyCrates.disable();
    }
}