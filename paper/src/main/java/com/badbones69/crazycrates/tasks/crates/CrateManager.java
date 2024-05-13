package com.badbones69.crazycrates.tasks.crates;

import ch.jalu.configme.SettingsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.enums.DataFiles;
import com.badbones69.crazycrates.api.objects.other.BrokeLocation;
import com.badbones69.crazycrates.api.ChestManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.support.holograms.types.FancyHologramsSupport;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.types.CasinoCrate;
import com.badbones69.crazycrates.tasks.crates.types.CosmicCrate;
import com.badbones69.crazycrates.tasks.crates.types.CrateOnTheGo;
import com.badbones69.crazycrates.tasks.crates.types.CsgoCrate;
import com.badbones69.crazycrates.tasks.crates.types.FireCrackerCrate;
import com.badbones69.crazycrates.tasks.crates.types.QuadCrate;
import com.badbones69.crazycrates.tasks.crates.types.QuickCrate;
import com.badbones69.crazycrates.tasks.crates.types.RouletteCrate;
import com.badbones69.crazycrates.tasks.crates.types.WarCrate;
import com.badbones69.crazycrates.tasks.crates.types.WheelCrate;
import com.badbones69.crazycrates.tasks.crates.types.WonderCrate;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import com.ryderbelserion.vital.common.configuration.YamlManager;
import com.ryderbelserion.vital.common.configuration.objects.CustomFile;
import com.ryderbelserion.vital.common.util.FileUtil;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import us.crazycrew.crazycrates.api.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.support.holograms.types.CMIHologramsSupport;
import com.badbones69.crazycrates.support.holograms.types.DecentHologramsSupport;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;

public class CrateManager {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private @NotNull final InventoryManager inventoryManager = this.plugin.getInventoryManager();
    private @NotNull final YamlManager yamlManager = ConfigManager.getYamlManager();

    private final List<CrateLocation> crateLocations = new ArrayList<>();
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();
    private final List<BrokeLocation> brokeLocations = new ArrayList<>();
    private final Map<UUID, Location> cratesInUse = new HashMap<>();
    private final List<String> brokeCrates = new ArrayList<>();
    private final List<Crate> crates = new ArrayList<>();

    private HologramManager holograms;

    private boolean giveNewPlayersKeys;

    /**
     * Reloads an individual crate.
     *
     * @param crate the crate object.
     */
    public void reloadCrate(@Nullable final Crate crate) {
        try {
            // If crate null, return.
            if (crate == null) return;

            // Grab the new file.
            FileConfiguration file = crate.getFile();

            // Close previews
            this.plugin.getServer().getOnlinePlayers().forEach(this.inventoryManager::closeCratePreview);

            // Purge the crate stuff
            crate.purge();

            // Profit?
            List<Prize> prizes = new ArrayList<>();

            ConfigurationSection prizesSection = file.getConfigurationSection("Crate.Prizes");

            if (prizesSection != null) {
                for (String prize : prizesSection.getKeys(false)) {
                    ConfigurationSection prizeSection = prizesSection.getConfigurationSection(prize);

                    List<Tier> tierPrizes = new ArrayList<>();

                    if (prizeSection != null) {
                        for (String tier : prizeSection.getStringList("Tiers")) {
                            for (Tier key : crate.getTiers()) {
                                if (key.getName().equalsIgnoreCase(tier)) {
                                    tierPrizes.add(key);
                                }
                            }
                        }

                        Prize alternativePrize = null;

                        ConfigurationSection alternativeSection = prizeSection.getConfigurationSection("Alternative-Prize");

                        if (alternativeSection != null) {
                            boolean isEnabled = alternativeSection.getBoolean("Toggle");

                            if (isEnabled) {
                                alternativePrize = new Prize(prizeSection.getString("DisplayName", "<lang:item.minecraft." + prizeSection.getString("DisplayItem", "stone") + ">"), prizeSection.getName(), alternativeSection);
                            }
                        }

                        prizes.add(new Prize(
                                prizeSection,
                                tierPrizes,
                                crate.getName(),
                                alternativePrize
                        ));
                    }
                }
            }

            crate.setPrize(prizes);
            crate.setPreviewItems(crate.getPreviewItems());

            for (UUID uuid : this.plugin.getInventoryManager().getViewers()) {
                final Player player = this.plugin.getServer().getPlayer(uuid);

                if (player != null) {
                    this.inventoryManager.openNewCratePreview(player, crate);
                }
            }

            this.inventoryManager.purge();
        } catch (Exception exception) {
            this.brokeCrates.add(crate.getName());
            this.plugin.getLogger().log(Level.WARNING, "There was an error while loading the " + crate.getName() + ".yml file.", exception);
        }
    }

    /**
     * Load the holograms.
     */
    public void loadHolograms() {
        if (this.holograms != null) {
            return;
        }
      
        if (Support.decent_holograms.isEnabled()) {
            this.holograms = new DecentHologramsSupport();

            if (MiscUtils.isLogging()) this.plugin.getLogger().info("DecentHolograms support has been enabled.");
        } else if (Support.cmi.isEnabled() && CMIModule.holograms.isEnabled()) {
            this.holograms = new CMIHologramsSupport();

            if (MiscUtils.isLogging()) this.plugin.getLogger().info("CMI Hologram support has been enabled.");
        } else if (Support.fancy_holograms.isEnabled()) {
            this.holograms = new FancyHologramsSupport();

            if (MiscUtils.isLogging()) this.plugin.getLogger().info("FancyHolograms support has been enabled.");
        } else {
            if (MiscUtils.isLogging()) {
                List.of(
                        "There was no hologram plugin found on the server. If you are using CMI",
                        "Please make sure you enabled the hologram module in modules.yml",
                        "You can run /crazycrates reload if using CMI otherwise restart your server."
                ).forEach(this.plugin.getLogger()::warning);
            }
        }
    }

    /**
     * @return a list of crate names.
     */
    public List<String> getCrateNames() {
        return new ArrayList<>(this.plugin.getInstance().getCrateFiles());
    }

    /**
     * Loads the crates.
     */
    public void loadCrates() {
        if (ConfigManager.getConfig().getProperty(ConfigKeys.update_examples_folder)) {
            Path path = this.plugin.getDataFolder().toPath();
            Class<? extends @NotNull CrazyCrates> classObject = this.plugin.getClass();

            FileUtil.extracts(classObject, "/crates/", path.resolve("examples").resolve("crates"), true);
            FileUtil.extracts(classObject, "/schematics/", path.resolve("examples").resolve("schematics"), true);

            List.of(
                    "config.yml",
                    "data.yml",
                    "locations.yml",
                    "messages.yml"
            ).forEach(file -> FileUtil.extract(classObject, file, path.resolve("examples"), true));
        }

        this.giveNewPlayersKeys = false;

        purge();

        // Removes all holograms so that they can be replaced.
        if (this.holograms != null) {
            this.holograms.removeAllHolograms(false);
        }

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("Loading all crate information...");

        for (final String crateName : getCrateNames()) {
            try {
                final @Nullable CustomFile customFile = this.yamlManager.getCustomFile(crateName);

                if (customFile == null) return;

                this.plugin.getLogger().warning("Custom File: " + customFile.getFileName());

                final YamlFile file = customFile.getYamlFile();

                final CrateType crateType = CrateType.getFromName(file.getString("Crate.CrateType", "CSGO"));

                final List<Prize> prizes = new ArrayList<>();
                final List<Tier> tiers = new ArrayList<>();

                final String previewName = file.contains("Crate.Preview-Name") ? file.getString("Crate.Preview-Name", "") : file.getString("Crate.CrateName", "");

                final int maxMassOpen = file.getInt("Crate.Max-Mass-Open", 10);
                final int requiredKeys = file.getInt("Crate.RequiredKeys", 0);

                ConfigurationSection section = file.getConfigurationSection("Crate.Tiers");

                if (file.contains("Crate.Tiers") && section != null) {
                    for (String tier : section.getKeys(false)) {
                        final String path = "Crate.Tiers." + tier;

                        final ConfigurationSection tierSection = file.getConfigurationSection(path);

                        if (tierSection != null) {
                            tiers.add(new Tier(tier, tierSection));
                        }
                    }
                }

                final boolean isTiersEmpty = crateType == CrateType.cosmic || crateType == CrateType.casino;

                if (isTiersEmpty && tiers.isEmpty()) {
                    this.brokeCrates.add(crateName);

                    if (MiscUtils.isLogging()) this.plugin.getLogger().warning("No tiers were found for " + crateName + ".yml file.");

                    continue;
                }

                final ConfigurationSection prizesSection = file.getConfigurationSection("Crate.Prizes");

                if (prizesSection != null) {
                    for (String prize : prizesSection.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizesSection.getConfigurationSection(prize);

                        final List<Tier> tierPrizes = new ArrayList<>();

                        Prize alternativePrize = null;

                        if (prizeSection != null) {
                            for (String tier : prizeSection.getStringList("Tiers")) {
                                for (Tier key : tiers) {
                                    if (key.getName().equalsIgnoreCase(tier)) {
                                        tierPrizes.add(key);
                                    }
                                }
                            }

                            final ConfigurationSection alternativeSection = prizeSection.getConfigurationSection("Alternative-Prize");

                            if (alternativeSection != null) {
                                final boolean isEnabled = alternativeSection.getBoolean("Toggle");

                                if (isEnabled) {
                                    alternativePrize = new Prize(prizeSection.getString("DisplayName", WordUtils.capitalizeFully(prizeSection.getString("DisplayItem", "Stone")
                                            .replaceAll("_", " "))), prizeSection.getName(), alternativeSection);
                                }
                            }

                            prizes.add(new Prize(prizeSection, tierPrizes, crateName, alternativePrize));
                        }
                    }
                }

                final int newPlayersKeys = file.getInt("Crate.StartingKeys", 0);

                if (!this.giveNewPlayersKeys) {
                    if (newPlayersKeys > 0) this.giveNewPlayersKeys = true;
                }

                final List<String> prizeMessage = file.contains("Crate.Prize-Message") ? file.getStringList("Crate.Prize-Message") : Collections.emptyList();

                final List<String> prizeCommands = file.contains("Crate.Prize-Commands") ? file.getStringList("Crate.Prize-Commands") : Collections.emptyList();

                final CrateHologram holo = new CrateHologram(file.getBoolean("Crate.Hologram.Toggle"), file.getDouble("Crate.Hologram.Height", 0.0), file.getInt("Crate.Hologram.Range", 8), file.getString("Crate.Hologram.Color", "transparent"), file.getStringList("Crate.Hologram.Message"));
                addCrate(new Crate(crateName, previewName, crateType, getKey(file), file.getString("Crate.PhysicalKey.Name", "Crate.PhysicalKey.Name is missing from " + crateName + ".yml"), prizes, file, newPlayersKeys, tiers, maxMassOpen, requiredKeys, prizeMessage, prizeCommands, holo));

                final Permission doesExist = this.plugin.getServer().getPluginManager().getPermission("crazycrates.open." + crateName);

                if (doesExist == null) {
                    Permission permission = new Permission(
                            "crazycrates.open." + crateName,
                            "Allows you to open " + crateName,
                            PermissionDefault.TRUE
                    );

                    this.plugin.getServer().getPluginManager().addPermission(permission);
                }
            } catch (Exception exception) {
                this.brokeCrates.add(crateName);
                this.plugin.getLogger().log(Level.WARNING, "There was an error while loading the " + crateName + ".yml file.", exception);
            }
        }

        addCrate(new Crate("Menu"));

        if (MiscUtils.isLogging()) {
            List.of(
                    "All crate information has been loaded.",
                    "Loading all the physical crate locations."
            ).forEach(line -> this.plugin.getLogger().info(line));
        }

        final YamlFile locations = DataFiles.locations.getYamlFile();

        int loadedAmount = 0;
        int brokeAmount = 0;

        final ConfigurationSection section = locations.getConfigurationSection("Locations");

        if (section != null) {
            for (final String locationName : section.getKeys(false)) {
                try {
                    final String worldName = locations.getString("Locations." + locationName + ".World");

                    // If name is null, we return.
                    if (worldName == null) return;

                    // If name is empty or blank, we return.
                    if (worldName.isEmpty() || worldName.isBlank()) return;

                    final World world = this.plugin.getServer().getWorld(worldName);
                    final int x = locations.getInt("Locations." + locationName + ".X");
                    final int y = locations.getInt("Locations." + locationName + ".Y");
                    final int z = locations.getInt("Locations." + locationName + ".Z");
                    final Location location = new Location(world, x, y, z);
                    final Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));

                    if (world != null && crate != null) {
                        this.crateLocations.add(new CrateLocation(locationName, crate, location));

                        if (this.holograms != null) {
                            this.holograms.createHologram(location, crate);
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
        if (MiscUtils.isLogging()) {
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
        final String[] schems = new File(this.plugin.getDataFolder() + "/schematics/").list();

        if (schems != null) {
            for (final String schematicName : schems) {
                if (schematicName.endsWith(".nbt")) {
                    this.crateSchematics.add(new CrateSchematic(schematicName, new File(plugin.getDataFolder() + "/schematics/" + schematicName)));

                    if (MiscUtils.isLogging()) this.plugin.getLogger().info(schematicName + " was successfully found and loaded.");
                }
            }
        }

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("All schematics were found and loaded.");

        cleanDataFile();

        this.inventoryManager.loadButtons();
    }

    // The crate that the player is opening.
    private final Map<UUID, Crate> playerOpeningCrates = new HashMap<>();

    // Keys that are being used in crates. Only needed in cosmic due to it taking the key after the player picks a prize and not in a start method.
    private final Map<UUID, KeyType> playerKeys = new HashMap<>();

    // A list of all current crate tasks that are running that a time. Used to force stop any crates it needs to.
    private final Map<UUID, ScheduledTask> currentTasks = new HashMap<>();

    private final Map<UUID, TimerTask> timerTasks = new HashMap<>();

    // A list of tasks being run by the QuadCrate type.
    private final Map<UUID, List<ScheduledTask>> currentQuadTasks = new HashMap<>();

    /**
     * Opens a crate for a player.
     *
     * @param player the player that is having the crate opened for them.
     * @param crate the crate that is being used.
     * @param location the location that may be needed for some crate types.
     * @param checkHand if it just checks the players hand or if it checks their inventory.
     */
    public void openCrate(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, @NotNull final Location location, final boolean virtualCrate, final boolean checkHand) {
        final SettingsManager config = ConfigManager.getConfig();

        if (crate.getCrateType() == CrateType.menu) {
            if (config.getProperty(ConfigKeys.enable_crate_menu)) {
                final CrateMainMenu crateMainMenu = new CrateMainMenu(player, config.getProperty(ConfigKeys.inventory_name), config.getProperty(ConfigKeys.inventory_size));

                player.openInventory(crateMainMenu.build().getInventory());

                return;
            }

            player.sendRichMessage(Messages.feature_disabled.getMessage(player));

            return;
        }

        CrateBuilder crateBuilder;

        switch (crate.getCrateType()) {
            case csgo -> crateBuilder = new CsgoCrate(crate, player, 27);
            case casino -> crateBuilder = new CasinoCrate(crate, player, 27);
            case wonder -> crateBuilder = new WonderCrate(crate, player, 45);
            case wheel -> crateBuilder = new WheelCrate(crate, player, 54);
            case roulette -> crateBuilder = new RouletteCrate(crate, player, 45);
            case war -> crateBuilder = new WarCrate(crate, player, 9);
            case cosmic -> crateBuilder = new CosmicCrate(crate, player, 27);
            case quad_crate -> {
                if (virtualCrate) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("{cratetype}", crate.getCrateType().getName());
                    placeholders.put("{crate}", crate.getName());

                    player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

                    removePlayerFromOpeningList(player);

                    return;
                }

                crateBuilder = new QuadCrate(crate, player, location);
            }

            case fire_cracker -> {
                if (this.cratesInUse.containsValue(location)) {
                    player.sendRichMessage(Messages.crate_in_use.getMessage(player, "{crate}", crate.getName()));

                    removePlayerFromOpeningList(player);

                    return;
                }

                if (virtualCrate) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("{cratetype}", crate.getCrateType().getName());
                    placeholders.put("{crate}", crate.getName());

                    player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

                    removePlayerFromOpeningList(player);

                    return;
                }

                crateBuilder = new FireCrackerCrate(crate, player, 45, location);
            }

            case crate_on_the_go -> {
                if (virtualCrate) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("{cratetype}", crate.getCrateType().getName());
                    placeholders.put("{crate}", crate.getName());

                    player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

                    removePlayerFromOpeningList(player);

                    return;
                }

                crateBuilder = new CrateOnTheGo(crate, player);
            }

            case quick_crate -> {
                if (this.cratesInUse.containsValue(location)) {
                    player.sendRichMessage(Messages.crate_in_use.getMessage(player, "{crate}", crate.getName()));

                    removePlayerFromOpeningList(player);

                    return;
                }

                if (virtualCrate) {
                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("{cratetype}", crate.getCrateType().getName());
                    placeholders.put("{crate}", crate.getName());

                    player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

                    removePlayerFromOpeningList(player);

                    return;
                }

                crateBuilder = new QuickCrate(crate, player, location);
            }

            default -> {
                crateBuilder = new CsgoCrate(crate, player, 27);

                if (MiscUtils.isLogging()) {
                    List.of(
                            crate.getName() + " has an invalid crate type. Your Value: " + crate.getFile().getString("Crate.CrateType", "CSGO"),
                            "We will use " + CrateType.csgo.getName() + " until you change the crate type.",
                            "Valid Crate Types: CSGO/Casino/Cosmic/QuadCrate/QuickCrate/Roulette/CrateOnTheGo/FireCracker/Wonder/Wheel/War"
                    ).forEach(line -> this.plugin.getLogger().warning(line));
                }
            }
        }

        // Open the crate.
        crateBuilder.open(keyType, checkHand);
    }

    /**
     * Adds a crate in use for when a player opens a crate.
     *
     * @param player the player opening the crate.
     * @param location the location the crate is at.
     */
    public void addCrateInUse(@NotNull final Player player, @NotNull final Location location) {
        this.cratesInUse.put(player.getUniqueId(), location);
    }

    /**
     * @param player the player attempting to open a crate.
     * @return the location of the crate in use.
     */
    public Location getCrateInUseLocation(@NotNull final Player player) {
        return this.cratesInUse.get(player.getUniqueId());
    }

    /**
     * @param player the player attempting to open a crate.
     * @return true or false.
     */
    public boolean isCrateInUse(@NotNull final Player player) {
        return this.cratesInUse.containsKey(player.getUniqueId());
    }

    /**
     * Removes a crate in use.
     *
     * @param player the player finishing a crate.
     */
    public void removeCrateInUse(@NotNull final Player player) {
        this.cratesInUse.remove(player.getUniqueId());
    }

    /**
     * @return hashmap of crates in use.
     */
    public Map<UUID, Location> getCratesInUse() {
        return Collections.unmodifiableMap(this.cratesInUse);
    }

    /**
     * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
     *
     * @param player the player that the crate is being ended for.
     */
    public void endCrate(@NotNull final Player player) {
        if (this.currentTasks.containsKey(player.getUniqueId())) {
            this.currentTasks.get(player.getUniqueId()).cancel();
        }
    }

    /**
     * Ends the tasks running by a player.
     *
     * @param player the player using the crate.
     */
    public void endQuadCrate(@NotNull final Player player) {
        if (this.currentQuadTasks.containsKey(player.getUniqueId())) {
            for (ScheduledTask task : this.currentQuadTasks.get(player.getUniqueId())) {
                task.cancel();
            }

            this.currentQuadTasks.remove(player.getUniqueId());
        }
    }

    /**
     * Add a quad crate task that is going on for a player.
     *
     * @param player the player opening the crate.
     * @param task the task of the quad crate.
     */
    public void addQuadCrateTask(@NotNull final Player player, @NotNull final ScheduledTask task) {
        if (!this.currentQuadTasks.containsKey(player.getUniqueId())) {
            this.currentQuadTasks.put(player.getUniqueId(), new ArrayList<>());
        }

        this.currentQuadTasks.get(player.getUniqueId()).add(task);
    }

    /**
     * Checks to see if the player has a quad crate task going on.
     *
     * @param player player that is being checked.
     * @return true if they do have a task and false if not.
     */
    public boolean hasQuadCrateTask(@NotNull final Player player) {
        return this.currentQuadTasks.containsKey(player.getUniqueId());
    }

    /**
     * Add a crate task that is going on for a player.
     *
     * @param player player opening the crate.
     * @param task task of the crate.
     */
    public void addCrateTask(@NotNull final Player player, @NotNull final ScheduledTask task) {
        this.currentTasks.put(player.getUniqueId(), task);
    }

    /**
     * Adds a repeating timer task for a player opening a crate.
     *
     * @param player player opening the crate.
     * @param task task of the crate.
     * @param delay delay before running the task.
     * @param period interval between task runs.
     */
    public void addRepeatingCrateTask(@NotNull final Player player, @NotNull final TimerTask task, final long delay, final long period) {
        this.timerTasks.put(player.getUniqueId(), task);

        this.plugin.getTimer().scheduleAtFixedRate(task, delay, period);
    }

    /**
     * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
     *
     * @param player player that the crate is being ended for.
     */
    public void removeCrateTask(@NotNull final Player player) {
        this.timerTasks.remove(player.getUniqueId()).cancel();
    }

    /**
     * Adds a timer task for a player opening a crate.
     *
     * @param player player opening the crate.
     * @param task task of the crate.
     * @param delay delay before running the task.
     */
    public void addCrateTask(Player player, TimerTask task, Long delay) {
        this.timerTasks.put(player.getUniqueId(), task);

        this.plugin.getTimer().schedule(task, delay);
    }

    /**
     * Gets a crate task that is on going for a player.
     *
     * @param player the player opening the crate.
     * @return the task of the crate.
     */
    public final ScheduledTask getCrateTask(@NotNull final Player player) {
        return this.currentTasks.get(player.getUniqueId());
    }

    /**
     * Checks to see if the player has a crate task going on.
     *
     * @param player the player that is being checked.
     * @return true if they do have a task and false if not.
     */
    public final boolean hasCrateTask(@NotNull final Player player) {
        return this.currentTasks.containsKey(player.getUniqueId());
    }

    /**
     * Add a player to the list of players that are currently opening crates.
     *
     * @param player the player that is opening a crate.
     * @param crate the crate the player is opening.
     */
    public void addPlayerToOpeningList(@NotNull final Player player, @NotNull final Crate crate) {
        this.playerOpeningCrates.put(player.getUniqueId(), crate);
    }

    /**
     * Remove a player from the list of players that are opening crates.
     *
     * @param player the player that has finished opening a crate.
     */
    public void removePlayerFromOpeningList(@NotNull final Player player) {
        this.playerOpeningCrates.remove(player.getUniqueId());
    }

    /**
     * Check if a player is opening a crate.
     *
     * @param player the player you are checking.
     * @return true if they are opening a crate and false if they are not.
     */
    public boolean isInOpeningList(@NotNull final Player player) {
        return this.playerOpeningCrates.containsKey(player.getUniqueId());
    }

    /**
     * Get the crate the player is currently opening.
     *
     * @param player the player you want to check.
     * @return the Crate of which the player is opening. May return null if no crate found.
     */
    public final Crate getOpeningCrate(@NotNull final Player player) {
        return this.playerOpeningCrates.get(player.getUniqueId());
    }

    /**
     * Set the type of key the player is opening a crate for.
     * This is only used in the Cosmic CrateType currently.
     *
     * @param player the player that is opening the crate.
     * @param keyType the KeyType that they are using.
     */
    public void addPlayerKeyType(@NotNull final Player player, @NotNull final KeyType keyType) {
        this.playerKeys.put(player.getUniqueId(), keyType);
    }

    /**
     * Remove the player from the list as they have finished the crate.
     * Currently, only used in the Cosmic CrateType.
     *
     * @param player the player you are removing.
     */
    public void removePlayerKeyType(@NotNull final Player player) {
        this.playerKeys.remove(player.getUniqueId());
    }

    /**
     * Check if the player is in the list.
     *
     * @param player the player you are checking.
     * @return true if they are in the list and false if not.
     */
    public final boolean hasPlayerKeyType(@NotNull final Player player) {
        return this.playerKeys.containsKey(player.getUniqueId());
    }

    /**
     * The key type the player's current crate is using.
     *
     * @param player the player that is using the crate.
     * @return the key type of the crate the player is using.
     */
    public @Nullable final KeyType getPlayerKeyType(@NotNull final Player player) {
        return this.playerKeys.get(player.getUniqueId());
    }

    /**
     * Nukes all data.
     */
    public void purge() {
        this.crates.clear();
        this.brokeCrates.clear();
        this.crateLocations.clear();
        this.crateSchematics.clear();
    }

    /**
     * Set a new player's default amount of keys.
     *
     * @param player the player that has just joined.
     */
    public void setNewPlayerKeys(@NotNull final Player player) {
        if (this.giveNewPlayersKeys) { // Checks if any crate gives new players keys and if not then no need to do all this stuff.
            final String uuid = player.getUniqueId().toString();

            if (!player.hasPlayedBefore()) {
                getUsableCrates().stream()
                        .filter(Crate :: doNewPlayersGetKeys)
                        .forEach(crate -> {
                            //Files.data.getFile(this.fileManager).set("Players." + uuid + "." + crate.getName(), crate.getNewPlayerKeys());
                            //Files.data.save(this.fileManager);
                        });
            }
        }
    }

    /**
     * Adds a crate to the arraylist.
     *
     * @param crate crate object.
     */
    public void addCrate(@NotNull final Crate crate) {
        this.crates.add(crate);
    }

    public void addLocation(@NotNull final CrateLocation crateLocation) {
        this.crateLocations.add(crateLocation);
    }

    /**
     * Removes a crate from the arraylist.
     *
     * @param crate crate object
     */
    public void removeCrate(@NotNull final Crate crate) {
        this.crates.remove(crate);
    }

    /**
     * @return true if the arraylist has a crate object otherwise false.
     */
    public boolean hasCrate(@NotNull final Crate crate) {
        return this.crates.contains(crate);
    }

    /**
     * Add a new physical crate location.
     *
     * @param location the location you wish to add.
     * @param crate the crate which you would like to set it to.
     */
    public void addCrateLocation(@NotNull final Location location, @NotNull final Crate crate) {
        final YamlFile locations = DataFiles.locations.getYamlFile();
        String id = "1"; // Location ID

        for (int i = 1; locations.contains("Locations." + i); i++) {
            id = (i + 1) + "";
        }

        for (final CrateLocation crateLocation : getCrateLocations()) {
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

        DataFiles.locations.save();

        addLocation(new CrateLocation(id, crate, location));

        if (this.holograms != null) this.holograms.createHologram(location, crate);
    }

    /**
     * Remove a physical crate location.
     *
     * @param id the id of the location.
     */
    public void removeCrateLocation(@NotNull final String id) {
        DataFiles.locations.getYamlFile().set("Locations." + id, null);
        DataFiles.locations.save();

        CrateLocation location = null;

        for (final CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getID().equalsIgnoreCase(id)) {
                location = crateLocation;

                break;
            }
        }

        if (location != null) {
            removeLocation(location);

            if (this.holograms != null) this.holograms.removeHologram(location.getLocation());
        }
    }

    /**
     * @return an unmodifiable list of crate objects.
     */
    public @NotNull final List<Crate> getUsableCrates() {
        List<Crate> crateList = new ArrayList<>(this.crates);

        crateList.removeIf(crate -> crate.getCrateType() == CrateType.menu);

        return crateList;
    }

    /**
     * @return an unmodifiable list of crate objects.
     */
    public @NotNull final List<Crate> getCrates() {
        return Collections.unmodifiableList(this.crates);
    }

    /**
     * Gets a crate object using the crate name.
     *
     * @param name name of the crate.
     * @return the crate object.
     */
    public @Nullable final Crate getCrateFromName(@Nullable final String name) {
        if (name == null) return null;
        if (name.isEmpty()) return null;

        Crate crate = null;

        for (Crate key : this.crates) {
            if (key.getName().equalsIgnoreCase(name)) {
                crate = key;

                break;
            }
        }

        return crate;
    }

    /**
     * Checks to see if the location is a physical crate.
     *
     * @param location location you are checking.
     * @return true if it is a physical crate and false if not.
     */
    public final boolean isCrateLocation(@NotNull final Location location) {
        for (final CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getLocation().equals(location)) return true;
        }

        return false;
    }

    /**
     * Check if an item is a key for a crate.
     *
     * @param item the item you are checking.
     * @return true if the item is a key and false if it is not.
     */
    public final boolean isKey(@NotNull final ItemStack item) {
        return getCrateFromKey(item) != null;
    }

    /**
     * Get a Crate from a key ItemStack the player.
     *
     * @param item the key ItemStack you are checking.
     * @return a crate if is a key from a crate otherwise null if it is not.
     */
    public @Nullable final Crate getCrateFromKey(@NotNull final ItemStack item) {
        if (!item.hasItemMeta()) return null;

        ItemMeta itemMeta = item.getItemMeta();

        // If null, return.
        if (itemMeta == null) return null;

        if (!itemMeta.getPersistentDataContainer().has(PersistentKeys.crate_key.getNamespacedKey())) {
            return getCrateNameFromOldKey(itemMeta);
        }

        String crateName = ItemUtils.getKey(itemMeta);

        return getCrateFromName(crateName);
    }

    /**
     * Gets the physical crate of the location.
     *
     * @param location location you are checking.
     * @return a crate location if the location is a physical crate otherwise null if not.
     */
    public @Nullable final CrateLocation getCrateLocation(@NotNull final Location location) {
        CrateLocation crateLocation = null;

        for (CrateLocation key : this.crateLocations) {
            if (key.getLocation().equals(location)) {
                crateLocation = key;

                break;
            }
        }

        return crateLocation;
    }

    public @Nullable final Crate getCrateFromLocation(@NotNull final Location location) {
        Crate crate = null;

        for (CrateLocation key : this.crateLocations) {
            if (key.getLocation().equals(location)) {
                crate = key.getCrate();

                break;
            }
        }

        return crate;
    }

    /**
     * Get a schematic based on its name.
     *
     * @param name the name of the schematic.
     * @return the CrateSchematic otherwise returns null if not found.
     */
    public @Nullable final CrateSchematic getCrateSchematic(@NotNull final String name) {
        if (name.isEmpty()) return null;

        for (CrateSchematic schematic : this.crateSchematics) {
            if (schematic.schematicName().equalsIgnoreCase(name)) {
                return schematic;
            }
        }

        return null;
    }

    /**
     * Check if an entity is a display reward for a crate.
     *
     * @param entity entity you wish to check.
     * @return true if it is a display reward item and false if not.
     */
    public final boolean isDisplayReward(@NotNull final Entity entity) {
        if (entity instanceof Item item) {
            ItemStack itemStack = item.getItemStack();

            if (itemStack.getType() == Material.AIR) return false;

            if (!itemStack.hasItemMeta()) return false;

            ItemMeta itemMeta = itemStack.getItemMeta();

            PersistentKeys prize = PersistentKeys.crate_prize;

            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            return container.has(prize.getNamespacedKey());
        }

        return false;
    }

    /**
     * Check if a key is from a specific Crate.
     *
     * @param item the key you are checking.
     * @param crate the crate you are checking.
     * @return true if it belongs to that Crate and false if it does not.
     */
    public final boolean isKeyFromCrate(@Nullable final ItemStack item, @Nullable final Crate crate) {
        // If the item is null for whatever reason.
        if (item == null) return false;

        // If crate is null, return.
        if (crate == null) return false;

        // If the crate type is menu, return.
        if (crate.getCrateType() == CrateType.menu) return false;

        // If the item type is AIR for whatever reason.
        if (item.getType() == Material.AIR) return false;

        // If the item has no meta.
        if (!item.hasItemMeta()) return false;

        // Get the item meta.
        final ItemMeta itemMeta = item.getItemMeta();

        // Check if it has key.
        final boolean hasNewCrateKey = itemMeta.getPersistentDataContainer().has(PersistentKeys.crate_key.getNamespacedKey());

        // If we find no value in PDC, it's likely a legacy key. It'll return false if it does not contain it.
        if (!hasNewCrateKey) {
            // Get the item meta as a string
            final String value = itemMeta.getAsString();

            final String[] sections = value.split(",");

            String pair = null;

            for (final String key : sections) {
                if (key.contains("CrazyCrates-Crate")) {
                    pair = key.trim().replaceAll("\\{", "").replaceAll("\"", "");

                    break;
                }
            }

            if (pair == null) {
                return false;
            }

            return crate.getName().equals(pair.split(":")[1]);
        }

        // Get the crate name.
        final String crateName = ItemUtils.getKey(itemMeta);

        return crate.getName().equals(crateName);
    }

    public @Nullable final Crate getCrateNameFromOldKey(final ItemMeta itemMeta) {
        if (itemMeta == null) return null;

        // Get the item meta as a string
        final String value = itemMeta.getAsString();

        final String[] sections = value.split(",");

        String pair = null;

        for (final String key : sections) {
            if (key.contains("CrazyCrates-Crate")) {
                pair = key.trim().replaceAll("\\{", "").replaceAll("\"", "");

                break;
            }
        }

        if (pair == null) return null;

        return getCrateFromName(pair.split(":")[1]);
    }

    /**
     * @return the hologram handler.
     */
    public @Nullable final HologramManager getHolograms() {
        return this.holograms;
    }

    /**
     * @return an unmodifiable list of crate locations.
     */
    public @NotNull final List<CrateLocation> getCrateLocations() {
        return Collections.unmodifiableList(this.crateLocations);
    }

    /**
     * Removes a crate location.
     *
     * @param crateLocation the location to remove.
     */
    public void removeLocation(@NotNull final CrateLocation crateLocation) {
        this.crateLocations.remove(crateLocation);
    }

    /**
     * @return an unmodifiable list of broke crates.
     */
    public @NotNull final List<String> getBrokeCrates() {
        return Collections.unmodifiableList(this.brokeCrates);
    }

    /**
     * @return an unmodifiable list of broken crate locations.
     */
    public @NotNull final List<BrokeLocation> getBrokeLocations() {
        return Collections.unmodifiableList(this.brokeLocations);
    }

    /**
     * Removes broken locations.
     *
     * @param crateLocation list of locations to remove.
     */
    public void removeBrokeLocation(@NotNull final List<BrokeLocation> crateLocation) {
        this.brokeLocations.removeAll(crateLocation);
    }

    /**
     * @return an unmodifiable list of crate schematics.
     */
    public @NotNull final List<CrateSchematic> getCrateSchematics() {
        return Collections.unmodifiableList(this.crateSchematics);
    }

    // Internal methods.
    private ItemBuilder getKey(@NotNull final FileConfiguration file) {
        final String name = file.getString("Crate.PhysicalKey.Name", "");
        final List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
        final String id = file.getString("Crate.PhysicalKey.Item", "tripwire_hook");
        final boolean glowing = file.getBoolean("Crate.PhysicalKey.Glowing", true);
        final boolean hideFlags = file.getBoolean("Crate.PhysicalKey.HideItemFlags", false);

        return new ItemBuilder().withType(id).setDisplayName(name).setDisplayLore(lore).setGlowing(glowing).setHidingItemFlags(hideFlags);
    }

    // Cleans the data file.
    private void cleanDataFile() {
        final YamlFile data = DataFiles.data.getYamlFile();

        if (!data.contains("Players")) return;

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("Cleaning up the data.yml file.");

        final List<String> removePlayers = new ArrayList<>();

        ConfigurationSection section = data.getConfigurationSection("Players");

        if (section == null) return;

        for (String uuid : section.getKeys(false)) {
            if (data.contains("Players." + uuid + ".tracking")) return;

            boolean hasKeys = false;
            final List<String> noKeys = new ArrayList<>();

            for (final Crate crate : getUsableCrates()) {
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
            if (MiscUtils.isLogging()) this.plugin.getLogger().info(removePlayers.size() + " player's data has been marked to be removed.");

            removePlayers.forEach(uuid -> data.set("Players." + uuid, null));

            if (MiscUtils.isLogging()) this.plugin.getLogger().info("All empty player data has been removed.");
        }

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("The data.yml file has been cleaned.");

        DataFiles.data.save();
    }

    // War Crate
    private final Map<UUID, Boolean> canPick = new HashMap<>();
    private final Map<UUID, Boolean> canClose = new HashMap<>();

    public void addPicker(@NotNull final Player player, final boolean value) {
        this.canPick.put(player.getUniqueId(), value);
    }

    public final boolean containsPicker(@NotNull final Player player) {
        return this.canPick.containsKey(player.getUniqueId());
    }

    public boolean isPicker(@NotNull final Player player) {
        return this.canPick.get(player.getUniqueId());
    }

    public void removePicker(@NotNull final Player player) {
        this.canPick.remove(player.getUniqueId());
    }

    public void addCloser(@NotNull final Player player, final boolean value) {
        this.canClose.put(player.getUniqueId(), value);
    }

    public final boolean containsCloser(@NotNull final Player player) {
        return this.canClose.containsKey(player.getUniqueId());
    }

    public void removeCloser(@NotNull final Player player) {
        this.canClose.remove(player.getUniqueId());
    }

    private final Map<UUID, Boolean> checkHands = new HashMap<>();

    public void addHands(@NotNull final Player player, final boolean checkHand) {
        this.checkHands.put(player.getUniqueId(), checkHand);
    }

    public void removeHands(@NotNull final Player player) {
        this.checkHands.remove(player.getUniqueId());
    }

    public final boolean getHand(@NotNull final Player player) {
        return this.checkHands.get(player.getUniqueId());
    }

    // QuickCrate/FireCracker
    private final List<Entity> allRewards = new ArrayList<>();
    private final Map<UUID, Entity> rewards = new HashMap<>();

    public void addReward(@NotNull final Player player, @NotNull final Entity entity) {
        this.allRewards.add(entity);

        this.rewards.put(player.getUniqueId(), entity);
    }

    public void endQuickCrate(@NotNull final Player player, @NotNull final Location location, @Nullable final Crate crate, final boolean useQuickCrateAgain) {
        if (hasCrateTask(player)) {
            getCrateTask(player).cancel();
            removeCrateTask(player);
        }

        if (this.rewards.get(player.getUniqueId()) != null) {
            this.allRewards.remove(this.rewards.get(player.getUniqueId()));

            this.rewards.get(player.getUniqueId()).remove();
            this.rewards.remove(player.getUniqueId());
        }

        ChestManager.closeChest(location.getBlock(), false);

        removeCrateInUse(player);
        removePlayerFromOpeningList(player);

        if (!useQuickCrateAgain) {
            if (crate != null && getHolograms() != null) getHolograms().createHologram(location, crate);
        }
    }

    public void purgeRewards() {
        if (!this.allRewards.isEmpty()) this.allRewards.stream().filter(Objects::nonNull).forEach(Entity::remove);
    }
}