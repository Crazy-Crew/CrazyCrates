package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestStateHandler;

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