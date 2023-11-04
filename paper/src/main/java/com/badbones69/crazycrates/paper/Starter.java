package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;

public class Starter {

    private CrazyManager crazyManager;

    private EventLogger eventLogger;

    public void run() {
        this.crazyManager = new CrazyManager();

        this.eventLogger = new EventLogger();
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public EventLogger getEventLogger() {
        return this.eventLogger;
    }
}