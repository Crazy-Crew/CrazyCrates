package com.badbones69.crazycrates.loader;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.ryderbelserion.vital.paper.Vital;
import java.util.Locale;

public class CrazyPlugin extends Vital {

    private final CrazyCrates instance;
    private final long startTime;

    public CrazyPlugin() {
        this.startTime = System.nanoTime();

        this.instance = new CrazyCrates(this);
    }

    @Override
    public void onLoad() {
        this.instance.onLoad();
    }

    @Override
    public void onEnable() {
        this.instance.onEnable();

        if (MiscUtils.isLogging()) this.instance.getLogger().info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
    }

    @Override
    public void onDisable() {
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

        this.instance.onDisable();
    }
}