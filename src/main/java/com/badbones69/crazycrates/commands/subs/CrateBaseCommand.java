package com.badbones69.crazycrates.commands.subs;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MenuListener;
import com.badbones69.crazycrates.listeners.PreviewListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

@Command(value = "crates", alias = {"crazycrates", "cc", "crate"})
public class CrateBaseCommand extends BaseCommand {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();

    private final FileManager fileManager = plugin.getFileManager();

    @SubCommand("clear")
    @Permission(value = "test", def = PermissionDefault.TRUE)
    public void onClear(Player player) {
        player.getInventory().clear();
    }

    @Default
    @Permission(value = "crazycrates.command.player.menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        MenuListener.openGUI(player);
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.command.player.help", def = PermissionDefault.TRUE)
    public void onHelp(Player player) {
        player.sendMessage(Messages.HELP.getMessage());
    }

    @SubCommand("transfer")
    @Permission(value = "crazycrates.command.player.transfer", def = PermissionDefault.OP)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            if (!player.getName().equalsIgnoreCase(sender.getName())) {

                if (crazyManager.getVirtualKeys(player, crate) >= amount) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
                    player.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        crazyManager.takeKeys(amount, sender, crate, KeyType.VIRTUAL_KEY, false);
                        crazyManager.addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);

                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Crate%", crate.getName());
                        placeholders.put("%Amount%", amount + "");
                        placeholders.put("%Player%", player.getName());

                        player.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));

                        placeholders.put("%Player%", player.getName());

                        player.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));
                    }
                } else {
                    sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
                }
            } else {
                sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            }
        } else {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
        }
    }

    @SubCommand("admin-help")
    @Permission(value = "crazycrates.command.admin.help", def = PermissionDefault.OP)
    public void onAdminHelp(CommandSender sender) {
        sender.sendMessage(Messages.ADMIN_HELP.getMessage());
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.command.admin.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        fileManager.reloadAllFiles();
        fileManager.setup();

        plugin.cleanFiles();
        crazyManager.loadCrates();

        sender.sendMessage(Messages.RELOAD.getMessage());
    }

    @SubCommand("debug")
    @Permission(value = "crazycrates.command.admin.debug", def = PermissionDefault.OP)
    public void onDebug(CommandSender sender, @Suggestion("crates") @ArgName("crate-name") String crateName) {
        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            crate.getPrizes().forEach(prize -> crazyManager.givePrize((Player) sender, prize));
        } else {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
        }
    }

    @SubCommand("schem-save")
    @Permission(value = "crazycrates.command.admin.schematic.save", def = PermissionDefault.OP)
    public void onAdminSave(Player player) {
        player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("schem-set")
    @Permission(value = "crazycrates.command.admin.schematic.set", def = PermissionDefault.OP)
    public void onAdminSet(Player player) {
        player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("admin")
    @Permission(value = "crazycrates.command.admin.access", def = PermissionDefault.OP)
    public void onAdminMenu(Player player) {
        int size = crazyManager.getCrates().size();
        int slots = 9;

        for (; size > 9; size -= 9) slots += 9;

        Inventory inv = player.getServer().createInventory(null, slots, Methods.color("&4&lAdmin Keys"));

        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (inv.firstEmpty() >= 0) inv.setItem(inv.firstEmpty(), crate.getAdminKey());
            }
        }

        player.openInventory(inv);
    }

    @SubCommand("list")
    @Permission(value = "crazycrates.command.admin.list", def = PermissionDefault.OP)
    public void onAdminList(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokecrates;

        crazyManager.getCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokecratesBuilder = new StringBuilder();

        crazyManager.getBrokeCrates().forEach(crate -> brokecratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokecrates = brokecratesBuilder.toString();

        sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));

        if (brokecrates.length() > 0) sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));

        sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
        sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;

        for (CrateLocation loc : crazyManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
            line++;
        }
    }

    @SubCommand("tp")
    @Permission(value = "crazycrates.command.admin.teleport", def = PermissionDefault.OP)
    public void onAdminTeleport(Player player, @Suggestion("locations") @ArgName("location-id") String id) {

        if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
            FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
            FileManager.Files.LOCATIONS.saveFile();
        }

        for (String name : FileManager.Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                World W = plugin.getServer().getWorld(FileManager.Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));

                int X = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
                int Y = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
                int Z = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");

                Location loc = new Location(W, X, Y, Z);

                player.teleport(loc.add(.5, 0, .5));
                player.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));

                return;
            }
        }

        player.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + id + "&c."));
    }

    @SubCommand("additem")
    @Permission(value = "crazycrates.command.admin.additem", def = PermissionDefault.OP)
    public void onAdminCrateAddItem(Player player, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("prizes") @ArgName("prize") String prize) {

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR) {
            Crate crate = crazyManager.getCrateFromName(crateName);

            if (crate != null) {
                try {
                    crate.addEditorItem(prize, item);
                } catch (Exception e) {
                    plugin.getServer().getLogger().warning("Failed to add a new prize to the " + crate.getName() + " crate.");

                    e.printStackTrace();
                }

                crazyManager.loadCrates();

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Crate%", crate.getName());
                placeholders.put("%Prize%", prize);

                player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
            } else {
                player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            }
        } else {
            player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
        }
    }

    @SubCommand("preview")
    @Permission(value = "crazycrates.command.admin.preview", def = PermissionDefault.OP)
    public void onAdminCratePreview(CommandSender sender, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("players") Player player) {

        Crate crate = null;

        for (Crate crates : crazyManager.getCrates()) {
            if (crates.getCrateType() != CrateType.MENU) {
                if (crates.getName().equalsIgnoreCase(crateName)) crate = crates;
            }
        }

        if (crate != null) {
            if (!crate.isPreviewEnabled()) {
                sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                return;
            }

            if (crate.getCrateType() != CrateType.MENU) {
                PreviewListener.setPlayerInMenu(player, false);
                PreviewListener.openNewPreview(player, crate);
            }
        }
    }

    @SubCommand("open")
    @Permission(value = "crazycrates.command.admin.open", def = PermissionDefault.OP)
    public void onAdminCrateOpen(CommandSender sender, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("players") Player player) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getName().equalsIgnoreCase(crateName)) {

                if (crazyManager.isInOpeningList(player)) {
                    sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                    return;
                }

                CrateType type = crate.getCrateType();

                if (type != null) {
                    FileConfiguration config = FileManager.Files.CONFIG.getFile();

                    boolean hasKey = false;
                    KeyType keyType = KeyType.VIRTUAL_KEY;

                    if (crazyManager.getVirtualKeys(player, crate) >= 1) {
                        hasKey = true;
                    } else {
                        if (config.getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
                            if (crazyManager.hasPhysicalKey(player, crate, false)) {
                                hasKey = true;
                                keyType = KeyType.PHYSICAL_KEY;
                            }
                        }
                    }

                    if (!hasKey) {
                        if (config.contains("Settings.Need-Key-Sound")) {
                            Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));

                            player.playSound(player.getLocation(), sound, 1f, 1f);
                        }

                        player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                        CrateControlListener.knockBack(player, player.getTargetBlock(null, 1).getLocation().add(.5, 0, .5));
                        return;
                    }

                    if (Methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

                    if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER && type != CrateType.QUAD_CRATE) {
                        crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Crate%", crate.getName());
                        placeholders.put("%Player%", player.getName());

                        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                    } else {
                        sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                    }

                } else {
                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
                }

                return;
            }
        }
    }

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.command.admin.forceopen", def = PermissionDefault.OP)
    public void onAdminForceOpen(CommandSender sender, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("players") Player player) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (crate.getName().equalsIgnoreCase(crateName)) {

                    if (crazyManager.isInOpeningList(player)) {
                        sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                        return;
                    }

                    CrateType type = crate.getCrateType();

                    if (type != null) {
                        if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
                            crazyManager.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);

                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%Crate%", crate.getName());
                            placeholders.put("%Player%", player.getName());

                            sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                        } else {
                            sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        }
                    } else {
                        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
                    }

                    return;
                }
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }

    @SubCommand("set")
    @Permission(value = "crazycrates.command.admin.set", def = PermissionDefault.OP)
    public void onAdminCrateSet(Player player, @Suggestion("crates") @ArgName("crate-name") String crateName) {
        for (Crate crate : crazyManager.getCrates()) {
            if (crate.getName().equalsIgnoreCase(crateName)) {
                Block block = player.getTargetBlock(null, 5);

                if (block.isEmpty()) {
                    player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                    return;
                }

                crazyManager.addCrateLocation(block.getLocation(), crate);

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Crate%", crate.getName());
                placeholders.put("%Prefix%", Methods.getPrefix());

                player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));

                return;
            }
        }

        player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }

    @SubCommand("give")
    @Permission(value = "crazycrates.command.admin.givekey", def = PermissionDefault.OP)
    public void onAdminCrateGive(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, Player target, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);
        Crate crate = crazyManager.getCrateFromName(crateName);

        String player;

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        if (crate.getCrateType() != CrateType.MENU) {
            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(target, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                    target.getInventory().addItem(crate.getKey(amount));
                } else {
                    player = target.getName();

                    if (target.isOnline()) {
                        crazyManager.addKeys(amount, target, crate, type);
                    } else {
                        if (!crazyManager.addOfflineKeys(player, crate, amount)) {
                            sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                        } else {
                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());

                            sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
                        }

                        return;
                    }
                }

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", amount + "");
                placeholders.put("%Player%", target.getName());
                placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());

                sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
                target.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
            }

            return;
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }

    @SubCommand("take")
    @Permission(value = "crazycrates.command.admin.takekey", def = PermissionDefault.OP)
    public void onAdminCrateTake(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount, @Suggestion("players") Player target) {
        KeyType type = KeyType.getFromName(keyType);

        Crate crate = crazyManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate != null) {
            if (crate.getCrateType() != CrateType.MENU) {

                if (!crazyManager.takeKeys(amount, target, crate, type, false)) {
                    Methods.failedToTakeKey(sender, crate);
                    return;
                }

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", String.valueOf(amount));
                placeholders.put("%Player%", target.getName());

                sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                return;
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }

    @SubCommand("giveall")
    @Permission(value = "crazycrates.command.admin.giveall", def = PermissionDefault.OP)
    public void onAdminCrateGiveAllKeys(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        Crate crate = crazyManager.getCrateFromName(crateName);

        if (crate != null) {
            if (crate.getCrateType() != CrateType.MENU) {
                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("%Amount%", amount + "");
                placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());

                sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));

                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
                    onlinePlayer.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        onlinePlayer.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

                        if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                            onlinePlayer.getInventory().addItem(crate.getKey(amount));
                            return;
                        }

                        crazyManager.addKeys(amount, onlinePlayer, crate, type);
                    }
                }

                return;
            }
        }

        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
    }
}