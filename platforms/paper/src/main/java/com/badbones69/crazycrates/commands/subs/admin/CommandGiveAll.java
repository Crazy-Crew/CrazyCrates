package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.enums.Permissions;
import com.badbones69.crazycrates.enums.types.CrateType;
import com.badbones69.crazycrates.enums.types.KeyType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandGiveAll extends CommandManager {

    @SubCommand("giveall")
    @Permission(value = "crazycrates.command.admin.giveall", def = PermissionDefault.OP)
    public void onAdminCrateGiveAllKeys(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            if (crate.getCrateType() != CrateType.MENU) {
                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", String.valueOf(amount));
                placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());

                sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));

                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {

                    if (Methods.permCheck(onlinePlayer, Permissions.CRAZY_CRATES_PLAYER_EXCLUDE_GIVE_ALL, true)) continue;

                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
                    onlinePlayer.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        onlinePlayer.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

                        if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                            onlinePlayer.getInventory().addItem(crate.getKey(amount));
                            return;
                        }

                        crazyManager.addKeys(amount, onlinePlayer, crate, type);

                        boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                        boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                        eventLogger.logKeyEvent(onlinePlayer, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, logFile, logConsole);
                    }
                }

                return;
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}