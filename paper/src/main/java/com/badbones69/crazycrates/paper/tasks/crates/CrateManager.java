package com.badbones69.crazycrates.paper.tasks.crates;

import ch.jalu.configme.SettingsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.core.config.impl.EditorKeys;
import com.badbones69.crazycrates.paper.listeners.items.NexoInteractListener;
import com.badbones69.crazycrates.paper.listeners.items.OraxenInteractListener;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateHologram;
import com.badbones69.crazycrates.paper.api.objects.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.crates.BrokeLocation;
import com.badbones69.crazycrates.paper.api.ChestManager;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.support.holograms.types.CMIHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.types.DecentHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.types.FancyHologramsSupport;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.tasks.crates.types.CasinoCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.CosmicCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.CrateOnTheGo;
import com.badbones69.crazycrates.paper.tasks.crates.types.CsgoCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.FireCrackerCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.QuadCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.QuickCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.RouletteCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WarCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WheelCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WonderCrate;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.ryderbelserion.fusion.core.managers.files.FileType;
import com.ryderbelserion.fusion.core.utils.FileUtils;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.files.LegacyCustomFile;
import com.ryderbelserion.fusion.paper.files.LegacyFileManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Matrix4f;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.UUID;
import java.util.WeakHashMap;

public class CrateManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final InventoryManager inventoryManager = this.plugin.getInventoryManager();
    private final LegacyFileManager fileManager = this.plugin.getFileManager();

    private final ComponentLogger logger = this.plugin.getComponentLogger();
    private final Server server = this.plugin.getServer();

    private final List<CrateLocation> crateLocations = new ArrayList<>();
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();
    private final List<BrokeLocation> brokeLocations = new ArrayList<>();
    private final Map<UUID, Location> cratesInUse = new HashMap<>();
    private final List<String> brokeCrates = new ArrayList<>();
    private final List<Crate> crates = new ArrayList<>();

    private final Map<UUID, Crate> crateEditors = new HashMap<>();

    public void addEditorCrate(@NotNull final Player player, @NotNull final Crate crate) {
        this.crateEditors.put(player.getUniqueId(), crate);
    }

    public void removeEditorCrate(@NotNull final Player player) {
        this.crateEditors.remove(player.getUniqueId());
    }

    public boolean hasEditorCrate(@NotNull final Player player) {
        return this.crateEditors.containsKey(player.getUniqueId());
    }

    public @Nullable Crate getEditorCrate(@NotNull final Player player) {
        return this.crateEditors.getOrDefault(player.getUniqueId(), null);
    }

    private final Map<UUID, Map<Integer, Tier>> tiers = new WeakHashMap<>();

    public void addTier(@NotNull final Player player, final int slot, final Tier tier) {
        final UUID uuid = player.getUniqueId();

        if (this.tiers.containsKey(uuid)) {
            this.tiers.get(uuid).put(slot, tier);

            return;
        }

        this.tiers.put(uuid, new WeakHashMap<>() {{
            put(slot, tier);
        }});
    }

    public void removeTier(@NotNull final Player player) {
        this.tiers.remove(player.getUniqueId());
    }

    public final Tier getTier(@NotNull final Player player, final int slot) {
        return this.tiers.get(player.getUniqueId()).get(slot);
    }

    public Map<UUID, Map<Integer, Tier>> getTiers() {
        return Collections.unmodifiableMap(this.tiers);
    }

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

            // Close the preview menu
            this.inventoryManager.closePreview();

            // Purge the crate stuff
            crate.purge();

            // Profit?
            List<Prize> prizes = new ArrayList<>();

            ConfigurationSection prizesSection = file.getConfigurationSection("Crate.Prizes");

            if (prizesSection != null) {
                for (final String prize : prizesSection.getKeys(false)) {
                    ConfigurationSection prizeSection = prizesSection.getConfigurationSection(prize);

                    List<Tier> tierPrizes = new ArrayList<>();

                    if (prizeSection != null) {
                        final List<ItemStack> editorItems = new ArrayList<>();

                        if (prizeSection.contains("Editor-Items")) {
                            List<?> list = prizeSection.getList("Editor-Items");

                            if (list != null) {
                                for (Object key : list) {
                                    editorItems.add((ItemStack) key);
                                }
                            }
                        }

                        for (final String tier : prizeSection.getStringList("Tiers")) {
                            for (final Tier key : crate.getTiers()) {
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
                                alternativePrize = new Prize(prizeSection.getString("DisplayName", ""), prizeSection.getName(), alternativeSection);
                            }
                        }

                        prizes.add(new Prize(
                                prizeSection,
                                editorItems, tierPrizes,
                                crate.getFileName(),
                                alternativePrize
                        ));
                    }
                }
            }

            crate.setPrize(prizes);

            this.inventoryManager.openPreview(crate);
        } catch (Exception exception) {
            final String fileName = crate.getFileName(); //todo() this might be null

            this.brokeCrates.add(fileName);

            if (MiscUtils.isLogging()) this.logger.warn("There was an error while loading the {} file.", fileName, exception);
        }
    }

    public void loadCustomItems() {
        final PluginManager manager = this.server.getPluginManager();

        final String pluginName = this.plugin.getFusion().getItemsPlugin().toLowerCase();

        switch (pluginName) {
            case "nexo" -> manager.registerEvents(new NexoInteractListener(), this.plugin);

            case "oraxen" -> manager.registerEvents(new OraxenInteractListener(), this.plugin);

            default -> {
                if (Plugins.nexo.isEnabled()) {
                    manager.registerEvents(new NexoInteractListener(), this.plugin);
                }

                if (Plugins.oraxen.isEnabled()) {
                    manager.registerEvents(new OraxenInteractListener(), this.plugin);
                }
            }
        }
    }

    /**
     * Load the holograms.
     */
    public void loadHolograms() {
        final String pluginName = this.config.getProperty(ConfigKeys.hologram_plugin).toLowerCase();

        switch (pluginName) {
            case "decentholograms" -> {
                if (!Plugins.decent_holograms.isEnabled()) return;

                if (this.holograms != null && this.holograms.getName().equalsIgnoreCase("DecentHolograms")) { // we don't need to do anything.
                    return;
                }

                this.holograms = new DecentHologramsSupport();
            }

            case "fancyholograms" -> {
                if (!Plugins.fancy_holograms.isEnabled()) return;

                this.holograms = new FancyHologramsSupport();
            }

            case "cmi" -> {
                if (!Plugins.cmi.isEnabled() && !CMIModule.holograms.isEnabled()) return;

                this.holograms = new CMIHologramsSupport();
            }

            case "none" -> {}

            default -> {
                if (Plugins.decent_holograms.isEnabled()) {
                    if (this.holograms == null) {
                        this.holograms = new DecentHologramsSupport();
                    }

                    break;
                }

                if (Plugins.fancy_holograms.isEnabled()) {
                    this.holograms = new FancyHologramsSupport();

                    break;
                }

                if (Plugins.cmi.isEnabled() && CMIModule.holograms.isEnabled()) {
                    this.holograms = new CMIHologramsSupport();
                }
            }
        }

        if (this.holograms == null) {
            if (MiscUtils.isLogging()) {
                List.of(
                        "There was no hologram plugin found on the server. If you are using CMI",
                        "Please make sure you enabled the hologram module in modules.yml",
                        "You can run /crazycrates reload if using CMI otherwise restart your server."
                ).forEach(this.logger::warn);
            }

            return;
        }

        if (MiscUtils.isLogging()) this.logger.info("{} support has been enabled.", this.holograms.getName());
    }

    public List<String> getCrateNames(final boolean keepExtension) {
        return this.plugin.getInstance().getCrateFiles(keepExtension);
    }

    public List<String> getCrateNames() {
        return this.plugin.getInstance().getCrateFiles(false);
    }

    private final SettingsManager config = ConfigManager.getConfig();

    /**
     * Loads the crates.
     */
    public void loadCrates() {
        if (this.config.getProperty(ConfigKeys.update_examples_folder)) {
            final Path path = this.plugin.getDataFolder().toPath();
            List.of(
                    "config.yml",
                    "data.yml",
                    "locations.yml",
                    "messages.yml",
                    "editor.yml"
            ).forEach(file -> FileUtils.extract(file, path.resolve("examples"), true, false));


            FileUtils.extract("guis", path.resolve("examples"), true, true);
            FileUtils.extract("logs", path.resolve("examples"), true, true);
            FileUtils.extract("crates", path.resolve("examples"), true, true);
            FileUtils.extract("schematics", path.resolve("examples"), true, true);
        }

        this.giveNewPlayersKeys = false;

        purge();

        // Removes all holograms so that they can be replaced.
        if (this.holograms != null) {
            this.holograms.purge(false);
        }

        if (MiscUtils.isLogging()) this.logger.info("Loading all crate information...");

        for (final String crateName : getCrateNames(true)) {
            try {
                final LegacyCustomFile customFile = this.fileManager.getFile(crateName, FileType.YAML);

                if (customFile == null) continue;

                final YamlConfiguration file = customFile.getConfiguration();

                if (file == null) continue;

                final CrateType crateType = CrateType.getFromName(file.getString("Crate.CrateType", "CSGO"));

                final ArrayList<Prize> prizes = new ArrayList<>();
                final List<Tier> tiers = new ArrayList<>();

                final String previewName = file.contains("Crate.Preview.Name") ? file.getString("Crate.Preview.Name", " ") : file.getString("Crate.Name", " ");

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

                    if (MiscUtils.isLogging()) this.logger.warn("No tiers were found for {}.yml file.", crateName);

                    continue;
                }

                final ConfigurationSection prizesSection = file.getConfigurationSection("Crate.Prizes");

                if (prizesSection != null) {
                    for (String prize : prizesSection.getKeys(false)) {
                        final ConfigurationSection prizeSection = prizesSection.getConfigurationSection(prize);

                        final List<Tier> tierPrizes = new ArrayList<>();

                        Prize alternativePrize = null;

                        if (prizeSection != null) {
                            final List<ItemStack> editorItems = new ArrayList<>();

                            if (prizeSection.contains("Editor-Items")) {
                                final List<?> keys = prizeSection.getList("Editor-Items");

                                if (keys != null) {
                                    for (final Object key : keys) {
                                        editorItems.add((ItemStack) key);
                                    }
                                }
                            }

                            for (final String tier : prizeSection.getStringList("Tiers")) {
                                for (final Tier key : tiers) {
                                    if (key.getName().equalsIgnoreCase(tier)) {
                                        tierPrizes.add(key);
                                    }
                                }
                            }

                            final ConfigurationSection alternativeSection = prizeSection.getConfigurationSection("Alternative-Prize");

                            if (alternativeSection != null) {
                                final boolean isEnabled = alternativeSection.getBoolean("Toggle");

                                if (isEnabled) {
                                    alternativePrize = new Prize(prizeSection.getString("DisplayName", "<lang:item.minecraft." + prizeSection.getString("DisplayItem", "stone").toLowerCase() + ">"), prizeSection.getName(), alternativeSection);
                                }
                            }

                            prizes.add(new Prize(prizeSection, editorItems, tierPrizes, crateName, alternativePrize));
                        }
                    }
                }

                final int newPlayersKeys = file.getInt("Crate.StartingKeys", 0);

                if (!this.giveNewPlayersKeys) {
                    if (newPlayersKeys > 0) this.giveNewPlayersKeys = true;
                }

                final List<String> prizeMessage = file.contains("Crate.Prize-Message") ? file.getStringList("Crate.Prize-Message") : Collections.emptyList();

                final List<String> prizeCommands = file.contains("Crate.Prize-Commands") ? file.getStringList("Crate.Prize-Commands") : Collections.emptyList();

                final CrateHologram holo = new CrateHologram(
                        file.getBoolean("Crate.Hologram.Toggle"),
                        file.getDouble("Crate.Hologram.Height", 0.0),
                        file.getInt("Crate.Hologram.Range", 8),
                        file.getString("Crate.Hologram.Color", "transparent"),
                        file.getInt("Crate.Hologram.Update-Interval", -1),
                        file.getStringList("Crate.Hologram.Message"));

                addCrate(new Crate(crateName.replaceAll(".yml", ""), previewName, crateType, getKey(file), file.getString("Crate.PhysicalKey.Name", "Crate.PhysicalKey.Name is missing from " + crateName + ".yml"), prizes, file, newPlayersKeys, tiers, maxMassOpen, requiredKeys, prizeMessage, prizeCommands, holo));

                final PluginManager server = this.server.getPluginManager();

                final boolean isNewSystemEnabled = this.config.getProperty(ConfigKeys.use_new_permission_system);

                final String node = isNewSystemEnabled ? "crazycrates.deny.open." + crateName : "crazycrates.open." + crateName;
                final String description = isNewSystemEnabled ? "Prevents you from opening " + crateName : "Allows you to open " + crateName;
                final PermissionDefault permissionDefault = isNewSystemEnabled ? PermissionDefault.FALSE : PermissionDefault.TRUE;

                if (server.getPermission(node) == null) {
                    final Permission permission = new Permission(node, description, permissionDefault);

                    server.addPermission(permission);
                }
            } catch (Exception exception) {
                this.brokeCrates.add(crateName);

                if (MiscUtils.isLogging()) this.logger.warn("There was an error while loading the {} file.", crateName, exception);
            }
        }

        addCrate(new Crate("Menu"));

        if (MiscUtils.isLogging()) {
            List.of(
                    "All crate information has been loaded.",
                    "Loading all the physical crate locations."
            ).forEach(this.logger::info);
        }

        final YamlConfiguration locations = FileKeys.locations.getConfiguration();

        int loadedAmount = 0;
        int brokeAmount = 0;

        final ConfigurationSection section = locations.getConfigurationSection("Locations");

        if (section != null) {
            for (final String locationName : section.getKeys(false)) {
                try {
                    final String worldName = locations.getString("Locations." + locationName + ".World");

                    // If the name is null, we return.
                    if (worldName == null) return;

                    // If the name is empty or blank, we return.
                    if (worldName.isBlank()) return;

                    final World world = this.server.getWorld(worldName);
                    final int x = locations.getInt("Locations." + locationName + ".X");
                    final int y = locations.getInt("Locations." + locationName + ".Y");
                    final int z = locations.getInt("Locations." + locationName + ".Z");
                    final Location location = new Location(world, x, y, z);
                    final Crate crate = getCrateFromName(locations.getString("Locations." + locationName + ".Crate"));

                    if (world != null && crate != null) {
                        this.crateLocations.add(new CrateLocation(locationName, crate, location));

                        if (this.holograms != null) {
                            this.holograms.createHologram(location, crate, locationName);
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
                    this.logger.info("All physical crate locations have been loaded.");
                } else {
                    this.logger.info("Loaded {} physical crate locations.", loadedAmount);
                    this.logger.info("Failed to load {} physical crate locations.", brokeAmount);
                }
            }

            this.logger.info("Searching for schematics to load.");
        }

        // Loading schematic files
        final String[] schems = new File(this.plugin.getDataFolder() + "/schematics/").list();

        if (schems != null) {
            for (final String schematicName : schems) {
                if (schematicName.endsWith(".nbt")) {
                    this.crateSchematics.add(new CrateSchematic(schematicName, new File(this.plugin.getDataFolder() + "/schematics/" + schematicName)));

                    if (MiscUtils.isLogging()) this.logger.info("{} was successfully found and loaded.", schematicName);
                }
            }
        }

        if (MiscUtils.isLogging()) this.logger.info("All schematics were found and loaded.");

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
     * @param checkHand if it just checks the player hand or if it checks their inventory.
     * @param eventType {@link EventType}
     */
    public void openCrate(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, @NotNull final Location location, final boolean virtualCrate, final boolean checkHand, final EventType eventType) {
        openCrate(player, crate, keyType, location, virtualCrate, checkHand, false, eventType);
    }

    /**
     * Opens a crate for a player.
     *
     * @param player the player that is having the crate opened for them
     * @param crate the crate that is being used
     * @param location the location that may be needed for some crate types
     * @param checkHand if it just checks the player hand or if it checks their inventory
     * @param isSilent true or false, this decides on sending the broadcast messages etc.
     */
    public void openCrate(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, @NotNull final Location location, final boolean virtualCrate, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        final SettingsManager config = ConfigManager.getConfig();

        if (crate.getCrateType() == CrateType.menu) {
            if (config.getProperty(ConfigKeys.enable_crate_menu)) {
                new CrateMainMenu(
                        player,
                        this.config.getProperty(ConfigKeys.inventory_name),
                        this.config.getProperty(ConfigKeys.inventory_rows)
                ).open();

                return;
            }

            Messages.feature_disabled.sendMessage(player);

            return;
        }

        final String fancyName = crate.getCrateName();

        CrateBuilder crateBuilder;

        switch (crate.getCrateType()) {
            case csgo -> crateBuilder = new CsgoCrate(crate, player, 27);
            case casino -> crateBuilder = new CasinoCrate(crate, player, 27);
            case wonder -> crateBuilder = new WonderCrate(crate, player, 45);
            case wheel -> crateBuilder = new WheelCrate(crate, player, 54);
            case roulette -> crateBuilder = new RouletteCrate(crate, player, 27);
            case war -> crateBuilder = new WarCrate(crate, player, 9);
            case cosmic -> crateBuilder = new CosmicCrate(crate, player, 27);
            case quad_crate -> {
                if (isVirtualCrate(player, crate, virtualCrate, fancyName)) return;

                crateBuilder = new QuadCrate(crate, player, location);
            }

            case fire_cracker -> {
                if (this.cratesInUse.containsValue(location)) {
                    Messages.crate_in_use.sendMessage(player, "{crate}", fancyName);

                    removePlayerFromOpeningList(player);

                    return;
                }

                if (isVirtualCrate(player, crate, virtualCrate, fancyName)) return;

                crateBuilder = new FireCrackerCrate(crate, player, 45, location);
            }

            case crate_on_the_go -> {
                if (isVirtualCrate(player, crate, virtualCrate, fancyName)) return;

                crateBuilder = new CrateOnTheGo(crate, player);
            }

            case quick_crate -> {
                if (this.cratesInUse.containsValue(location)) {
                    Messages.crate_in_use.sendMessage(player, "{crate}", fancyName);

                    removePlayerFromOpeningList(player);

                    return;
                }

                if (isVirtualCrate(player, crate, virtualCrate, fancyName)) return;

                crateBuilder = new QuickCrate(crate, player, location);
            }

            default -> {
                crateBuilder = new CsgoCrate(crate, player, 27);

                if (MiscUtils.isLogging()) {
                    List.of(
                            crate.getFileName() + " has an invalid crate type. Your Value: " + crate.getFile().getString("Crate.CrateType", "CSGO"),
                            "We will use " + CrateType.csgo.getName() + " until you change the crate type.",
                            "Valid Crate Types: CSGO/Casino/Cosmic/QuadCrate/QuickCrate/Roulette/CrateOnTheGo/FireCracker/Wonder/Wheel/War"
                    ).forEach(this.logger::warn);
                }
            }
        }

        // Open the crate.
        crateBuilder.open(keyType, checkHand, isSilent, eventType);
    }

    private boolean isVirtualCrate(@NotNull final Player player, @NotNull final Crate crate, final boolean virtualCrate, @NotNull final String fancyName) {
        if (virtualCrate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", fancyName);

            Messages.cant_be_a_virtual_crate.sendMessage(player, placeholders);

            removePlayerFromOpeningList(player);

            return true;
        }

        return false;
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
        final UUID uuid = player.getUniqueId();

        if (this.currentTasks.containsKey(uuid)) {
            this.currentTasks.get(uuid).cancel();
            this.currentTasks.remove(uuid);
        }
    }

    /**
     * Ends the tasks running by a player.
     *
     * @param player the player using the crate.
     */
    public void endQuadCrate(@NotNull final Player player) {
        final UUID uuid = player.getUniqueId();

        if (this.currentQuadTasks.containsKey(uuid)) {
            for (ScheduledTask task : this.currentQuadTasks.get(uuid)) {
                task.cancel();
            }

            this.currentQuadTasks.remove(uuid);
        }
    }

    /**
     * Add a quad crate task that is going on for a player.
     *
     * @param player the player opening the crate.
     * @param task the task of the quad crate.
     */
    public void addQuadCrateTask(@NotNull final Player player, @NotNull final ScheduledTask task) {
        final UUID uuid = player.getUniqueId();

        if (!this.currentQuadTasks.containsKey(uuid)) {
            this.currentQuadTasks.put(uuid, new ArrayList<>());
        }

        this.currentQuadTasks.get(uuid).add(task);
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
        final TimerTask task = this.timerTasks.remove(player.getUniqueId());

        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Adds a timer task for a player opening a crate.
     *
     * @param player player opening the crate.
     * @param task task of the crate.
     * @param delay delay before running the task.
     */
    public void addCrateTask(@NotNull final Player player, @NotNull final TimerTask task, final long delay) {
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
                            FileKeys.data.getConfiguration().set("Players." + uuid + "." + crate.getFileName(), crate.getNewPlayerKeys());
                            FileKeys.data.save();
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

    /**
     * Adds a crate location.
     *
     * @param crateLocation {@link CrateLocation}
     */
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

    private final SettingsManager editor = ConfigManager.getEditor();

    public void addCrateByLocation(@NotNull final Player player, @NotNull final Location location) {
        if (!player.hasPermission("crazycrates.editor")) {
            removeEditorCrate(player);

            Messages.force_editor_exit.sendMessage(player, "{reason}", "lacking the permission crazycrates.editor");

            return;
        }

        final Crate crate = getEditorCrate(player);

        if (crate == null) {
            removeEditorCrate(player);

            Messages.force_editor_exit.sendMessage(player, "{reason}", "Crate does not exist.");

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            Messages.cannot_set_type.sendMessage(player);

            return;
        }

        if (isCrateLocation(location)) {
            if (this.editor.getProperty(EditorKeys.overwrite_old_crate_locations)) {
                final CrateLocation crateLocation = getCrateLocation(location);

                if (crateLocation == null) return;

                removeLocation(crateLocation); // remove old location

                addCrateLocation(location, crate); // add new location

                Messages.physical_crate_overridden.sendMessage(player, new HashMap<>() {{
                    put("{id}", crateLocation.getID());
                    put("{crate}", crate.getCrateName());
                }});

                spawnItem(location, ItemType.EMERALD.createItemStack());

                return;
            }

            Messages.physical_crate_already_exists.sendMessage(player, new HashMap<>() {{
                final CrateLocation crateLocation = getCrateLocation(location);

                put("{id}", crateLocation != null ? crateLocation.getID() : "N/A");
                put("{crate}", crateLocation != null ? crateLocation.getCrate().getCrateName() : "N/A");
            }});

            spawnItem(location, ItemType.REDSTONE.createItemStack());

            return;
        }

        addCrateLocation(location, crate);

        Messages.created_physical_crate.sendMessage(player, "{crate}", crate.getCrateName());

        spawnItem(location, ItemType.EMERALD.createItemStack());
    }

    private void spawnItem(@NotNull final Location location, @NotNull final ItemStack itemStack) {
        final World world = location.getWorld();

        final ItemDisplay itemDisplay = world.spawn(location.toCenterLocation().add(0.0, 1.0, 0.0), ItemDisplay.class, entity -> entity.setItemStack(itemStack));

        itemDisplay.setPersistent(false);
        itemDisplay.setBillboard(Display.Billboard.CENTER);
        itemDisplay.setDisplayHeight(0.5f);
        itemDisplay.setDisplayWidth(0.5f);

        final Matrix4f scale = new Matrix4f().scale(0.5f);

        new FoliaScheduler(null, itemDisplay) {
            @Override
            public void run() {
                if (!itemDisplay.isValid()) { // cancel just in case
                    cancel();

                    return;
                }

                itemDisplay.setTransformationMatrix(scale.rotateY(((float) Math.toRadians(180)) + 0.1F));
                itemDisplay.setInterpolationDelay(0);
                itemDisplay.setInterpolationDuration(20);
            }
        }.runAtFixedRate(1, 20);

        // remove item display after 5 seconds.
        new FoliaScheduler(location) {
            @Override
            public void run() {
                if (!itemDisplay.isValid()) { // cancel just in case
                    cancel();

                    return;
                }

                itemDisplay.remove();
            }
        }.runDelayed(100);
    }

    /**
     * Add a new physical crate location.
     *
     * @param location the location you wish to add.
     * @param crate the crate which you would like to set it to.
     */
    public void addCrateLocation(@NotNull final Location location, @Nullable final Crate crate) {
        if (crate == null) return;

        final YamlConfiguration locations = FileKeys.locations.getConfiguration();
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

        locations.set("Locations." + id + ".Crate", crate.getFileName());
        locations.set("Locations." + id + ".World", location.getWorld().getName());
        locations.set("Locations." + id + ".X", location.getBlockX());
        locations.set("Locations." + id + ".Y", location.getBlockY());
        locations.set("Locations." + id + ".Z", location.getBlockZ());

        FileKeys.locations.save();

        addLocation(new CrateLocation(id, crate, location));

        if (this.holograms != null) this.holograms.createHologram(location, crate, id);
    }

    /**
     * Remove a physical crate location.
     *
     * @param id the id of the location.
     */
    public void removeCrateLocation(@NotNull final String id) {
        FileKeys.locations.getConfiguration().set("Locations." + id, null);
        FileKeys.locations.save();

        CrateLocation location = null;

        for (final CrateLocation crateLocation : getCrateLocations()) {
            if (crateLocation.getID().equalsIgnoreCase(id)) {
                location = crateLocation;

                break;
            }
        }

        if (location != null) {
            removeLocation(location);

            if (this.holograms != null && location.getCrate().getHologram().isEnabled()) this.holograms.removeHologram(location.getID());
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

        for (final Crate key : this.crates) {
            if (key.getFileName().equalsIgnoreCase(name)) {
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
            final String arg1 = MiscUtils.location(crateLocation.getLocation());
            final String arg2 = MiscUtils.location(location);

            if (arg1.equals(arg2)) return true;
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
        return getCrateFromName(ItemUtils.getKey(item.getPersistentDataContainer()));
    }

    /**
     * Gets the physical crate of the location.
     *
     * @param location location you are checking.
     * @return a crate location if the location is a physical crate otherwise null if not.
     */
    public @Nullable final CrateLocation getCrateLocation(@NotNull final Location location) {
        CrateLocation crateLocation = null;

        final String asString = MiscUtils.location(location);

        for (final CrateLocation key : this.crateLocations) {
            final String locationAsString = MiscUtils.location(key.getLocation());

            if (locationAsString.equals(asString)) {
                crateLocation = key;

                break;
            }
        }

        return crateLocation;
    }

    /**
     * Gets the crate from location.
     *
     * @param location location you are checking.
     * @return {@link Crate}
     */
    public @Nullable final Crate getCrateFromLocation(@NotNull final Location location) {
        Crate crate = null;

        final String asString = MiscUtils.location(location);

        for (final CrateLocation key : this.crateLocations) {
            final String locationAsString = MiscUtils.location(key.getLocation());

            if (locationAsString.equals(asString)) {
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

        for (final CrateSchematic schematic : this.crateSchematics) {
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
            final ItemStack itemStack = item.getItemStack();

            if (itemStack.getType() == Material.AIR) return false;

            return itemStack.getPersistentDataContainer().has(ItemKeys.crate_prize.getNamespacedKey());
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
        if (item == null || crate == null) return false;
        if (crate.getCrateType() == CrateType.menu) return false;
        if (item.getType() == Material.AIR) return false;

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        if (!container.has(ItemKeys.crate_key.getNamespacedKey())) return false;

        return crate.getFileName().equals(ItemUtils.getKey(container));
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
    private LegacyItemBuilder getKey(@NotNull final FileConfiguration file) {
        final String name = file.getString("Crate.PhysicalKey.Name", "");
        final String customModelData = file.getString("Crate.PhysicalKey.Custom-Model-Data", "");
        final String namespace = file.getString("Crate.PhysicalKey.Model.Namespace", "");
        final String id = file.getString("Crate.PhysicalKey.Model.Id", "");
        final List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
        final boolean glowing = file.getBoolean("Crate.PhysicalKey.Glowing", true);
        final boolean hideFlags = file.getBoolean("Crate.PhysicalKey.HideItemFlags", false);

        final LegacyItemBuilder itemBuilder = file.contains("Crate.PhysicalKey.Data") ? new LegacyItemBuilder().fromBase64(file.getString("Crate.PhysicalKey.Data", "")) : new LegacyItemBuilder().withType(file.getString("Crate.PhysicalKey.Item", "tripwire_hook").toLowerCase());

        return itemBuilder.setDisplayName(name).setDisplayLore(lore).setGlowing(glowing).setItemModel(namespace, id).setHidingItemFlags(hideFlags).setCustomModelData(customModelData);
    }

    // Cleans the data file.
    private void cleanDataFile() {
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        if (!data.contains("Players")) return;

        final boolean isLogging = MiscUtils.isLogging();

        if (isLogging) this.logger.info("Cleaning up the data.yml file.");

        final List<String> removePlayers = new ArrayList<>();

        ConfigurationSection section = data.getConfigurationSection("Players");

        if (section == null) return;

        for (final String uuid : section.getKeys(false)) {
            if (data.contains("Players." + uuid + ".tracking")) return;

            boolean hasKeys = false;
            final List<String> noKeys = new ArrayList<>();

            for (final Crate crate : getUsableCrates()) {
                final String fileName = crate.getFileName();

                if (data.getInt("Players." + uuid + "." + fileName) <= 0) {
                    noKeys.add(fileName);
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
            if (isLogging) this.logger.info("{} player's data has been marked to be removed.", removePlayers.size());

            removePlayers.forEach(uuid -> data.set("Players." + uuid, null));

            if (isLogging) this.logger.info("All empty player data has been removed.");
        }

        if (isLogging) this.logger.info("The data.yml file has been cleaned.");

        FileKeys.data.save();
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
        return this.canPick.getOrDefault(player.getUniqueId(), false);
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

        final UUID uuid = player.getUniqueId();

        if (this.rewards.get(uuid) != null) {
            this.allRewards.remove(this.rewards.get(uuid));

            this.rewards.get(uuid).remove();
            this.rewards.remove(uuid);
        }

        new FoliaScheduler(location) {
            @Override
            public void run() {
                ChestManager.closeChest(location.getBlock(), false);
            }
        }.run();

        removeCrateInUse(player);
        removePlayerFromOpeningList(player);

        if (!useQuickCrateAgain) {
            if (this.holograms != null && crate != null && crate.getHologram().isEnabled()) {
                final CrateLocation crateLocation = getCrateLocation(location);

                if (crateLocation != null) this.holograms.createHologram(location, crate, crateLocation.getID());
            }
        }
    }

    public void removeCrateByLocation(@NotNull final Player player, @NotNull final Location location, final boolean isAlreadyChecked) {
        if (!player.hasPermission("crazycrates.editor")) {
            removeEditorCrate(player);

            Messages.force_editor_exit.sendMessage(player, "{reason}", "lacking the permission crazycrates.editor");

            return;
        }

        // isAlreadyChecked boolean prevents an unnecessary double lookup
        if (isAlreadyChecked || isCrateLocation(location)) {
            final CrateLocation crateLocation = getCrateLocation(location);

            if (crateLocation != null) {
                final String id = crateLocation.getID();

                removeCrateLocation(id);

                Messages.removed_physical_crate.sendMessage(player, "{id}", id);
            }
        }
    }

    /**
     * Checks if the player's equipment slot item is a key.
     *
     * @param player the player
     * @param equipmentSlot the equipment slot
     * @return true or false
     */
    public boolean isKey(@NotNull final Player player, @NotNull final EquipmentSlot equipmentSlot) {
        return isKey(player.getInventory().getItem(equipmentSlot));
    }

    /**
     * Purge all rewards!
     */
    public void purgeRewards() {
        if (!this.allRewards.isEmpty()) this.allRewards.stream().filter(Objects::nonNull).forEach(Entity::remove);
    }

    /**
     * Gets the {@link Tier}
     *
     * @param crate {@link Crate}
     * @param item {@link ItemStack}
     * @return {@link Tier}
     */
    public final Tier getTier(@NotNull final Crate crate, @NotNull final ItemStack item) {
        final PersistentDataContainerView container = item.getPersistentDataContainer();

        final NamespacedKey key = ItemKeys.crate_tier.getNamespacedKey();

        if (!container.has(key)) return null;

        return crate.getTier(container.get(key, PersistentDataType.STRING));
    }

    private final Map<UUID, ArrayList<Integer>> slots = new HashMap<>();

    public void addSlot(@NotNull final Player player, final int rawSlot) {
        final UUID uuid = player.getUniqueId();

        if (this.slots.containsKey(uuid)) {
            ArrayList<Integer> slots = this.slots.get(uuid);

            slots.add(rawSlot);

            this.slots.put(uuid, slots);

            return;
        }

        this.slots.put(uuid, new ArrayList<>() {{
            add(rawSlot);
        }});
    }

    public final ArrayList<Integer> getSlots(@NotNull final Player player) {
        return this.slots.get(player.getUniqueId());
    }

    public final boolean containsSlot(@NotNull final Player player) {
        return this.slots.containsKey(player.getUniqueId());
    }

    public void removeSlot(@NotNull final Player player) {
        this.slots.remove(player.getUniqueId());
    }
}