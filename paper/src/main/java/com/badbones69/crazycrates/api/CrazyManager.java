package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.enums.BrokeLocation;
import com.badbones69.crazycrates.common.enums.crates.CrateType;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.common.configuration.objects.CrateHologram;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.*;
import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.common.schematics.CrateSchematic;
import com.badbones69.crazycrates.support.holograms.DecentHologramsSupport;
import com.badbones69.crazycrates.support.holograms.HolographicSupport;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import com.badbones69.crazycrates.support.structures.StructureHandler;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class CrazyManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyLogger crazyLogger;

    @Inject private FileManager fileManager;

    @Inject private Methods methods;

    // All the crates that have been loaded.
    private final ArrayList<Crate> crates = new ArrayList<>();

    // A list of all the physical crate locations.
    private final ArrayList<CrateLocation> crateLocations = new ArrayList<>();

    // List of all the broken crates.
    private final ArrayList<String> brokecrates = new ArrayList<>();
    
    // List of broken physical crate locations.
    private final List<BrokeLocation> brokeLocations = new ArrayList<>();
    
    // The crate that the player is opening.
    private final HashMap<UUID, Crate> playerOpeningCrates = new HashMap<>();
    
    // Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
    private final HashMap<UUID, KeyType> playerKeys = new HashMap<>();
    
    // A list of tasks being run by the QuadCrate type.
    private final HashMap<UUID, ArrayList<BukkitTask>> currentQuadTasks = new HashMap<>();
    
    // A list of current crate schematics for Quad Crate.
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();
    
    // True if at least one crate gives new players keys and false if none give new players keys.
    private boolean giveNewPlayersKeys;
    
    // The hologram api that is being hooked into.
    private HologramController hologramController;
    
    // Schematic locations for 1.13+.
    private final HashMap<UUID, Location[]> schemLocations = new HashMap<>();
    
    // Loads all the information the plugin needs to run.
    public void loadCrates() {
        giveNewPlayersKeys = false;
        crates.clear();
        brokecrates.clear();
        crateLocations.clear();
        crateSchematics.clear();

        if (PluginSupport.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            hologramController = new HolographicSupport();
        } else if (PluginSupport.DECENT_HOLOGRAMS.isPluginLoaded()) {
            hologramController = new DecentHologramsSupport();
        }

        // Removes all holograms so that they can be replaced.
        if (hologramController != null) hologramController.removeAllHolograms();

        if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>Loading all crate information...</red>");

        for (String crateName : fileManager.getAllCratesNames()) {
            try {
                FileConfiguration file = fileManager.getFile(crateName).getFile();
                CrateType crateType = CrateType.getFromName(file.getString("Crate.CrateType"));
                ArrayList<Prize> prizes = new ArrayList<>();
                String previewName = file.contains("Crate.Preview-Name") ? file.getString("Crate.Preview-Name") : file.getString("Crate.Name");
                ArrayList<Tier> tiers = new ArrayList<>();

                if (file.contains("Crate.Tiers") && file.getConfigurationSection("Crate.Tiers") != null) {
                    for (String tier : file.getConfigurationSection("Crate.Tiers").getKeys(false)) {
                        String path = "Crate.Tiers." + tier;
                        tiers.add(new Tier(tier, file.getString(path + ".Name"), file.getString(path + ".Color"), file.getInt(path + ".Chance"), file.getInt(path + ".MaxRange")));
                    }
                }

                if (crateType == CrateType.COSMIC && tiers.isEmpty()) {
                    brokecrates.add(crateName);

                    if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>No tiers were found for this cosmic crate</red> <gold>" + crateName + ".yml</gold> <red>file.</red>");

                    continue;
                }

                for (String prize : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
                    Prize altPrize = null;
                    String path = "Crate.Prizes." + prize;
                    ArrayList<Tier> prizeTiers = new ArrayList<>();

                    for (String tier : file.getStringList(path + ".Tiers")) {
                        for (Tier loadedTier : tiers) {
                            if (loadedTier.getName().equalsIgnoreCase(tier)) prizeTiers.add(loadedTier);
                        }
                    }

                    if (file.contains(path + ".Alternative-Prize")) {
                        if (file.getBoolean(path + ".Alternative-Prize.Toggle")) {
                            altPrize = new Prize("Alternative-Prize",
                                    file.getStringList(path + ".Alternative-Prize.Messages"),
                                    file.getStringList(path + ".Alternative-Prize.Commands"),
                                    null, // No editor items
                                    getItems(file, prize + ".Alternative-Prize"));
                        }
                    }

                    ArrayList<ItemStack> editorItems = new ArrayList<>();

                    if (file.contains(path + ".Editor-Items")) {
                        List<?> list = file.getList(path + ".Editor-Items");

                        if (list != null) list.forEach(type -> editorItems.add((ItemStack) list));
                    }

                    prizes.add(new Prize(prize, getDisplayItem(file, prize),
                            file.getStringList(path + ".Messages"),
                            file.getStringList(path + ".Commands"),
                            editorItems,
                            getItems(file, prize),
                            crateName,
                            file.getInt(path + ".Chance", 100),
                            file.getInt(path + ".MaxRange", 100),
                            file.getBoolean(path + ".Firework"),
                            file.getStringList(path + ".BlackListed-Permissions"),
                            prizeTiers,
                            altPrize));
                }

                int newPlayersKeys = file.getInt("Crate.StartingKeys");

                if (!giveNewPlayersKeys) {
                    if (newPlayersKeys > 0) giveNewPlayersKeys = true;
                }

                CrateHologram holo = new CrateHologram(file.getBoolean("Crate.Hologram.Toggle"), file.getDouble("Crate.Hologram.Height", 0.0), file.getStringList("Crate.Hologram.Message"));
                crates.add(new Crate(crateName, previewName, crateType, getKey(file), prizes, file, newPlayersKeys, tiers, holo, methods, fileManager, crazyLogger));
            } catch (Exception e) {
                brokecrates.add(crateName);

                if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>There was an error while loading the</red> <gold>" + crateName + ".yml</gold> <red>file.</red>");

                e.printStackTrace();
            }
        }

        crates.add(new Crate("Menu", "Menu", CrateType.MENU, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null, null, methods, fileManager, crazyLogger));

        if (Config.TOGGLE_VERBOSE) {
            crazyLogger.debug("<red>All crate information has been loaded.</red>");
            crazyLogger.debug("<red>Loading all the physical crate locations.</red>");
        }

        int loadedAmount = 0;
        int brokeAmount = 0;

        /*
        if (locations.getConfigurationSection("Locations") != null) {
            for (String locationName : locations.getConfigurationSection("Locations").getKeys(false)) {
                try {
                    String worldName = locations.getString("Locations." + locationName + ".World");
                    World world = plugin.getServer().getWorld(worldName);
                    int x = locations.getInt("Locations." + locationName + ".X");
                    int y = locations.getInt("Locations." + locationName + ".Y");
                    int z = locations.getInt("Locations." + locationName + ".Z");
                    Location location = new Location(world, x, y, z);
                    Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));

                    if (world != null && crate != null) {
                        crateLocations.add(new CrateLocation(locationName, crate, location));

                        if (hologramController != null) hologramController.createHologram(location.getBlock(), crate);

                        loadedAmount++;
                    } else {
                        brokeLocations.add(new BrokeLocation(locationName, crate, x, y, z, worldName, plugin));
                        brokeAmount++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
         */

        // Checking if all physical locations loaded
        if (loadedAmount > 0 || brokeAmount > 0) {
            if (brokeAmount <= 0) {
                if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>All physical crate locations have been loaded.</red>");
            } else {
                if (Config.TOGGLE_VERBOSE) {
                    crazyLogger.debug("<red>Loaded</red> <gold>" + loadedAmount + "</gold> <red>physical crate locations.</red>");
                    crazyLogger.debug("<red>Failed to load</red> <gold>" + brokeAmount + "</gold> <red>physical crate locations.</red>");
                }
            }
        }

        // Loading schematic files
        if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>Searching for schematics to load.</red>");

        String[] schems = new File(plugin.getDataFolder() + "/schematics/").list();

        for (String schematicName : schems) {
            if (schematicName.endsWith(".nbt")) {
                crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/schematics/" + schematicName)));

                if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<gold>" + schematicName + "</gold> <red>was successfully found and loaded.</red>");
            }
        }

        if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>All schematics were found and loaded.</red>");

        //previewListener.loadButtons();
    }
    
    /**
     * Opens a crate for a player.
     *
     * @param player The player that is having the crate opened for them.
     * @param crate The crate that is being used.
     * @param location The location that may be needed for some crate types.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     */
    public void openCrate(Player player, Crate crate, KeyType keyType, Location location, boolean virtualCrate, boolean checkHand) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (!crate.canWinPrizes(player)) {
                //player.sendMessage(Messages.NO_PRIZES_FOUND.getMessage(methods));
                removePlayerFromOpeningList(player);
                removePlayerKeyType(player);
                return;
            }
        }
        
        addPlayerToOpeningList(player, crate);
        boolean broadcast = crate.getFile() != null && crate.getFile().getBoolean("Crate.OpeningBroadCast");

        if (broadcast && crate.getCrateType() != CrateType.QUAD_CRATE) {
            if (crate.getFile().contains("Crate.BroadCast")) {
                if (!crate.getFile().getString("Crate.BroadCast").isEmpty()) {
                    //plugin.getServer().broadcastMessage(methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll(
                    //        "%Prefix%", methods.getPrefix()).replaceAll("%prefix%", methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
                }
            }

            broadcast = false;
        }

        switch (crate.getCrateType()) {
            case MENU:
                //menuListener.openGUI(player);
                break;
            case COSMIC:
                //cosmicCrate.openCosmic(player, crate, keyType, checkHand);
                break;
            case CSGO:
                //csgoCrate.openCSGO(player, crate, keyType, checkHand);
                break;
            case ROULETTE:
                //rouletteCrate.openRoulette(player, crate, keyType, checkHand);
                break;
            case WHEEL:
                //wheelCrate.startWheel(player, crate, keyType, checkHand);
                break;
            case WONDER:
                //wonderCrate.startWonder(player, crate, keyType, checkHand);
                break;
            case WAR:
                //warCrate.openWarCrate(player, crate, keyType, checkHand);
                break;
            case QUAD_CRATE:
                Location lastLocation = player.getLocation();
                lastLocation.setPitch(0F);
                CrateSchematic crateSchematic = getCrateSchematics().get(new Random().nextInt(getCrateSchematics().size()));
                StructureHandler handler = new StructureHandler(crateSchematic.schematicFile());

                //QuadCrateManager session = new QuadCrateManager(player, crate, keyType, location, lastLocation, checkHand, handler, this, methods, chestStateHandler);
                //broadcast = session.startCrate(plugin);
                break;
            case FIRE_CRACKER:
                //if (crateControlListener.containsPlayer(location)) {
                    //player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage(methods));
                //   removePlayerFromOpeningList(player);
                //    return;
                //} else {
                //    if (virtualCrate) {
                        //player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage(methods));
                //        removePlayerFromOpeningList(player);
                //        return;
                //    } else {
                //        crateControlListener.addPlayer(player, location);
                //        fireCrackerCrate.startFireCracker(player, crate, keyType, location);
                //    }
                //}
                break;
            case QUICK_CRATE:
                //if (crateControlListener.containsPlayer(location)) {
                //    //player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage(methods));
                //    removePlayerFromOpeningList(player);
                //    return;
                //} else {
                //    if (virtualCrate && location.equals(player.getLocation())) {
                //        //player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage(methods));
                //        removePlayerFromOpeningList(player);
                //        return;
                //    } else {
                //        crateControlListener.addPlayer(player, location);
                //        quickCrate.openCrate(player, location, crate, keyType);
                //    }
                //}
                break;
            case CRATE_ON_THE_GO:
                if (virtualCrate) {
                    //player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage(methods));
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (takeKeys(1, player, crate, keyType, true)) {
                        Prize prize = crate.pickPrize(player);
                        givePrize(player, prize);

                        if (prize.useFireworks()) methods.firework(player.getLocation().add(0, 1, 0));

                        removePlayerFromOpeningList(player);
                    } else {
                        methods.failedToTakeKey(player, crate);
                    }
                }

                break;
        }

        if (broadcast) {
            if (!crate.getFile().getString("Crate.BroadCast").isEmpty()) {
                //player.getServer().broadcastMessage(
                //        methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll(
                //                "%Prefix%", methods.getPrefix()).replaceAll("%prefix%", methods.getPrefix()).replaceAll(
                //                        "%Player%", player.getName()).replaceAll("%player%", player.getName())));
            }
        }
    }
    
    /**
     * Ends the tasks running by a player.
     *
     * @param player The player using the crate.
     */
    public void endQuadCrate(Player player) {
        if (currentQuadTasks.containsKey(player.getUniqueId())) {
            currentQuadTasks.get(player.getUniqueId()).forEach(BukkitTask::cancel);

            currentQuadTasks.remove(player.getUniqueId());
        }
    }
    
    /**
     * Add a quad crate task that is going on for a player.
     *
     * @param player The player opening the crate.
     * @param task The task of the quad crate.
     */
    public void addQuadCrateTask(Player player, BukkitTask task) {
        if (!currentQuadTasks.containsKey(player.getUniqueId())) currentQuadTasks.put(player.getUniqueId(), new ArrayList<>());

        currentQuadTasks.get(player.getUniqueId()).add(task);
    }
    
    /**
     * Checks to see if the player has a quad crate task going on.
     *
     * @param player The player that is being checked.
     * @return True if they do have a task and false if not.
     */
    public boolean hasQuadCrateTask(Player player) {
        return currentQuadTasks.containsKey(player.getUniqueId());
    }
    
    /**
     * A list of all the physical crate locations.
     *
     * @return List of locations.
     */
    public ArrayList<CrateLocation> getCrateLocations() {
        return crateLocations;
    }
    
    /**
     * Checks to see if the location is a physical crate.
     *
     * @param loc The location you are checking.
     * @return True if it is a physical crate and false if not.
     */
    public boolean isCrateLocation(Location loc) {
        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(loc)) return true;
        }

        return false;
    }
    
    /**
     * Gets the physical crate of the location.
     *
     * @param loc The location you are checking.
     * @return A CrateLocation if the location is a physical crate otherwise null if not.
     */
    public CrateLocation getCrateLocation(Location loc) {
        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(loc)) return crateLocation;
        }

        return null;
    }
    
    /**
     * Get a list of all the broke physical crate locations.
     *
     * @return List of broken crate locations.
     */
    public List<BrokeLocation> getBrokeCrateLocations() {
        return brokeLocations;
    }
    
    /**
     * Add a new physical crate location.
     *
     * @param location The location you wish to add.
     * @param crate The crate which you would like to set it to.

    public void addCrateLocation(Location location, Crate crate) {
        FileConfiguration locations = Files.LOCATIONS.getFile();
        String id = "1"; // Location ID

        for (int i = 1; locations.contains("Locations." + i); i++) {
            id = (i + 1) + "";
        }

        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(location)) {
                id = crateLocation.getID();
                break;
            }
        }
        
        locations.set("Locations." + id + ".Crate", crate.getName());
        locations.set("Locations." + id + ".World", location.getWorld().getName());
        locations.set("Locations." + id + ".X", location.getBlockX());
        locations.set("Locations." + id + ".Y", location.getBlockY());
        locations.set("Locations." + id + ".Z", location.getBlockZ());
        Files.LOCATIONS.saveFile();

        crateLocations.add(new CrateLocation(id, crate, location));

        if (hologramController != null) hologramController.createHologram(location.getBlock(), crate);
    }     */
    
    /**
     * Remove a physical crate location.
     *
     * @param id The id of the location.

    public void removeCrateLocation(String id) {
        Files.LOCATIONS.getFile().set("Locations." + id, null);
        Files.LOCATIONS.saveFile();
        CrateLocation location = null;

        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getID().equalsIgnoreCase(id)) {
                location = crateLocation;
                break;
            }
        }

        if (location != null) {
            crateLocations.remove(location);

            if (hologramController != null) hologramController.removeHologram(location.getLocation().getBlock());
        }
    }     */
    
    /**
     * Get a list of broken crates.
     *
     * @return An ArrayList of all the broken crates.
     */
    public ArrayList<String> getBrokeCrates() {
        return brokecrates;
    }
    
    /**
     * Get a list of all the crates loaded into the plugin.
     *
     * @return An ArrayList of all the loaded crates.
     */
    public ArrayList<Crate> getCrates() {
        return crates;
    }
    
    /**
     * Get a crate by its name.
     *
     * @param name The name of the crate you wish to grab.
     * @return Returns a Crate object of the crate it found and if none are found it returns null.
     */
    public Crate getCrateFromName(String name) {
        for (Crate crate : getCrates()) {
            if (crate.getName().equalsIgnoreCase(name)) return crate;
        }

        return null;
    }
    
    /**
     * Load the crate preview of a crate.
     *
     * @param crate The crate you wish to load the preview of.
     * @return An Inventory object of the preview.
     */
    public Inventory loadPreview(Crate crate) {
        FileConfiguration file = crate.getFile();
        int slots = 9;

        for (int size = file.getConfigurationSection("Crate.Prizes").getKeys(false).size(); size > 9 && slots < crate.getMaxSlots(); size -= 9) {
            slots += 9;
        }

        Inventory inv = plugin.getServer().createInventory(null, slots, file.getString("Crate.Name"));

        for (String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
            String id = file.getString("Crate.Prizes." + reward + ".DisplayItem", "Stone");
            String name = file.getString("Crate.Prizes." + reward + ".DisplayName", "");
            List<String> lore = file.getStringList("Crate.Prizes." + reward + ".Lore");
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();
            String player = file.getString("Crate.Prizes." + reward + ".Player", "");
            boolean glowing = file.getBoolean("Crate.Prizes." + reward + ".Glowing");
            int amount = file.getInt("Crate.Prizes." + reward + ".DisplayAmount", 1);
            boolean unbreakable = file.getBoolean("Crate.Prizes." + reward + ".Unbreakable", false);
            boolean hideItemFlags = file.getBoolean("Crate.Prizes." + reward + ".HideItemsFlags", false);

            for (String enchantmentName : file.getStringList("Crate.Prizes." + reward + ".DisplayEnchantments")) {
                Enchantment enchantment = methods.getEnchantment(enchantmentName.split(":")[0]);

                if (enchantment != null) enchantments.put(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
            }

            try {
                inv.setItem(inv.firstEmpty(), new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setUnbreakable(unbreakable).hideItemFlags(hideItemFlags).setEnchantments(enchantments).setGlow(glowing).setPlayerName(player).build());
            } catch (Exception e) {
                inv.addItem(new ItemBuilder().setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + reward)).build());
            }
        }

        return inv;
    }
    
    /**
     * Give a player a prize they have won.
     *
     * @param player The player you wish to give the prize to.
     * @param prize The prize the player has won.
     */
    public void givePrize(Player player, Prize prize) {
        if (prize != null) {
            prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;

            for (ItemStack item : prize.getItems()) {

                if (item == null) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", prize.getCrate());
                    placeholders.put("%Prize%", prize.getName());
                    //player.sendMessage(Messages.PRIZE_ERROR.getMessage(placeholders, methods));
                    continue;
                }

                if (!player.getInventory().isEmpty()) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }

            for (ItemBuilder item : prize.getItemBuilders()) {
                ItemBuilder clone = new ItemBuilder(item);

                if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) {
                    clone.setName(PlaceholderAPI.setPlaceholders(player, clone.getName()));
                    clone.setLore(PlaceholderAPI.setPlaceholders(player, clone.getLore()));
                }

                if (!player.getInventory().isEmpty()) {
                    player.getInventory().addItem(clone.build());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), clone.build());
                }
            }

            for (String command : prize.getCommands()) { // /give %player% iron %random%:1-64
                if (command.contains("%random%:")) {
                    String cmd = command;
                    StringBuilder commandBuilder = new StringBuilder();

                    for (String word : cmd.split(" ")) {
                        if (word.startsWith("%random%:")) {
                            word = word.replace("%random%:", "");

                            try {
                                long min = Long.parseLong(word.split("-")[0]);
                                long max = Long.parseLong(word.split("-")[1]);
                                commandBuilder.append(pickNumber(min, max)).append(" ");
                            } catch (Exception e) {
                                commandBuilder.append("1 ");

                                if (Config.TOGGLE_VERBOSE) {
                                    crazyLogger.debug("<red>The prize</red> <gold>" + prize.getName() + "</gold> <red>in the</red> <gold>" + prize.getCrate() + "</gold> <red>crate has caused an error when trying to run a command.</red>");
                                    crazyLogger.debug("<red>Command:</red> <gold>" + cmd + "</gold>");
                                }
                            }
                        } else {
                            commandBuilder.append(word).append(" ");
                        }
                    }

                    command = commandBuilder.toString();
                    command = command.substring(0, command.length() - 1);
                }

                if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) command = PlaceholderAPI.setPlaceholders(player, command);

                //plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), methods.color(command.replace("%Player%", player.getName()).replace("%player%", player.getName())));
            }

            for (String message : prize.getMessages()) {
                if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) message = PlaceholderAPI.setPlaceholders(player, message);

                //player.sendMessage(methods.color(message).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
                //.replace("%displayname%", prize.getDisplayItemBuilder().getName()).replace("%DisplayName%", prize.getDisplayItemBuilder().getName()));
            }
        } else {
            if (Config.TOGGLE_VERBOSE) crazyLogger.debug("<red>No prize was found when giving</red> <gold>" + player.getName() + "</gold> <red>a prize.</red>");
        }
    }
    
    /**
     * Give keys to an offline player.
     *
     * @param player The offline player you wish to give keys to.
     * @param crate The Crate of which key you are giving to the player.
     * @param keys The amount of keys you wish to give to the player.
     * @return Returns true if it successfully gave the offline player a key and false if there was an error.
     */
    public boolean addOfflineKeys(String player, Crate crate, int keys) {
        try {
            //FileConfiguration data = Files.DATA.getFile();
            player = player.toLowerCase();

            //if (data.contains("Offline-Players." + player + "." + crate.getName())) keys += data.getInt("Offline-Players." + player + "." + crate.getName());

            //data.set("Offline-Players." + player + "." + crate.getName(), keys);
            //Files.DATA.saveFile();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
    
    /**
     * Take keys from an offline player.
     *
     * @param player The player which you are taking keys from.
     * @param crate The Crate of which key you are taking from the player.
     * @param keys The amount of keys you wish to take.
     * @return Returns true if it took the keys and false if an error occurred.
     */
    public boolean takeOfflineKeys(String player, Crate crate, int keys) {
        try {
            //FileConfiguration data = Files.DATA.getFile();
            player = player.toLowerCase();
            int playerKeys = 0;

            //if (data.contains("Offline-Players." + player + "." + crate.getName())) playerKeys = data.getInt("Offline-Players." + player + "." + crate.getName());

            //data.set("Offline-Players." + player + "." + crate.getName(), playerKeys - keys);
            //Files.DATA.saveFile();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
    
    /**
     * Load the offline keys of a player who has come online.
     *
     * @param player The player which you would like to load the offline keys for.

    public void loadOfflinePlayersKeys(Player player) {
        //FileConfiguration data = Files.DATA.getFile();
        String name = player.getName().toLowerCase();

        if (data.contains("Offline-Players." + name)) {
            for (Crate crate : getCrates()) {
                if (data.contains("Offline-Players." + name + "." + crate.getName())) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, KeyReceiveReason.OFFLINE_PLAYER, 1);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) addKeys(data.getInt("Offline-Players." + name + "." + crate.getName()), player, crate, KeyType.VIRTUAL_KEY);
                }
            }

            data.set("Offline-Players." + name, null);
            Files.DATA.saveFile();
        }
    }     */
    
    /**
     * Add a player to the list of players that are currently opening crates.
     *
     * @param player The player that is opening a crate.
     * @param crate The crate the player is opening.
     */
    public void addPlayerToOpeningList(Player player, Crate crate) {
        playerOpeningCrates.put(player.getUniqueId(), crate);
    }
    
    /**
     * Remove a player from the list of players that are opening crates.
     *
     * @param player The player that has finished opening a crate.
     */
    public void removePlayerFromOpeningList(Player player) {
        playerOpeningCrates.remove(player.getUniqueId());
    }
    
    /**
     * Check if a player is opening a crate.
     *
     * @param player The player you are checking.
     * @return True if they are opening a crate and false if they are not.
     */
    public boolean isInOpeningList(Player player) {
        return playerOpeningCrates.containsKey(player.getUniqueId());
    }
    
    /**
     * Get the crate the player is currently opening.
     *
     * @param player The player you want to check.
     * @return The Crate of which the player is opening. May return null if no crate found.
     */
    public Crate getOpeningCrate(Player player) {
        return playerOpeningCrates.get(player.getUniqueId());
    }
    
    /**
     * Check if an item is a key for a crate.
     *
     * @param item The item you are checking.
     * @return True if the item is a key and false if it is not.
     */
    public boolean isKey(ItemStack item) {
        return getCrateFromKey(item) != null;
    }
    
    /**
     * Get a Crate from a key ItemStack the player.
     *
     * @param item The key ItemStack you are checking.
     * @return Returns a Crate if is a key from a crate otherwise null if it is not.
     */
    public Crate getCrateFromKey(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            for (Crate crate : getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (isKeyFromCrate(item, crate)) return crate;
                }
            }
        }

        return null;
    }
    
    /**
     * Check if a key is from a specific Crate.
     *
     * @param item The key ItemStack you are checking.
     * @param crate The Crate you are checking.
     * @return Returns true if it belongs to that Crate and false if it does not.
     */
    public boolean isKeyFromCrate(ItemStack item, Crate crate) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (item != null && item.getType() != Material.AIR) return methods.isSimilar(item, crate);
        }

        return false;
    }
    
    /**
     * Set the type of key the player is opening a crate for.
     * This is only used in the Cosmic CrateType currently.
     *
     * @param player The player that is opening the crate.
     * @param keyType The KeyType that they are using.
     */
    public void addPlayerKeyType(Player player, KeyType keyType) {
        playerKeys.put(player.getUniqueId(), keyType);
    }
    
    /**
     * Remove the player from the list as they have finished the crate.
     * Currently, only used in the Cosmic CrateType.
     *
     * @param player The player you are removing.
     */
    public void removePlayerKeyType(Player player) {
        playerKeys.remove(player.getUniqueId());
    }
    
    /**
     * Check if the player is in the list.
     *
     * @param player The player you are checking.
     * @return True if they are in the list and false if not.
     */
    public boolean hasPlayerKeyType(Player player) {
        return playerKeys.containsKey(player.getUniqueId());
    }
    
    /**
     * The key type the player's current crate is using.
     *
     * @param player The player that is using the crate.
     * @return The key type of the crate the player is using.
     */
    public KeyType getPlayerKeyType(Player player) {
        return playerKeys.get(player.getUniqueId());
    }
    
    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     *
     * @param player The player being checked.
     * @param crate The crate that has the key you are checking.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return True if they have the key and false if not.
     */
    public boolean hasPhysicalKey(Player player, Crate crate, boolean checkHand) {
        List<ItemStack> items = new ArrayList<>();

        if (checkHand) {
            items.add(player.getEquipment().getItemInMainHand());
            items.add(player.getEquipment().getItemInOffHand());
        } else {
            items.addAll(Arrays.asList(player.getInventory().getContents()));
            items.removeAll(Arrays.asList(player.getInventory().getArmorContents()));
        }

        for (ItemStack item : items) {
            if (item != null) {
                if (isKeyFromCrate(item, crate)) return true;
            }
        }

        return false;
    }
    
    /**
     * Get a physical key from a players inventory.
     *
     * @param player The player you are checking.
     * @param crate The Crate of whose key you are getting.
     * @return The ItemStack in the player's inventory. This will return null if not found.
     */
    public ItemStack getPhysicalKey(Player player, Crate crate) {
        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (methods.isSimilar(item, crate)) return item;
        }

        return null;
    }
    
    /**
     * Get the amount of virtual keys a player has.
     *
     * @param player The player you are checking.
     * @return The amount of virtual keys they own.
     */
    public HashMap<Crate, Integer> getVirtualKeys(Player player) {
        HashMap<Crate, Integer> keys = new HashMap<>();

        getCrates().forEach(crate -> keys.put(crate, getVirtualKeys(player, crate)));

        return keys;
    }
    
    /**
     * Get the amount of virtual keys a player has based on their name.
     *
     * @param playerName The name of the player you are checking.
     * @return The amount of virtual keys the player by that name has.
     */
    public HashMap<Crate, Integer> getVirtualKeys(String playerName) {
        HashMap<Crate, Integer> keys = new HashMap<>();
        //FileConfiguration data = Files.DATA.getFile();

        //for (String uuid : data.getConfigurationSection("Players").getKeys(false)) {
        //    if (playerName.equalsIgnoreCase(data.getString("Players." + uuid + ".Name"))) getCrates().forEach(crate -> keys.put(crate, data.getInt("Players." + uuid + "." + crate.getName())));
        //}

        return keys;
    }
    
    /**
     * Get the locations a player sets for when creating a new schematic.
     *
     * @return The list of locations set by players.
     */
    public HashMap<UUID, Location[]> getSchematicLocations() {
        return schemLocations;
    }
    
    /**
     * Get the amount of virtual keys a player has.
     */
    public int getVirtualKeys(Player player, Crate crate) {
        return 1;
        //return Files.DATA.getFile().getInt("Players." + player.getUniqueId() + "." + crate.getName());
    }
    
    /**
     * Get the amount of physical keys a player has.
     */
    public int getPhysicalKeys(Player player, Crate crate) {
        int keys = 0;

        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (methods.isSimilar(item, crate)) keys += item.getAmount();
        }

        return keys;
    }
    
    /**
     * Get the total amount of keys a player has.
     */
    public Integer getTotalKeys(Player player, Crate crate) {
        return getVirtualKeys(player, crate) + getPhysicalKeys(player, crate);
    }
    
    /**
     * Take a key from a player.
     *
     * @param amount The amount of keys you wish to take.
     * @param player The player you wish to take keys from.
     * @param crate The crate key you are taking.
     * @param keyType The type of key you are taking from the player.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return Returns true if successfully taken keys and false if not.
     */
    public boolean takeKeys(int amount, Player player, Crate crate, KeyType keyType, boolean checkHand) {
        switch (keyType) {
            case PHYSICAL_KEY:
                int takeAmount = amount;

                try {
                    List<ItemStack> items = new ArrayList<>();

                    if (checkHand) {
                        items.add(player.getEquipment().getItemInMainHand());
                        items.add(player.getEquipment().getItemInOffHand());
                    } else {
                        items.addAll(Arrays.asList(player.getInventory().getContents()));
                        items.remove(player.getEquipment().getItemInOffHand());
                    }

                    for (ItemStack item : items) {
                        if (item != null) {
                            if (isKeyFromCrate(item, crate)) {
                                int keyAmount = item.getAmount();

                                if ((takeAmount - keyAmount) >= 0) {
                                    methods.removeItemAnySlot(player.getInventory(), item);
                                    takeAmount -= keyAmount;
                                } else {
                                    item.setAmount(keyAmount - takeAmount);
                                    takeAmount = 0;
                                }

                                if (takeAmount <= 0) return true;
                            }
                        }
                    }

                    // This needs to be done as player.getInventory().removeItem(ItemStack); does NOT remove from the offhand.
                    if (takeAmount > 0) {
                        ItemStack item = player.getEquipment().getItemInOffHand();

                        if (item != null) {
                            if (isKeyFromCrate(item, crate)) {
                                int keyAmount = item.getAmount();

                                if ((takeAmount - keyAmount) >= 0) {
                                    player.getEquipment().setItemInOffHand(new ItemStack(Material.AIR, 1));
                                    takeAmount -= keyAmount;
                                } else {
                                    item.setAmount(keyAmount - takeAmount);
                                    takeAmount = 0;
                                }

                                if (takeAmount <= 0) return true;
                            }
                        }
                    }
                } catch (Exception exception) {
                    methods.failedToTakeKey(player, crate, exception);
                    return false;
                }

                // Returns true because it was able to take some keys.
                if (takeAmount < amount) return true;

                break;
            case VIRTUAL_KEY:
                String uuid = player.getUniqueId().toString();
                int keys = getVirtualKeys(player, crate);
                //Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
                int newAmount = Math.max((keys - amount), 0);

                if (newAmount == 0) {
                    //Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), null);
                } else {
                    //Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), newAmount);
                }

                //Files.DATA.saveFile();
                return true;
            case FREE_KEY: // Returns true because it's FREE
                return true;
        }

        return false;
    }
    
    /**
     * Give a player keys to a Crate.
     *
     * @param amount The amount of keys you are giving them.
     * @param player The player you want to give the keys to.
     * @param crate The Crate of whose keys you are giving.
     * @param keyType The type of key you are giving to the player.
     */
    public void addKeys(int amount, Player player, Crate crate, KeyType keyType) {
        switch (keyType) {
            case PHYSICAL_KEY -> {
                if (!player.getInventory().isEmpty()) {
                    if (Config.GIVE_VIRTUAL_KEYS_WITH_FULL_INVENTORY) {
                        addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);
                        return;
                    }

                    player.getWorld().dropItem(player.getLocation(), crate.getKey(amount));
                    return;
                }

                player.getInventory().addItem(crate.getKey(amount));
            }
            case VIRTUAL_KEY -> {
                String uuid = player.getUniqueId().toString();
                int keys = getVirtualKeys(player, crate);

                //Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
                //Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));
                //Files.DATA.saveFile();
            }
        }
    }
    
    /**
     * Set the amount of virtual keys a player has.
     *
     * @param amount The amount the player will have.
     * @param player The player you are setting the keys to.
     * @param crate The Crate of whose keys are being set.
     */
    public void setKeys(int amount, Player player, Crate crate) {
        String uuid = player.getUniqueId().toString();

        //Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
        //Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), amount);
        //Files.DATA.saveFile();
    }
    
    /**
     * Set a new player's default amount of keys.
     *
     * @param player The player that has just joined.
     */
    public void setNewPlayerKeys(Player player) {
        if (giveNewPlayersKeys) { // Checks if any crate gives new players keys and if not then no need to do all this stuff.
            String uuid = player.getUniqueId().toString();

            if (!player.hasPlayedBefore()) {
                //crates.stream()
                //.filter(Crate :: doNewPlayersGetKeys)
                //.forEach(crate -> Files.DATA.getFile().set("Player." + uuid + "." + crate, crate.getNewPlayerKeys()));
            }
        }
    }
    
    /**
     * Get the hologram plugin settings that is being used.
     *
     * @return The hologram controller for the holograms.
     */
    public HologramController getHologramController() {
        return hologramController;
    }
    
    /**
     * Load all the schematics inside the Schematics folder.
     */
    public void loadSchematics() {
        crateSchematics.clear();
        String[] schems = new File(plugin.getDataFolder() + "/schematics/").list();

        for (String schematicName : schems) {
            if (schematicName.endsWith(".nbt")) crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/schematics/" + schematicName)));
        }
    }
    
    /**
     * Get the list of all the schematics currently loaded onto the server.
     *
     * @return The list of all loaded schematics.
     */
    public List<CrateSchematic> getCrateSchematics() {
        return crateSchematics;
    }
    
    /**
     * Get a schematic based on its name.
     *
     * @param name The name of the schematic.
     * @return Returns the CrateSchematic otherwise returns null if not found.
     */
    public CrateSchematic getCrateSchematic(String name) {
        for (CrateSchematic schematic : crateSchematics) {
            if (schematic.schematicName().equalsIgnoreCase(name)) return schematic;
        }

        return null;
    }
    
    /**
     * Check if an entity is a display reward for a crate.
     *
     * @param entity Entity you wish to check.
     * @return True if it is a display reward item and false if not.
     */
    public boolean isDisplayReward(Entity entity) {
        if (entity instanceof Item) {
            ItemStack item = ((Item) entity).getItemStack();

            if (item.getType() != Material.AIR) return new NBTItem(item).hasKey("crazycrates-item");
        }

        return false;
    }
    
    private ItemStack getKey(FileConfiguration file) {
        String name = file.getString("Crate.PhysicalKey.Name");
        List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
        String id = file.getString("Crate.PhysicalKey.Item");
        boolean glowing = false;

        if (file.contains("Crate.PhysicalKey.Glowing")) glowing = file.getBoolean("Crate.PhysicalKey.Glowing");

        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlow(glowing).build();
    }
    
    private ItemBuilder getDisplayItem(FileConfiguration file, String prize) {
        String path = "Crate.Prizes." + prize + ".";
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            itemBuilder.setMaterial(file.getString(path + "DisplayItem"))
            .setAmount(file.getInt(path + "DisplayAmount", 1))
            .setName(file.getString(path + "DisplayName"))
            .setLore(file.getStringList(path + "Lore"))
            .setGlow(file.getBoolean(path + "Glowing"))
            .setUnbreakable(file.getBoolean(path + "Unbreakable"))
            .hideItemFlags(file.getBoolean(path + "HideItemFlags"))
            .addItemFlags(file.getStringList(path + "Flags"))
            .addPatterns(file.getStringList(path + "Patterns"))
            .setPlayerName(file.getString(path + "Player"));

            if (file.contains(path + "DisplayEnchantments")) {
                for (String enchantmentName : file.getStringList(path + "DisplayEnchantments")) {
                    Enchantment enchantment = methods.getEnchantment(enchantmentName.split(":")[0]);

                    if (enchantment != null) itemBuilder.addEnchantments(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                }
            }

            return itemBuilder;
        } catch (Exception ignored) {
            return new ItemBuilder().setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
        }
    }
    
    private List<ItemBuilder> getItems(FileConfiguration file, String prize) {
        return ItemBuilder.convertStringList(file.getStringList("Crate.Prizes." + prize + ".Items"), prize);
    }
    
    private long pickNumber(long min, long max) {
        max++;

        try {
            // new Random() does not have a nextLong(long bound) method.
            return min + ThreadLocalRandom.current().nextLong(max - min);
        } catch (IllegalArgumentException e) {
            return min;
        }
    }
}