package com.badbones69.crazycrates.paper.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.cratetypes.CSGO;
import com.badbones69.crazycrates.paper.cratetypes.FireCracker;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import com.badbones69.crazycrates.paper.cratetypes.Roulette;
import com.badbones69.crazycrates.paper.cratetypes.War;
import com.badbones69.crazycrates.paper.cratetypes.Wheel;
import com.badbones69.crazycrates.paper.cratetypes.Wonder;
import org.bukkit.Location;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.BrokeLocation;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent.KeyReceiveReason;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.api.events.crates.CrateOpenEvent;
import us.crazycrew.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.managers.QuadCrateManager;
import us.crazycrew.crazycrates.paper.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.listeners.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.paper.api.support.structures.StructureHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import us.crazycrew.crazycrates.common.crates.quadcrates.CrateSchematic;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class CrazyManager {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final SettingsManager config = this.plugin.getConfigManager().getConfig();

    // The crate that the player is opening.
    private final HashMap<UUID, Crate> playerOpeningCrates = new HashMap<>();

    // Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
    private final HashMap<UUID, KeyType> playerKeys = new HashMap<>();

    // A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
    private final HashMap<UUID, BukkitTask> currentTasks = new HashMap<>();

    // A list of tasks being run by the QuadCrate type.
    private final HashMap<UUID, ArrayList<BukkitTask>> currentQuadTasks = new HashMap<>();

    // The time in seconds a quadcrate can go until afk kicks them from it.
    private int quadCrateTimer;

    // The hologram api that is being hooked into.
    private HologramController hologramController;

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

        switch (crate.getCrateType()) {
            case menu -> {
                if (this.config.getProperty(Config.enable_crate_menu)) MenuListener.openGUI(player); else player.sendMessage(Translation.feature_disabled.getString());
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
                    player.sendMessage(Translation.quick_crate_in_use.getString());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate) {
                        player.sendMessage(Translation.cant_be_a_virtual_crate.getString());
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
                    player.sendMessage(Translation.quick_crate_in_use.getString());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (virtualCrate && location.equals(player.getLocation())) {
                        player.sendMessage(Translation.cant_be_a_virtual_crate.getString());
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
                    player.sendMessage(Translation.cant_be_a_virtual_crate.getString());
                    removePlayerFromOpeningList(player);
                    return;
                } else {
                    if (this.plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
                        Prize prize = crate.pickPrize(player);
                        this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);

                        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                        removePlayerFromOpeningList(player);
                    } else {
                        MiscUtils.failedToTakeKey(player, crate);
                    }
                }
            }
        }

        //this.plugin.getCrazyCrates().getStarter().getEventLogger().logCrateEvent(player, crate, keyType, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
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
     * The time in seconds a quadcrate will last before kicking the player.
     *
     * @return The time in seconds till kick.
     */
    public int getQuadCrateTimer() {
        return this.quadCrateTimer;
    }

    /**
     * Add a new physical crate location.
     *
     * @param location The location you wish to add.
     * @param crate The crate which you would like to set it to.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public void addCrateLocation(Location location, Crate crate) {
        this.plugin.getCrateManager().addCrateLocation(location, crate);
    }

    /**
     * Remove a physical crate location.
     *
     * @param id The id of the location.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public void removeCrateLocation(String id) {
        this.plugin.getCrateManager().removeCrateLocation(id);
    }

    /**
     * Get a list of all the broke physical crate locations.
     *
     * @return List of broken crate locations.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public List<BrokeLocation> getBrokeCrateLocations() {
        return this.plugin.getCrazyManager().getBrokeCrateLocations();
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public void loadCrates() {
        this.plugin.getCrateManager().loadCrates();
    }

    @Deprecated(since = "1.16", forRemoval = true)
    public void pickPrize(Player player, Crate crate, Prize prize) {
        this.plugin.getCrazyHandler().getPrizeManager().pickPrize(player, crate, prize);
    }

    /**
     * A list of all the physical crate locations.
     *
     * @return list of locations.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public ArrayList<CrateLocation> getCrateLocations() {
        return new ArrayList<>(this.plugin.getCrateManager().getCrateLocations());
    }

    /**
     * A map of all the schematic locations
     *
     * @return map of schematic locations.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public HashMap<UUID, Location[]> getSchematicLocations() {
        return this.plugin.getCrazyManager().getSchematicLocations();
    }

    /**
     * Checks to see if the location is a physical crate.
     *
     * @param loc The location you are checking.
     * @return true if it is a physical crate and false if not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean isCrateLocation(Location loc) {
        return this.plugin.getCrateManager().isCrateLocation(loc);
    }

    /**
     * Gets the physical crate of the location.
     *
     * @param loc The location you are checking.
     * @return a CrateLocation if the location is a physical crate otherwise null if not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public CrateLocation getCrateLocation(Location loc) {
        return this.plugin.getCrateManager().getCrateLocation(loc);
    }

    /**
     * Get a list of broken crates.
     *
     * @return An ArrayList of all the broken crates.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public List<String> getBrokeCrates() {
        return this.plugin.getCrateManager().getBrokeCrates();
    }

    /**
     * Get a list of all the crates loaded into the plugin.
     *
     * @return an ArrayList of all the loaded crates.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public List<Crate> getCrates() {
        return this.plugin.getCrateManager().getCrates();
    }

    /**
     * Get a crate by its name.
     *
     * @param name The name of the crate you wish to grab.
     *
     * @return returns a Crate object of the crate it found and if none are found it returns null.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public Crate getCrateFromName(String name) {
        return this.plugin.getCrateManager().getCrateFromName(name);
    }

    /**
     * Give keys to an offline player.
     *
     * @param player The offline player you wish to give keys to.
     * @param crate The Crate of which key you are giving to the player.
     * @param keys The amount of keys you wish to give to the player.
     *
     * @return returns true if it successfully gave the offline player a key and false if there was an error.
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
     *
     * @return returns true if it took the keys and false if an error occurred.
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
     * Check if an item is a key for a crate.
     *
     * @param item The item you are checking.
     * @return true if the item is a key and false if it is not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean isKey(ItemStack item) {
        return this.plugin.getCrateManager().isKey(item);
    }

    /**
     * Get a Crate from a key ItemStack the player.
     *
     * @param item The key ItemStack you are checking.
     * @return returns a Crate if is a key from a crate otherwise null if it is not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public Crate getCrateFromKey(ItemStack item) {
        return this.plugin.getCrateManager().getCrateFromKey(item);
    }

    /**
     * Check if a key is from a specific Crate.
     *
     * @param item The key ItemStack you are checking.
     * @param crate The Crate you are checking.
     *
     * @return returns true if it belongs to that Crate and false if it does not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean isKeyFromCrate(ItemStack item, Crate crate) {
        return this.plugin.getCrateManager().isKeyFromCrate(item, crate);
    }

    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     *
     * @param player The player being checked.
     * @param crate The crate that has the key you are checking.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     *
     * @return true if they have the key and false if not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean hasPhysicalKey(Player player, Crate crate, boolean checkHand) {
        return this.plugin.getCrazyHandler().getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), checkHand);
    }

    /**
     * Get the amount of virtual keys a player has.
     *
     * @param player The player you are checking.
     *
     * @return the amount of virtual keys they own.
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
     *
     * @return the amount of virtual keys the player by that name has.
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
     *
     * @return returns true if successfully taken keys and false if not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean takeKeys(int amount, Player player, Crate crate, com.badbones69.crazycrates.api.enums.types.KeyType keyType, boolean checkHand) {
        return this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, player.getUniqueId(), crate.getName(), KeyType.getFromName(keyType.getName().toLowerCase()), checkHand);
    }

    /**
     * Add a key to a player.
     *
     * @param amount The amount of keys you wish to add.
     * @param player The player you wish to add keys to.
     * @param crate The crate key you are adding.
     */
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
    @Deprecated(since = "1.16", forRemoval = true)
    public void setNewPlayerKeys(Player player) {
        this.plugin.getCrateManager().setNewPlayerKeys(player);
    }

    /**
     * Get the list of all the schematics currently loaded onto the server.
     *
     * @return The list of all loaded schematics.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public List<CrateSchematic> getCrateSchematics() {
        return this.plugin.getCrateManager().getCrateSchematics();
    }

    /**
     * Get a schematic based on its name.
     *
     * @param name The name of the schematic.
     *
     * @return returns the CrateSchematic otherwise returns null if not found.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public CrateSchematic getCrateSchematic(String name) {
        return this.plugin.getCrateManager().getCrateSchematic(name);
    }

    /**
     * Check if an entity is a display reward for a crate.
     *
     * @param entity Entity you wish to check.
     *
     * @return true if it is a display reward item and false if not.
     */
    @Deprecated(since = "1.16", forRemoval = true)
    public boolean isDisplayReward(Entity entity) {
        return this.plugin.getCrateManager().isDisplayReward(entity);
    }
}