package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class CrazyCrates extends JavaPlugin {

    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private CrazyCratesPaper platform;

    @Override
    public void onEnable() {
        this.platform = new CrazyCratesPaper(this, new FusionPaper(this), getDataPath());
        this.platform.init();
    }

    @Override
    public void onDisable() {
        if (this.platform != null) {
            this.platform.disable();
        }
    }

    public @NonNull final CrazyCratesPaper getPlatform() {
        return this.platform;
    }
}