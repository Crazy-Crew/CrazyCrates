package us.crazycrew.crazycrates.commands;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import us.crazycrew.crazycrates.ApiLoader;
import us.crazycrew.crazycrates.configurations.PluginSettings;
import java.util.EnumSet;
import java.util.HashMap;

public enum Permissions {

    CRATES_COMMAND_PLAYER_HELP("crazycrates.command.player.help", "Shows all player commands for CrazyCrates.", new HashMap<>() {{
        put(prefix + ".command.player.key", true);
    }}, PermissionDefault.TRUE),

    CRATES_COMMAND_PLAYER_MENU("command.player.menu", "Opens the primary crate menu.", null, PermissionDefault.TRUE),
    CRATES_COMMAND_PLAYER_KEY("command.player.key", "Check the number of keys you have.", null, PermissionDefault.OP),
    CRATES_COMMAND_PLAYER_KEY_OTHERS("command.player.key.others", "Check the number of keys a player has.", new HashMap<>() {{
        put(prefix + ".command.player.key", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_PLAYER_ROOT("command.players.*", "Gives access to all player commands.", new HashMap<>() {{
        put(prefix + ".command.player.key.others", true);
        put(prefix + ".command.player.help", true);
        put(prefix + ".command.player.menu", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_ROOT("command.admin.*", "Gives access to all admin commands.", new HashMap<>() {{
        put(prefix + ".command.admin.schematic.*", true);
        put(prefix + ".command.admin.help", true);
        put(prefix + ".command.admin.convert", true);
        put(prefix + ".command.admin.debug", true);
        put(prefix + ".command.admin.setmenu", true);
        put(prefix + ".command.admin.giveall", true);
        put(prefix + ".command.admin.forceopen", true);
        put(prefix + ".command.admin.open.others", true);
        put(prefix + ".command.admin.list", true);
        put(prefix + ".command.admin.preview", true);
        put(prefix + ".command.admin.additem", true);
        put(prefix + ".command.admin.access", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_ADDITEM("command.admin.additem", "Add items in-game to a prize in a crate.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_ACCESS("command.admin.access", "Opens the Admin Keys GUI.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_PREVIEW("command.admin.preview", "Opens the preview of a crate for a player.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_LIST("command.admin.list", "Displays a list of all crates.", new HashMap<>() {{
        put(prefix + ".command.admin.teleport", true);
    }}, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_OPEN("command.admin.open", "Tries to open a crate for yourself if you have a key.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_MASS_OPEN("command.admin.mass-open", "Mass opens crates. Defaults to 10 but can be changed in the crate config files.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_OPEN_OTHERS("command.admin.open.others", "Tries to open a crate for a player if they have a key.", new HashMap<>() {{
        put(prefix + ".command.admin.open", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_TAKEKEY("command.admin.takekey", "Allows you to take keys from a player.", new HashMap<>() {{
        put(prefix + ".command.player.transfer", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_PLAYER_TRANSFER("command.player.transfer", "Allows players to send virtual keys to another player.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_FORCEOPEN("command.admin.forceopen", "Opens a crate for a player for free.", new HashMap<>() {{
        put(prefix + ".command.admin.open", true);
        put(prefix + ".command.admin.open.others", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_TELEPORT("command.admin.teleport", "Teleport to a crate.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_GIVEKEY("command.admin.givekey", "Give a key(s) to a player to use on a crate.", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_GIVERANDOMKEY("command.admin.giverandomkey", "Gives a random key out of all the crates available.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_GIVEALL("command.admin.giveall", "Gives all online players keys to use on a crate.", new HashMap<>() {{
        put(prefix + ".command.admin.givekey", true);
        put(prefix + ".command.admin.giverandomkey", true);
        put(prefix + ".command.admin.takekey", true);
        put(prefix + ".command.player.transfer", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_SET("command.admin.set", "Set a block you are looking at as the specified crate.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_SETMENU("command.admin.setmenu", "", new HashMap<>() {{
        put(prefix + ".command.admin.set", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_RELOAD("command.admin.reload", "Reloads the configuration and data files.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_DEBUG("command.admin.debug", "Allows you to spit out a bunch of information.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_CONVERT("command.admin.convert", "Tries to convert supported plugin's crate files into crazy crate's crate files.", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_SCHEMATIC_ROOT("command.admin.schematic.*", "Gives access to all commands relating to schematics.", new HashMap<>() {{
        put(prefix + ".command.admin.schematic.save", true);
        put(prefix + ".command.admin.schematic.set", true);
    }}, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_SCHEMATIC_SAVE("command.admin.schematic.save", "Save the new schematic file to the schematics folder. 1.13+ only", null, PermissionDefault.OP),
    CRATES_COMMAND_ADMIN_SCHEMATIC_SET("command.admin.schematic.set", "Set position #1 or #2 for when making a new schematic for quadcrates. 1.13+ only", null, PermissionDefault.OP),

    CRATES_COMMAND_ADMIN_HELP("command.admin.help", "", new HashMap<>() {{
        put(prefix + ".command.admin.reload", true);
    }}, PermissionDefault.OP);

    private final String node;
    private final String description;
    private final HashMap<String, Boolean> children;
    private final PermissionDefault permissionDefault;

    private static final String prefix = ApiLoader.getPluginConfig().getProperty(PluginSettings.COMMAND_PERMISSION);

    /**
     * @param node permission node without the prefix
     * @param description description of the permission
     * @param children sub permissions
     * @param permissionDefault true, false, op, not-op
     */
    Permissions(String node, String description, HashMap<String, Boolean> children, PermissionDefault permissionDefault) {
        this.node = node;
        this.description = description;
        this.children = children;
        this.permissionDefault = permissionDefault;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return individual node without the prefix i.e command.admin.help
     */
    public String getNode() {
        return this.node;
    }

    /**
     * @return permission default i.e true, false, op, not op
     */
    public PermissionDefault getPermissionDefault() {
        return this.permissionDefault;
    }

    /**
     * @return sub permissions of the main permission
     */
    public HashMap<String, Boolean> getChildren() {
        return this.children;
    }

    /**
     * @return completed permission node
     */
    public String getPermissionNode() {
        return prefix + "." + this.node;
    }

    /**
     * Registers all permissions into the plugin manager.
     *
     * @param pluginManager server's plugin manager
     */
    public static void register(PluginManager pluginManager) {
        EnumSet.allOf(Permissions.class).forEach(action -> {
            if (pluginManager.getPermission(action.getPermissionNode()) == null) return;
            pluginManager.addPermission(
                    new Permission(
                            action.getPermissionNode(),
                            action.getDescription(),
                            action.getPermissionDefault(),
                            action.getChildren()
                    ));
        });
    }
}