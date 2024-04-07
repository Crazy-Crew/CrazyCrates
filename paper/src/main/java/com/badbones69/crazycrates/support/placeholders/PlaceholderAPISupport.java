package com.badbones69.crazycrates.support.placeholders;

import com.badbones69.crazycrates.tasks.crates.CrateManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return "N/A";

        // The player who sees the placeholder.
        Player human = (Player) player;

        // This is if the person opening the gui is to be used.
        for (Crate crate : this.crateManager.getUsableCrates()) {
            if (identifier.equalsIgnoreCase(crate.getName())) {
                return NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(human.getUniqueId(), crate.getName()));
            }

            if (identifier.equalsIgnoreCase(crate.getName() + "_physical")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(human.getUniqueId(), crate.getName()));
            }

            if (identifier.equalsIgnoreCase(crate.getName() + "_total")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(human.getUniqueId(), crate.getName()));
            }

            if (identifier.equalsIgnoreCase(crate.getName() + "_opened")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(human.getUniqueId(), crate.getName()));
            }

            if (identifier.equalsIgnoreCase("crates_opened")) {
                return NumberFormat.getNumberInstance().format(this.userManager.getTotalCratesOpened(human.getUniqueId()));
            }
        }

        // Gets the {player_name} or whatever
        int index = identifier.lastIndexOf("_");
        String value = PlaceholderAPI.setPlaceholders(human, "%" + StringUtils.substringBetween(identifier.substring(0, index), "{", "}") + "%");

        // Get player
        Player target = this.plugin.getServer().getPlayer(value);

        // If player is offline.
        if (target == null) {
            UUID offlinePlayer = CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(value)).thenApply(OfflinePlayer::getUniqueId).join();

            String crateName = identifier.split("_")[2];

            return getKeys(offlinePlayer, identifier, crateName, value);
        }

        // If player is online.
        String crateName = identifier.split("_")[2];

        return getKeys(target.getUniqueId(), identifier, crateName, value);
    }

    private String getKeys(UUID uuid, String identifier, String crateName, String value) {
        if (this.plugin.getCrateManager().getCrateFromName(crateName) == null && identifier.endsWith("opened")) { // %crazycrates_<player>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalCratesOpened(uuid));
        }

        Crate crate = this.plugin.getCrateManager().getCrateFromName(crateName);

        if (crate == null) {
            this.plugin.getLogger().warning("Crate: " + crateName + " is not a valid crate name.");
            return "N/A";
        }

        String result = value + "_" + crateName + "_" + identifier.split("_")[3];

        if (result.endsWith("total")) { // %crazycrates_<player>_<crate>_total%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, crate.getName()));
        }

        if (result.endsWith("physical")) { // %crazycrates_<player>_<crate>_physical%
            return NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, crate.getName()));
        }

        if (result.endsWith("virtual")) { // %crazycrates_<player>_<crate>_virtual%
            return NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, crate.getName()));
        }

        if (result.endsWith("opened")) { // %crazycrates_<player>_<crate>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, crate.getName()));
        }

        return "N/A";
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}