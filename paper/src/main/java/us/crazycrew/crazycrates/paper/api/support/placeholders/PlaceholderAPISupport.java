package us.crazycrew.crazycrates.paper.api.support.placeholders;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import java.text.NumberFormat;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final BukkitUserManager userManager = this.crazyHandler.getUserManager();
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "N/A";
        }

        // The player who sees the placeholder.
        Player human = (Player) player;

        for (Crate crate : this.crazyHandler.getCrateManager().getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
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
        }

        // Get the player name online or offline i.e <player>
        String playerName = identifier.split("_")[0];

        Player target = this.plugin.getServer().getPlayer(playerName);

        if (target == null) {
            this.plugin.getLogger().warning("Player: " + playerName + " is likely not online or doesn't exist.");
            return "N/A";
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_opened")) { // %crazycrates_<player>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalCratesOpened(human.getUniqueId()));
        }

        // Get the crate name i.e <crate>
        String crateName = identifier.split("_")[1];

        Crate crate = this.plugin.getCrateManager().getCrateFromName(crateName);

        if (crate == null) {
            this.plugin.getLogger().warning("Crate: " + crateName + " is not a valid crate name.");
            return "N/A";
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_total")) { // %crazycrates_<player>_<crate>_total%
            return NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(target.getUniqueId(), crate.getName()));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_physical")) { // %crazycrates_<player>_<crate>_physical%
            return NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(target.getUniqueId(), crate.getName()));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_virtual")) { // %crazycrates_<player>_<crate>_virtual%
            return NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(target.getUniqueId(), crate.getName()));
        }

        if (identifier.equalsIgnoreCase(target.getName() + "_" + crate.getName() + "_opened")) { // %crazycrates_<player>_<crate>_opened%
            return NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(target.getUniqueId(), crate.getName()));
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