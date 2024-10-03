package com.ryderbelserion.crazycrates.common.plugin;

import com.ryderbelserion.crazycrates.common.plugin.bootstrap.CrazyCratesBootstrap;

public abstract class AbstractCratesPlugin implements CrazyCratesBootstrap {

    public final void load() {

    }

    public final void enable() {
        registerListeners();

        registerCommands();
    }

    protected abstract void registerListeners();

    protected abstract void registerCommands();

}