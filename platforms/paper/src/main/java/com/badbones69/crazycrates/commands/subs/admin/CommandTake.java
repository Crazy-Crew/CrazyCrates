package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.types.CrateType;
import com.badbones69.crazycrates.enums.types.KeyType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandTake extends CommandManager {

    @SubCommand("take")
    @Permission(value = "crazycrates.command.admin.takekey", def = PermissionDefault.OP)
    public void onAdminCrateTake(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") Player target) {
        KeyType type = KeyType.getFromName(keyType);

        Crate crate = crazyManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate != null) {
            if (crate.getCrateType() != CrateType.MENU) {

                if (!crazyManager.takeKeys(amount, target, crate, type, false)) {
                    Methods.failedToTakeKey(sender, crate);
                    return;
                }

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", String.valueOf(amount));
                placeholders.put("%Player%", target.getName());

                sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                eventLogger.logKeyEvent(target, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);

                return;
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}