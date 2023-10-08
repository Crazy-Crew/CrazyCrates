package com.badbones69.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventLogger {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public void logCrateEvent(Player player, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", player, player, crate, keyType), CrateEventType.crate_open_event.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(LegacyUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), player, player, crate, keyType));
    }

    public void logKeyEvent(Player target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(LegacyUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
    }

    public void logCrateEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%", target, sender, crate, keyType), CrateEventType.crate_open_event.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(LegacyUtils.color("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name%&r | Key Type: %key_type% | Key Item: %key_item%"), target, sender, crate, keyType));
    }

    public void logKeyEvent(OfflinePlayer target, CommandSender sender, Crate crate, KeyType keyType, KeyEventType keyEventType, boolean logFile, boolean logConsole) {
        if (logFile) log(setEntryData("Player: %player% | Sender: %sender% | Key Name: %key_name% | Key Type: %key_type%", target, sender, crate, keyType), keyEventType.getName());

        if (logConsole) this.plugin.getLogger().info(setEntryData(LegacyUtils.color("Player: %player% | Sender: %sender% | Key Name: %key_name%&r | Key Type: %key_type%"), target, sender, crate, keyType));
    }

    public void logCommandEvent(CommandEventType commandEventType) {
        log("", commandEventType.getName());
    }

    private void log(String toLog, String eventType) {
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(this.plugin.getDataFolder() + "/" + FileManager.Files.LOGS.getFileName(), true));

            bufferedWriter.write("[" + getDateTime() + " " + eventType + "]: " + toLog + System.getProperty("line.separator"));
            bufferedWriter.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException exception) {
                exception.printStackTrace();
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

        key_give_event("key_give_event"),
        key_sent_event("key_sent_event"),
        key_received_event("key_received_event"),
        key_removed_event("key_removed_event");

        private final String keyEventName;

        KeyEventType(String name) {
            this.keyEventName = name;
        }

        public String getName() {
            return this.keyEventName;
        }
    }

    public enum CrateEventType {

        crate_open_event("crate_open_event");

        private final String crateEventName;

        CrateEventType(String name) {
            this.crateEventName = name;
        }

        public String getName() {
            return this.crateEventName;
        }
    }

    public enum CommandEventType {

        command_success("command_event_success"),
        command_fail("command_event_fail");

        private final String commandEventName;

        CommandEventType(String name) {
            this.commandEventName = name;
        }

        public String getName() {
            return this.commandEventName;
        }
    }
}