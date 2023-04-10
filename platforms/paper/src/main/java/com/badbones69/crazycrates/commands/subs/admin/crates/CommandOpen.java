package com.badbones69.crazycrates.commands.subs.admin.crates;

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
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandOpen extends CommandManager {

    @SubCommand("open-others")
    @Permission(value = "crazycrates.command.admin.open.others", def = PermissionDefault.OP)
    public void onAdminCrateOpenOthers(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        openCrate(sender, player, crateName);
    }

    @SubCommand("open")
    @Permission(value = "crazycrates.command.admin.open", def = PermissionDefault.OP)
    public void onAdminCrateOpen(Player player, @Suggestion("crates") String crateName) {
        openCrate(player, player, crateName);
    }

    private void openCrate(CommandSender sender, Player player, String crateName) {
        for (Crate crate : crazyManager.getCrates()) {

            if (crate.getName().equalsIgnoreCase(crateName)) {

                if (crazyManager.isInOpeningList(player)) {
                    sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                    return;
                }

                CrateType type = crate.getCrateType();

                if (type != null) {
                    FileConfiguration config = FileManager.Files.CONFIG.getFile();

                    boolean hasKey = false;
                    KeyType keyType = KeyType.VIRTUAL_KEY;

                    if (crazyManager.getVirtualKeys(player, crate) >= 1) {
                        hasKey = true;
                    } else {
                        if (config.getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
                            if (crazyManager.hasPhysicalKey(player, crate, false)) {
                                hasKey = true;
                                keyType = KeyType.PHYSICAL_KEY;
                            }
                        }
                    }

                    if (!hasKey) {
                        if (config.contains("Settings.Need-Key-Sound")) {
                            Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));

                            player.playSound(player.getLocation(), sound, 1f, 1f);
                        }

                        player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                        return;
                    }

                    if (Methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

                    if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER && type != CrateType.QUAD_CRATE) {
                        crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Crate%", crate.getName());
                        placeholders.put("%Player%", player.getName());

                        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

                        boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
                        boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

                        eventLogger.logKeyEvent(player, sender, crate, keyType, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);
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
}