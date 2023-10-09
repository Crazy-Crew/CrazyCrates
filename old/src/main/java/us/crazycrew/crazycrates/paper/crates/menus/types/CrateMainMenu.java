package us.crazycrew.crazycrates.paper.crates.menus.types;

import dev.triumphteam.gui.guis.Gui;
import us.crazycrew.crazycrates.paper.CrazyCrates;

public class CrateMainMenu {

    private final CrazyCrates plugin;

    public CrateMainMenu(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    Gui gui;

    public void create() {

    }

    public Gui getMenu() {
        return this.gui;
    }
}