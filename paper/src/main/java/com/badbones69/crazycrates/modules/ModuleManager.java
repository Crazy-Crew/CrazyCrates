package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.annotation.Nonnull;

public class ModuleManager extends AbstractModule {

    private final CrazyManager crazyManager;


    // Dependencies
    private final PlaceholderAPISupport placeholderAPISupport = new PlaceholderAPISupport();

    public ModuleManager(CrazyManager crazyManager) {
        this.crazyManager = crazyManager;
    }

    @Nonnull
    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(CrazyManager.class).toInstance(crazyManager);

        // Dependencies
        bind(PlaceholderAPISupport.class).toInstance(placeholderAPISupport);
    }
}