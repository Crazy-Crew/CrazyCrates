package com.badbones69.crazycrates.support.placeholders;

import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @Override
    public @NotNull final String onRequest(final OfflinePlayer player, @NotNull final String identifier) {
        if (player == null) return "N/A";

        if (identifier.isEmpty()) return "N/A";

        // The player who sees the placeholder.
        final Player human = (Player) player;

        // This is if the person opening the gui is to be used.
        for (Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();

            if (identifier.equalsIgnoreCase(fileName)) {
                return NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(human.getUniqueId(), fileName));
            }

            if (identifier.equalsIgnoreCase(fileName + "_physical")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(human.getUniqueId(), fileName));
            }

            if (identifier.equalsIgnoreCase(fileName + "_total")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(human.getUniqueId(), fileName));
            }

            if (identifier.equalsIgnoreCase(fileName + "_opened")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(human.getUniqueId(), fileName));
            }

            if (identifier.equalsIgnoreCase("crates_opened")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getTotalCratesOpened(human.getUniqueId()));
            }
        }

        // Gets the {player_name} or whatever
        final int index = identifier.lastIndexOf("_");
        final String value = PlaceholderAPI.setPlaceholders(human, "%" + StringUtils.substringBetween(identifier.substring(0, index), "{", "}") + "%");

        // Get player
        final Player target = this.plugin.getServer().getPlayer(value);

        // If player is offline.
        if (target == null) {
            final UUID offlinePlayer = CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(value)).thenApply(OfflinePlayer::getUniqueId).join();

            final String crateName = identifier.split("_")[2];

            return getKeys(offlinePlayer, identifier, crateName, value);
        }

        // If player is online.
        final String crateName = identifier.split("_")[2];

        return getKeys(target.getUniqueId(), identifier, crateName, value);
    }

    private @NotNull String getKeys(@NotNull final UUID uuid, @NotNull final String identifier, @NotNull final String crateName, @NotNull final String value) {
        if (crateName.isEmpty() || value.isEmpty()) return "N/A";

        if (this.crateManager.getCrateFromName(crateName) == null && identifier.endsWith("opened")) { // %crazycrates_<player>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalCratesOpened(uuid));
        }

        final Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            if (MiscUtils.isLogging()) this.plugin.getComponentLogger().warn("Crate: {} is not a valid crate name.", crateName);

            return "N/A";
        }

        final String fileName = crate.getFileName();

        final String result = value + "_" + fileName + "_" + identifier.split("_")[3];

        if (result.endsWith("total")) { // %crazycrates_<player>_<crate>_total%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, fileName));
        }

        if (result.endsWith("physical")) { // %crazycrates_<player>_<crate>_physical%
            return NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, fileName));
        }

        if (result.endsWith("virtual")) { // %crazycrates_<player>_<crate>_virtual%
            return NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, fileName));
        }

        if (result.endsWith("opened")) { // %crazycrates_<player>_<crate>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, fileName));
        }

        return "N/A";
    }
    
    @Override
    public final boolean persist() {
        return true;
    }

    @Override
    public final boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull final String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    public @NotNull final String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public @NotNull final String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }
}