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
public class EventLogger {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    private final String fileName = Files.LOGS.getFileName();

    public void logCrateEvent(Player player, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", player, player, crate, keyType), CrateEventType.CRATE_EVENT.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), player, player, crate, keyType));
    }

    public void logKeyEvent(Player target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
    }

    public void logCrateEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", target, sender, crate, keyType), CrateEventType.CRATE_EVENT.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), target, sender, crate, keyType));
    }

    public void logKeyEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(MsgUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
    }

    public void logCommandEvent(CommandEventType commandEventType) {
        log("", commandEventType.getName());
    }

    private void log(String toLog, String eventType) {
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(this.plugin.getDataFolder() + "/" + this.fileName, true));

            bufferedWriter.write("[" + getDateTime() + " " + eventType + "]: " + toLog + System.getProperty("line.separator"));
            bufferedWriter.flush();
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to write to " + this.fileName, exception);
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException exception) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to close buffer for " + this.fileName, exception);
            }
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    @SuppressWarnings("DEPRECATIONS")
    private String setEntryData(String string, Player player, CommandSender sender, Crate crate, KeyType keyType) {
        return string.replace("%player%", player.getName()).replace("%crate_name%", crate.getName()).replace("%sender%", sender.getName())
                .replace("%crate_type%", crate.getCrateType().getName()).replace("%key_name%", crate.getKey().getItemMeta().getDisplayName())
                .replace("%key_type%", keyType.getName()).replace("%key_item%", crate.getKey().getType().toString());
    }

    private String setEntryData(String string, OfflinePlayer player, CommandSender sender, Crate crate, KeyType keyType) {
        return string.replace("%player%", player.getName()).replace("%crate_name%", crate.getName()).replace("%sender%", sender.getName())
                .replace("%crate_type%", crate.getCrateType().getName()).replace("%key_name%", crate.getKey().getItemMeta().getDisplayName())
                .replace("%key_type%", keyType.getName()).replace("%key_item%", crate.getKey().getType().toString());
    }

    public enum KeyEventType {

        KEY_EVENT_GIVEN("KEY EVENT GIVEN"),
        KEY_EVENT_SENT("KEY EVENT SENT"),
        KEY_EVENT_RECEIVED("KEY EVENT RECEIVED"),
        KEY_EVENT_REMOVED("KEY EVENT REMOVED");

        private final String keyEventName;

        KeyEventType(String name) {
            this.keyEventName = name;
        }

        public String getName() {
            return keyEventName;
        }
    }

    public enum CrateEventType {

        CRATE_EVENT("CRATE EVENT");

        private final String crateEventName;

        CrateEventType(String name) {
            this.crateEventName = name;
        }

        public String getName() {
            return crateEventName;
        }
    }

    public enum CommandEventType {

        COMMAND_SUCCESS("COMMAND EVENT SUCCESS"),
        COMMAND_FAIL("COMMAND EVENT FAIL");

        private final String commandEventName;

        CommandEventType(String name) {
            this.commandEventName = name;
        }

        public String getName() {
            return commandEventName;
        }
    }
}