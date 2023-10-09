package us.crazycrew.crazycrates.paper.crates.menus;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.menus.types.CrateMainMenu;

public class GuiManager {

    private final CrazyCrates plugin;

    public GuiManager(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    private CrateMainMenu crateMainMenu;

    public void load() {
        this.crateMainMenu = new CrateMainMenu(this.plugin);
    }

    public void unload() {

    }

    public void reload() {

    }

    public CrateMainMenu getCrateMainMenu() {
        return this.crateMainMenu;
    }
}