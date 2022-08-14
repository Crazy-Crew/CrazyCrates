package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.annotation.Nonnull;

public class PluginModule extends AbstractModule {

    private final CrazyCrates plugin;

    private final CrazyManager crazyManager = new CrazyManager();
    private final FileManager fileManager = new FileManager();

    private final Methods methods = new Methods();

    public PluginModule(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(CrazyCrates.class).toInstance(plugin);

        bind(CrazyManager.class).toInstance(crazyManager);

        bind(FileManager.class).toInstance(fileManager);

        bind(Methods.class).toInstance(methods);
    }
}