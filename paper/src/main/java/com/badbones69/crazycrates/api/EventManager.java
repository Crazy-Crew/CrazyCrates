package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * //TODO() To the moon!
 * A few goals in mind in revamp.
 * <p></p>
 * 1) Try not to crash the server when the writes get to big.
 * 2) User friendly enum names
 * 3) Allow admins or players with permissions to view the logs of all players through a gui.
 * 4) Allow players to have some type of /crazycrates profile to see everything they've done. ( I do have "crates opened" stuff added which might make this useless )"
 * 5) No one else touch this class.
 */
public class EventManager {

    @NotNull
    private final static CrazyCrates plugin = CrazyCrates.get();

    private final static String fileName = Files.LOGS.getFileName();

    public static void logCrateEvent(Player player, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) {
            log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", player, player, crate, keyType), CrateEventType.CRATE_EVENT.getName());
        }

        if (logConsole) {
            plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), player, player, crate, keyType));
        }
    }

    public static void logKeyEvent(Player target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) {
            log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());
        }

        if (logConsole) {
            plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
        }
    }

    public static void logCrateEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) {
            log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", target, sender, crate, keyType), CrateEventType.CRATE_EVENT.getName());
        }

        if (logConsole) {
            plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), target, sender, crate, keyType));
        }
    }

    public static void logKeyEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) {
            log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());
        }

        if (logConsole) {
            plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
        }
    }

    public static void logCommandEvent(CommandEventType commandEventType) {
        log("", commandEventType.getName());
    }

    private static void log(String toLog, String eventType) {
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/" + fileName, true));

            bufferedWriter.write("[" + getDateTime() + " " + eventType + "]: " + toLog + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to write to " + fileName, exception);
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException exception) {
                plugin.getLogger().log(Level.WARNING, "Failed to close buffer for " + fileName, exception);
            }
        }
    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    @SuppressWarnings("DEPRECATIONS")
    private static String setEntryData(String string, Player player, CommandSender sender, Crate crate, KeyType keyType) {
        return string.replace("%player%", player.getName()).replace("%crate_name%", crate.getName()).replace("%sender%", sender.getName())
                .replace("%crate_type%", crate.getCrateType().getName()).replace("%key_name%", crate.getKeyName())
                .replace("%key_type%", keyType.getName()).replace("%key_item%", crate.getKey().getType().toString());
    }

    private static String setEntryData(String string, OfflinePlayer player, CommandSender sender, Crate crate, KeyType keyType) {
        return string.replace("%player%", player.getName()).replace("%crate_name%", crate.getName()).replace("%sender%", sender.getName())
                .replace("%crate_type%", crate.getCrateType().getName()).replace("%key_name%", crate.getKeyName())
                .replace("%key_type%", keyType.getName()).replace("%key_item%", crate.getKey().getType().toString());
    }

    public enum KeyEventType {
        KEY_EVENT_GIVEN("key_event_given"),
        KEY_EVENT_SENT("key_event_sent"),
        KEY_EVENT_RECEIVED("key_event_received"),
        KEY_EVENT_REMOVED("key_event_removed");

        private final String keyEventName;

        KeyEventType(String name) {
            this.keyEventName = name;
        }

        public String getName() {
            return keyEventName;
        }
    }

    public enum CrateEventType {
        CRATE_EVENT("crate_event");

        private final String crateEventName;

        CrateEventType(String name) {
            this.crateEventName = name;
        }

        public String getName() {
            return crateEventName;
        }
    }

    public enum CommandEventType {
        COMMAND_SUCCESS("command_event_success"),
        COMMAND_FAIL("command_event_fail");

        private final String commandEventName;

        CommandEventType(String name) {
            this.commandEventName = name;
        }

        public String getName() {
            return commandEventName;
        }
    }
}