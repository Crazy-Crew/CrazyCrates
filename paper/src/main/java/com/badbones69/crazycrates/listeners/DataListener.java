package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DataListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        //this.userManager.createUser(player.getUniqueId());
    }
}
