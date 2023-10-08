package com.badbones69.crazycrates.paper.api.crates;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.builder.GuiBuilder;

public class CrateMainMenu extends GuiBuilder {

    private final CrazyCrates plugin;

    public CrateMainMenu(CrazyCrates plugin, int size, String title) {
        super(plugin, size, title);

        this.plugin = plugin;
    }

    @Override
    public GuiBuilder build() {
        return this;
    }
}