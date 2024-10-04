package com.ryderbelserion.crazycrates.common.plugin;

import com.ryderbelserion.crazycrates.common.api.CrazyCratesApiProvider;
import com.ryderbelserion.crazycrates.common.plugin.bootstrap.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.plugin.configs.ConfigManager;
import org.jetbrains.annotations.ApiStatus;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.CrazyCratesApi;
import us.crazycrew.crazycrates.CrazyCratesProvider;

public abstract class AbstractCratesPlugin implements CrazyCratesPlugin {

    private CrazyCratesApiProvider apiProvider;

    public final void load() {
        ConfigManager.load(getDataDirectory().toFile());
    }

    public final void enable() {
        // register listeners
        registerListeners();

        // register commands
        registerCommands();

        // setup managers
        setupManagers();

        // create the provider
        this.apiProvider = new CrazyCratesApiProvider(this);

        // register the api's singleton
        CrazyCratesProvider.register(this.apiProvider);

        // register the old api temporarily
        CratesProvider.register(this.apiProvider);

        // register the platform provider i.e. service manager
        registerPlatformAPI(this.apiProvider);
    }

    public final void disable() {
        // unregister the api
        CrazyCratesProvider.unregister();

        // unregister the old api
        CratesProvider.unregister();
    }

    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    @ApiStatus.Internal
    protected abstract void onLoad();

    @ApiStatus.Internal
    protected abstract void onEnable();

    @ApiStatus.Internal
    protected abstract void onDisable();

    protected abstract void registerPlatformAPI(CrazyCratesApi api);

    protected abstract void registerListeners();

    protected abstract void registerCommands();

    protected abstract void setupManagers();

    @Override
    public CrazyCratesApiProvider getApiProvider() {
        return this.apiProvider;
    }
}