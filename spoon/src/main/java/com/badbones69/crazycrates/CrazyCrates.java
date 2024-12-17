package com.badbones69.crazycrates;

import com.ryderbelserion.FusionApi;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyCrates extends JavaPlugin {

    private final FusionApi api = FusionApi.get();

    @Override
    public void onEnable() {
        this.api.enable(this);
    }

    @Override
    public void onDisable() {
        this.api.disable();
    }

    public @NotNull FusionApi getApi() {
        return this.api;
    }
}