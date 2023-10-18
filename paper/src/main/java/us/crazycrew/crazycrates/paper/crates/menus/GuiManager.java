package us.crazycrew.crazycrates.paper.crates.menus;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.menus.types.CrateMainMenu;
import java.util.HashMap;
import java.util.UUID;

public class GuiManager {

    private final CrazyCrates plugin;

    private HashMap<UUID, CrateMainMenu> uuids;

    public GuiManager(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.uuids = new HashMap<>();
    }

    public void unload() {
        // If it is empty, we don't need to do any looping.
        if (this.uuids.isEmpty()) return;

        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (this.uuids.containsKey(player.getUniqueId())) {
                this.uuids.get(player.getUniqueId()).getGui().close(player, true);

                removePlayer(player.getUniqueId());

                //TODO() Send a message notifying why the gui closed with a toggle.
            }
        });
    }

    public void reload() {
        unload();
        load();
    }

    public void addPlayer(UUID uuid, CrateMainMenu crateMainMenu) {
        if (this.uuids.containsKey(uuid)) {
            removePlayer(uuid);
        }

        this.uuids.put(uuid, crateMainMenu);
    }

    public void removePlayer(UUID uuid) {
        this.uuids.remove(uuid);
    }
}