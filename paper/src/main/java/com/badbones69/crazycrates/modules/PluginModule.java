package com.badbones69.crazycrates.modules;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.FireworkDamageListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.support.structures.QuadCrateSpiralHandler;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.utilities.AdventureUtils;
import com.badbones69.crazycrates.utilities.CommonUtils;
import com.badbones69.crazycrates.utilities.ScheduleUtils;
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

    // Listeners
    private final BrokeLocationsListener brokeLocationsListener = new BrokeLocationsListener();
    private final CrateControlListener crateControlListener = new CrateControlListener();
    private final FireworkDamageListener fireworkDamageListener = new FireworkDamageListener();
    private final MiscListener miscListener = new MiscListener();

    // Old methods class.
    private final Methods methods = new Methods();

    // New functions class to replace Methods slowly.
    private final CommonUtils commonUtils = new CommonUtils();

    private final AdventureUtils adventureUtils = new AdventureUtils();
    private final ScheduleUtils scheduleUtils = new ScheduleUtils();

    private final ChestStateHandler chestStateHandler = new ChestStateHandler();
    private final QuadCrateSpiralHandler quadCrateSpiralHandler = new QuadCrateSpiralHandler();

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

        bind(BrokeLocationsListener.class).toInstance(brokeLocationsListener);
        bind(CrateControlListener.class).toInstance(crateControlListener);
        bind(FireworkDamageListener.class).toInstance(fireworkDamageListener);
        bind(MiscListener.class).toInstance(miscListener);

        bind(Methods.class).toInstance(methods);

        bind(CommonUtils.class).toInstance(commonUtils);
        bind(AdventureUtils.class).toInstance(adventureUtils);
        bind(ScheduleUtils.class).toInstance(scheduleUtils);

        bind(ChestStateHandler.class).toInstance(chestStateHandler);
        bind(QuadCrateSpiralHandler.class).toInstance(quadCrateSpiralHandler);
    }
}