package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.utilities.CommonUtils;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.annotation.Nonnull;

public class PluginModule extends AbstractModule {

    private final CrazyCrates plugin;

    private final CrazyManager crazyManager = new CrazyManager();
    private final FileManager fileManager = new FileManager();

    private final CrazyLogger crazyLogger = new CrazyLogger();

    // Old methods class.
    private final Methods methods = new Methods();

    // New functions class to replace Methods slowly.
    private final CommonUtils commonUtils = new CommonUtils();

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

        bind(CrazyLogger.class).toInstance(crazyLogger);

        bind(Methods.class).toInstance(methods);
        bind(CommonUtils.class).toInstance(commonUtils);
    }
}