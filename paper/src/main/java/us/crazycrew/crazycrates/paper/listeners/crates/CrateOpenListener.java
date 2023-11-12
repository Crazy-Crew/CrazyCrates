package us.crazycrew.crazycrates.paper.listeners.crates;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.managers.CrateManager;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.cratetypes.Cosmic;
import us.crazycrew.crazycrates.paper.listeners.CrateControlListener;
import us.crazycrew.crazycrates.paper.api.support.libraries.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.other.MsgUtils;
import java.util.List;

@SuppressWarnings("deprecation")
public class CrateOpenListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        Crate crate = event.getCrate();

        if (crate.getCrateType() != CrateType.menu) {
            if (!crate.canWinPrizes(player)) {
                player.sendMessage(Translation.no_prizes_found.getString());
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                event.setCancelled(true);

                return;
            }
        }

        if (!(player.hasPermission("crazycrates.open." + crate.getName()) || player.hasPermission("crazycrates.open.*"))) {
            player.sendMessage(Translation.no_crate_permission.getString());
            this.crateManager.removePlayerFromOpeningList(player);
            this.crateManager.removeCrateInUse(player);

            event.setCancelled(true);

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);
        if (crate.getCrateType() != CrateType.cosmic) this.crazyHandler.getUserManager().addOpenedCrate(player.getUniqueId(), crate.getName());

        JavaPlugin plugin = event.getPlugin();

        FileConfiguration configuration = event.getConfiguration();

        String broadcastMessage = configuration.getString("Crate.BroadCast", "");
        boolean broadcastToggle = configuration.contains("Crate.OpeningBroadCast") && configuration.getBoolean("Crate.OpeningBroadCast");

        if (broadcastToggle) {
            if (!broadcastMessage.isBlank()) {
                plugin.getServer().broadcastMessage(MsgUtils.color(broadcastMessage.replaceAll("%prefix%", MsgUtils.getPrefix())).replaceAll("%player%", player.getName()));
            }
        }

        boolean commandToggle = configuration.contains("Crate.opening-command") && configuration.getBoolean("Crate.opening-command.toggle");

        if (commandToggle) {
            List<String> commands = configuration.getStringList("Crate.opening-command.commands");

            if (!commands.isEmpty()) {
                commands.forEach(line -> {
                    String builder;

                    if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                        builder = PlaceholderAPI.setPlaceholders(player, line.replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName()));
                    } else {
                        builder = line.replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName());
                    }

                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), builder);
                });
            }
        }

        if (crate.getCrateType() == CrateType.cosmic) {
            Cosmic.openCosmic(player, crate, event.getKeyType(), event.isCheckHand());
        }
    }
}