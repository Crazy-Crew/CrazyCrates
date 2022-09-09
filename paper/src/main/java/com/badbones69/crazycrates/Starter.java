package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.utilities.CommonUtils;
import com.badbones69.crazycrates.api.utilities.LoggerUtils;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;

public class Starter {

    private AdventureUtils adventureUtils;
    private LoggerUtils loggerUtils;

    private CommonUtils commonUtils;

    private ScheduleUtils scheduleUtils;

    private Methods methods;

    private FileManager fileManager;

    private CrazyManager crazyManager;

    private ChestStateHandler chestStateHandler;

    private CrateTaskHandler crateTaskHandler;

    public void run() {
        adventureUtils = new AdventureUtils();
        loggerUtils = new LoggerUtils();

        commonUtils = new CommonUtils();
        scheduleUtils = new ScheduleUtils();

        methods = new Methods();

        fileManager = new FileManager();
        crazyManager = new CrazyManager();

        chestStateHandler = new ChestStateHandler();
        crateTaskHandler = new CrateTaskHandler();
    }

    public LoggerUtils getLoggerUtils() {
        return loggerUtils;
    }

    public AdventureUtils getAdventureUtils() {
        return adventureUtils;
    }

    public CommonUtils getCommonUtils() {
        return commonUtils;
    }

    public ScheduleUtils getScheduleUtils() {
        return scheduleUtils;
    }

    public Methods getMethods() {
        return methods;
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

    public CrateTaskHandler getCrateTaskHandler() {
        return crateTaskHandler;
    }
}