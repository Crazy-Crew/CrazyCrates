package com.badbones69.crazycrates.paper.listeners.crates;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.common.config.ConfigManager;
import com.badbones69.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.List;
import java.util.Map;

public class CrateOpenListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Server server = this.plugin.getServer();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final Crate crate = event.getCrate();

        final String fileName = crate.getFileName();
        final String fancyName = crate.getCrateName();
        final CrateType crateType = crate.getCrateType();

        if (crateType != CrateType.menu) {
            if (crate.getPrizes().isEmpty() || !crate.canWinPrizes(player)) {
                Messages.no_prizes_found.sendMessage(player, "{crate}", fancyName);

                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                event.setCancelled(true);

                return;
            }
        }

        if (!player.hasPermission("crazycrates.open." + fileName)) {
            Messages.no_crate_permission.sendMessage(player, "{crate}", fancyName);

            this.crateManager.removePlayerFromOpeningList(player);
            this.crateManager.removeCrateInUse(player);

            event.setCancelled(true);

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        if (crateType != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), fileName);

        final YamlConfiguration configuration = event.getConfiguration();

        final String broadcastMessage = configuration.getString("Crate.BroadCast", "");
        final boolean broadcastToggle = configuration.getBoolean("Crate.OpeningBroadCast", false);

        if (broadcastToggle && crateType != CrateType.cosmic && !event.isSilent()) { //todo() add a permission?
            if (!broadcastMessage.isBlank()) {
                this.server.broadcast(this.fusion.asComponent(player, broadcastMessage, Map.of(
                        "%crate%", fancyName,
                        "%prefix%", this.config.getProperty(ConfigKeys.command_prefix),
                        "%player%", playerName
                )));
            }
        }

        final boolean commandToggle = configuration.contains("Crate.opening-command") && configuration.getBoolean("Crate.opening-command.toggle");

        if (commandToggle) {
            final List<String> commands = configuration.getStringList("Crate.opening-command.commands");

            for (final String line : commands) {
                if (line.isBlank()) continue;

                MiscUtils.sendCommand(player, line, Map.of(
                        "%prefix%", this.config.getProperty(ConfigKeys.command_prefix),
                        "%player%", playerName,
                        "%crate%", fileName
                ));
            }
        }

        EventManager.logEvent(event.getEventType(), playerName, player, crate, event.getKeyType(), 1);
    }
}