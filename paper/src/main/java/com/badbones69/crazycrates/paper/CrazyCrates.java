package com.badbones69.crazycrates.paper;

@Deprecated(since = "1.16", forRemoval = true)
public class CrazyCrates {

    private static CrazyCrates plugin;

    private Starter starter;

    public void enable() {
        plugin = this;

        this.starter = new Starter();
        this.starter.run();
    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return this.starter;
    }
}