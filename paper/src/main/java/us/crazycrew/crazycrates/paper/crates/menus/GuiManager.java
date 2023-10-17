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
        this.crateMainMenu.create();
    }

    public void unload() {
        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            this.crateMainMenu.getGui().close(player, true);

            //TODO() Send a message notifying why the gui closed with a toggle.
        });
    }

    public void reload() {
        unload();
        load();
    }

    public CrateMainMenu getCrateMainMenu() {
        return this.crateMainMenu;
    }
}