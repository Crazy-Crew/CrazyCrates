package com.badbones69.crazycrates.paper.api;

import ch.jalu.configme.SettingsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.BrokeLocation;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.managers.QuadCrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.plugin.builder.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import us.crazycrew.crazycrates.paper.api.plugin.users.BukkitUserManager;
import com.badbones69.crazycrates.paper.support.holograms.CMIHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.DecentHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.HolographicDisplaysSupport;
import com.badbones69.crazycrates.paper.support.structures.StructureHandler;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.common.config.types.menus.MainMenuConfig;
import us.crazycrew.crazycrates.common.crates.CrateHologram;
import us.crazycrew.crazycrates.common.crates.quadcrates.CrateSchematic;
import us.crazycrew.crazycrates.paper.api.MetricsHandler;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CrazyManager {

    private final CrazyCrates plugin;
    private final CrazyHandler crazyHandler;
    private final FileManager fileManager;
    private final BukkitUserManager userManager;

    private final ConfigManager configManager;
    private final SettingsManager config;
    private final SettingsManager menuConfig;
    private final SettingsManager pluginConfig;

    public CrazyManager(CrazyCrates plugin) {
        this.plugin = plugin;

        this.crazyHandler = this.plugin.getCrazyHandler();
        this.userManager = this.crazyHandler.getUserManager();

        this.fileManager = this.crazyHandler.getFileManager();

        this.configManager = this.crazyHandler.getConfigManager();
        this.config = this.configManager.getConfig();
        this.menuConfig = this.configManager.getMainMenuConfig();
        this.pluginConfig = this.configManager.getPluginConfig();
    }

    // All the crates that have been loaded.
    private final ArrayList<Crate> crates = new ArrayList<>();

    // A list of all the physical crate locations.
    private final ArrayList<CrateLocation> crateLocations = new ArrayList<>();

    // List of all the broken crates.
    private final ArrayList<String> brokeCrates = new ArrayList<>();

    // List of broken physical crate locations.
    private final List<BrokeLocation> brokeLocations = new ArrayList<>();

    // The crate that the player is opening.
    private final HashMap<UUID, Crate> playerOpeningCrates = new HashMap<>();

    // Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
    private final HashMap<UUID, KeyType> playerKeys = new HashMap<>();

    // A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
    private final HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();

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

    public void load(boolean serverStart) {
        if (serverStart) {

        }

        //TODO() Re-work how this functions.
        loadCrates();
    }

    public void reload(boolean serverStop) {
        MetricsHandler metricsHandler = this.crazyHandler.getMetrics();

        if (serverStop) {
            metricsHandler.stop();
            return;
        }

        this.configManager.reload();

        boolean metrics = this.pluginConfig.getProperty(PluginConfig.toggle_metrics);

        if (metrics) {
            metricsHandler.start();
        } else {
            metricsHandler.stop();
        }

        loadCrates();
    }

    // Loads all the information the plugin needs to run.
    public void loadCrates() {
        this.giveNewPlayersKeys = false;
        this.crates.clear();
        this.brokeCrates.clear();
        this.crateLocations.clear();
        this.crateSchematics.clear();

        // Removes all holograms so that they can be replaced.
        if (this.hologramController != null) this.hologramController.removeAllHolograms();

        /*if (PluginSupport.DECENT_HOLOGRAMS.isPluginEnabled()) {
            this.hologramController = new DecentHologramsSupport();
            LegacyLogger.info("DecentHolograms support has been enabled.");
        } else if (PluginSupport.CMI.isPluginEnabled() && CMIModule.holograms.isEnabled()) {
            this.hologramController = new CMIHologramsSupport();
            LegacyLogger.info("CMI Hologram support has been enabled.");
        } else if (PluginSupport.HOLOGRAPHIC_DISPLAYS.isPluginEnabled()) {
            this.hologramController = new HolographicDisplaysSupport();
            LegacyLogger.info("Holographic Displays support has been enabled.");
        } else LegacyLogger.warn("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");*/

        if (this.plugin.isLogging()) LegacyLogger.info("Loading all crate information...");

        for (String crateName : this.fileManager.getAllCratesNames()) {
            try {
                FileConfiguration file = this.fileManager.getFile(crateName).getFile();
                CrateType crateType = CrateType.getFromName(file.getString("Crate.CrateType"));
                ArrayList<Prize> prizes = new ArrayList<>();
                String previewName = file.contains("Crate.Preview-Name") ? file.getString("Crate.Preview-Name") : file.getString("Crate.Name");
                ArrayList<Tier> tiers = new ArrayList<>();
                int maxMassOpen =  file.contains("Crate.Max-Mass-Open") ? file.getInt("Crate.Max-Mass-Open") : 10;
                int requiredKeys = file.contains("Crate.RequiredKeys") ? file.getInt("Crate.RequiredKeys") : 0;

                if (file.contains("Crate.Tiers") && file.getConfigurationSection("Crate.Tiers") != null) {
                    for (String tier : file.getConfigurationSection("Crate.Tiers").getKeys(false)) {
                        String path = "Crate.Tiers." + tier;
                        tiers.add(new Tier(tier, file.getString(path + ".Name"), file.getString(path + ".Color"), file.getInt(path + ".Chance"), file.getInt(path + ".MaxRange")));
                    }
                }

                if (crateType == CrateType.COSMIC && tiers.isEmpty()) {
                    this.brokeCrates.add(crateName);
                    LegacyLogger.warn("No tiers were found for this cosmic crate " + crateName + ".yml file.");
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

                if (!this.giveNewPlayersKeys) {
                    if (newPlayersKeys > 0) this.giveNewPlayersKeys = true;
                }

                List<String> prizeMessage = file.contains("Crate.Prize-Message") ? file.getStringList("Crate.Prize-Message") : Collections.emptyList();

                CrateHologram holo = new CrateHologram(file.getBoolean("Crate.Hologram.Toggle"), file.getDouble("Crate.Hologram.Height", 0.0), file.getStringList("Crate.Hologram.Message"));
                this.crates.add(new Crate(crateName, previewName, crateType, getKey(file), prizes, file, newPlayersKeys, tiers, maxMassOpen, requiredKeys, prizeMessage, holo));
            } catch (Exception exception) {
                this.brokeCrates.add(crateName);

                LegacyLogger.warn("There was an error while loading the " + crateName + ".yml file.", exception);
            }
        }

        this.crates.add(new Crate("Menu", "Menu", CrateType.MENU, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null, 0, 0, Collections.emptyList(), null));

        if (this.plugin.isLogging()) {
            List.of(
                    "All crate information has been loaded.",
                    "Loading all the physical crate locations."
            ).forEach(LegacyLogger::info);
        }

        FileConfiguration locations = Files.LOCATIONS.getFile();
        int loadedAmount = 0;
        int brokeAmount = 0;

        if (locations.getConfigurationSection("Locations") != null) {
            for (String locationName : locations.getConfigurationSection("Locations").getKeys(false)) {
                try {
                    String worldName = locations.getString("Locations." + locationName + ".World");
                    World world = this.plugin.getServer().getWorld(worldName);
                    int x = locations.getInt("Locations." + locationName + ".X");
                    int y = locations.getInt("Locations." + locationName + ".Y");
                    int z = locations.getInt("Locations." + locationName + ".Z");
                    Location location = new Location(world, x, y, z);
                    Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));

                    if (world != null && crate != null) {
                        this.crateLocations.add(new CrateLocation(locationName, crate, location));

                        if (this.hologramController != null) {
                            this.hologramController.createHologram(location.getBlock(), crate);
                        }

                        loadedAmount++;
                    } else {
                        this.brokeLocations.add(new BrokeLocation(locationName, crate, x, y, z, worldName));
                        brokeAmount++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Checking if all physical locations loaded
        if (this.plugin.isLogging()) {
            if (loadedAmount > 0 || brokeAmount > 0) {
                if (brokeAmount <= 0) {
                    LegacyLogger.info("All physical crate locations have been loaded.");
                } else {
                    LegacyLogger.info("Loaded " + loadedAmount + " physical crate locations.");
                    LegacyLogger.info("Failed to load " + brokeAmount + " physical crate locations.");
                }
            }
        }

        // Loading schematic files
        if (this.plugin.isLogging()) LegacyLogger.info("Searching for schematics to load.");

        String[] schems = new File(plugin.getDataFolder() + "/schematics/").list();

        for (String schematicName : schems) {
            if (schematicName.endsWith(".nbt")) {
                this.crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(this.plugin.getDataFolder() + "/schematics/" + schematicName)));

                if (this.plugin.isLogging()) LegacyLogger.info(schematicName + " was successfully found and loaded.");
            }
        }

        if (this.plugin.isLogging()) LegacyLogger.info("All schematics were found and loaded.");

        cleanDataFile();

        this.plugin.getCrazyHandler().getMenuManager().loadButtons();
    }

    // This method is deigned to help clean the data.yml file of any unless info that it may have.
    public void cleanDataFile() {
        FileConfiguration data = Files.DATA.getFile();

        if (data.contains("Players")) {
            if (this.plugin.isLogging()) LegacyLogger.info("Cleaning up the data.yml file.");

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
                    noKeys.forEach(crate -> data.set("Players." + uuid + "." + crate, null));
                } else {
                    removePlayers.add(uuid);
                }
            }

            if (!removePlayers.isEmpty()) {
                if (this.plugin.isLogging()) LegacyLogger.info(removePlayers.size() + " player's data has been marked to be removed.");

                removePlayers.forEach(uuid -> data.set("Players." + uuid, null));

                if (this.plugin.isLogging()) LegacyLogger.info("All empty player data has been removed.");
            }

            if (this.plugin.isLogging()) LegacyLogger.info("The data.yml file has been cleaned.");

            Files.DATA.saveFile();
        }
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
                //TODO() Update messages enum
                //player.sendMessage(Messages.NO_PRIZES_FOUND.getMessage());
                removePlayerFromOpeningList(player);
                removePlayerKeyType(player);
                return;
            }
        }

        if (!(player.hasPermission("crazycrates.open." + crate.getName()) || !player.hasPermission("crazycrates.open.*"))) {
            //TODO() Update messages enum
            //player.sendMessage(Messages.NO_CRATE_PERMISSION.getMessage());
            removePlayerFromOpeningList(player);
            //TODO() Static abuse
            //CrateControlListener.inUse.remove(player);
            return;
        }

        addPlayerToOpeningList(player, crate);

        //if (crate.getFile() != null) this.methods.broadCastMessage(crate.getFile(), player);

        switch (crate.getCrateType()) {
            case MENU -> {
                boolean openMenu = this.menuConfig.getProperty(MainMenuConfig.crate_menu_toggle);

                //TODO() Update messages enum
                //if (openMenu) MenuListener.openGUI(player); else player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
                if (openMenu) this.crazyHandler.getMenuManager().openMainMenu(player); else player.sendMessage("");
            }
            case COSMIC -> {
                //TODO()
                //Cosmic.openCosmic(player, crate, keyType, checkHand);
            }
            case CSGO -> {
                //TODO()
                //CSGO.openCSGO(player, crate, keyType, checkHand);
            }
            case ROULETTE -> {
                //TODO()
                //Roulette.openRoulette(player, crate, keyType, checkHand);
            }
            case WHEEL -> {
                //TODO()
                //Wheel.startWheel(player, crate, keyType, checkHand);
            }
            case WONDER -> {
                //TODO()
                //Wonder.startWonder(player, crate, keyType, checkHand);
            }
            case WAR -> {
                //TODO()
                //War.openWarCrate(player, crate, keyType, checkHand);
            }
            case QUAD_CRATE -> {
                Location lastLocation = player.getLocation();
                lastLocation.setPitch(0F);
                CrateSchematic crateSchematic = getCrateSchematics().get(new Random().nextInt(getCrateSchematics().size()));
                StructureHandler handler = new StructureHandler(crateSchematic.getSchematicFile());
                CrateLocation crateLocation = getCrateLocation(location);
                QuadCrateManager session = new QuadCrateManager(player, crate, keyType, crateLocation.getLocation(), lastLocation, checkHand, handler);

                session.startCrate();
            }
            case FIRE_CRACKER -> {
                //TODO() static shit
                /*if (CrateControlListener.inUse.containsValue(location)) {
                    //TODO() Update messages enum
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate) {
                        //TODO() Update messages enum
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        //CrateControlListener.inUse.put(player, location);
                        //FireCracker.startFireCracker(player, crate, keyType, location, hologramController);
                    }
                }*/
            }
            case QUICK_CRATE -> {
                //TODO() static shit
                /*
                if (CrateControlListener.inUse.containsValue(location)) {
                    //TODO() Update messages enum
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate && location.equals(player.getLocation())) {
                        //TODO() Update messages enum
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        //TODO() static shit
                        //CrateControlListener.inUse.put(player, location);
                        //QuickCrate.openCrate(player, location, crate, keyType, hologramController);
                    }
                }*/
            }
            case CRATE_ON_THE_GO -> {
                if (virtualCrate) {
                    //TODO() Update messages enum
                    //player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (this.userManager.takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
                        Prize prize = crate.pickPrize(player);
                        givePrize(player, prize, crate);

                        //if (prize.useFireworks()) this.methods.firework(player.getLocation().add(0, 1, 0));

                        removePlayerFromOpeningList(player);
                    } else {
                        //this.methods.failedToTakeKey(player.getName(), crate);
                    }
                }
            }
        }

        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        this.crazyHandler.getEventLogger().logCrateEvent(player, crate, keyType, logFile, logConsole);
    }

    /**
     * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
     *
     * @param player The player that the crate is being ended for.
     */
    public void endCrate(Player player) {
        if (this.currentTasks.containsKey(player.getUniqueId())) {
            this.currentTasks.get(player.getUniqueId()).cancel();
            removeCrateTask(player);
        }
    }

    /**
     * Ends the tasks running by a player.
     *
     * @param player The player using the crate.
     */
    public void endQuadCrate(Player player) {
        if (this.currentQuadTasks.containsKey(player.getUniqueId())) {
            for (BukkitTask task : this.currentQuadTasks.get(player.getUniqueId())) {
                task.cancel();
            }

            this.currentQuadTasks.remove(player.getUniqueId());
        }
    }

    /**
     * Add a quad crate task that is going on for a player.
     *
     * @param player The player opening the crate.
     * @param task The task of the quad crate.
     */
    public void addQuadCrateTask(Player player, BukkitTask task) {
        if (!this.currentQuadTasks.containsKey(player.getUniqueId())) {
            this.currentQuadTasks.put(player.getUniqueId(), new ArrayList<>());
        }

        this.currentQuadTasks.get(player.getUniqueId()).add(task);
    }

    /**
     * Checks to see if the player has a quad crate task going on.
     *
     * @param player The player that is being checked.
     * @return True if they do have a task and false if not.
     */
    public boolean hasQuadCrateTask(Player player) {
        return this.currentQuadTasks.containsKey(player.getUniqueId());
    }

    /**
     * Add a crate task that is going on for a player.
     *
     * @param player The player opening the crate.
     * @param task The task of the crate.
     */
    public void addCrateTask(Player player, BukkitTask task) {
        this.currentTasks.put(player.getUniqueId(), task);
    }

    /**
     * Remove a task from the list of current tasks.
     *
     * @param player The player using the crate.
     */
    public void removeCrateTask(Player player) {
        this.currentTasks.remove(player.getUniqueId());
    }

    /**
     * Checks to see if the player has a crate task going on.
     *
     * @param player The player that is being checked.
     * @return True if they do have a task and false if not.
     */
    public boolean hasCrateTask(Player player) {
        return this.currentTasks.containsKey(player.getUniqueId());
    }

    /**
     * A list of all the physical crate locations.
     *
     * @return List of locations.
     */
    public ArrayList<CrateLocation> getCrateLocations() {
        return this.crateLocations;
    }

    /**
     * Checks to see if the location is a physical crate.
     *
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
     *
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
     *
     * @return List of broken crate locations.
     */
    public List<BrokeLocation> getBrokeCrateLocations() {
        return this.brokeLocations;
    }

    /**
     * Add a new physical crate location.
     *
     * @param location The location you wish to add.
     * @param crate The crate which you would like to set it to.
     */
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

        this.crateLocations.add(new CrateLocation(id, crate, location));

        if (this.hologramController != null) this.hologramController.createHologram(location.getBlock(), crate);
    }

    /**
     * Remove a physical crate location.
     *
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
            this.crateLocations.remove(location);

            if (this.hologramController != null) this.hologramController.removeHologram(location.getLocation().getBlock());
        }
    }

    /**
     * Get a list of broken crates.
     *
     * @return An ArrayList of all the broken crates.
     */
    public ArrayList<String> getBrokeCrates() {
        return this.brokeCrates;
    }

    /**
     * Get a list of all the crates loaded into the plugin.
     *
     * @return An ArrayList of all the loaded crates.
     */
    public ArrayList<Crate> getCrates() {
        return this.crates;
    }

    /**
     * Get a crate by its name.
     *
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
     * Give a player a prize they have won.
     *
     * @param player The player you wish to give the prize to.
     * @param prize The prize the player has won.
     */
    public void givePrize(Player player, Prize prize, Crate crate) {
        if (prize != null) {
            prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;

            for (ItemStack item : prize.getItems()) {

                if (item == null) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("{crate}", prize.getCrate());
                    placeholders.put("{prize}", prize.getName());
                    //TODO() Update messages enum
                    //player.sendMessage(Messages.PRIZE_ERROR.getMessage(placeholders));
                    continue;
                }

                //if (!this.methods.isInventoryFull(player)) {
                //    player.getInventory().addItem(item);
                //} else {
                //    player.getWorld().dropItemNaturally(player.getLocation(), item);
                //}
            }

            for (ItemBuilder item : prize.getItemBuilders()) {
                ItemBuilder clone = new ItemBuilder(item);

                //if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                //    clone.setName(PlaceholderAPI.setPlaceholders(player, clone.getName()));
                 //   clone.setLore(PlaceholderAPI.setPlaceholders(player, clone.getLore()));
                //}

                //if (!this.methods.isInventoryFull(player)) {
                //    player.getInventory().addItem(clone.build());
                //} else {
                //    player.getWorld().dropItemNaturally(player.getLocation(), clone.build());
                //}
            }

            for (String command : prize.getCommands()) { // /give %player% iron %random%:1-64
                if (command.contains("{random}:")) {
                    String cmd = command;
                    StringBuilder commandBuilder = new StringBuilder();

                    for (String word : cmd.split(" ")) {
                        if (word.startsWith("{random}:")) {
                            word = word.replace("{random}:", "");

                            try {
                                long min = Long.parseLong(word.split("-")[0]);
                                long max = Long.parseLong(word.split("-")[1]);
                                commandBuilder.append(pickNumber(min, max)).append(" ");
                            } catch (Exception e) {
                                commandBuilder.append("1 ");
                                LegacyLogger.warn("The prize " + prize.getName() + " in the " + prize.getCrate() + " crate has caused an error when trying to run a command.");
                                LegacyLogger.warn("Command: " + cmd);
                            }
                        } else {
                            commandBuilder.append(word).append(" ");
                        }
                    }

                    command = commandBuilder.toString();
                    command = command.substring(0, command.length() - 1);
                }

                //if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) command = PlaceholderAPI.setPlaceholders(player, command);

                //this.methods.sendCommand(command.replaceAll("\\{player}", player.getName()).replaceAll("\\{reward}}", quoteReplacement(prize.getDisplayItemBuilder().getUpdatedName())).replaceAll("\\{crate}", crate.getCrateInventoryName()));
            }

            if (!crate.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
                for (String message : crate.getPrizeMessage()) {
                   // if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                    //    message = PlaceholderAPI.setPlaceholders(player, message);
                    //}

                    //this.methods.sendMessage(player, message.replaceAll("\\{player}", player.getName()).replaceAll("\\{reward}", quoteReplacement(prize.getDisplayItemBuilder().getName())).replaceAll("\\{reward}", crate.getCrateInventoryName()), false);
                }

                return;
            }

            for (String message : prize.getMessages()) {
                //if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                //    message = PlaceholderAPI.setPlaceholders(player, message);
                //}

                //this.methods.sendMessage(player, message.replaceAll("\\{player}", player.getName()).replaceAll("\\{reward}", quoteReplacement(prize.getDisplayItemBuilder().getName())).replaceAll("\\{crate}", crate.getCrateInventoryName()), false);
            }
        } else {
            LegacyLogger.warn("No prize was found when giving " + player.getName() + " a prize.");
        }
    }

    /**
     * Add a player to the list of players that are currently opening crates.
     *
     * @param player The player that is opening a crate.
     * @param crate The crate the player is opening.
     */
    public void addPlayerToOpeningList(Player player, Crate crate) {
        this.playerOpeningCrates.put(player.getUniqueId(), crate);
    }

    /**
     * Remove a player from the list of players that are opening crates.
     *
     * @param player The player that has finished opening a crate.
     */
    public void removePlayerFromOpeningList(Player player) {
        this.playerOpeningCrates.remove(player.getUniqueId());
    }

    /**
     * Check if a player is opening a crate.
     *
     * @param player The player you are checking.
     * @return True if they are opening a crate and false if they are not.
     */
    public boolean isInOpeningList(Player player) {
        return this.playerOpeningCrates.containsKey(player.getUniqueId());
    }

    /**
     * Get the crate the player is currently opening.
     *
     * @param player The player you want to check.
     * @return The Crate of which the player is opening. May return null if no crate found.
     */
    public Crate getOpeningCrate(Player player) {
        return this.playerOpeningCrates.get(player.getUniqueId());
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
     *
     * @param item The key ItemStack you are checking.
     * @param crate The Crate you are checking.
     * @return Returns true if it belongs to that Crate and false if it does not.
     */
    public boolean isKeyFromCrate(ItemStack item, Crate crate) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (item != null && item.getType() != Material.AIR) {
                //TODO() Uncomment method.
                //return this.methods.isSimilar(item, crate);
            }
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
        this.playerKeys.put(player.getUniqueId(), keyType);
    }

    /**
     * Remove the player from the list as they have finished the crate.
     * Currently, only used in the Cosmic CrateType.
     *
     * @param player The player you are removing.
     */
    public void removePlayerKeyType(Player player) {
        this.playerKeys.remove(player.getUniqueId());
    }

    /**
     * Check if the player is in the list.
     *
     * @param player The player you are checking.
     * @return True if they are in the list and false if not.
     */
    public boolean hasPlayerKeyType(Player player) {
        return this.playerKeys.containsKey(player.getUniqueId());
    }

    /**
     * The key type the player's current crate is using.
     *
     * @param player The player that is using the crate.
     * @return The key type of the crate the player is using.
     */
    public KeyType getPlayerKeyType(Player player) {
        return this.playerKeys.get(player.getUniqueId());
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
                if (isKeyFromCrate(item, crate)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the locations a player sets for when creating a new schematic.
     *
     * @return The list of locations set by players.
     */
    public HashMap<UUID, Location[]> getSchematicLocations() {
        return this.schemLocations;
    }

    /**
     * Set a new player's default amount of keys.
     *
     * @param player The player that has just joined.
     */
    public void setNewPlayerKeys(Player player) {
        if (this.giveNewPlayersKeys) { // Checks if any crate gives new players keys and if not then no need to do all this stuff.
            String uuid = player.getUniqueId().toString();

            if (!player.hasPlayedBefore()) {
                this.crates.stream()
                .filter(Crate :: doNewPlayersGetKeys)
                .forEach(crate -> {
                    Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), crate.getNewPlayerKeys());
                    Files.DATA.saveFile();
                });
            }
        }
    }

    /**
     * Get the hologram plugin settings that is being used.
     *
     * @return The hologram controller for the holograms.
     */
    public HologramController getHologramController() {
        return this.hologramController;
    }

    /**
     * Load all the schematics inside the Schematics folder.
     */
    public void loadSchematics() {
        this.crateSchematics.clear();
        String[] schems = new File(this.plugin.getDataFolder() + "/schematics/").list();

        assert schems != null;
        for (String schematicName : schems) {
            if (schematicName.endsWith(".nbt")) {
                this.crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/schematics/" + schematicName)));
            }
        }
    }

    /**
     * Get the list of all the schematics currently loaded onto the server.
     *
     * @return The list of all loaded schematics.
     */
    public List<CrateSchematic> getCrateSchematics() {
        return this.crateSchematics;
    }

    /**
     * Get a schematic based on its name.
     *
     * @param name The name of the schematic.
     * @return Returns the CrateSchematic otherwise returns null if not found.
     */
    public CrateSchematic getCrateSchematic(String name) {
        for (CrateSchematic schematic : this.crateSchematics) {
            if (schematic.getSchematicName().equalsIgnoreCase(name)) {
                return schematic;
            }
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

            if (file.contains(path + "DisplayDamage") && file.getInt(path + "DisplayDamage") >= 1) {
                itemBuilder.setDamage(file.getInt(path + "DisplayDamage"));
            }

            if (file.contains(path + "DisplayTrim.Pattern")) {
                itemBuilder.setTrimPattern(Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(file.getString(path + "DisplayTrim.Pattern").toLowerCase())));
            }

            if (file.contains(path + "DisplayTrim.Material")) {
                itemBuilder.setTrimMaterial(Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(file.getString(path + "DisplayTrim.Material").toLowerCase())));
            }

            if (file.contains(path + "DisplayEnchantments")) {
                for (String enchantmentName : file.getStringList(path + "DisplayEnchantments")) {
                    //Enchantment enchantment = this.methods.getEnchantment(enchantmentName.split(":")[0]);

                    //if (enchantment != null) {
                    //    itemBuilder.addEnchantments(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                    //}
                }
            }

            return itemBuilder;
        } catch (Exception e) {
            return new ItemBuilder().setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
        }
    }

    private List<ItemBuilder> getItems(FileConfiguration file, String prize) {
        return ItemBuilder.convertStringList(file.getStringList("Crate.Prizes." + prize + ".Items"), prize);
    }

    public long pickNumber(long min, long max) {
        max++;

        try {
            return min + new Random().nextLong(max - min);
        } catch (IllegalArgumentException e) {
            return min;
        }
    }
}
