package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;

public class Starter {

    private FileManager fileManager;

    private CrazyManager crazyManager;

    private ChestStateHandler chestStateHandler;

    private EventLogger eventLogger;

    public void run() {
        fileManager = new FileManager();
        crazyManager = new CrazyManager();

        chestStateHandler = new ChestStateHandler();

        eventLogger = new EventLogger();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public ChestStateHandler getChestStateHandler() {
        return chestStateHandler;
    }

    public EventLogger getEventLogger() {
        return eventLogger;
    }
}