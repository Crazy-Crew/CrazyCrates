package com.badbones69.crazycrates.commands.subs.admin.crates;

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

public class CommandForceOpen extends CommandManager {

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.command.admin.forceopen", def = PermissionDefault.OP)
    public void onAdminForceOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (crate.getName().equalsIgnoreCase(crateName)) {

                    if (crazyManager.isInOpeningList(player)) {
                        sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                        return;
                    }

                    CrateType type = crate.getCrateType();

                    if (type != null) {
                        if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
                            crazyManager.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);

                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%Crate%", crate.getName());
                            placeholders.put("%Player%", player.getName());

                            sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

                            boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                            boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                            eventLogger.logKeyEvent(player, sender, crate, KeyType.FREE_KEY, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);
                        } else {
                            sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        }
                    } else {
                        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
                    }

                    return;
                }
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}