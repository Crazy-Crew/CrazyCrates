package com.badbones69.crazycrates.listeners.tasks;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerKeyTask implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        crazyManager.setNewPlayerKeys(e.getPlayer());
        crazyManager.loadOfflinePlayersKeys(e.getPlayer());
    }
}