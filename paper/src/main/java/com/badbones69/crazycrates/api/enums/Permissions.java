package com.badbones69.crazycrates.api.enums;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public enum Permissions {

    CRAZYCRATES_PLAYER_TRANSFER_KEYS("command.player.transfer", "Allows players to send virtual keys to another player.", PermissionDefault.OP),
    CRAZYCRATES_PLAYER_HELP("help", "Shows the help menu for Crazy Crates.", PermissionDefault.TRUE),
    CRAZYCRATES_PLAYER_EXCLUDE("command.exclude.player.giveall", "Permission to prevent a player from getting keys.", PermissionDefault.OP),
    CRAZYCRATES_OPEN("command.admin.open", "Tries to open a crate for the player if they have a key.", PermissionDefault.OP),
    CRAZYCRATES_OPEN_OTHER("command.admin.open.others", "Tries to open a crate for a player if they have a key.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.open", true);
    }}),
    CRAZYCRATES_MASS_OPEN("command.admin.massopen", "Used to mass open a crate without an animation.", PermissionDefault.OP),
    CRAZYCRATES_FORCE_OPEN("command.admin.forceopen", "Opens a crate for a player for free.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.open.others", true);
        put("crazycrates.command.admin.open", true);
    }}),
    CRAZYCRATES_GIVE_KEY("command.admin.givekey", "Give a key(s) to a player to use on a crate.", PermissionDefault.OP),
    CRAZYCRATES_GIVE_RANDOM_KEY("command.admin.giverandomkey", "Give a random key(s) to a player to use on a crate.", PermissionDefault.OP),
    CRAZYCRATES_GIVE_ALL("command.admin.giveall", "Gives all online players keys to use on crates.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.givekey", true);
        put("crazycrates.command.admin.giverandomkey", true);
        put("crazycrates.command.admin.takekey", true);
        put("crazycrates.command.player.transfer", true);
    }}),
    CRAZYCRATES_TAKE_KEY("command.admin.takekey", "Allows you to take keys from a player.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.player.transfer", true);
    }}),
    CRAZYCRATES_SET_CRATE("command.admin.set", "Set the block you are looking at as the specified crate.", PermissionDefault.OP),
    CRAZYCRATES_SET_MENU("command.admin.setmenu", "Sets the block you are looking at to open the (/crate) crate menu.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.set", true);
    }}),
    CRAZYCRATES_RELOAD("command.admin.reload", "Reloads the entire plugin.", PermissionDefault.OP),
    CRAZYCRATES_DEBUG("command.admin.debug", "Debugs the plugin.", PermissionDefault.OP),
    CRAZYCRATES_CONVERT("command.admin.convert", "Converts data from other supported crate plugins into crazy crates.", PermissionDefault.OP),
    CRAZYCRATES_WAND("wand", "Gives a wand that lets you select 2 points to create schematics.", PermissionDefault.OP),
    CRAZYCRATES_SAVE("save", "Save the new schematic file to the schematics folder.", PermissionDefault.OP),
    CRAZYCRATES_ADDITEM("command.admin.additem", "Adds items in-game to a prize in a crate.", PermissionDefault.OP),
    CRAZYCRATES_PREVIEW("command.admin.preview", "Opens the preview of any crate for a player.", PermissionDefault.OP),
    CRAZYCRATES_ACCESS("command.admin.access", "General purpose access for admins.", PermissionDefault.OP),
    CRAZYCRATES_MENU("command.player.menu", "Opens the primary crate menu.", PermissionDefault.TRUE),
    CRAZYCRATES_KEY("command.player.key", "Check the number of keys you have.", PermissionDefault.TRUE),
    CRAZYCRATES_LIST("command.admin.list", "Displays a list of all available crates.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.teleport", true);
    }}),
    CRAZYCRATES_TELEPORT("command.admin.teleport", "Teleports to a crate.", PermissionDefault.OP),
    CRAZYCRATES_KEY_ALL("command.player.key.others", "Check the number of keys a player has.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.player.key", true);
    }}),
    CRAZYCRATES_ADMIN_ALL("command.admin.*", "Give all admin based permissions.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.admin.open.others", true);
        put("crazycrates.command.admin.forceopen", true);
        put("crazycrates.command.admin.preview", true);
        put("crazycrates.command.admin.additem", true);
        put("crazycrates.command.admin.setmenu", true);
        put("crazycrates.command.admin.giveall", true);
        put("crazycrates.command.admin.convert", true);
        put("crazycrates.command.admin.access", true);
        put("crazycrates.command.admin.debug", true);
        put("crazycrates.command.admin.list", true);

        put("crazycrates.wand", true);
        put("crazycrates.save", true);
    }}),

    CRAZYCRATES_PLAYER_ALL("command.players.*", "Give all player based permissions.", PermissionDefault.OP, new HashMap<>() {{
        put("crazycrates.command.player.key.others", true);
        put("crazycrates.command.player.menu", true);
        put("crazycrates.help", true);
    }});

    private final String node;
    private final String description;
    private final PermissionDefault isDefault;
    private final HashMap<String, Boolean> children;

    /**
     * A constructor to build a permission
     *
     * @param node the default permission
     * @param description the permission description
     */
    Permissions(String node, String description, PermissionDefault isDefault, HashMap<String, Boolean> children) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;

        this.children = children;
    }

    /**
     * A constructor to build a permission
     *
     * @param node the default permission
     * @param description the permission description
     */
    Permissions(String node, String description, PermissionDefault isDefault) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;
        this.children = new HashMap<>();
    }

    /**
     * Get a built permission with no action type.
     *
     * @return a completed permission
     */
    public String getPermission() {
        return "crazycrates." + this.node;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    public PermissionDefault isDefault() {
        return this.isDefault;
    }

    public HashMap<String, Boolean> getChildren() {
        return this.children;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission());
    }
}