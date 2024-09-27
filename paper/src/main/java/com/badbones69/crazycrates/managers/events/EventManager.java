package com.badbones69.crazycrates.managers.events;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.managers.config.ConfigManager;
import com.badbones69.crazycrates.managers.config.impl.ConfigKeys;
import com.badbones69.crazycrates.managers.events.enums.EventType;
import com.ryderbelserion.vital.common.utils.FileUtil;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventManager {

    private final static CrazyCrates plugin = CrazyCrates.getPlugin();

    private final static SettingsManager config = ConfigManager.getConfig();

    public static void logEvent(final EventType type, final Player player, final CommandSender sender, final Crate crate, final KeyType keyType, final int amount) {
        handle(type, player, sender, crate, keyType, amount);
    }

    public static void logEvent(final EventType type, final OfflinePlayer player, final CommandSender sender, final Crate crate, final KeyType keyType, final int amount) {
        handle(type, player, sender, crate, keyType, amount);
    }

    private static void handle(final EventType type, final OfflinePlayer player, final CommandSender sender, final Crate crate, final KeyType keyType, final int amount) {
        String message = "";
        File file = null;

        switch (type) {
            case event_key_given, event_key_removed, event_key_received, event_key_sent, event_key_taken, event_key_taken_multiple -> {
                message = "Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%"
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%player%", player.getName())
                        .replace("%sender%", sender.getName())
                        .replace("%key_name%", crate.getKeyName());

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                file = Files.key_log.getFile();
            }

            /*case event_command_sent -> {

            }

            case event_command_failed -> {

            }*/

            case event_crate_opened -> {
                message = "Player: %player% | Crate Name: %crate_name% | Force Opened: %force_opened% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%"
                        .replace("%player%", player.getName())
                        .replace("%crate_name%", crate.getCrateName())
                        .replace("%crate_type%", crate.getCrateType().getName())
                        .replace("%key_name%", crate.getKeyName())
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%key_item%", crate.getKey().getType().getKey().getKey())
                        .replace("%force_opened%", "no");

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                file = Files.crate_log.getFile();
            }

            case event_crate_force_opened -> {
                message = "Player: %player% | Crate Name: %crate_name% | Force Opened: %force_opened% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%"
                        .replace("%player%", player.getName())
                        .replace("%crate_name%", crate.getCrateName())
                        .replace("%crate_type%", crate.getCrateType().getName())
                        .replace("%key_name%", crate.getKeyName())
                        .replace("%key_type%", keyType.getFriendlyName())
                        .replace("%key_item%", crate.getKey().getType().getKey().getKey())
                        .replace("%force_opened%", "yes");

                if (amount >= 1) {
                    message = message + " | Amount: %amount%".replace("%amount%", String.valueOf(amount));
                }

                file = Files.crate_log.getFile();
            }
        }

        log(message, file, type);
    }

    private static void log(final String message, final File file, final EventType type) {
        final boolean log_to_file = config.getProperty(ConfigKeys.log_to_file);

        final String time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

        if (log_to_file) {
            FileUtil.write(file, "[" + time + " " + type.getEvent() + "]: " + message);
        }

        final boolean log_to_console = config.getProperty(ConfigKeys.log_to_console);

        if (log_to_console) {
            plugin.getComponentLogger().info("[{} {}]: {}", time, type.getEvent(), AdvUtil.parse(message));
        }
    }
}