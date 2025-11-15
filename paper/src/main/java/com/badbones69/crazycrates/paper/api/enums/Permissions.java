package com.badbones69.crazycrates.paper.api.enums;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public enum Permissions {

    CRAZYCRATES_PLAYER_TRANSFER_KEYS("transfer", "Allows players to send virtual keys to another player.", PermissionDefault.OP),
    CRAZYCRATES_PLAYER_HELP("help", "Shows the help menu for Crazy Crates.", PermissionDefault.TRUE),
    CRAZYCRATES_PLAYER_EXCLUDE("exclude.give-all", "Permission to prevent a player from getting keys.", PermissionDefault.FALSE),
    CRAZYCRATES_OPEN("open", "Tries to open a crate for the player if they have a key.", PermissionDefault.OP),
    CRAZYCRATES_OPEN_OTHER("open-others", "Tries to open a crate for a player if they have a key.", PermissionDefault.OP, Map.of("crazycrates.open", true)),
    CRAZYCRATES_MASS_OPEN("massopen", "Used to mass open a crate without an animation.", PermissionDefault.OP),
    CRAZYCRATES_FORCE_OPEN("forceopen", "Opens a crate for a player for free.", PermissionDefault.OP, Map.ofEntries(
            Map.entry("crazycrates.open-others", true),
            Map.entry("crazycrates.open", true)
    )),
    CRAZYCRATES_GIVE_KEY("givekey", "Give a key(s) to a player to use on a crate.", PermissionDefault.OP),
    CRAZYCRATES_GIVE_RANDOM_KEY("giverandomkey", "Give a random key(s) to a player to use on a crate.", PermissionDefault.OP),
    CRAZYCRATES_GIVE_ALL("giveall", "Gives all online players keys to use on crates.", PermissionDefault.OP, Map.ofEntries(
            Map.entry("crazycrates.giverandomkey", true),
            Map.entry("crazycrates.transfer", true),
            Map.entry("crazycrates.givekey", true),
            Map.entry("crazycrates.takekey", true)
    )),
    CRAZYCRATES_TAKE_KEY("takekey", "Allows you to take keys from a player.", PermissionDefault.OP, Map.of("crazycrates.transfer", true)),
    CRAZYCRATES_SET_CRATE("set", "Set the block you are looking at as the specified crate.", PermissionDefault.OP),
    CRAZYCRATES_SET_MENU("setmenu", "Sets the block you are looking at to open the (/crate) crate menu.", PermissionDefault.OP, Map.of("crazycrates.set", true)),
    CRAZYCRATES_RELOAD("reload", "Reloads the entire plugin.", PermissionDefault.OP),
    CRAZYCRATES_DEBUG("debug", "Debugs the plugin.", PermissionDefault.OP),
    CRAZYCRATES_CONVERT("convert", "Converts data from other supported crate plugins into crazy crates.", PermissionDefault.OP),
    CRAZYCRATES_WAND("wand", "Gives a wand that lets you select 2 points to create schematics.", PermissionDefault.OP),
    CRAZYCRATES_SAVE("save", "Save the new schematic file to the schematics folder.", PermissionDefault.OP),
    CRAZYCRATES_ADDITEM("additem", "Adds items in-game to a prize in a crate.", PermissionDefault.OP),
    CRAZYCRATES_PREVIEW("preview", "Opens the preview of any crate for a player.", PermissionDefault.OP),
    CRAZYCRATES_ACCESS("admin", "General purpose access for admins.", PermissionDefault.OP),
    CRAZYCRATES_MENU("gui", "Opens the primary crate menu.", PermissionDefault.TRUE),
    CRAZYCRATES_KEY("keys", "Check the number of keys you have.", PermissionDefault.TRUE),
    CRAZYCRATES_LIST("list", "Displays a list of all available crates.", PermissionDefault.OP, Map.of("crazycrates.teleport", true)),
    CRAZYCRATES_CLAIM("claim", "Allows access to /crazycrates claim {crate}", PermissionDefault.OP),
    CRAZYCRATES_TELEPORT("teleport", "Teleports to a crate.", PermissionDefault.OP),
    CRAZYCRATES_KEY_ALL("keys-others", "Check the number of keys a player has.", PermissionDefault.OP, Map.of("crazycrates.keys", true)),
    CRAZYCRATES_ADMIN_ALL("admin.*", "Give all admin based permissions.", PermissionDefault.OP, Map.ofEntries(
            Map.entry("crazycrates.open-others", true),
            Map.entry("crazycrates.forceopen", true),
            Map.entry("crazycrates.preview", true),
            Map.entry("crazycrates.additem", true),
            Map.entry("crazycrates.setmenu", true),
            Map.entry("crazycrates.giveall", true),
            Map.entry("crazycrates.convert", true),
            Map.entry("crazycrates.admin", true),
            Map.entry("crazycrates.debug", true),
            Map.entry("crazycrates.list", true),
            Map.entry("crazycrates.save", true),
            Map.entry("crazycrates.claim", true)
    )),

    CRAZYCRATES_PLAYER_ALL("players.*", "Give all player based permissions.", PermissionDefault.OP, Map.ofEntries(
            Map.entry("crazycrates.key-others", true),
            Map.entry("crazycrates.gui", true),
            Map.entry("crazycrates.help", true)
    ));

    private final String node;
    private final String description;
    private final PermissionDefault isDefault;
    private final Map<String, Boolean> children;

    /**
     * A constructor to build a permission
     *
     * @param node the default permission
     * @param description the permission description
     */
    Permissions(@NotNull final String node, @NotNull final String description, @NotNull final PermissionDefault isDefault, @NotNull final Map<String, Boolean> children) {
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
    Permissions(@NotNull final String node, @NotNull final String description, @NotNull final PermissionDefault isDefault) {
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
    public @NotNull final String getPermission() {
        return "crazycrates." + this.node;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public @NotNull final String getDescription() {
        return this.description;
    }

    public @NotNull final PermissionDefault isDefault() {
        return this.isDefault;
    }

    public @NotNull final Map<String, Boolean> getChildren() {
        return Collections.unmodifiableMap(this.children);
    }

    public final boolean hasPermission(@NotNull final Player player) {
        return player.hasPermission(getPermission());
    }
}