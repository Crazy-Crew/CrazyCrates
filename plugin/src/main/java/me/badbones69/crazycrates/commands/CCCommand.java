package me.badbones69.crazycrates.commands;

import com.sun.istack.internal.NotNull;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent.KeyReciveReason;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.GUIMenu;
import me.badbones69.crazycrates.controllers.Preview;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.converters.CratesPlusConverter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class CCCommand implements CommandExecutor {

    private final FileManager fileManager = FileManager.getInstance();
    private final CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLable, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (Methods.permCheck(sender, "menu")) {
                GUIMenu.openGUI(player);
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                    if (Methods.permCheck(sender, "access")) {
                        player.sendMessage(Messages.HELP.getMessage());
                    }
                    break;
                case "additem":
                    additemCommand(player, args);
                    break;
                case "admin":
                    adminCommand(player);
                    break;
                case "prewview":
                    previewCommand(player, args);
                    break;
                case "list":
                    listCommand(player);
                    break;
                case "open":
                    openCommand(player, args);
                    break;
                case "fo":
                case "fopen":
                case "forceopen":
                    forceopenCommand(player, args);
                    break;
                case "tp":
                    tpCommand(player, args);
                    break;
                case "give":
                    giveCommand(player, args);
                    break;
                case "giveall":
                    giveallCommand(player, args);
                    break;
                case "take":
                    takeCommand(player, args);
                    break;
                case "transfer":
                    transferCommand(player, args);
                    break;
                case "s":
                case "set":
                    setCommand(player, args);
                    break;
                case "reload":
                    reloadCommand(player);
                    break;
                case "convert":
                    convertCommand(player);
                    break;
                case "set1":
                case "set2":
                    setCommand(player, args[0]);
                    break;
                case "save":
                    saveCommand(player, args);
                    break;
                case "debug":
                    debugCommand(player, args);
                    break;
                default:
                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease do /cc help for more info."));
                    break;
            }
        }
        return true;
    }

    private void convertCommand(CommandSender sender) {
        if (Methods.permCheck(sender, "admin")) {
            if (Support.CRATESPLUS.isPluginLoaded()) {
                try {
                    CratesPlusConverter.convert();
                    sender.sendMessage(Messages.CONVERT_CRATES_PLUS.getMessage("%Prefix%", Methods.getPrefix()));
                } catch (Exception e) {
                    sender.sendMessage(Messages.ERROR_CONVERTING_FILES.getMessage());
                    System.out.println("Error while trying to convert files with Crazy Crates v" + cc.getPlugin().getDescription().getVersion());
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(Messages.NO_FILES_TO_CONVERT.getMessage());
            }
        }
    }

    private void setCommand(Player sender, String setCommand) {
        if (!Methods.permCheck(sender, "admin")) return;
        if (Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
            sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
            return;
        }
        int set = setCommand.equalsIgnoreCase("set1") ? 1 : 2;
        Block block = sender.getTargetBlockExact(10);
        if (block == null || block.isEmpty()) {
            sender.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
            return;
        }
        if (cc.getSchematicLocations().containsKey(sender.getUniqueId())) {
            cc.getSchematicLocations().put(sender.getUniqueId(), new Location[]{set == 1 ? block.getLocation() : cc.getSchematicLocations().getOrDefault(sender.getUniqueId(), null)[0], set == 2 ? block.getLocation() : cc.getSchematicLocations().getOrDefault(sender.getUniqueId(), null)[1]});
        } else {
            cc.getSchematicLocations().put(sender.getUniqueId(), new Location[]{set == 1 ? block.getLocation() : null, set == 2 ? block.getLocation() : null});
        }
        sender.sendMessage(Methods.getPrefix("&7You have set location #" + set + "."));

        //Commented code is for debugging schematic files if there is an issue with them.
        /*				}else if(args[0].equalsIgnoreCase("pasteall")) {// /cc pasteall
        					if(!Methods.permCheck(sender, "admin")) return true;
        					Location location = ((Player) sender).getLocation().subtract(0, 1, 0);
        					for(CrateSchematic schematic : cc.getCrateSchematics()) {
        						cc.getNMSSupport().pasteSchematic(schematic.getSchematicFile(), location);
        						location.add(0, 0, 6);
        					}
        					sender.sendMessage(Methods.getPrefix("&7Pasted all of the schematics."));
        					return true;
        				}else if(args[0].equalsIgnoreCase("paste")) {// /cc paste <schematic file name>
        					if(!Methods.permCheck(sender, "admin")) return true;
        					if(args.length >= 2) {
        						String name = args[1];
        						Location location = ((Player) sender).getLocation().subtract(0, 1, 0);
        						CrateSchematic schematic = cc.getCrateSchematic(name);
        						if(schematic != null) {
        							cc.getNMSSupport().pasteSchematic(schematic.getSchematicFile(), location);
        							sender.sendMessage("Pasted the " + schematic.getSchematicName() + " schematic.");
        						}else {
        							sender.sendMessage(Methods.getPrefix("&cNo schematics by the name of " + name + " where found."));
        						}
        					}else {
        						sender.sendMessage(Methods.getPrefix("&c/cc paste <schematic file name>"));
        					}
        					return true; */
    }

    private void saveCommand(Player sender, String[] args) {
        // /cc save <file name>
        if (!Methods.permCheck(sender, "admin")) return;
        if (Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
            sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
            return;
        }
        Location[] locations = cc.getSchematicLocations().get((sender).getUniqueId());
        if (locations != null && locations[0] != null && locations[1] != null) {
            if (args.length >= 2) {
                File file = new File(cc.getPlugin().getDataFolder() + "/Schematics/" + args[1]);
                cc.getNMSSupport().saveSchematic(locations, sender.getName(), file);
                sender.sendMessage(Methods.getPrefix("&7Saved the " + args[1] + ".nbt into the Schematics folder."));
                cc.loadSchematics();
            } else {
                sender.sendMessage(Methods.getPrefix("&cYou need to specify a schematic file name."));
            }
        } else {
            sender.sendMessage(Methods.getPrefix("&cYou need to use /cc set1/set2 to set the connors of your schematic."));
        }
    }

    private void additemCommand(Player sender, String[] args) {
        // /cc additem0 <crate>1 <prize>2
        if (!Methods.permCheck(sender, "admin")) return;
        if (args.length >= 3) {
            ItemStack item = cc.getNMSSupport().getItemInMainHand(sender);
            if (item != null && item.getType() != Material.AIR) {
                Crate crate = cc.getCrateFromName(args[1]);
                if (crate != null) {
                    String prize = args[2];
                    try {
                        crate.addEditorItem(prize, item);
                    } catch (Exception e) {
                        System.out.println(fileManager.getPrefix() + "Failed to add a new prize to the " + crate.getName() + " crate.");
                        e.printStackTrace();
                    }
                    cc.loadCrates();
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getName());
                    placeholders.put("%Prize%", prize);
                    sender.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
                } else {
                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));

                }
            } else {
                sender.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
            }
        } else {
            sender.sendMessage(Methods.getPrefix("&c/cc additem <crate> <prize>"));
        }
    }

    private void reloadCommand(Player sender) {
        if (!Methods.permCheck(sender, "admin")) return;
        fileManager.reloadAllFiles();
        fileManager.setup(cc.getPlugin());
        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }
        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }
        cc.loadCrates();
        sender.sendMessage(Messages.RELOAD.getMessage());
    }

    private void debugCommand(Player sender, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return;
        if (args.length >= 2) {
            Crate crate = cc.getCrateFromName(args[1]);
            if (crate != null) {
                for (Prize prize : crate.getPrizes()) {
                    cc.givePrize(sender, prize);
                }
            } else {
                sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));

            }
        } else {
            sender.sendMessage(Methods.getPrefix("&c/cc debug <crate>"));
        }
    }

    private void adminCommand(Player sender) {
        if (!Methods.permCheck(sender, "admin")) return;
        int size = cc.getCrates().size();
        int slots = 9;
        for (; size > 9; size -= 9)
            slots += 9;
        Inventory inv = Bukkit.createInventory(null, slots, Methods.color("&4&lAdmin Keys"));
        for (Crate crate : cc.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (inv.firstEmpty() >= 0) {
                    inv.setItem(inv.firstEmpty(), crate.getAdminKey());
                }
            }
        }
        sender.openInventory(inv);
    }

    private void listCommand(Player sender) {
        if (!Methods.permCheck(sender, "admin")) return;
        String crates = "";
        String brokecrates = "";
        for (Crate crate : cc.getCrates()) {
            crates += "&a" + crate.getName() + "&8, ";
        }
        for (String crate : cc.getBrokeCrates()) {
            brokecrates += "&c" + crate + ".yml&8, ";
        }
        sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));
        if (brokecrates.length() > 0) {
            sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));
        }
        sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
        sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;
        for (CrateLocation loc : cc.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();
            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();
            sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
            line++;
        }
    }

    private void tpCommand(Player sender, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return;
        if (args.length == 2) {
            String locationName = args[1];
            if (!Files.LOCATIONS.getFile().contains("Locations")) {
                Files.LOCATIONS.getFile().set("Locations.Clear", null);
                Files.LOCATIONS.saveFile();
            }
            for (String name : Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
                if (name.equalsIgnoreCase(locationName)) {
                    World W = Bukkit.getServer().getWorld(Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));
                    int X = Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
                    int Y = Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
                    int Z = Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");
                    Location loc = new Location(W, X, Y, Z);
                    sender.teleport(loc.add(.5, 0, .5));
                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));
                    return;
                }
            }
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + locationName + "&c."));
        } else {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc TP <Location Name>"));
        }
    }

    private void setCommand(Player sender, String[] args) { // /Crate Set <Crate>
        if (!Methods.permCheck(sender, "admin")) return;
        if (args.length == 2) {
            String crateName = args[1]; //Crate
            for (Crate crate : cc.getCrates()) {
                if (crate.getName().equalsIgnoreCase(crateName)) {
                    Block block = sender.getTargetBlock(null, 5);
                    if (block.isEmpty()) {
                        sender.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                        return;
                    }
                    CrazyCrates.getInstance().addCrateLocation(block.getLocation(), crate);
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getName());
                    placeholders.put("%Prefix%", Methods.getPrefix());
                    sender.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
                    return;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc Set <Crate>"));
    }

    private void previewCommand(Player sender, String[] args) {// /cc Preview <Crate> [Player]
        if (!Methods.permCheck(sender, "preview"))
            return;
        if (args.length < 2) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
            return;
        }
        Crate crate = null;
        for (Crate c : cc.getCrates()) {
            if (c.getCrateType() != CrateType.MENU) {
                if (c.getName().equalsIgnoreCase(args[1])) {
                    crate = c;
                }
            }
        }
        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
            return;
        }
        if (!crate.isPreviewEnabled()) {
            sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
            return;
        }
        if (crate.getCrateType() != CrateType.MENU) {
            Player player;
            if (args.length >= 3) {
                if (!Methods.isOnline(args[2], sender))
                    return;
                player = Methods.getPlayer(args[2]);

            } else {
                player = sender;
            }

            Preview.setPlayerInMenu(player, false);
            Preview.openNewPreview(player, crate);
        }
    }

    private void openCommand(Player sender, String[] args) {// /cc Open <Crate> [Player]
        if (!Methods.permCheck(sender, "open")) return;
        if (args.length < 2) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate open <Crate> [Player]"));
            return;
        }
        for (Crate crate : cc.getCrates()) {
            if (crate.getName().equalsIgnoreCase(args[1])) {
                Player player;
                if (args.length >= 3) {
                    if (!Methods.permCheck(sender, "open.other")) return;
                    if (Methods.isOnline(args[2], sender)) {
                        player = Methods.getPlayer(args[2]);
                    } else {
                        return;
                    }
                } else {
                    player = sender;
                }
                if (CrazyCrates.getInstance().isInOpeningList(player)) {
                    sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                    return;
                }
                CrateType type = crate.getCrateType();
                if (type != null) {
                    FileConfiguration config = Files.CONFIG.getFile();
                    boolean hasKey = false;
                    KeyType keyType = KeyType.VIRTUAL_KEY;
                    if (cc.getVirtualKeys(player, crate) >= 1) {
                        hasKey = true;
                    } else {
                        if (config.getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
                            if (cc.hasPhysicalKey(player, crate, false)) {
                                hasKey = true;
                                keyType = KeyType.PHYSICAL_KEY;
                            }
                        }
                    }
                    if (!hasKey) {
                        if (config.contains("Settings.Need-Key-Sound")) {
                            try {
                                Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));
                                player.playSound(player.getLocation(), sound, 1f, 1f);
                            } catch (IllegalArgumentException exception) {
                                Bukkit.getLogger().log(Level.WARNING, "Illegal sound defined in 'Settings.Need-Key-Sound'");
                            }
                        }
                        player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                        CrateControl.knockBack(player, player.getTargetBlock(null, 1).getLocation().add(.5, 0, .5));
                        return;
                    }
                    if (Methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }
                    if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER && type != CrateType.QUAD_CRATE) {
                        cc.openCrate(player, crate, keyType, player.getLocation(), true, false);
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Crate%", crate.getName());
                        placeholders.put("%Player%", player.getName());
                        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                    } else {
                        sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                    }
                } else {
                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                }
                return;
            }
        }
    }

    private void forceopenCommand(Player sender, String[] args) {// /cc ForceOpen <Crate> [Player]
        if (!Methods.permCheck(sender, "forceopen")) return;
        if (args.length >= 2) {
            for (Crate crate : cc.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (crate.getName().equalsIgnoreCase(args[1])) {
                        Player player;
                        if (args.length >= 3) {
                            if (Methods.isOnline(args[2], sender)) {
                                player = Methods.getPlayer(args[2]);
                            } else {
                                return;
                            }
                        } else {
                            player = sender;
                        }
                        if (CrazyCrates.getInstance().isInOpeningList(player)) {
                            sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                            return;
                        }
                        CrateType type = crate.getCrateType();
                        if (type != null) {
                            if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
                                cc.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Crate%", crate.getName());
                                placeholders.put("%Player%", player.getName());
                                sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                            } else {
                                sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                        }
                        return;
                    }
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
            return;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate forceopen <Crate> [Player]"));
    }

    private void transferCommand(Player sender, String[] args) {// /crate transfer <crate> <player> [amount]
        if (!Methods.permCheck(sender, "transfer")) return;
        if (args.length < 3) {
            sender.sendMessage(Methods.getPrefix("&c/Crate Transfer <Crate> <Player> [Amount]"));
            return;
        }
        Crate crate = cc.getCrateFromName(args[1]);
        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
            return;
        }
        if (args[2].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            return;
        }
        Player target;
        if (Methods.isOnline(args[2], sender)) {
            target = Methods.getPlayer(args[2]);
            int amount = 1;
            if (args.length >= 4) {
                if (!Methods.isInt(args[3])) {
                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                    return;
                }
                amount = Integer.parseInt(args[3]);
            }
            if (cc.getVirtualKeys(sender, crate) >= amount) {
                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(target, crate, KeyReciveReason.TRANSFER);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    cc.takeKeys(amount, sender, crate, KeyType.VIRTUAL_KEY, false);
                    cc.addKeys(amount, target, crate, KeyType.VIRTUAL_KEY);
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getName());
                    placeholders.put("%Amount%", amount + "");
                    placeholders.put("%Player%", target.getName());
                    sender.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));
                    placeholders.put("%Player%", sender.getName());
                    target.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));
                }
            } else {
                sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
            }
        }
    }

    private void giveallCommand(Player sender, String[] args) {// /Crate GiveAll <Physical/Virtual> <Crate> [Amount]
        if (!Methods.permCheck(sender, "admin")) return;
        if (args.length < 3) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate GiveAll <Physical/Virtual> <Crate> <Amount>"));
            return;
        }
        int amount = 1;
        if (args.length >= 4) {
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return;
            }
            amount = Integer.parseInt(args[3]);
        }
        KeyType type = KeyType.getFromName(args[1]);
        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }
        Crate crate = cc.getCrateFromName(args[2]);
        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return;
        }
        if (crate.getCrateType() != CrateType.MENU) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%Amount%", amount + "");
            placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());
            sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, KeyReciveReason.GIVE_ALL_COMMAND);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    player.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
                    if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                        player.getInventory().addItem(crate.getKey(amount));
                        return;
                    }
                    cc.addKeys(amount, player, crate, type);
                }
            }

        }
    }

    private void giveCommand(Player sender, String[] args) {// /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
        if (!Methods.permCheck(sender, "admin")) return;
        Player target;
        KeyType type = KeyType.PHYSICAL_KEY;
        Crate crate = null;
        int amount = 1;
        if (args.length >= 2) {
            type = KeyType.getFromName(args[1]);
            if (type == null || type == KeyType.FREE_KEY) {
                sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
                return;
            }
        }
        if (args.length >= 3) {
            crate = cc.getCrateFromName(args[2]);
            if (crate == null) {
                sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                return;
            }
        }
        if (args.length >= 4) {
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return;
            }
            amount = Integer.parseInt(args[3]);
        }
        if (args.length >= 5) {
            target = Methods.getPlayer(args[4]);
        } else {
            target = sender;
        }
        if (args.length >= 3) {
            if (crate.getCrateType() != CrateType.MENU) {
                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(target, crate, KeyReciveReason.GIVE_COMMAND);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                        target.getInventory().addItem(crate.getKey(amount));
                    } else {
                        if (target != null) {
                            cc.addKeys(amount, target, crate, type);
                        } else {
                            if (!cc.addOfflineKeys(args[4], crate, amount)) {
                                sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                            } else {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Amount%", String.valueOf(amount));
                                placeholders.put("%Player%", args[4]);
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
                    if (target != null) {
                        target.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
                    }
                }
                return;
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Give <Physical/Virtual> <Crate> [Amount] [Player]"));
    }

    private void takeCommand(Player sender, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return;
        KeyType keyType = null;
        if (args.length >= 2) {
            keyType = KeyType.getFromName(args[1]);
        }
        if (keyType == null || keyType == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }
        if (args.length == 3) {
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", "1");
                    placeholders.put("%Player%", sender.getName());
                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                    if (!cc.takeKeys(1, sender, crate, keyType, false)) {
                        Methods.failedToTakeKey(sender, crate);
                    }
                    return;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return;
        } else if (args.length == 4) {
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return;
            }
            int amount = Integer.parseInt(args[3]);
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", amount + "");
                    placeholders.put("%Player%", sender.getName());
                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                    if (!cc.takeKeys(amount, sender, crate, keyType, false)) {
                        Methods.failedToTakeKey(sender, crate);
                    }
                    return;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return;
        } else if (args.length == 5) {
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return;
            }
            int amount = Integer.parseInt(args[3]);
            Player target = Methods.getPlayer(args[4]);
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (keyType == KeyType.VIRTUAL_KEY) {
                        if (target != null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                            if (!cc.takeKeys(amount, target, crate, KeyType.VIRTUAL_KEY, false)) {
                                Methods.failedToTakeKey(sender, crate);
                            }
                        } else {
                            if (!cc.takeOfflineKeys(args[4], crate, amount)) {
                                sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                            } else {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Amount%", amount + "");
                                placeholders.put("%Player%", args[4]);
                                sender.sendMessage(Messages.TAKE_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
                            }
                            return;
                        }
                    } else if (keyType == KeyType.PHYSICAL_KEY) {
                        if (target != null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                            if (!cc.takeKeys(amount, target, crate, KeyType.PHYSICAL_KEY, false)) {
                                Methods.failedToTakeKey(sender, crate);
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", args[4]));
                        }
                    }
                    return;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Take <Physical/Virtual> <Crate> [Amount] [Player]"));
    }
}