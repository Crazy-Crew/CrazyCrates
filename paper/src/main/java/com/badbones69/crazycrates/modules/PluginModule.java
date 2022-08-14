package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.utilities.Functions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import javax.annotation.Nonnull;
import java.io.File;

public class PluginModule extends AbstractModule {

    private final CrazyCrates plugin;

    private final CrazyManager crazyManager = new CrazyManager();
    private final FileManager fileManager = new FileManager();

    // Old methods class.
    private final Methods methods = new Methods();
    // New functions class to replace Methods slowly.
    private final Functions functions = new Functions();

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
        bind(Functions.class).toInstance(functions);

        bind(File.class).annotatedWith(Names.named("ConfigFolder")).toInstance(plugin.getDataFolder());
    }
}