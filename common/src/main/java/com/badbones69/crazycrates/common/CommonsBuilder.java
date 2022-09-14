package com.badbones69.crazycrates.common;

import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.common.utilities.logger.CrazyLogger;

public class CommonsBuilder {

    private AdventureUtils adventureUtils;

    private CrazyLogger crazyLogger;

    public void run() {
        adventureUtils = new AdventureUtils();
        crazyLogger = new CrazyLogger();
    }

    public AdventureUtils getAdventureUtils() {
        return adventureUtils;
    }

    public CrazyLogger getCrazyLogger() {
        return crazyLogger;
    }
}