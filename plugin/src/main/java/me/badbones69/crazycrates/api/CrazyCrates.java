package me.badbones69.crazycrates.api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.enums.BrokeLocation;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent.KeyReciveReason;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.*;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.GUIMenu;
import me.badbones69.crazycrates.controllers.Preview;
import me.badbones69.crazycrates.cratetypes.*;
import me.badbones69.crazycrates.multisupport.HologramsSupport;
import me.badbones69.crazycrates.multisupport.HolographicSupport;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import me.badbones69.crazycrates.multisupport.nms.v1_10_R1.NMS_v1_10_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_11_R1.NMS_v1_11_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_12_R1.NMS_v1_12_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_13_R2.NMS_v1_13_R2;
import me.badbones69.crazycrates.multisupport.nms.v1_14_R1.NMS_v1_14_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_15_R1.NMS_v1_15_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_16_R3.NMS_v1_16_R3;
import me.badbones69.crazycrates.multisupport.nms.v1_17_R1.NMS_v1_17_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_18_R1.NMS_v1_18_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_8_R3.NMS_v1_8_R3;
import me.badbones69.crazycrates.multisupport.nms.v1_9_R2.NMS_v1_9_R2;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class CrazyCrates {
    
    /**
     * FileManager object.
     */
    private static final FileManager fileManager = FileManager.getInstance();
    /**
     * The instance of this class.
     */
    private static final CrazyCrates instance = new CrazyCrates();
    /**
     * All the crates that have been loaded.
     */
    private final ArrayList<Crate> crates = new ArrayList<>();
    /**
     * A list of all the physical crate locations.
     */
    private final ArrayList<CrateLocation> crateLocations = new ArrayList<>();
    /**
     * List of all the broken crates.
     */
    private final ArrayList<String> brokecrates = new ArrayList<>();
    
    /**
     * List of broken physical crate locations.
     */
    private final List<BrokeLocation> brokeLocations = new ArrayList<>();
    
    /**
     * The crate that the player is opening.
     */
    private final HashMap<UUID, Crate> playerOpeningCrates = new HashMap<>();
    
    /**
     * Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
     */
    private final HashMap<UUID, KeyType> playerKeys = new HashMap<>();
    
    /**
     * A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
     */
    private final HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();
    
    /**
     * A list of tasks being ran by the QuadCrate type.
     */
    private final HashMap<UUID, ArrayList<BukkitTask>> currentQuadTasks = new HashMap<>();
    
    /**
     * The time in seconds a quadcrate can go until afk kicks them from it.
     */
    private Integer quadCrateTimer;
    
    /**
     * A list of current crate schematics for Quad Crate.
     */
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();
    
    /**
     * If the player's inventory is full when given a physical key it will instead give them virtual keys. If false it will drop the keys on the ground.
     */
    private boolean giveVirtualKeysWhenInventoryFull;
    
    /**
     * True if at least one crate gives new players keys and false if none give new players keys.
     */
    private boolean giveNewPlayersKeys;
    
    /**
     * True if using 1.13+ material names and false if using lower versions.
     */
    private boolean useNewMaterial;
    
    private boolean useNewSounds;
    
    /**
     * The NMS version needed to be used.
     */
    private NMSSupport nmsSupport;
    
    /**
     * The hologram api that is being hooked into.
     */
    private HologramController hologramController;
    
    /**
     * Schematic locations for 1.13+
     */
    private final HashMap<UUID, Location[]> schemLocations = new HashMap<>();
    
    /**
     * The CrazyCrates plugin.
     */
    private Plugin plugin;
    
    /**
     * Gets the instance of the CrazyCrates class.
     * @return Instance of this class.
     */
    public static CrazyCrates getInstance() {
        return instance;
    }
    
    /**
     * Get the file manager that controls all yml files.
     * @return The FileManager that controls all yml files.
     */
    public static FileManager getFileManager() {
        return fileManager;
    }
    
    /**
     * Loads all the information the plugin needs to run.
     */
    public void loadCrates() {
        giveNewPlayersKeys = false;
        crates.clear();
        brokecrates.clear();
        crateLocations.clear();
        crateSchematics.clear();
        Version version = Version.getCurrentVersion();
        useNewMaterial = version.isNewer(Version.v1_12_R1);
        useNewSounds = version.isNewer(Version.v1_8_R3);
        switch (version) {
            case v1_8_R3:
                nmsSupport = new NMS_v1_8_R3();
                break;
            case v1_9_R2:
                nmsSupport = new NMS_v1_9_R2();
                break;
            case v1_10_R1:
                nmsSupport = new NMS_v1_10_R1();
                break;
            case v1_11_R1:
                nmsSupport = new NMS_v1_11_R1();
                break;
            case v1_12_R1:
                nmsSupport = new NMS_v1_12_R1();
                break;
            case v1_13_R2:
                nmsSupport = new NMS_v1_13_R2();
                break;
            case v1_14_R1:
                nmsSupport = new NMS_v1_14_R1();
                break;
            case v1_15_R1:
                nmsSupport = new NMS_v1_15_R1();
                break;
            case v1_16_R3:
                nmsSupport = new NMS_v1_16_R3();
                break;
            case v1_17_R1:
                nmsSupport = new NMS_v1_17_R1();
                break;
            case v1_18_R1:
                nmsSupport = new NMS_v1_18_R1();
                break;
        }
        plugin = Bukkit.getPluginManager().getPlugin("CrazyCrates");
        quadCrateTimer = Files.CONFIG.getFile().getInt("Settings.QuadCrate.Timer") * 20;
        giveVirtualKeysWhenInventoryFull = Files.CONFIG.getFile().getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            hologramController = new HolographicSupport();
        } else if (Support.HOLOGRAMS.isPluginLoaded()) {
            hologramController = new HologramsSupport();
        }
        //Removes all holograms so that they can be replaced.
        if (hologramController != null) {
            hologramController.removeAllHolograms();
        }
        if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + "Loading all crate information...");
        for (String crateName : fileManager.getAllCratesNames()) {
            //			if(fileManager.isLogging()) plugin.getLogger().info(fileManager.getPrefix() + "Loading " + crateName + ".yml information....");
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
                    Bukkit.getLogger().log(Level.WARNING, fileManager.getPrefix() + "No tiers were found for this cosmic crate " + crateName + ".yml file.");
                    continue;
                }
                for (String prize : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
                    Prize altPrize = null;
                    String path = "Crate.Prizes." + prize;
                    ArrayList<Tier> prizeTiers = new ArrayList<>();
                    for (String tier : file.getStringList(path + ".Tiers")) {
                        for (Tier loadedTier : tiers) {
                            if (loadedTier.getName().equalsIgnoreCase(tier)) {
                                prizeTiers.add(loadedTier);
                            }
                        }
                    }
                    if (file.contains(path + ".Alternative-Prize")) {
                        if (file.getBoolean(path + ".Alternative-Prize.Toggle")) {
                            altPrize = new Prize("Alternative-Prize",
                            file.getStringList(path + ".Alternative-Prize.Messages"),
                            file.getStringList(path + ".Alternative-Prize.Commands"),
                            null,//No editor items
                            getItems(file, prize + ".Alternative-Prize"));
                        }
                    }
                    ArrayList<ItemStack> editorItems = new ArrayList<>();
                    if (file.contains(path + ".Editor-Items")) {
                        for (Object list : file.getList(path + ".Editor-Items")) {
                            editorItems.add((ItemStack) list);
                        }
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
                if (giveNewPlayersKeys = false) {
                    if (newPlayersKeys > 0) {
                        giveNewPlayersKeys = true;
                    }
                }
                crates.add(new Crate(crateName, previewName, crateType, getKey(file), prizes, file, newPlayersKeys, tiers,
                new CrateHologram(file.getBoolean("Crate.Hologram.Toggle"), file.getDouble("Crate.Hologram.Height", 0.0), file.getStringList("Crate.Hologram.Message"))));
            } catch (Exception e) {
                brokecrates.add(crateName);
                Bukkit.getLogger().log(Level.WARNING, fileManager.getPrefix() + "There was an error while loading the " + crateName + ".yml file.");
                e.printStackTrace();
            }
        }
        crates.add(new Crate("Menu", "Menu", CrateType.MENU, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null, null));
        if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + "All crate information has been loaded.");
        if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + "Loading all the physical crate locations.");
        FileConfiguration locations = Files.LOCATIONS.getFile();
        int loadedAmount = 0;
        int brokeAmount = 0;
        if (locations.getConfigurationSection("Locations") != null) {
            for (String locationName : locations.getConfigurationSection("Locations").getKeys(false)) {
                try {
                    String worldName = locations.getString("Locations." + locationName + ".World");
                    World world = Bukkit.getWorld(worldName);
                    int x = locations.getInt("Locations." + locationName + ".X");
                    int y = locations.getInt("Locations." + locationName + ".Y");
                    int z = locations.getInt("Locations." + locationName + ".Z");
                    Location location = new Location(world, x, y, z);
                    Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));
                    if (world != null && crate != null) {
                        crateLocations.add(new CrateLocation(locationName, crate, location));
                        if (hologramController != null) {
                            hologramController.createHologram(location.getBlock(), crate);
                        }
                        loadedAmount++;
                    } else {
                        brokeLocations.add(new BrokeLocation(locationName, crate, x, y, z, worldName));
                        brokeAmount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Checking if all physical locations loaded
        if (fileManager.isLogging()) {
            if (loadedAmount > 0 || brokeAmount > 0) {
                if (brokeAmount <= 0) {
                    Bukkit.getLogger().info(fileManager.getPrefix() + "All physical crate locations have been loaded.");
                } else {
                    Bukkit.getLogger().info(fileManager.getPrefix() + "Loaded " + loadedAmount + " physical crate locations.");
                    Bukkit.getLogger().info(fileManager.getPrefix() + "Failed to load " + brokeAmount + " physical crate locations.");
                }
            }
        }
        //Loading schematic files
        if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + "Searching for schematics to load.");
        String[] schems = new File(plugin.getDataFolder() + "/Schematics/").list();
        boolean isNewer = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
        for (String schematicName : schems) {
            if (isNewer) {
                if (schematicName.endsWith(".nbt")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                    if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + schematicName + " was successfully found and loaded.");
                }
            } else {
                if (schematicName.endsWith(".schematic")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".schematic", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                    if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + schematicName + " was successfully found and loaded.");
                }
            }
        }
        if (fileManager.isLogging()) Bukkit.getLogger().info(fileManager.getPrefix() + "All schematics were found and loaded.");
        cleanDataFile();
        Preview.loadButtons();
    }
    
    /**
     * If the player's inventory is full when given a physical key it will instead give them virtual keys. If false it will drop the keys on the ground.
     * @return True if the player will get a virtual key and false if it drops on the floor.
     */
    public boolean getGiveVirtualKeysWhenInventoryFull() {
        return giveVirtualKeysWhenInventoryFull;
    }
    
    /**
     * This method is deigned to help clean the data.yml file of any unless info that it may have.
     */
    public void cleanDataFile() {
        FileConfiguration data = Files.DATA.getFile();
        if (data.contains("Players")) {
            boolean logging = fileManager.isLogging();
            if (logging) Bukkit.getLogger().info(fileManager.getPrefix() + "Cleaning up the data.yml file.");
            List<String> removePlayers = new ArrayList<>();
            for (String uuid : data.getConfigurationSection("Players").getKeys(false)) {
                boolean hasKeys = false;
                List<String> noKeys = new ArrayList<>();
                for (Crate crate : getCrates()) {
                    if (data.getInt("Players." + uuid + "." + crate.getName()) <= 0) {
                        noKeys.add(crate.getName());
                    } else {
                        hasKeys = true;
                    }
                }
                if (hasKeys) {
                    for (String crate : noKeys) {
                        data.set("Players." + uuid + "." + crate, null);
                    }
                } else {
                    removePlayers.add(uuid);
                }
            }
            if (removePlayers.size() > 0) {
                if (logging) Bukkit.getLogger().info(fileManager.getPrefix() + removePlayers.size() + " player's data has been marked to be removed.");
                for (String uuid : removePlayers) {
                    data.set("Players." + uuid, null);
                }
                if (logging) Bukkit.getLogger().info(fileManager.getPrefix() + "All empty player data has been removed.");
            }
            if (logging) Bukkit.getLogger().info(fileManager.getPrefix() + "The data.yml file has been cleaned.");
            Files.DATA.saveFile();
        }
    }
    
    /**
     * Opens a crate for a player.
     * @param player The player that is having the crate opened for them.
     * @param crate The crate that is being used.
     * @param location The location that may be needed for some crate types.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     */
    public void openCrate(Player player, Crate crate, KeyType keyType, Location location, boolean virtualCrate, boolean checkHand) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (!crate.canWinPrizes(player)) {
                player.sendMessage(Messages.NO_PRIZES_FOUND.getMessage());
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
                    Bukkit.broadcastMessage(Methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
                }
            }
            broadcast = false;
        }
        switch (crate.getCrateType()) {
            case MENU:
                GUIMenu.openGUI(player);
                break;
            case COSMIC:
                Cosmic.openCosmic(player, crate, keyType, checkHand);
                break;
            case CSGO:
                CSGO.openCSGO(player, crate, keyType, checkHand);
                break;
            case ROULETTE:
                Roulette.openRoulette(player, crate, keyType, checkHand);
                break;
            case WHEEL:
                Wheel.startWheel(player, crate, keyType, checkHand);
                break;
            case WONDER:
                Wonder.startWonder(player, crate, keyType, checkHand);
                break;
            case WAR:
                War.openWarCrate(player, crate, keyType, checkHand);
                break;
            case QUAD_CRATE:
                Location last = player.getLocation();
                last.setPitch(0F);
                QuadCrateSession session = new QuadCrateSession(player, crate, keyType, location, last, checkHand);
                broadcast = session.startCrate();
                break;
            case FIRE_CRACKER:
                if (CrateControl.inUse.containsValue(location)) {
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate) {
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        CrateControl.inUse.put(player, location);
                        FireCracker.startFireCracker(player, crate, keyType, location);
                    }
                }
                break;
            case QUICK_CRATE:
                if (CrateControl.inUse.containsValue(location)) {
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate && location.equals(player.getLocation())) {
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        CrateControl.inUse.put(player, location);
                        QuickCrate.openCrate(player, location, crate, keyType);
                    }
                }
                break;
            case CRATE_ON_THE_GO:
                if (virtualCrate) {
                    player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (takeKeys(1, player, crate, keyType, true)) {
                        Prize prize = crate.pickPrize(player);
                        givePrize(player, prize);
                        if (prize.useFireworks()) {
                            Methods.fireWork(player.getLocation().add(0, 1, 0));
                        }
                        removePlayerFromOpeningList(player);
                    } else {
                        Methods.failedToTakeKey(player, crate);
                    }
                }
                break;
        }
        if (broadcast) {
            if (!crate.getFile().getString("Crate.BroadCast").isEmpty()) {
                Bukkit.broadcastMessage(Methods.color(crate.getFile().getString("Crate.BroadCast").replaceAll("%Prefix%", Methods.getPrefix()).replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
            }
        }
    }
    
    /**
     * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
     * @param player The player that the crate is being ended for.
     */
    public void endCrate(Player player) {
        if (currentTasks.containsKey(player.getUniqueId())) {
            currentTasks.get(player.getUniqueId()).cancel();
            removeCrateTask(player);
        }
    }
    
    /**
     * Ends the tasks running by a player.
     * @param player The player using the crate.
     */
    public void endQuadCrate(Player player) {
        if (currentQuadTasks.containsKey(player.getUniqueId())) {
            for (BukkitTask task : currentQuadTasks.get(player.getUniqueId())) {
                task.cancel();
            }
            currentQuadTasks.remove(player.getUniqueId());
        }
    }
    
    /**
     * Add a quad crate task that is going on for a player.
     * @param player The player opening the crate.
     * @param task The task of the quad crate.
     */
    public void addQuadCrateTask(Player player, BukkitTask task) {
        if (!currentQuadTasks.containsKey(player.getUniqueId())) {
            currentQuadTasks.put(player.getUniqueId(), new ArrayList<>());
        }
        currentQuadTasks.get(player.getUniqueId()).add(task);
    }
    
    /**
     * Checks to see if the player has a quad crate task going on.
     * @param player The player that is being checked.
     * @return True if they do have a task and false if not.
     */
    public boolean hasQuadCrateTask(Player player) {
        return currentQuadTasks.containsKey(player.getUniqueId());
    }
    
    /**
     * Add a crate task that is going on for a player.
     * @param player The player opening the crate.
     * @param task The task of the crate.
     */
    public void addCrateTask(Player player, BukkitTask task) {
        currentTasks.put(player.getUniqueId(), task);
    }
    
    /**
     * Remove a task from the list of current tasks.
     * @param player The player using the crate.
     */
    public void removeCrateTask(Player player) {
        currentTasks.remove(player.getUniqueId());
    }
    
    /**
     * Checks to see if the player has a crate task going on.
     * @param player The player that is being checked.
     * @return True if they do have a task and false if not.
     */
    public boolean hasCrateTask(Player player) {
        return currentTasks.containsKey(player.getUniqueId());
    }
    
    /**
     * A list of all the physical crate locations.
     * @return List of locations.
     */
    public ArrayList<CrateLocation> getCrateLocations() {
        return crateLocations;
    }
    
    /**
     * Checks to see if the a location is a physical crate.
     * @param loc The location you are checking.
     * @return True if it is a physical crate and false if not.
     */
    public boolean isCrateLocation(Location loc) {
        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(loc)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the physical crate of the location.
     * @param loc The location you are checking.
     * @return A CrateLocation if the location is a physical crate otherwise null if not.
     */
    public CrateLocation getCrateLocation(Location loc) {
        for (CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(loc)) {
                return crateLocation;
            }
        }
        return null;
    }
    
    /**
     * Get a list of all the broke physical crate locations.
     * @return List of broken crate locations.
     */
    public List<BrokeLocation> getBrokeCrateLocations() {
        return brokeLocations;
    }
    
    /**
     * Add a new physical crate location.
     * @param location The location you wish to add.
     * @param crate The crate which you would like to set it to.
     */
    public void addCrateLocation(Location location, Crate crate) {
        FileConfiguration locations = Files.LOCATIONS.getFile();
        String id = "1"; //Location ID
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
        if (hologramController != null) {
            hologramController.createHologram(location.getBlock(), crate);
        }
    }
    
    /**
     * Remove a physical crate location.
     * @param id The id of the location.
     */
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
            if (hologramController != null) {
                hologramController.removeHologram(location.getLocation().getBlock());
            }
        }
    }
    
    /**
     * Get a list of broken crates.
     * @return An ArrayList of all the broken crates.
     */
    public ArrayList<String> getBrokeCrates() {
        return brokecrates;
    }
    
    /**
     * Get a list of all the crates loaded into the plugin.
     * @return An ArrayList of all the loaded crates.
     */
    public ArrayList<Crate> getCrates() {
        return crates;
    }
    
    /**
     * Get a crate by its name.
     * @param name The name of the crate you wish to grab.
     * @return Returns a Crate object of the crate it found and if none are found it returns null.
     */
    public Crate getCrateFromName(String name) {
        for (Crate crate : getCrates()) {
            if (crate.getName().equalsIgnoreCase(name)) {
                return crate;
            }
        }
        return null;
    }
    
    /**
     * The time in seconds a quadcrate will last before kicking the player.
     * @return The time in seconds till kick.
     */
    public Integer getQuadCrateTimer() {
        return quadCrateTimer;
    }
    
    /**
     * Load the crate preview of a crate.
     * @param crate The crate you wish to load the preview of.
     * @return An Inventory object of the preview.
     */
    public Inventory loadPreview(Crate crate) {
        FileConfiguration file = crate.getFile();
        int slots = 9;
        for (int size = file.getConfigurationSection("Crate.Prizes").getKeys(false).size(); size > 9 && slots < crate.getMaxSlots(); size -= 9) {
            slots += 9;
        }
        Inventory inv = Bukkit.createInventory(null, slots, Methods.sanitizeColor(file.getString("Crate.Name")));
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
                Enchantment enchantment = Methods.getEnchantment(enchantmentName.split(":")[0]);
                if (enchantment != null) {
                    enchantments.put(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                }
            }
            try {
                inv.setItem(inv.firstEmpty(), new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setUnbreakable(unbreakable).hideItemFlags(hideItemFlags).setEnchantments(enchantments).setGlowing(glowing).setPlayer(player).build());
            } catch (Exception e) {
                inv.addItem(new ItemBuilder().setMaterial("RED_TERRACOTTA", "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + reward)).build());
            }
        }
        return inv;
    }
    
    /**
     * Give a player a prize they have won.
     * @param player The player you wish to give the prize to.
     * @param prize The prize the player has won.
     */
    public void givePrize(Player player, Prize prize) {
        if (prize != null) {
            prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;
            for (ItemStack item : prize.getItems()) {
                if (!Methods.isInventoryFull(player)) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
            for (ItemBuilder item : prize.getItemBuilders()) {
                ItemBuilder clone = new ItemBuilder(item);
                if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
                    clone.setName(PlaceholderAPI.setPlaceholders(player, clone.getName()));
                    clone.setLore(PlaceholderAPI.setPlaceholders(player, clone.getLore()));
                }
                if (!Methods.isInventoryFull(player)) {
                    player.getInventory().addItem(clone.build());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), clone.build());
                }
            }
            for (String command : prize.getCommands()) {// /give %player% iron %random%:1-64
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
                                Bukkit.getLogger().warning("[CrazyCrates]>> The prize " + prize.getName() + " in the " + prize.getCrate() + " crate has caused an error when trying to run a command.");
                                Bukkit.getLogger().warning("[CrazyCrates]>> Command: " + cmd);
                            }
                        } else {
                            commandBuilder.append(word).append(" ");
                        }
                    }
                    command = commandBuilder.toString();
                    command = command.substring(0, command.length() - 1);
                }
                if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
                    command = PlaceholderAPI.setPlaceholders(player, command);
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Methods.color(command.replace("%Player%", player.getName()).replace("%player%", player.getName())));
            }
            for (String message : prize.getMessages()) {
                if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
                    message = PlaceholderAPI.setPlaceholders(player, message);
                }
                player.sendMessage(Methods.color(message).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
                .replace("%displayname%", prize.getDisplayItemBuilder().getName()).replace("%DisplayName%", prize.getDisplayItemBuilder().getName()));
            }
        } else {
            plugin.getLogger().warning("[CrazyCrates]>> No prize was found when giving " + player.getName() + " a prize.");
        }
    }
    
    /**
     * Give keys to an offline player.
     * @param player The offline player you wish to give keys to.
     * @param crate The Crate of which key you are giving to the player.
     * @param keys The amount of keys you wish to give to the player.
     * @return Returns true if it successfully gave the offline player a key and false if there was an error.
     */
    public boolean addOfflineKeys(String player, Crate crate, int keys) {
        try {
            FileConfiguration data = Files.DATA.getFile();
            player = player.toLowerCase();
            if (data.contains("Offline-Players." + player + "." + crate.getName())) {
                keys += data.getInt("Offline-Players." + player + "." + crate.getName());
            }
            data.set("Offline-Players." + player + "." + crate.getName(), keys);
            Files.DATA.saveFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Take keys from an offline player.
     * @param player The player which you are taking keys from.
     * @param crate The Crate of which key you are taking from the player.
     * @param keys The amount of keys you wish to take.
     * @return Returns true if it took the keys and false if an error occurred.
     */
    public boolean takeOfflineKeys(String player, Crate crate, int keys) {
        try {
            FileConfiguration data = Files.DATA.getFile();
            player = player.toLowerCase();
            int playerKeys = 0;
            if (data.contains("Offline-Players." + player + "." + crate.getName())) {
                playerKeys = data.getInt("Offline-Players." + player + "." + crate.getName());
            }
            data.set("Offline-Players." + player + "." + crate.getName(), playerKeys - keys);
            Files.DATA.saveFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Load the offline keys of a player who has came online.
     * @param player The player which you would like to load the offline keys for.
     */
    public void loadOfflinePlayersKeys(Player player) {
        FileConfiguration data = Files.DATA.getFile();
        String name = player.getName().toLowerCase();
        if (data.contains("Offline-Players." + name)) {
            for (Crate crate : getCrates()) {
                if (data.contains("Offline-Players." + name + "." + crate.getName())) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, KeyReciveReason.OFFLINE_PLAYER, 1);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        addKeys(data.getInt("Offline-Players." + name + "." + crate.getName()), player, crate, KeyType.VIRTUAL_KEY);
                    }
                }
            }
            data.set("Offline-Players." + name, null);
            Files.DATA.saveFile();
        }
    }
    
    /**
     * Add a player to the list of players that are currently opening crates.
     * @param player The player that is opening a crate.
     * @param crate The crate the player is opening.
     */
    public void addPlayerToOpeningList(Player player, Crate crate) {
        playerOpeningCrates.put(player.getUniqueId(), crate);
    }
    
    /**
     * Remove a player from the list of players that are opening crates.
     * @param player The player that has finished opening a crate.
     */
    public void removePlayerFromOpeningList(Player player) {
        playerOpeningCrates.remove(player.getUniqueId());
    }
    
    /**
     * Check if a player is opening a crate.
     * @param player The player you are checking.
     * @return True if they are opening a crate and false if they are not.
     */
    public boolean isInOpeningList(Player player) {
        return playerOpeningCrates.containsKey(player.getUniqueId());
    }
    
    /**
     * Get the crate the player is currently opening.
     * @param player The player you want to check.
     * @return The Crate of which the player is opening. May return null if no crate found.
     */
    public Crate getOpeningCrate(Player player) {
        return playerOpeningCrates.get(player.getUniqueId());
    }
    
    /**
     * Check if an item is a key for a crate.
     * @param item The item you are checking.
     * @return True if the item is a key and false if it is not.
     */
    public boolean isKey(ItemStack item) {
        return getCrateFromKey(item) != null;
    }
    
    /**
     * Get a Crate from a key ItemStack the player.
     * @param item The key ItemStack you are checking.
     * @return Returns a Crate if is a key from a crate otherwise null if it is not.
     */
    public Crate getCrateFromKey(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            for (Crate crate : getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (isKeyFromCrate(item, crate)) {
                        return crate;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Check if a key is from a specific Crate.
     * @param item The key ItemStack you are checking.
     * @param crate The Crate you are checking.
     * @return Returns true if it belongs to that Crate and false if it does not.
     */
    public boolean isKeyFromCrate(ItemStack item, Crate crate) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (item != null && item.getType() != Material.AIR) {
                return Methods.isSimilar(item, crate);
            }
        }
        return false;
    }
    
    /**
     * Set the type of key the player is opening a crate for.
     * This is only used in the Cosmic CrateType currently.
     * @param player The player that is opening the crate.
     * @param keyType The KeyType that they are using.
     */
    public void addPlayerKeyType(Player player, KeyType keyType) {
        playerKeys.put(player.getUniqueId(), keyType);
    }
    
    /**
     * Remove the player from the list as they have finished the crate.
     * Currently only used in the Cosmic CrateType.
     * @param player The player you are removing.
     */
    public void removePlayerKeyType(Player player) {
        playerKeys.remove(player.getUniqueId());
    }
    
    /**
     * Check if the player is in the list.
     * @param player The player you are checking.
     * @return True if they are in the list and false if not.
     */
    public boolean hasPlayerKeyType(Player player) {
        return playerKeys.containsKey(player.getUniqueId());
    }
    
    /**
     * The key type the player's current crate is using.
     * @param player The player that is using the crate.
     * @return The key type of the crate the player is using.
     */
    public KeyType getPlayerKeyType(Player player) {
        return playerKeys.get(player.getUniqueId());
    }
    
    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     * @param player The player being checked.
     * @param crate The crate that has the key you are checking.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return True if they have the key and false if not.
     */
    public boolean hasPhysicalKey(Player player, Crate crate, boolean checkHand) {
        List<ItemStack> items = new ArrayList<>();
        if (checkHand) {
            items.add(nmsSupport.getItemInMainHand(player));
            if (Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
                items.add(player.getEquipment().getItemInOffHand());
            }
        } else {
            items.addAll(Arrays.asList(player.getInventory().getContents()));
            items.removeAll(Arrays.asList(player.getInventory().getArmorContents()));
        }
        for (ItemStack item : items) {
            if (item != null) {
                if (isKeyFromCrate(item, crate)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Get a physical key from a players inventory.
     * @param player The player you are checking.
     * @param crate The Crate of who's key you are getting.
     * @return The ItemStack in the player's inventory. This will return null if not found.
     */
    public ItemStack getPhysicalKey(Player player, Crate crate) {
        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if(item == null || item.getType() == Material.AIR) continue;
            if (Methods.isSimilar(item, crate)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Get the amount of virtual keys a player has.
     * @param player The player you are checking.
     * @return The amount of virtual keys they own.
     */
    public HashMap<Crate, Integer> getVirtualKeys(Player player) {
        HashMap<Crate, Integer> keys = new HashMap<>();
        for (Crate crate : getCrates()) {
            keys.put(crate, getVirtualKeys(player, crate));
        }
        return keys;
    }
    
    /**
     * Get the amount of virtual keys a player has based on their name.
     * @param playerName The name of the player you are checking.
     * @return The amount of virtual keys the player by that name has.
     */
    public HashMap<Crate, Integer> getVirtualKeys(String playerName) {
        HashMap<Crate, Integer> keys = new HashMap<>();
        FileConfiguration data = Files.DATA.getFile();
        for (String uuid : data.getConfigurationSection("Players").getKeys(false)) {
            if (playerName.equalsIgnoreCase(data.getString("Players." + uuid + ".Name"))) {
                for (Crate crate : getCrates()) {
                    keys.put(crate, data.getInt("Players." + uuid + "." + crate.getName()));
                }
            }
        }
        return keys;
    }
    
    /**
     * Get the locations a player sets for when creating a new schematic.
     * @return The list of locations set by players.
     */
    public HashMap<UUID, Location[]> getSchematicLocations() {
        return schemLocations;
    }
    
    /**
     * Get the amount of virtual keys a player has.
     */
    public int getVirtualKeys(Player player, Crate crate) {
        return Files.DATA.getFile().getInt("Players." + player.getUniqueId() + "." + crate.getName());
    }
    
    /**
     * Get the amount of physical keys a player has.
     */
    public int getPhysicalKeys(Player player, Crate crate) {
        int keys = 0;
        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (Methods.isSimilar(item, crate)) {
                keys += item.getAmount();
            }
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
                boolean hasOffhand = Version.getCurrentVersion().isNewer(Version.v1_8_R3);
                try {
                    List<ItemStack> items = new ArrayList<>();
                    if (checkHand) {
                        items.add(nmsSupport.getItemInMainHand(player));
                        if (hasOffhand) {
                            items.add(player.getEquipment().getItemInOffHand());
                        }
                    } else {
                        items.addAll(Arrays.asList(player.getInventory().getContents()));
                        if (hasOffhand) {//Offhand needs to be removed due to it not being removed in the list.
                            items.remove(player.getEquipment().getItemInOffHand());
                        }
                    }
                    for (ItemStack item : items) {
                        if (item != null) {
                            if (isKeyFromCrate(item, crate)) {
                                int keyAmount = item.getAmount();
                                if ((takeAmount - keyAmount) >= 0) {
                                    final HashMap<Integer, ItemStack> integerItemStackHashMap = Methods.removeItemAnySlot(player.getInventory(), item);
                                    takeAmount -= keyAmount;
                                } else {
                                    item.setAmount(keyAmount - takeAmount);
                                    takeAmount = 0;
                                }
                                if (takeAmount <= 0) {
                                    return true;
                                }
                            }
                        }
                    }
                    //This needs to be done as player.getInventory().removeItem(ItemStack); does NOT remove from the off hand.
                    if (takeAmount > 0 && hasOffhand) {
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
                                if (takeAmount <= 0) {
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Methods.failedToTakeKey(player, crate, e);
                    return false;
                }
                //Returns true because it was able to take some keys.
                if (takeAmount < amount) {
                    return true;
                }
                break;
            case VIRTUAL_KEY:
                String uuid = player.getUniqueId().toString();
                int keys = getVirtualKeys(player, crate);
                Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
                int newAmount = Math.max((keys - amount), 0);
                if (newAmount <= 0) {
                    Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), null);
                } else {
                    Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), newAmount);
                }
                Files.DATA.saveFile();
                return true;
            case FREE_KEY://Returns true because its FREE
                return true;
        }
        return false;
    }
    
    /**
     * Give a player keys to a Crate.
     * @param amount The amount of keys you are giving them.
     * @param player The player you want to give the keys to.
     * @param crate The Crate of who's keys you are giving.
     * @param keyType The type of key you are giving to the player.
     */
    public void addKeys(int amount, Player player, Crate crate, KeyType keyType) {
        switch (keyType) {
            case PHYSICAL_KEY:
                if (Methods.isInventoryFull(player)) {
                    if (giveVirtualKeysWhenInventoryFull && crate.getCrateType() != CrateType.CRATE_ON_THE_GO) {
                        addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), crate.getKey(amount));
                    }
                } else {
                    player.getInventory().addItem(crate.getKey(amount));
                }
                break;
            case VIRTUAL_KEY:
                String uuid = player.getUniqueId().toString();
                int keys = getVirtualKeys(player, crate);
                Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
                Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));
                Files.DATA.saveFile();
                break;
        }
    }
    
    /**
     * Set the amount of virtual keys a player has.
     * @param amount The amount the player will have.
     * @param player The player you are setting the keys to.
     * @param crate The Crate of who's keys are being set.
     */
    public void setKeys(int amount, Player player, Crate crate) {
        String uuid = player.getUniqueId().toString();
        Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
        Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), amount);
        Files.DATA.saveFile();
    }
    
    /**
     * Set a new player's default amount of keys.
     * @param player The player that has just joined.
     */
    public void setNewPlayerKeys(Player player) {
        if (giveNewPlayersKeys) {// Checks if any crate gives new players keys and if not then no need to do all this stuff.
            String uuid = player.getUniqueId().toString();
            if (!player.hasPlayedBefore()) {
                crates.stream()
                .filter(Crate :: doNewPlayersGetKeys)
                .forEach(crate -> Files.DATA.getFile().set("Player." + uuid + "." + crate, crate.getNewPlayerKeys()));
            }
        }
    }
    
    /**
     * Get the NMS version being used.
     * @return Version of NMS, returns null if not found.
     */
    public NMSSupport getNMSSupport() {
        return nmsSupport;
    }
    
    /**
     * Get the hologram plugin settings that is being used.
     * @return The hologram controller for the holograms.
     */
    public HologramController getHologramController() {
        return hologramController;
    }
    
    /**
     * Get the CrazyCrates Plugin.
     * @return The CrazyCrates Plugin object.
     */
    public Plugin getPlugin() {
        return plugin;
    }
    
    /**
     * Check if the server uses new 1.13+ material names.
     * @return True if the server is 1.13+ and false if it is 1.12.2-.
     */
    public boolean useNewMaterial() {
        return useNewMaterial;
    }
    
    /**
     * Get the correct sound for the version of minecraft.
     * @param newSound The sound from 1.9+
     * @param oldSound The sound from 1.8.8-
     * @return The Sound object of the current minecraft version.
     */
    public Sound getSound(String newSound, String oldSound) {
        return Sound.valueOf(useNewSounds ? newSound : oldSound);
    }
    
    /**
     * Load all the schematics inside the Schematics folder.
     */
    public void loadSchematics() {
        crateSchematics.clear();
        String[] schems = new File(plugin.getDataFolder() + "/Schematics/").list();
        boolean isNewer = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
        for (String schematicName : schems) {
            if (isNewer) {
                if (schematicName.endsWith(".nbt")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                }
            } else {
                if (schematicName.endsWith(".schematic")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".schematic", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                }
            }
        }
    }
    
    /**
     * Get the list of all the schematics currently loaded onto the server.
     * @return The list of all loaded schematics.
     */
    public List<CrateSchematic> getCrateSchematics() {
        return crateSchematics;
    }
    
    /**
     * Get a schematic based on its name.
     * @param name The name of the schematic.
     * @return Returns the CrateSchematic otherwise returns null if not found.
     */
    public CrateSchematic getCrateSchematic(String name) {
        for (CrateSchematic schematic : crateSchematics) {
            if (schematic.getSchematicName().equalsIgnoreCase(name)) {
                return schematic;
            }
        }
        return null;
    }
    
    /**
     * Check if an entity is a display reward for a crate.
     * @param entity Entity you wish to check.
     * @return True if it is a display reward item and false if not.
     */
    public boolean isDisplayReward(Entity entity) {
        if (entity instanceof Item) {
            ItemStack item = ((Item) entity).getItemStack();
            if (item.getType() != Material.AIR) {
                return new NBTItem(item).hasKey("crazycrates-item");
            }
        }
        return false;
    }
    
    private ItemStack getKey(FileConfiguration file) {
        String name = file.getString("Crate.PhysicalKey.Name");
        List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
        String id = file.getString("Crate.PhysicalKey.Item");
        boolean glowing = false;
        if (file.contains("Crate.PhysicalKey.Glowing")) {
            glowing = file.getBoolean("Crate.PhysicalKey.Glowing");
        }
        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build();
    }
    
    private ItemBuilder getDisplayItem(FileConfiguration file, String prize) {
        String path = "Crate.Prizes." + prize + ".";
        ItemBuilder itemBuilder = new ItemBuilder();
        try {
            itemBuilder.setMaterial(file.getString(path + "DisplayItem"))
            .setAmount(file.getInt(path + "DisplayAmount", 1))
            .setName(file.getString(path + "DisplayName"))
            .setLore(file.getStringList(path + "Lore"))
            .setGlowing(file.getBoolean(path + "Glowing"))
            .setUnbreakable(file.getBoolean(path + "Unbreakable"))
            .hideItemFlags(file.getBoolean(path + "HideItemFlags"))
            .addItemFlags(file.getStringList(path + "Flags"))
            .addPatterns(file.getStringList(path + "Patterns"))
            .setPlayer(file.getString(path + "Player"));
            if (file.contains(path + "DisplayEnchantments")) {
                for (String enchantmentName : file.getStringList(path + "DisplayEnchantments")) {
                    Enchantment enchantment = Methods.getEnchantment(enchantmentName.split(":")[0]);
                    if (enchantment != null) {
                        itemBuilder.addEnchantments(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                    }
                }
            }
            return itemBuilder;
        } catch (Exception e) {
            return new ItemBuilder().setMaterial("RED_TERRACOTTA", "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
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