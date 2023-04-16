package com.badbones69.crazycrates.commands.subs.player;

import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.types.KeyType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandTransfer extends CommandManager {

    @SubCommand("transfer")
    @Permission(value = "crazycrates.command.player.transfer", def = PermissionDefault.OP)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            if (!player.getName().equalsIgnoreCase(sender.getName())) {

                if (crazyManager.getVirtualKeys(sender, crate) >= amount) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        crazyManager.takeKeys(amount, sender, crate, KeyType.VIRTUAL_KEY, false);
                        crazyManager.addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);

                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Crate%", crate.getName());
                        placeholders.put("%Amount%", String.valueOf(amount));
                        placeholders.put("%Player%", player.getName());

                        sender.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));

                        placeholders.put("%Player%", sender.getName());

                        player.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));

                        boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                        boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                        eventLogger.logKeyEvent(player, sender, crate, KeyType.VIRTUAL_KEY, EventLogger.KeyEventType.KEY_EVENT_RECEIVED, logFile, logConsole);
                    }
                } else {
                    sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
                }
            } else {
                sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            }
        } else {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
        }
    }
}