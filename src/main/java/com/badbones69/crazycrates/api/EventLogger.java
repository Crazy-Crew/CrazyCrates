package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.objects.Crate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*

Command
Key Given / Taken / Sent / Received
Crate Opened

Crate Name, Key Name, Player Name, Who sent keys, Who got keys, who gave keys.

 */

public class EventLogger {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private File logFile;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    public void load() throws IOException {
        setLogFile(new File(plugin.getDataFolder(), "events.log"));
        setFileWriter(new FileWriter(getLogFile(), true));
        setBufferedWriter(new BufferedWriter(getFileWriter()));
        checkLogFileExists(getLogFile());
    }

    public void unload() throws IOException {
        getBufferedWriter().close();
    }

    public void logCrateEvent(String playerName, Crate crate, KeyType keyType) {
        log(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%",
                playerName, crate, keyType), CrateEventType.CRATE_EVENT.toString());

        System.out.println(setEntryData("Player: %player% | Crate Name: %crate_name% | Crate Type: %crate_type% | Key Name: %key_name% | Key Type: %key_type% | Key Item: %key_item%",
                playerName, crate, keyType));
    }

    public void logKeyEvent(KeyEventType keyEventType) {
        log("", keyEventType.getName());
    }

    public void logCommandEvent(CommandEventType commandEventType) {
        log("", commandEventType.getName());
    }

    private void log(String toLog, String eventType) {
        try {
            getBufferedWriter().write("[" + getDateTime() + " " + eventType + "]: " + toLog);
            getBufferedWriter().newLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyyyy HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    private File getLogFile() {
        return logFile;
    }

    private void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    private FileWriter getFileWriter() {
        return fileWriter;
    }

    private void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    private BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    private void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    private String setEntryData(String string, String playerName, Crate crate, KeyType keyType) {
        return string.replace("%player%", playerName).replace("%crate_name%", crate.getName())
                .replace("%crate_type%", crate.getCrateType().getName()).replace("%key_name%", crate.getKey().displayName().toString())
                .replace("%key_type%", keyType.getName()).replace("%key_item%", crate.getKey().getType().toString());
    }

    private boolean checkLogFileExists(File file) {
        if(!file.exists()) {
            return file.mkdirs();
        }
        return false;
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

