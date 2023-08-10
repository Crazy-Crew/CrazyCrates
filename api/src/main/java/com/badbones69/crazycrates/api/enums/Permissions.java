package com.badbones69.crazycrates.api.enums;

public enum Permissions {

    CRAZY_CRATES_PLAYER_KEY("player.key", "Check the number of keys you have."),
    CRAZY_CRATES_PLAYER_KEY_OTHERS("player.key.others", "Check the number of keys a player has."),
    CRAZY_CRATES_PLAYER_TRANSFER_KEYS("player.transfer", "Allows players to send virtual keys to another player."),
    CRAZY_CRATES_PLAYER_MENU("player.menu", "Opens the primary crate menu."),
    CRAZY_CRATES_PLAYER_HELP("player.help", "Shows the help menu for Crazy Crates."),

    CRAZY_CRATES_PLAYER_EXCLUDE_GIVE_ALL("exclude.player.giveall", "Permission to prevent a player from getting keys from /GiveAll."),

    CRAZY_CRATES_ADMIN_HELP("admin.help", "Shows the help menu for admins."),
    CRAZY_CRATES_ADMIN_ACCESS("admin.access", "General purpose access for admins."),
    CRAZY_CRATES_ADMIN_ADD_ITEM("admin.additem", "Adds items in-game to a prize in a crate."),
    CRAZY_CRATES_ADMIN_PREVIEW("admin.preview", "Opens the preview of any crate for a player."),
    CRAZY_CRATES_ADMIN_LIST("admin.list", "Displays a list of all available crates."),
    CRAZY_CRATES_ADMIN_OPEN("admin.open", "Tries to open a crate for the player if they have a key."),
    CRAZY_CRATES_ADMIN_OPEN_OTHER("admin.open.others", "Tries to open a crate for a player if they have a key."),
    CRAZY_CRATES_ADMIN_MASS_OPEN("admin.massopen", "Used to mass open a crate without an animation."),
    CRAZY_CRATES_ADMIN_FORCE_OPEN("admin.forceopen", "Opens a crate for a player for free."),
    CRAZY_CRATES_ADMIN_TELEPORT("admin.teleport", "Teleports to a crate."),
    CRAZY_CRATES_ADMIN_GIVE_KEY("admin.givekey", "Give a key(s) to a player to use on a crate."),
    CRAZY_CRATES_ADMIN_GIVE_RANDOM_KEY("admin.giverandomkey", "Give a random key(s) to a player to use on a crate."),
    CRAZY_CRATES_ADMIN_GIVE_ALL("admin.giveall", "Gives all online players keys to use on crates."),
    CRAZY_CRATES_ADMIN_TAKE_KEY("admin.takekey", "Allows you to take keys from a player."),
    CRAZY_CRATES_ADMIN_SET_CRATE("admin.set", "Set the block you are looking at as the specified crate."),
    CRAZY_CRATES_ADMIN_SET_MENU("admin.setmenu", "Sets the block you are looking at to open the (/cc) crate menu."),
    CRAZY_CRATES_ADMIN_RELOAD("admin.reload", "Reloads the entire plugin."),
    CRAZY_CRATES_ADMIN_DEBUG("admin.debug", "Debugs the plugin."),
    CRAZY_CRATES_ADMIN_CONVERT("admin.convert", "Converts data from other supported crate plugins into crazy crates."),
    CRAZY_CRATES_ADMIN_SCHEMATIC("admin.schematic.*", "Gives all permissions related to schematics."),
    CRAZY_CRATES_ADMIN_SCHEMATIC_SET("admin.schematic.set", "Sets the positions #1 or #2 when making a new schematic for quadcrates."),
    CRAZY_CRATES_ADMIN_SCHEMATIC_SAVE("admin.schematic.save", "Saves the new schematic file to the schematics folder.");

    private final String defaultPermission;
    private final String description;

    /**
     * A constructor to build a permission
     *
     * @param defaultPermission the default permission
     * @param description the permission description
     */
    Permissions(String defaultPermission, String description) {
        this.defaultPermission = defaultPermission;
        this.description = description;
    }

    /**
     * Get a built permission with an action type.
     *
     * @param action the action type i.e "command"
     * @return a completed permission
     */
    public String getPermission(String action) {
        return "crazycrates." + action + "." + defaultPermission;
    }

    /**
     * Get a built permission with no action type.
     *
     * @return a completed permission
     */
    public String getPermission() {
        return "crazycrates.command." + defaultPermission;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}