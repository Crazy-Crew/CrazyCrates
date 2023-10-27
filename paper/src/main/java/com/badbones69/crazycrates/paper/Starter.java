package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestStateHandler;

@Deprecated(since = "1.16", forRemoval = true)
public class Starter {

    private CrazyManager crazyManager;

    private ChestStateHandler chestStateHandler;

    private EventLogger eventLogger;

    public void run() {
        this.crazyManager = new CrazyManager();

        this.chestStateHandler = new ChestStateHandler();

        this.eventLogger = new EventLogger();
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public ChestStateHandler getChestStateHandler() {
        return this.chestStateHandler;
    }

    public EventLogger getEventLogger() {
        return this.eventLogger;
    }
}