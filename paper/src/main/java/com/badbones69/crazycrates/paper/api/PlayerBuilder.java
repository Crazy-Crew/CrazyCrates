package com.badbones69.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record PlayerBuilder(String name) {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final Server server = plugin.getServer();

    /**
     * Gets the {@link OfflinePlayer}.
     *
     * @return {@link OfflinePlayer}
     * @since 0.0.1
     */
    public @Nullable OfflinePlayer getOfflinePlayer() {
        if (this.name.isEmpty()) return null;

        CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> server.getOfflinePlayer(this.name)).thenApply(OfflinePlayer::getUniqueId);

        return server.getOfflinePlayer(future.join());
    }

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     * @since 0.0.1
     */
    public @Nullable Player getPlayer() {
        if (this.name.isEmpty()) return null;

        return server.getPlayerExact(this.name);
    }
}