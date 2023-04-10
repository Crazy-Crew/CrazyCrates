package com.badbones69.crazycrates.commands.subs;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public record CustomPlayer(String name) {
    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    public UUID getUUID() {
        Player player = plugin.getServer().getPlayer(name);

        return (player != null && player.isOnline()) ? player.getUniqueId() : Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId();
    }
}
