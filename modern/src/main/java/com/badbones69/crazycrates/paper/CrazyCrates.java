package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyCrates extends JavaPlugin {

    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private CratePlatform platform;

    @Override
    public void onEnable() {
        this.platform = new CratePlatform(
                this,
                new FusionPaper(this)
        );

        this.platform.init();
    }

    @Override
    public void onDisable() {
        if (this.platform != null) {
            this.platform.stop();
        }
    }

    public @NotNull final CratePlatform getPlatform() {
        return this.platform;
    }
}