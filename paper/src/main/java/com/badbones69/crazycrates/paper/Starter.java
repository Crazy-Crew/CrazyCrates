package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;

@Deprecated(since = "1.16", forRemoval = true)
public class Starter {

    private CrazyManager crazyManager;

    private EventLogger eventLogger;

    public void run() {
        this.crazyManager = new CrazyManager();

        this.eventLogger = new EventLogger();
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public EventLogger getEventLogger() {
        return this.eventLogger;
    }
}