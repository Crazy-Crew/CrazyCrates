package com.badbones69.crazycrates.paper.api;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.BrokeLocation;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent.KeyReceiveReason;
import us.crazycrew.crazycrates.paper.api.events.crates.CrateOpenEvent;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.managers.QuadCrateManager;
import com.badbones69.crazycrates.paper.api.objects.*;
import us.crazycrew.crazycrates.paper.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.paper.cratetypes.*;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.listeners.MenuListener;
import com.badbones69.crazycrates.paper.listeners.PreviewListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.common.crates.CrateHologram;
import com.badbones69.crazycrates.paper.support.holograms.CMIHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.DecentHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.HolographicDisplaysSupport;
import com.badbones69.crazycrates.paper.support.libraries.PluginSupport;
import com.badbones69.crazycrates.paper.support.structures.StructureHandler;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import us.crazycrew.crazycrates.common.crates.quadcrates.CrateSchematic;
import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class CrazyManager {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final FileManager fileManager = this.plugin.getFileManager();

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

    // A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
    private final HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();

    // A list of tasks being run by the QuadCrate type.
    private final HashMap<UUID, ArrayList<BukkitTask>> currentQuadTasks = new HashMap<>();

    // The time in seconds a quadcrate can go until afk kicks them from it.
    private Integer quadCrateTimer;

    // A list of current crate schematics for Quad Crate.
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();

    // True if at least one crate gives new players keys and false if none give new players keys.
    private boolean giveNewPlayersKeys;

    // The hologram api that is being hooked into.
    private HologramController hologramController;

    // Loads all the information the plugin needs to run.
    public void loadCrates() {
        this.giveNewPlayersKeys = false;

        this.plugin.getCrateManager().clearCrates();

        this.brokecrates.clear();
        this.crateLocations.clear();
        this.crateSchematics.clear();

        this.quadCrateTimer = Files.CONFIG.getFile().getInt("Settings.QuadCrate.Timer") * 20;

        // Removes all holograms so that they can be replaced.
        if (this.hologramController != null) this.hologramController.removeAllHolograms();

        if (PluginSupport.DECENT_HOLOGRAMS.isPluginEnabled()) {
            this.hologramController = new DecentHologramsSupport();
            if (this.plugin.isLogging())this.plugin.getLogger().info("DecentHolograms support has been enabled.");
        } else if (PluginSupport.CMI.isPluginEnabled() && CMIModule.holograms.isEnabled()) {
            this.hologramController = new CMIHologramsSupport();
            if (this.plugin.isLogging())this.plugin.getLogger().info("CMI Hologram support has been enabled.");
        } else if (PluginSupport.HOLOGRAPHIC_DISPLAYS.isPluginEnabled()) {
            this.hologramController = new HolographicDisplaysSupport();
            if (this.plugin.isLogging())this.plugin.getLogger().info("Holographic Displays support has been enabled.");
        } else if (this.plugin.isLogging())this.plugin.getLogger().warning("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");

        if (this.plugin.isLogging()) this.plugin.getLogger().info("Loading all crate information...");

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

                if (crateType == CrateType.cosmic && tiers.isEmpty()) {
                    this.brokecrates.add(crateName);
                    if (this.plugin.isLogging()) this.plugin.getLogger().warning("No tiers were found for this cosmic crate " + crateName + ".yml file.");
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
                this.plugin.getCrateManager().addCrate(new Crate(crateName, previewName, crateType, getKey(file), prizes, file, newPlayersKeys, tiers, maxMassOpen, requiredKeys, prizeMessage, holo));
            } catch (Exception exception) {
                this.brokecrates.add(crateName);
                this.plugin.getLogger().log(Level.WARNING, "There was an error while loading the " + crateName + ".yml file.", exception);
            }
        }

        this.plugin.getCrateManager().addCrate(new Crate("Menu", "Menu", CrateType.menu, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null, 0, 0, Collections.emptyList(), null));

        if (this.plugin.isLogging()) {
            List.of(
                    "All crate information has been loaded.",
                    "Loading all the physical crate locations."
            ).forEach(line -> this.plugin.getLogger().info(line));
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
                    Crate crate = this.plugin.getCrateManager().getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));

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

                } catch (Exception ignored) {}
            }
        }

        // Checking if all physical locations loaded
        if (this.plugin.isLogging()) {
            if (loadedAmount > 0 || brokeAmount > 0) {
                if (brokeAmount <= 0) {
                    this.plugin.getLogger().info("All physical crate locations have been loaded.");
                } else {
                    this.plugin.getLogger().info("Loaded " + loadedAmount + " physical crate locations.");
                    this.plugin.getLogger().info("Failed to load " + brokeAmount + " physical crate locations.");
                }
            }

            this.plugin.getLogger().info("Searching for schematics to load.");
        }

        // Loading schematic files
        String[] schems = new File(this.plugin.getDataFolder() + "/schematics/").list();

        if (schems != null) {
            for (String schematicName : schems) {
                if (schematicName.endsWith(".nbt")) {
                    this.crateSchematics.add(new CrateSchematic(schematicName, new File(plugin.getDataFolder() + "/schematics/" + schematicName)));

                    if (this.plugin.isLogging()) this.plugin.getLogger().info(schematicName + " was successfully found and loaded.");
                }
            }
        }

        if (this.plugin.isLogging()) this.plugin.getLogger().info("All schematics were found and loaded.");

        cleanDataFile();
        PreviewListener.loadButtons();
    }

    // This method is deigned to help clean the data.yml file of any unless info that it may have.
    public void cleanDataFile() {
        FileConfiguration data = Files.DATA.getFile();

        if (!data.contains("Players")) return;

        if (this.plugin.isLogging()) this.plugin.getLogger().info("Cleaning up the data.yml file.");

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
            if (this.plugin.isLogging()) this.plugin.getLogger().info(removePlayers.size() + " player's data has been marked to be removed.");

            removePlayers.forEach(uuid -> data.set("Players." + uuid, null));

            if (this.plugin.isLogging()) this.plugin.getLogger().info("All empty player data has been removed.");
        }

        if (this.plugin.isLogging()) this.plugin.getLogger().info("The data.yml file has been cleaned.");
        Files.DATA.saveFile();
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
        CrateOpenEvent crateOpenEvent = new CrateOpenEvent(this.plugin, player, crate, keyType, checkHand, crate.getFile());
        crateOpenEvent.callEvent();

        if (crateOpenEvent.isCancelled()) {
            List.of(
                    "Crate " + crate.getName() + " event has been cancelled.",
                    "A few reasons for why this happened can be found below",
                    "",
                    " 1) No valid prizes can be found, Likely a yaml issue.",
                    " 2) The player does not have the permission to open the crate."
            ).forEach(this.plugin.getLogger()::warning);

            return;
        }

        FileConfiguration config = Files.CONFIG.getFile();

        switch (crate.getCrateType()) {
            case menu -> {
                boolean openMenu = config.getBoolean("Settings.Enable-Crate-Menu");

                if (openMenu) MenuListener.openGUI(player); else player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
            }
            case csgo -> CSGO.openCSGO(player, crate, keyType, checkHand);
            case roulette -> Roulette.openRoulette(player, crate, keyType, checkHand);
            case wheel -> Wheel.startWheel(player, crate, keyType, checkHand);
            case wonder -> Wonder.startWonder(player, crate, keyType, checkHand);
            case war -> War.openWarCrate(player, crate, keyType, checkHand);
            case quad_crate -> {
                Location lastLocation = player.getLocation();
                lastLocation.setPitch(0F);

                boolean isRandom = crate.getFile().contains("Crate.structure.file") && !crate.getFile().getBoolean("Crate.structure.random", true);

                CrateSchematic schematic = isRandom ? getCrateSchematic(crate.getFile().getString("Crate.structure.file")) : getCrateSchematics().get(new Random().nextInt(getCrateSchematics().size()));

                StructureHandler handler = new StructureHandler(schematic.getSchematicFile());
                CrateLocation crateLocation = getCrateLocation(location);
                QuadCrateManager session = new QuadCrateManager(player, crate, keyType, crateLocation.getLocation(), lastLocation, checkHand, handler);

                session.startCrate();
            }
            case fire_cracker -> {
                if (CrateControlListener.inUse.containsValue(location)) {
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate) {
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        CrateControlListener.inUse.put(player, location);
                        FireCracker.startFireCracker(player, crate, keyType, location, hologramController);
                    }
                }
            }
            case quick_crate -> {
                if (CrateControlListener.inUse.containsValue(location)) {
                    player.sendMessage(Messages.QUICK_CRATE_IN_USE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate && location.equals(player.getLocation())) {
                        player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        removePlayerFromOpeningList(player);
                        return;
                    } else {
                        CrateControlListener.inUse.put(player, location);
                        QuickCrate.openCrate(player, location, crate, keyType, hologramController);
                    }
                }
            }
            case crate_on_the_go -> {
                if (virtualCrate) {
                    player.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (this.plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
                        Prize prize = crate.pickPrize(player);
                        this.plugin.getCrazyHandler().givePrize(player, prize, crate);

                        if (prize.useFireworks()) this.plugin.getCrazyHandler().getMethods().firework(player.getLocation().add(0, 1, 0));

                        removePlayerFromOpeningList(player);
                    } else {
                        this.plugin.getCrazyHandler().getMethods().failedToTakeKey(player, crate);
                    }
                }
            }
        }

        boolean logFile = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Actions.Log-Console");

        this.plugin.getStarter().getEventLogger().logCrateEvent(player, crate, keyType, logFile, logConsole);
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
        return this.brokecrates;
    }

    /**
     * Get a list of all the crates loaded into the plugin.
     *
     * @return An ArrayList of all the loaded crates.
     */
    public List<Crate> getCrates() {
        return this.plugin.getCrateManager().getCrates();
    }

    /**
     * Get a crate by its name.
     *
     * @param name The name of the crate you wish to grab.
     * @return Returns a Crate object of the crate it found and if none are found it returns null.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public Crate getCrateFromName(String name) {
        return this.plugin.getCrateManager().getCrateFromName(name);
    }

    /**
     * The time in seconds a quadcrate will last before kicking the player.
     *
     * @return The time in seconds till kick.
     */
    public int getQuadCrateTimer() {
        return this.quadCrateTimer;
    }

    /**
     * Give keys to an offline player.
     *
     * @param player The offline player you wish to give keys to.
     * @param crate The Crate of which key you are giving to the player.
     * @param keys The amount of keys you wish to give to the player.
     * @return Returns true if it successfully gave the offline player a key and false if there was an error.
     */
    @Deprecated(since = "1.16", forRemoval = true)
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
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to add offline keys to player: This is from a deprecated method", exception);
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
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean takeOfflineKeys(String player, Crate crate, int keys) {
        CrateBaseCommand.CustomPlayer customPlayer = new CrateBaseCommand.CustomPlayer(player);

        UUID uuid = customPlayer.getPlayer() == null ? customPlayer.getOfflinePlayer().getUniqueId() : customPlayer.getPlayer().getUniqueId();

        return this.plugin.getCrazyHandler().getUserManager().takeOfflineKeys(uuid, crate.getName(), keys, KeyType.virtual_key);
    }

    /**
     * Load the offline keys of a player who has come online.
     *
     * @param player The player which you would like to load the offline keys for.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public void loadOfflinePlayersKeys(Player player) {
        FileConfiguration data = Files.DATA.getFile();
        String name = player.getName().toLowerCase();

        if (data.contains("Offline-Players." + name)) {
            for (Crate crate : getCrates()) {
                if (data.contains("Offline-Players." + name + "." + crate.getName())) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, KeyReceiveReason.OFFLINE_PLAYER, 1);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        int keys = getVirtualKeys(player, crate);
                        int addedKeys = data.getInt("Offline-Players." + name + "." + crate.getName());

                        data.set("Players." + player.getUniqueId() + "." + crate.getName(), (Math.max((keys + addedKeys), 0)));

                        Files.DATA.saveFile();
                    }
                }
            }

            data.set("Offline-Players." + name, null);

            Files.DATA.saveFile();
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
                if (crate.getCrateType() != CrateType.menu) {
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
        if (crate.getCrateType() != CrateType.menu) {
            if (item != null && item.getType() != Material.AIR) {
                return this.plugin.getCrazyHandler().getMethods().isSimilar(item, crate);
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
     * Get a physical key from a players inventory.
     *
     * @param player The player you are checking.
     * @param crate The Crate of whose key you are getting.
     * @return The ItemStack in the player's inventory. This will return null if not found.
     */
    public ItemStack getPhysicalKey(Player player, Crate crate) {
        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (this.plugin.getCrazyHandler().getMethods().isSimilar(item, crate)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Get the amount of virtual keys a player has.
     *
     * @param player The player you are checking.
     * @return The amount of virtual keys they own.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public HashMap<Crate, Integer> getVirtualKeys(Player player) {
        HashMap<Crate, Integer> keys = new HashMap<>();

        for (Crate crate : getCrates()) {
            keys.put(crate, getVirtualKeys(player, crate));
        }

        return keys;
    }

    /**
     * Get the amount of virtual keys a player has based on their name.
     *
     * @param playerName The name of the player you are checking.
     * @return The amount of virtual keys the player by that name has.
     */
    @Deprecated(since = "1.16", forRemoval = true)
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
     * Get the amount of virtual keys a player has.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public int getVirtualKeys(Player player, Crate crate) {
        return this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName());
    }

    /**
     * Get the amount of physical keys a player has.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public int getPhysicalKeys(Player player, Crate crate) {
        return this.plugin.getCrazyHandler().getUserManager().getPhysicalKeys(player.getUniqueId(), crate.getName());
    }

    /**
     * Get the total amount of keys a player has.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public Integer getTotalKeys(Player player, Crate crate) {
        UserManager userManager = this.plugin.getCrazyHandler().getUserManager();

        UUID uuid = player.getUniqueId();
        String crateName = crate.getName();

        return userManager.getVirtualKeys(uuid, crateName) + userManager.getPhysicalKeys(uuid, crateName);
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
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean takeKeys(int amount, Player player, Crate crate, com.badbones69.crazycrates.api.enums.types.KeyType keyType, boolean checkHand) {
        return this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, player.getUniqueId(), crate.getName(), KeyType.getFromName(keyType.getName().toLowerCase()), checkHand);
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public void addVirtualKeys(int amount, Player player, Crate crate) {
        this.plugin.getCrazyHandler().getUserManager().addVirtualKeys(amount, player.getUniqueId(), crate.getName());
    }

    /**
     * Give a player keys to a Crate.
     *
     * @param amount The amount of keys you are giving them.
     * @param player The player you want to give the keys to.
     * @param crate The Crate of whose keys you are giving.
     * @param keyType The type of key you are giving to the player.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public void addKeys(int amount, Player player, Crate crate, com.badbones69.crazycrates.api.enums.types.KeyType keyType) {
        this.plugin.getCrazyHandler().getUserManager().addKeys(amount, player.getUniqueId(), crate.getName(), KeyType.getFromName(keyType.getName().toLowerCase()));
    }

    /**
     * Set the amount of virtual keys a player has.
     *
     * @param amount The amount the player will have.
     * @param player The player you are setting the keys to.
     * @param crate The Crate of whose keys are being set.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public void setKeys(int amount, Player player, Crate crate) {
        this.plugin.getCrazyHandler().getUserManager().setKeys(amount, player.getUniqueId(), crate.getName());
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
                this.plugin.getCrateManager().getCrates().stream()
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
                return new NBTItem(item).hasTag("crazycrates-item");
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
                    Enchantment enchantment = this.plugin.getCrazyHandler().getMethods().getEnchantment(enchantmentName.split(":")[0]);

                    if (enchantment != null) {
                        itemBuilder.addEnchantments(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                    }
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
}