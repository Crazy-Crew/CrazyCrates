package com.badbones69.crazycrates.paper;

public class CrazyCrates {

    private static CrazyCrates plugin;

    private Starter starter;

    public void enable() {
        plugin = this;

        this.starter = new Starter();
        this.starter.run();
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public static CrazyCrates getPlugin() {
        return plugin;
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public Starter getStarter() {
        return this.starter;
    }
}