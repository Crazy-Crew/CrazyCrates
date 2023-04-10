package com.badbones69.crazycrates.commands.subs.admin;

import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.commands.subs.CustomPlayer;
import com.badbones69.crazycrates.enums.types.CrateType;
import com.badbones69.crazycrates.enums.types.KeyType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandGive extends CommandManager {

    @SubCommand("give-random")
    @Permission(value = "crazycrates.command.admin.giverandomkey", def = PermissionDefault.OP)
    public void onAdminCrateGiveRandom(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        Crate crate = crazyManager.getCrates().get((int) crazyManager.pickNumber(0, (crazyManager.getCrates().size() - 2)));

        onAdminCrateGive(sender, keyType, crate.getName(), amount, target);
    }

    @SubCommand("give")
    @Permission(value = "crazycrates.command.admin.givekey", def = PermissionDefault.OP)
    public void onAdminCrateGive(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);
        Crate crate = crazyManager.getCrateFromName(crateName);

        Player player = plugin.getServer().getPlayer(target.getUUID());
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(target.getUUID());

        Player person;

        if (player != null) person = player; else person = offlinePlayer.getPlayer();

        String name;

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        if (crate.getCrateType() != CrateType.MENU) {
            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(person, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                    if (person != null) person.getInventory().addItem(crate.getKey(amount));
                } else {
                    if (person != null && person.isOnline()) {
                        crazyManager.addKeys(amount, person, crate, type);
                    } else {
                        if (!crazyManager.addOfflineKeys(offlinePlayer.getName(), crate, amount)) {
                            sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                        } else {
                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%Amount%", String.valueOf(amount));
                            placeholders.put("%Player%", offlinePlayer.getName());

                            sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
                        }

                        return;
                    }
                }

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", String.valueOf(amount));
                placeholders.put("%Player%", player.getName());
                placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());

                boolean fullMessage = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
                boolean inventoryCheck = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

                sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
                if (!inventoryCheck || !fullMessage && !Methods.isInventoryFull(player) && player.isOnline()) player.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

                boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, logFile, logConsole);
            }

            return;
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}