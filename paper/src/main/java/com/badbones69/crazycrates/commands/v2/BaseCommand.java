package com.badbones69.crazycrates.commands.v2;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.files.FileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Command(value = "crazycrates", alias = {"crates", "crate"})
public abstract class BaseCommand {

    protected final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    protected final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    protected final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    protected final @NotNull FileManager fileManager = this.plugin.getFileManager();

    protected final @NotNull SettingsManager config = ConfigManager.getConfig();

    public record CustomPlayer(String name) {
        public @NotNull OfflinePlayer getOfflinePlayer() {
            CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(name)).thenApply(OfflinePlayer::getUniqueId);

            return Bukkit.getServer().getOfflinePlayer(future.join());
        }

        public Player getPlayer() {
            return Bukkit.getServer().getPlayer(name);
        }
    }
}