package com.badbones69.crazycrates.listeners.crates;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.CrateOpenEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.List;

public class CrateOpenListener implements Listener {

    @NotNull
    private final CrazyCratesPaper plugin = CrazyCratesPaper.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final BukkitUserManager userManager = this.plugin.getUserManager();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        Crate crate = event.getCrate();

        if (crate.getCrateType() != CrateType.menu) {
            if (!crate.canWinPrizes(player)) {
                player.sendMessage(Messages.no_prizes_found.getMessage("{crate}", crate.getName(), player));
                this.crateManager.removePlayerFromOpeningList(player);
                this.crateManager.removePlayerKeyType(player);

                event.setCancelled(true);

                return;
            }
        }

        if (!player.hasPermission("crazycrates.open." + crate.getName()) || !player.hasPermission("crazycrates.open." + crate.getName().toLowerCase())) {
            player.sendMessage(Messages.no_crate_permission.getMessage("{crate}", crate.getName(), player));

            this.crateManager.removePlayerFromOpeningList(player);
            this.crateManager.removeCrateInUse(player);

            event.setCancelled(true);

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        if (crate.getCrateType() != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), crate.getName());

        FileConfiguration configuration = event.getConfiguration();

        String broadcastMessage = configuration.getString("Crate.BroadCast", "");
        boolean broadcastToggle = configuration.contains("Crate.OpeningBroadCast") && configuration.getBoolean("Crate.OpeningBroadCast");

        if (broadcastToggle) {
            if (!broadcastMessage.isBlank()) {
                this.plugin.getServer().broadcastMessage(MsgUtils.color(broadcastMessage.replaceAll("%prefix%", MsgUtils.getPrefix())).replaceAll("%player%", player.getName()));
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
    }
}