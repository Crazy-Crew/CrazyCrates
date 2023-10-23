package us.crazycrew.crazycrates.api.enums;

public enum Permissions {

    crazycrates_player_key("key", "Check the number of keys you have."),
    crazycrates_player_key_others("key-others", "Check the number of keys a player has."),
    crazycrates_player_transfer_keys("transfer", "Allows players to send virtual keys to another player."),
    crazycrates_player_menu("crate-menu", "Opens the primary crate menu."),
    crazycrates_player_help("help", "Shows the help menu for Crazy Crates."),
    crazycrates_player_exclude_give_all("exclude-player-give-all", "Permission to prevent a player from getting keys from /GiveAll."),
    crazycrates_admin_access("admin-access", "General purpose access for admins."),
    crazycrates_admin_add_item("additem", "Adds items in-game to a prize in a crate."),
    crazycrates_admin_preview("preview", "Opens the preview of any crate for a player."),
    crazycrates_admin_list("list", "Displays a list of all available crates."),
    crazycrates_admin_open("open", "Tries to open a crate for the player if they have a key."),
    crazycrates_admin_open_other("open-others", "Tries to open a crate for a player if they have a key."),
    crazycrates_admin_mass_open("mass-open", "Used to mass open a crate without an animation."),
    crazycrates_admin_force_open("force-open", "Opens a crate for a player for free."),
    crazycrates_admin_teleport("teleport", "Teleports to a crate."),
    crazycrates_admin_give_key("give-key", "Give a key(s) to a player to use on a crate."),
    crazycrates_admin_give_random_key("give-random-key", "Give a random key(s) to a player to use on a crate."),
    crazycrates_admin_give_all("give-all", "Gives all online players keys to use on crates."),
    crazycrates_admin_take_key("take-key", "Allows you to take keys from a player."),
    crazycrates_admin_set_crate("set-crate", "Set a block you are looking at as the specified crate."),
    crazycrates_admin_reload("reload", "Reloads the entire plugin."),
    crazycrates_admin_debug("debug", "Debugs the plugin."),
    crazycrates_admin_convert("convert", "Converts data from other supported crate plugins into crazy crates."),
    crazycrates_admin_schematic("schematic.*", "Gives all permissions related to schematics."),
    crazycrates_admin_schematic_set("schematic-set", "Sets the positions #1 or #2 when making a new schematic for quadcrates."),
    crazycrates_admin_schematic_save("schematic-save", "Saves the new schematic file to the schematics folder.");

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
        return "crazycrates." + action + "." + this.defaultPermission;
    }

    /**
     * Get a built permission with no action type.
     *
     * @return a completed permission
     */
    public String getPermission() {
        return "crazycrates." + this.defaultPermission;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
}