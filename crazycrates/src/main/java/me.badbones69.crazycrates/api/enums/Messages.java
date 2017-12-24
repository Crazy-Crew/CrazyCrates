package me.badbones69.crazycrates.api.enums;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.controlers.FileManager.Files;

import java.util.HashMap;
import java.util.List;

public enum Messages
{

    HELP("Help"),
    NO_TELEPORTING("No-Teleporting"),
    NO_COMMANDS_WHILE_CRATE_OPENED("No-Commands-While-In-Crate"),
    NO_KEY("No-Key"),
    NO_VIRTUAL_KEY("No-Virtual-Key"),
    ALREADY_OPENING_CRATE("Already-Opening-Crate"),
    QUICK_CRATE_IN_USE("Quick-Crate-In-Use"),
    WORLD_DISABLED("World-Disabled"),
    RELOAD("Reload"),
    NOT_ONLINE("Not-Online"),
    NO_PERMISSION("No-Permission"),
    CRATE_ALREADY_OPENED("Crate-Already-Opened"),
    CANT_BE_A_VIRTUAL_CRATE("Cant-Be-Virtual-Crate"),
    INVENTORY_FULL("Inventory-Full"),
    TO_CLOSE_TO_ANOTHER_PLAYER("To-Close-To-Another-Player"),
    NEEDS_MORE_ROOM("Needs-More-Room"),
    OUT_OF_TIME("Out-Of-Time"),
    MUST_BE_A_PLAYER("Must-Be-A-Player"),
    MUST_BE_LOOKING_AT_A_BLOCK("Must-Be-Looking-At-A-Block"),
    CREATED_PHYSICAL_CRATE("Created-Physical-Crate"),
    NOT_A_CRATE("Not-A-Crate"),
    NOT_A_NUMBER("Not-A-Number"),
    GIVEN_EVERYONE_KEYS("Given-Everyone-Keys"),
    GIVEN_A_PLAYER_KEYS("Given-A-Player-Keys"),
    GIVEN_OFFLINE_PLAYER_KEYS("Given-Offline-Player-Keys"),
    TAKE_A_PLAYER_KEYS("Take-A-Player-Keys"),
    TAKE_OFFLINE_PLAYER_KEYS("Take-Offline-Player-Keys"),
    OPENED_A_CRATE("Opened-A-Crate"),
    INTERNAL_ERROR("Internal-Error");

    private String path;

    private Messages(String path)
    {
        this.path = path;
    }

    public String getMessage()
    {
        if (isList())
        {
            return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
        }
        else
        {
            return Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
        }
    }

    public String getMessage(HashMap<String, String> placeholders)
    {
        String message = "";
        if (isList())
        {
            message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
        }
        else
        {
            message = Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
            for (String ph : placeholders.keySet())
            {
                if (message.contains(ph))
                {
                    message = message.replaceAll(ph, placeholders.get(ph));
                }
            }
        }
        return message;
    }

    private String convertList(List<String> list)
    {
        String message = "";
        for (String m : list)
        {
            message += m + "\n";
        }
        return message;
    }

    private String convertList(List<String> list, HashMap<String, String> placeholders)
    {
        String message = "";
        for (String m : list)
        {
            message += m + "\n";
        }
        for (String ph : placeholders.keySet())
        {
            message = message.replaceAll(ph, placeholders.get(ph));
        }
        return message;
    }

    private Boolean isList()
    {
        if (Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty())
        {
            return false;
        }
        return true;
    }

}