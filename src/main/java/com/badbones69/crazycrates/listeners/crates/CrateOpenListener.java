package com.badbones69.crazycrates.listeners.crates;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.List;

public class CrateOpenListener implements Listener {

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        final Player player = event.getPlayer();
        final Crate crate = event.getCrate();

        final String fileName = crate.getFileName();
        final String fancyName = crate.getCrateName();

        if (crate.getCrateType() != CrateType.menu) {
            if (crate.getPrizes().isEmpty() || !crate.canWinPrizes(player)) {
                Messages.no_prizes_found.sendMessage(player, "{crate}", fancyName);

                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                event.setCancelled(true);

                return;
            }
        }

        if (this.config.getProperty(ConfigKeys.use_new_permission_system)) {
            if (player.hasPermission("crazycrates.deny.open." + fileName)) {
                Messages.no_crate_permission.sendMessage(player, "{crate}", fancyName);

                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removeCrateInUse(player);

                event.setCancelled(true);

                return;
            }
        } else {
            if (!player.hasPermission("crazycrates.open." + fileName)) {
                Messages.no_crate_permission.sendMessage(player, "{crate}", fancyName);

                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removeCrateInUse(player);

                event.setCancelled(true);

                return;
            }
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        if (crate.getCrateType() != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), fileName);

        final YamlConfiguration configuration = event.getConfiguration();

        final String broadcastMessage = configuration.getString("Crate.BroadCast", "");
        final boolean broadcastToggle = configuration.getBoolean("Crate.OpeningBroadCast", false);

        if (broadcastToggle && crate.getCrateType() != CrateType.cosmic) {
            if (!broadcastMessage.isBlank()) {
                final String builder = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, broadcastMessage) : broadcastMessage;

                if (this.plugin.isLegacy()) {
                    this.plugin.getServer().broadcastMessage(ItemUtil.color(builder.replaceAll("%crate%", fancyName).replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName())));
                } else {
                    this.plugin.getServer().broadcast(AdvUtil.parse(builder.replaceAll("%crate%", fancyName).replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName())));
                }
            }
        }

        final boolean commandToggle = configuration.contains("Crate.opening-command") && configuration.getBoolean("Crate.opening-command.toggle");

        if (commandToggle) {
            final List<String> commands = configuration.getStringList("Crate.opening-command.commands");

            if (!commands.isEmpty()) {
                commands.forEach(line -> {
                    String builder;

                    if (Support.placeholder_api.isEnabled() ) {
                        builder = PlaceholderAPI.setPlaceholders(player, line.replaceAll("%crate%", fileName).replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName()));
                    } else {
                        builder = line.replaceAll("%crate%", fileName).replaceAll("%prefix%", MsgUtils.getPrefix()).replaceAll("%player%", player.getName());
                    }

                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), builder);
                });
            }
        }
    }
}