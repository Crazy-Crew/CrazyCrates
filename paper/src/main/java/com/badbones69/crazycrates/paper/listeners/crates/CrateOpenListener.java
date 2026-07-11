package com.badbones69.crazycrates.paper.listeners.crates;

import ch.jalu.configme.SettingsManager;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.List;
import java.util.Map;

public class CrateOpenListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final BukkitUserManager userManager = this.platform.getUserManager();

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final Crate crate = event.getCrate();

        final String fileName = crate.getFileName();
        final String fancyName = crate.getCrateName();
        final CrateType crateType = crate.getCrateType();

        if (event.isCancelled()) { // if cancelled return.
            this.crateManager.endCrate(crate, player);

            return;
        }

        if (crateType != CrateType.menu) {
            if (crate.getPrizes().isEmpty() || !crate.canWinPrizes(player)) {
                Message.prizes_empty.sendMessage(player, "{value}", fancyName);

                this.crateManager.endCrate(crate, player);

                event.setCancelled(true);

                return;
            }
        }

        if (!player.hasPermission("crazycrates.open." + fileName)) {
            Message.crate_no_permission.sendMessage(player, "{crate}", fancyName);

            this.crateManager.endCrate(crate, player);

            event.setCancelled(true);

            return;
        }

        final ConfigurationSection configuration = event.getConfiguration();

        switch (crateType) {
            case cosmic -> {}

            default -> {
                final String broadcastMessage = configuration.getString("BroadCast", "");
                final boolean broadcastToggle = configuration.getBoolean("OpeningBroadCast", false);

                if (broadcastToggle && !event.isSilent()) {
                    if (!broadcastMessage.isBlank()) {
                        this.server.broadcast(this.fusion.asComponent(player, broadcastMessage, Map.of(
                                "%crate%", fancyName,
                                "%prefix%", this.config.getProperty(ConfigKeys.command_prefix),
                                "%player%", playerName
                        )));
                    }
                }

                this.userManager.addOpenedCrate(player.getUniqueId(), fileName, event.getAmount());
            }
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        final int amount = event.getAmount();

        if (configuration.getBoolean("opening-command.toggle", false)) {
            final List<String> commands = configuration.getStringList("opening-command.commands");

            for (final String line : commands) {
                if (line.isBlank()) continue;

                MiscUtils.sendCommand(player, line, Map.of(
                        "%prefix%", this.config.getProperty(ConfigKeys.command_prefix),
                        "%player%", playerName,
                        "%crate%", fileName
                ));
            }
        }

        EventManager.logEvent(event.getEventType(), playerName, player, crate, event.getKeyType(), amount);
    }
}