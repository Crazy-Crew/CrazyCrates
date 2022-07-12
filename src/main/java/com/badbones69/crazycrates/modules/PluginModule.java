package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.annotation.Nonnull;

public class PluginModule extends AbstractModule {

    private final CrazyCrates plugin;

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
    }
}