package com.badbones69.crazycrates.paper.support.placeholders;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.api.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;

import java.text.NumberFormat;

@SuppressWarnings("deprecation")
public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "N/A";
        }

        // The player who sees the placeholder.
        Player human = (Player) player;

        for (Crate crate : this.crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (identifier.equalsIgnoreCase(crate.getName())) {
                    return NumberFormat.getNumberInstance().format(this.crazyManager.getVirtualKeys(human, crate));
                }

                if (identifier.equalsIgnoreCase(crate.getName() + "_physical")) {
                    return NumberFormat.getNumberInstance().format(this.crazyManager.getPhysicalKeys(human, crate));
                }

                if (identifier.equalsIgnoreCase(crate.getName() + "_total")) {
                    return NumberFormat.getNumberInstance().format(this.crazyManager.getTotalKeys(human, crate));
                }

                if (identifier.equalsIgnoreCase(crate.getName() + "_opened")) {
                    return NumberFormat.getNumberInstance().format(this.crazyManager.getCratesOpened(human, crate));
                }

                if (identifier.equalsIgnoreCase("crates_opened")) {
                    return NumberFormat.getNumberInstance().format(this.crazyManager.getTotalCratesOpened(human));
                }
            }
        }

        // Get the player name online or offline i.e <player>
        String playerName = identifier.split("_")[0];

        Player target = this.plugin.getServer().getPlayer(playerName);

        if (target == null) {
            this.plugin.getLogger().warning("Player: " + playerName + " is likely not online or doesn't exist.");
            return "N/A";
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_opened")) { // %crazycrates_<player>_opened%
            return NumberFormat.getNumberInstance().format(this.crazyManager.getTotalCratesOpened(target));
        }

        // Get the crate name i.e <crate>
        String crateName = identifier.split("_")[1];

        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null) {
            this.plugin.getLogger().warning("Crate: " + crateName + " is not a valid crate name.");
            return "N/A";
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_total")) { // %crazycrates_<player>_<crate>_total%
            return NumberFormat.getNumberInstance().format(this.crazyManager.getTotalKeys(target, crate));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_physical")) { // %crazycrates_<player>_<crate>_physical%
            return NumberFormat.getNumberInstance().format(this.crazyManager.getPhysicalKeys(target, crate));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_virtual")) { // %crazycrates_<player>_<crate>_virtual%
            return NumberFormat.getNumberInstance().format(this.crazyManager.getVirtualKeys(target, crate));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_opened")) { // %crazycrates_<player>_<crate>_opened%
            return NumberFormat.getNumberInstance().format(this.crazyManager.getCratesOpened(target, crate));
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
    public @NotNull String getIdentifier() {
        return "crazycrates";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}