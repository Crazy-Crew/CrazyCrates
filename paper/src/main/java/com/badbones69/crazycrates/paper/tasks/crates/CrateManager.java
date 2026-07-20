package com.badbones69.crazycrates.paper.tasks.crates;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateRequirement;
import com.badbones69.crazycrates.paper.cache.CacheManager;
import com.badbones69.crazycrates.paper.cache.objects.ChunkCrate;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.ryderbelserion.crazycrates.common.enums.CrateStatus;
import com.ryderbelserion.crazycrates.common.objects.CrazyLocation;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.config.RootKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.gui.GuiKeys;
import us.crazycrew.crazycrates.api.config.impl.types.editor.EditorKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.listeners.items.NexoInteractListener;
import com.badbones69.crazycrates.paper.listeners.items.OraxenInteractListener;
import com.badbones69.crazycrates.paper.managers.BukkitKeyManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.support.holograms.types.CMIHologramsSupport;
import com.badbones69.crazycrates.paper.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.paper.tasks.crates.other.quadcrates.QuadCrateManager;
import com.badbones69.crazycrates.paper.tasks.crates.types.QuadCrate;
import com.badbones69.crazycrates.paper.tasks.menus.CrateMainMenu;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateHologram;
import com.badbones69.crazycrates.paper.api.objects.crates.quadcrates.CrateSchematic;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.ChestManager;
import com.badbones69.crazycrates.paper.utils.ItemUtil;
import com.badbones69.crazycrates.paper.support.holograms.types.DecentHologramsSupport;
import com.badbones69.crazycrates.paper.support.holograms.types.FancyHologramsSupport;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.tasks.crates.types.CasinoCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.CosmicCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.CrateOnTheGo;
import com.badbones69.crazycrates.paper.tasks.crates.types.CsgoCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.FireCrackerCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.QuickCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.RouletteCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WarCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WheelCrate;
import com.badbones69.crazycrates.paper.tasks.crates.types.WonderCrate;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Matrix4f;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.CrazyCrates;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CrateManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final CacheManager cacheManager = this.platform.getCacheManager();

    private final StorageHolder storageHolder = this.platform.getStorageHolder();

    private final ConfigManager configManager = this.platform.getConfigManager();

    private final PropertyManager pluginConfig = this.configManager.getConfig();

    private final BukkitUserManager userManager = this.platform.getUserManager();

    private final PropertyManager editorConfig = this.configManager.getEditorConfig();

    private final PaperFileManager fileManager = this.platform.getFileManager();
    private final FusionPaper fusion = this.platform.getFusion();
    private final Path dataPath = this.platform.getDataPath();

    private final ComponentLogger logger = this.plugin.getComponentLogger();
    private final Server server = this.plugin.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();

    private final InventoryManager inventoryManager = this.platform.getInventoryManager();
    private final BukkitKeyManager keyManager = this.platform.getKeyManager();

    private final List<CrazyLocation> brokenLocations = new ArrayList<>();

    private final List<QuadCrateManager> quadSessions = new ArrayList<>();
    private final List<CrateSchematic> crateSchematics = new ArrayList<>();
    private final List<String> brokeCrates = new ArrayList<>();
    private final List<Crate> crates = new ArrayList<>();

    // current crate locations, stores the uuid and the crate location.
    private final Map<String, CrateLocation> locations = new HashMap<>();
    // current players editing for this crate.
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

    public void addQuadSession(@NotNull final QuadCrateManager session) {
        this.quadSessions.add(session);
    }

    public void removeQuadSession(@NotNull final QuadCrateManager session) {
        this.quadSessions.remove(session);
    }

    public void purgeQuadSessions() {
        this.quadSessions.clear();
    }

    public List<QuadCrateManager> getQuadSessions() {
        return this.quadSessions;
    }

    private final Map<UUID, Map<Integer, Tier>> tiers = new WeakHashMap<>();

    public void addTier(@NotNull final Player player, final int slot, final Tier tier) {
        final UUID uuid = player.getUniqueId();

        if (this.tiers.containsKey(uuid)) {
            this.tiers.get(uuid).put(slot, tier);

            return;
        }

        final Map<Integer, Tier> map = new HashMap<>();

        map.put(slot, tier);

        this.tiers.put(uuid, map);
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
            final ConfigurationSection section = crate.getSection();

            // Close the preview menu
            this.inventoryManager.closePreview();

            // Purge the crate stuff
            crate.purge();

            // Profit?
            final List<Prize> prizes = new ArrayList<>();

            final ConfigurationSection prizesSection = section.getConfigurationSection("Prizes");

            if (prizesSection != null) {
                for (final String prize : prizesSection.getKeys(false)) {
                    ConfigurationSection prizeSection = prizesSection.getConfigurationSection(prize);

                    final List<Tier> tierPrizes = new ArrayList<>();

                    if (prizeSection != null) {
                        final List<ItemStack> editorItems = new ArrayList<>();

                        if (prizeSection.contains("Editor-Items")) {
                            final List<?> list = prizeSection.getList("Editor-Items");

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

                        final ConfigurationSection alternativeSection = prizeSection.getConfigurationSection("Alternative-Prize");

                        if (alternativeSection != null) {
                            final boolean isEnabled = alternativeSection.getBoolean("Toggle");

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
        } catch (final Exception exception) {
            final String fileName = crate.getFileName();

            this.brokeCrates.add(fileName);

            this.fusion.log(Level.WARNING, "There was an error while loading the %s file.", exception, fileName);
        }
    }

    public void loadCustomItems() {
        final PluginManager manager = this.server.getPluginManager();

        final String pluginName = this.fusion.getItemsPlugin().toLowerCase();

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
        switch (this.pluginConfig.getProperty(RootKeys.get_hologram_plugin)) {
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

                if (Plugins.cmi.isEnabled() && CMIModule.holograms.isEnabled()) {
                    this.holograms = new CMIHologramsSupport();
                }

                if (Plugins.fancy_holograms.isEnabled()) {
                    this.holograms = new FancyHologramsSupport();
                }
            }
        }

        if (this.holograms == null) {
            this.fusion.log(Level.WARNING, "There was no hologram plugin found on the server.");

            return;
        }

        this.fusion.log(Level.INFO, "%s support has been enabled.", this.holograms.getName());
    }

    public List<String> getCrateNames(final boolean keepExtension) {
        return this.platform.getCrateFiles(keepExtension);
    }

    public void loadExamples() {
        if (this.pluginConfig.getProperty(RootKeys.is_update_examples_folder)) {
            final Path examples = this.dataPath.resolve("examples");

            if (Files.exists(examples)) {
                try (final Stream<Path> values = Files.walk(examples)) {
                    values.sorted(Comparator.reverseOrder()).forEach(path -> { // sorted in reverse order, to ensure the directories are empty first.
                        try {
                            this.fusion.log(Level.INFO, "Successfully deleted path %s, re-generating the examples later.", path);

                            Files.delete(path);
                        } catch (final IOException exception) {
                            this.fusion.log(Level.WARNING, "Failed to delete %s in loop.", exception, path);
                        }
                    });
                } catch (final Exception exception) {
                    this.fusion.log(Level.WARNING, "Failed to delete %s.", exception, examples);
                }
            }

            this.fileManager.extractFolder("guis", FileType.YAML, examples);
            this.fileManager.extractFolder("logs", FileType.YAML, examples);
            this.fileManager.extractFolder("crates", FileType.YAML, examples);
            this.fileManager.extractFolder("schematics", FileType.NBT, examples);
            this.fileManager.extractFolder("buttons", FileType.YAML, examples);

            List.of(
                    "config.yml",
                    "data.yml",
                    "locations.yml",
                    "messages.yml",
                    "editor.yml"
            ).forEach(file -> this.fileManager.extractFile(file, examples.resolve(file)));
        }
    }

    /**
     * Loads the crates.
     */
    public void loadCrates() {
        loadExamples();

        this.giveNewPlayersKeys = false;

        purge();

        // Removes all holograms so that they can be replaced.
        if (this.holograms != null) {
            this.holograms.purge(false);
        }

        this.fusion.log(Level.INFO, "Loading all crate information...");

        final Path crates = this.dataPath.resolve("crates");

        for (final Path path : this.fusion.getFilesByPath(crates, ".yml")) {
            final Optional<PaperCustomFile> optional = this.fileManager.getPaperFile(path);

            if (optional.isEmpty()) continue;

            final PaperCustomFile customFile = optional.get();

            if (!customFile.isLoaded()) continue;

            final YamlConfiguration configuration = customFile.getConfiguration();

            final ConfigurationSection section = configuration.getConfigurationSection("Crate");

            if (section == null) continue;

            final CrateType type = CrateType.getFromName(section.getString("CrateType", "CSGO"));

            final List<Prize> prizes = new ArrayList<>();
            final List<Tier> tiers = new ArrayList<>();

            final String preview = section.contains("Preview.Name") ? section.getString("Preview.Name", " ") : section.getString("Name", " ");

            final int maxMassOpen = section.getInt("Max-Mass-Open", 10);

            final int requiredKeys = section.getInt("RequiredKeys", 0);

            final ConfigurationSection prizesSection = section.getConfigurationSection("Prizes");

            final String fileName = customFile.getFileName();

            if (prizesSection == null) {
                this.brokeCrates.add(fileName);

                this.fusion.log(Level.WARNING, "No prizes section was found for %s.yml file.", fileName);

                continue;
            }

            if (type == CrateType.cosmic || type == CrateType.casino) {
                final ConfigurationSection tiersGroup = section.getConfigurationSection("Tiers");

                if (tiersGroup != null) {
                    for (final String tier : tiersGroup.getKeys(false)) {
                        final ConfigurationSection origin = tiersGroup.getConfigurationSection(tier);

                        if (origin == null) continue;

                        tiers.add(new Tier(tier, origin));
                    }
                }

                if (tiers.isEmpty()) {
                    this.brokeCrates.add(fileName);

                    this.fusion.log(Level.WARNING, "No tiers were found for %s.yml file.", fileName);

                    continue;
                }
            }

            for (final String prize : prizesSection.getKeys(false)) {
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
                        final boolean isEnabled = alternativeSection.getBoolean("Toggle", false);

                        if (isEnabled) {
                            alternativePrize = new Prize(prizeSection.getString("DisplayName", "<lang:item.minecraft." + prizeSection.getString("DisplayItem", "stone").toLowerCase() + ">"), prizeSection.getName(), alternativeSection);
                        }
                    }

                    prizes.add(new Prize(prizeSection, editorItems, tierPrizes, fileName, alternativePrize));
                }
            }

            final int keys = section.getInt("StartingKeys", 0);

            if (!this.giveNewPlayersKeys) {
                if (keys > 0) this.giveNewPlayersKeys = true;
            }

            final ConfigurationSection hologram = section.getConfigurationSection("Hologram");

            CrateHologram crateHologram = new CrateHologram();

            if (hologram != null) {
                crateHologram = new CrateHologram(
                        hologram.getBoolean("Toggle", false),
                        hologram.getDouble("Height", 0.0),
                        hologram.getInt("Range", 8),
                        hologram.getString("Color", "transparent"),
                        hologram.getBoolean("TextShadow", false),
                        hologram.getInt("Update-Interval", -1),
                        hologram.getStringList("Message")
                );
            }

            final List<String> messages = section.getStringList("Prize-Message");
            final List<String> commands = section.getStringList("Prize-Commands");

            addCrate(
                    new Crate(
                            fileName,
                            preview,
                            type,
                            getKey(section, fileName),
                            section.getString("PhysicalKey.Name", "Crate.PhysicalKey.Name is missing from " + fileName),
                            prizes,
                            section,
                            keys,
                            tiers,
                            maxMassOpen,
                            requiredKeys,
                            messages,
                            commands,
                            crateHologram
                    )
            );

            final String cleanName = fileName.replace(".yml", "");

            final String node = "crazycrates.open.%s".formatted(cleanName);
            final String description = "Allows you to open %s".formatted(cleanName);

            if (this.pluginManager.getPermission(node) == null) {
                final Permission permission = new Permission(node, description, PermissionDefault.TRUE);

                this.pluginManager.addPermission(permission);
            }
        }

        addCrate(new Crate("Menu"));

        loadLocations();

        final List<Path> values = this.fusion.getFilesByPath(this.dataPath.resolve("schematics"), ".nbt");

        for (final Path path : values) {
            final String fileName = path.getFileName().toString();

            final CrateSchematic schematic = new CrateSchematic(fileName, path);

            this.crateSchematics.add(schematic);

            this.fusion.log(Level.INFO, "%s was successfully found and loaded.", fileName);
        }

        this.fusion.log(Level.INFO, "All schematics were found and loaded.");

        cleanDataFile();

        this.inventoryManager.loadButtons();
    }

    public void loadLocations() {
        this.fusion.log(Level.INFO, "All crate information has been loaded, Loading physical crate locations!");

        final Map<CrateStatus, List<CrazyLocation>> locations = this.storageHolder.getCrateLocations();

        if (locations.isEmpty()) {
            return;
        }

        for (final Map.Entry<CrateStatus, List<CrazyLocation>> index : locations.entrySet()) {
            final CrateStatus status = index.getKey();

            for (final CrazyLocation location : index.getValue()) {
                if (status.equals(CrateStatus.failed)) {
                    this.brokenLocations.add(location);

                    continue;
                }

                if (status.equals(CrateStatus.unavailable)) {
                    this.brokenLocations.add(location);

                    continue;
                }

                final World world = this.server.getWorld(location.getWorldName());

                if (world == null) {
                    this.brokenLocations.add(location);

                    continue;
                }

                final Crate crate = getCrateFromName(location.getCrateName());

                if (crate == null) {
                    this.brokenLocations.add(location);

                    continue;
                }

                final ChunkCrate chunk = new ChunkCrate(new Location(world, location.getX(), location.getY(), location.getZ())).init(location.getId());
                final Location value = chunk.getLocation();
                final String id = chunk.getId();

                this.locations.put(id, new CrateLocation(id, crate, value)); // add to cache!

                if (this.holograms != null && crate.getHologram().isEnabled()) {
                    this.holograms.createHologram(value, crate, id);
                }
            }
        }

        // Checking if all physical locations loaded
        if (this.fusion.isVerbose()) {
            final int brokeAmount = this.brokenLocations.size();
            final int loadedAmount = this.crates.size();

            if (loadedAmount > 0 || brokeAmount > 0) {
                if (brokeAmount == 0) {
                    this.logger.info("All physical crate locations have been loaded.");
                } else {
                    this.logger.info("Loaded {} physical crate locations.", loadedAmount);
                    this.logger.warn("Failed to load {} physical crate locations.", brokeAmount);

                    this.brokenLocations.forEach(location -> this.logger.warn("The physical crate location {} failed to load!", location.getId()));
                }
            }

            this.logger.info("Searching for schematics to load.");
        }
    }

    public boolean removeCrateByLocation(@NotNull final Player player, @NotNull final Location location) {
        if (!player.hasPermission("crazycrates.editor")) {
            removeEditorCrate(player);

            Message.crate_editor_force_exit.sendMessage(player, "{reason}", "lacking the permission crazycrates.editor");

            return false;
        }

        final Optional<CrateLocation> optional = getCrateLocation(location);

        if (optional.isEmpty()) {
            Message.physical_crate_doesnt_exist.sendMessage(player);

            return false;
        }

        final CrateLocation crateLocation = optional.get();

        final String id = crateLocation.getID();

        this.storageHolder.removeCrateLocation(id);

        if (this.holograms != null) {
            this.holograms.removeHologram(id);
        }

        new ChunkCrate(crateLocation.getLocation()).remove();

        this.locations.remove(id);

        Message.physical_crate_removed.sendMessage(player, "{id}", id);

        return true;
    }

    public boolean addCrateByLocation(@NotNull final Player player, @NotNull final Location location, @Nullable final Crate crate) {
        if (!player.hasPermission("crazycrates.editor")) {
            removeEditorCrate(player);

            Message.crate_editor_force_exit.sendMessage(player, "{reason}", "lacking the permission crazycrates.editor");

            return false;
        }

        if (crate == null) {
            this.crateEditors.remove(player.getUniqueId());

            Message.crate_editor_force_exit.sendMessage(player, "{reason}", "Crate does not exist.");

            return false;
        }

        if (crate.getCrateType() == CrateType.menu && !this.pluginConfig.getProperty(GuiKeys.is_crate_menu_enabled)) {
            Message.crate_cannot_set_type.sendMessage(player);

            return false;
        }

        if (!isCrateLocation(location)) {
            addCrateLocation(location, crate);

            Message.physical_crate_created.sendMessage(player, "{crate}", crate.getCrateName());

            spawnItem(location, ItemType.EMERALD.createItemStack());

            return true;
        }

        if (this.editorConfig.getProperty(EditorKeys.overwrite_old_crate_locations)) {
            final Optional<CrateLocation> optional = getCrateLocation(location);

            if (optional.isEmpty()) {
                return false;
            }

            final CrateLocation crateLocation = optional.get();

            removeLocation(crateLocation); // remove old location

            addCrateLocation(location, crate); // add new location

            Message.physical_crate_overwrite.sendMessage(player, Map.of(
                    "{id}", crateLocation.getID(),
                    "{crate}", crate.getCrateName()
            ));

            spawnItem(location, ItemType.EMERALD.createItemStack());

            return true;
        }

        final Map<String, String> placeholders = new HashMap<>();

        getCrateLocation(location).ifPresentOrElse(crateLocation -> {
            placeholders.putIfAbsent("{crate}", crateLocation.getCrate().getCrateName());
            placeholders.putIfAbsent("{id}", crateLocation.getID());
        }, () -> {
            placeholders.putIfAbsent("{crate}", "N/A");
            placeholders.putIfAbsent("{id}", "N/A");
        });

        Message.physical_crate_exists.sendMessage(player, placeholders);

        spawnItem(location, ItemType.REDSTONE.createItemStack());

        return true;
    }

    /**
     * Add a new physical crate location.
     *
     * @param location the location you wish to add.
     * @param crate the crate which you would like to set it to.
     */
    private void addCrateLocation(@NotNull final Location location, @Nullable final Crate crate) {
        if (crate == null) return;

        final String id = this.storageHolder.addCrateLocation(
                crate.getFileName(),
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockY()
        );

        new ChunkCrate(location).init(id);

        this.locations.put(id, new CrateLocation(id, crate, location));

        if (this.holograms != null && crate.getHologram().isEnabled()) {
            this.holograms.createHologram(location, crate, id);
        }
    }

    /**
     * Adds a crate location.
     *
     * @param crateLocation {@link CrateLocation}
     */
    public void addLocation(@NotNull final CrateLocation crateLocation) {
        this.locations.put(crateLocation.getID(), crateLocation);
    }

    /**
     * Fetch the id from the chunk.
     *
     * @param location the location where the chunk is
     * @return the id from the chunk
     */
    public String getKey(final Location location) {
        return new ChunkCrate(location).get();
    }

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
    public void openCrate(@NotNull final Player player, @NotNull final Crate crate, @NotNull final KeyType keyType, @NotNull final Location location, final boolean isVirtual, final boolean checkHand, final boolean isSilent, final EventType eventType) {
        if (crate.getCrateType() == CrateType.menu) {
            if (this.pluginConfig.getProperty(GuiKeys.is_crate_menu_enabled)) {
                new CrateMainMenu(
                        player,
                        this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_name),
                        this.pluginConfig.getProperty(GuiKeys.crate_menu_inventory_rows)
                ).open();

                return;
            }

            Message.feature_disabled.sendMessage(player);

            return;
        }

        final String worldName = player.getWorld().getName();

        for (final String world : crate.getDisabledWorlds()) {
            if (world.equalsIgnoreCase(worldName)) {
                Message.world_disabled.sendMessage(player, "{world}", worldName);

                return;
            }
        }

        final String fancyName = crate.getCrateName();

        CrateBuilder crateBuilder;

        switch (crate.getCrateType()) {
            case csgo -> crateBuilder = new CsgoCrate(crate, player, location, 27);
            case casino -> crateBuilder = new CasinoCrate(crate, player, location, 27);
            case wonder -> crateBuilder = new WonderCrate(crate, player, location, 45);
            case wheel -> crateBuilder = new WheelCrate(crate, player, location, 54);
            case roulette -> crateBuilder = new RouletteCrate(crate, player, location, 27);
            case war -> crateBuilder = new WarCrate(crate, player, location, 9);
            case cosmic -> crateBuilder = new CosmicCrate(crate, player, location, 27);

            case quad_crate -> {
                if (isVirtualCrate(player, crate, isVirtual, fancyName)) {
                    return;
                }

                crateBuilder = new QuadCrate(crate, player, location);
            }

            case fire_cracker -> {
                if (isVirtualCrate(player, crate, isVirtual, fancyName)) {
                    return;
                }

                crateBuilder = new FireCrackerCrate(crate, player, location, 45);
            }

            case crate_on_the_go -> {
                if (isVirtualCrate(player, crate, isVirtual, fancyName)) {
                    return;
                }

                crateBuilder = new CrateOnTheGo(crate, player, location);
            }

            case quick_crate -> {
                if (isVirtualCrate(player, crate, isVirtual, fancyName)) {
                    return;
                }

                crateBuilder = new QuickCrate(crate, player, location);
            }

            default -> {
                crateBuilder = new CsgoCrate(crate, player, location, 27);

                if (this.fusion.isVerbose()) {
                    List.of(
                            crate.getFileName() + " has an invalid crate type. Your Value: " + crate.getSection().getString("CrateType", "CSGO"),
                            "We will use " + CrateType.csgo.getName() + " until you change the crate type.",
                            "Valid Crate Types: CSGO/Casino/Cosmic/QuadCrate/QuickCrate/Roulette/CrateOnTheGo/FireCracker/Wonder/Wheel/War"
                    ).forEach(this.logger::warn);
                }
            }
        }

        // Open the crate.
        crateBuilder.open(keyType, checkHand, isSilent, crate.useRequiredKeys() ? crate.getRequiredKeys() : 1, eventType);
    }

    private boolean isVirtualCrate(@NotNull final Player player, @NotNull final Crate crate, final boolean isVirtual, @NotNull final String fancyName) {
        if (isVirtual) {
            Message.not_physical_crate.sendMessage(player, Map.of(
                    "{cratetype}", crate.getCrateType().getName(),
                    "{crate}", fancyName
            ));

            this.cacheManager.removeActiveCrate(player.getUniqueId());

            return true;
        }

        return false;
    }

    public CrateRequirement getRequirement(@NotNull final Crate crate,
                                           @NotNull final Player player,
                                           @NotNull final KeyType keyType,
                                           final boolean isVirtualCrate
    ) {
        final int requiredKeys = crate.useRequiredKeys() ? crate.getRequiredKeys() : 1;
        final CrateType crateType = crate.getCrateType();
        final String crateName = crate.getFileName();
        final UUID uuid = player.getUniqueId();

        return switch (crateType) {
            case crate_on_the_go ->
                    new CrateRequirement(requiredKeys, keyType, crate, this.userManager.getPhysicalKeys(uuid, crateName));

            default -> {
                final int keys = isVirtualCrate && this.pluginConfig.getProperty(CrateKeys.virtual_accepts_physical_keys) && keyType.equals(KeyType.physical_key)
                        ? this.userManager.getTotalKeys(uuid, crateName)
                        : this.userManager.getVirtualKeys(uuid, crateName);

                yield new CrateRequirement(requiredKeys, keyType, crate, keys);
            }
        };
    }

    public void removeBrokenCrateLocation(@NonNull final CrazyLocation location) {
        this.brokenLocations.remove(location);
    }

    public final List<CrazyLocation> getBrokenLocations() {
        return this.brokenLocations;
    }

    /**
     * This forces a crate to end and will not give out a prize. This is meant for people who leave the server to stop any errors or lag from happening.
     *
     * @param player the player that the crate is being ended for.
     */
    public void endCrate(@Nullable final Crate crate, @NotNull final Player player, @Nullable final Location location, final boolean isRunning) {
        final UUID uuid = player.getUniqueId();

        if (this.currentTasks.containsKey(uuid)) {
            this.currentTasks.get(uuid).cancel();

            this.currentTasks.remove(uuid);
        }

        if (this.currentQuadTasks.containsKey(uuid)) {
            final List<ScheduledTask> tasks = this.currentQuadTasks.remove(uuid);

            for (final ScheduledTask task : tasks) {
                task.cancel();
            }
        }

        Optional.ofNullable(crate).ifPresent(index -> {
            final CrateType crateType = index.getCrateType();

            switch (crateType) {
                case cosmic -> {
                    final CosmicCrateManager crateManager = (CosmicCrateManager) index.getManager();

                    crateManager.removePickedPlayer(player);
                }

                case quad_crate, fire_cracker -> Optional.ofNullable(location).ifPresent(value -> {
                    if (this.holograms != null && crate.getHologram().isEnabled()) {
                        getCrateLocation(value).ifPresent(crateLocation -> this.holograms.createHologram(value, crate, crateLocation.getID()));
                    }
                });

                case quick_crate -> Optional.ofNullable(location).ifPresent(value -> {
                    new FoliaScheduler(this.plugin, value) {
                        @Override
                        public void run() {
                            ChestManager.closeChest(value.getBlock(), false);
                        }
                    }.runNow();

                    if (!isRunning) {
                        if (this.holograms != null && crate.getHologram().isEnabled()) {
                            getCrateLocation(value).ifPresent(crateLocation -> this.holograms.createHologram(value, crate, crateLocation.getID()));
                        }
                    }
                });
            }
        });

        this.cacheManager.removeActiveCrate(uuid);

        removePlayerKeyType(player);

        removeEditorCrate(player);

        removeCrateTask(player);

        removeReward(player);

        removeCloser(player);
        removePicker(player);
        removeHands(player);
        removeSlot(player);
        removeTier(player);
    }

    public void endQuickCrate(@NotNull final Player player, @NotNull final Location location, @Nullable final Crate crate, final boolean isRunning) {
        endCrate(crate, player, location, isRunning);
    }

    public void endCrate(@Nullable final Crate crate, @NotNull final Player player, @NotNull final Location location) {
        endCrate(crate, player, location, false);
    }

    public void endCrate(@Nullable final Crate crate, @NotNull final Player player) {
        endCrate(crate, player, null, false);
    }

    public void endCrate(@NotNull final Player player) {
        endCrate(null, player, null, false);
    }

    /**
     * Ends the tasks running by a player.
     *
     * @param player the player using the crate.
     */
    public void endQuadCrate(@NotNull final Player player) {
        final UUID uuid = player.getUniqueId();

        if (this.currentQuadTasks.containsKey(uuid)) {
            for (final ScheduledTask task : this.currentQuadTasks.get(uuid)) {
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

        if (!hasQuadCrateTask(player)) {
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

        this.platform.getTimer().scheduleAtFixedRate(task, delay, period);
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

        this.platform.getTimer().schedule(task, delay);
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
        this.locations.clear();
        this.brokeCrates.clear();
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

    private void spawnItem(@NotNull final Location location, @NotNull final ItemStack itemStack) {
        final World world = location.getWorld();

        final ItemDisplay itemDisplay = world.spawn(location.toCenterLocation().add(0.0, 1.0, 0.0), ItemDisplay.class, entity -> entity.setItemStack(itemStack));

        itemDisplay.setPersistent(false);
        itemDisplay.setBillboard(Display.Billboard.CENTER);
        itemDisplay.setDisplayHeight(0.5f);
        itemDisplay.setDisplayWidth(0.5f);

        final Matrix4f scale = new Matrix4f().scale(0.5f);

        new FoliaScheduler(this.plugin, null, itemDisplay) {
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
        new FoliaScheduler(this.plugin, location) {
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
     * @return an unmodifiable list of crate objects.
     */
    public @NotNull final List<Crate> getUsableCrates() {
        final List<Crate> crateList = new ArrayList<>(this.crates);

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

    public @NotNull final Optional<Crate> getCrateByName(@NotNull final String name) {
        if (name.isEmpty()) return Optional.empty();

        Crate crate = null;

        for (final Crate key : this.crates) {
            if (key.getFileName().equalsIgnoreCase(name)) {
                crate = key;

                break;
            }
        }

        return Optional.ofNullable(crate);
    }

    /**
     * Checks to see if the location is a physical crate.
     *
     * @param location location you are checking.
     * @return true if it is a physical crate and false if not.
     */
    public final boolean isCrateLocation(@NotNull final Location location) {
        return getCrateLocation(location).isPresent();
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
        return getCrateFromName(this.keyManager.getKey(item));
    }

    /**
     * Gets the physical crate of the location.
     *
     * @param location location you are checking.
     * @return a crate location if the location is a physical crate otherwise null if not.
     */
    public final Optional<CrateLocation> getCrateLocation(@NotNull final Location location) {
        return Optional.ofNullable(this.locations.get(getKey(location)));
    }

    /**
     * Gets the crate from location.
     *
     * @param location location you are checking.
     * @return {@link Crate}
     */
    public final Crate getCrateFromLocation(@NotNull final Location location) {
        return getCrateLocation(location).map(CrateLocation::getCrate).orElse(null);
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

            if (itemStack.isEmpty()) return false;

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
        if (item.isEmpty()) return false;

        final String key = this.keyManager.getKey(item);

        if (key.isEmpty()) return false;

        return crate.getFileName().equals(key);
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
        return this.locations.values().stream().toList();
    }

    public final Set<String> getCrateIds() {
        return this.locations.keySet();
    }

    /**
     * Removes a crate location.
     *
     * @param crateLocation the location to remove.
     */
    public void removeLocation(@NotNull final CrateLocation crateLocation) {
        this.locations.remove(crateLocation.getID());
    }

    /**
     * @return an unmodifiable list of broke crates.
     */
    public @NotNull final List<String> getBrokeCrates() {
        return Collections.unmodifiableList(this.brokeCrates);
    }

    /**
     * @return an unmodifiable list of crate schematics.
     */
    public @NotNull final List<CrateSchematic> getCrateSchematics() {
        return Collections.unmodifiableList(this.crateSchematics);
    }

    // Internal methods.
    private ItemBuilder getKey(@NotNull final ConfigurationSection section, @NotNull final String crateName) {
        final ConfigurationSection index = section.getConfigurationSection("PhysicalKey");

        if (index == null) {
            throw new FusionException("Could not find PhysicalKey configuration section in %s".formatted(crateName));
        }

        final String name = index.getString("Name", "");
        final String customModelData = index.getString("Custom-Model-Data", "");
        final String namespace = index.getString("Model.Namespace", "");
        final String id = index.getString("Model.Id", "");
        final List<String> lore = index.getStringList("Lore");

        final ItemBuilder itemBuilder = ItemBuilder.from(index.getString("Item", "tripwire_hook").toLowerCase());

        if (index.contains("Data")) {
            itemBuilder.withBase64(index.getString("Data", ""));
        }

        final String rgb = index.getString("Settings.RGB", "");

        final String color = index.getString("Settings.Color", "");

        final String value = !color.isEmpty() ? color : !rgb.isEmpty() ? rgb : "";

        itemBuilder.setColor(value);

        ItemUtil.addGlow(itemBuilder, index.getString("Glowing", "none"));

        ItemUtil.addItemModel(itemBuilder, namespace, id);
        ItemUtil.addCustomModel(itemBuilder, customModelData);

        return itemBuilder.withDisplayName(name).withDisplayLore(lore).hideComponents(index.getStringList("flags.components"));
    }

    // Cleans the data file.
    private void cleanDataFile() {
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        if (!data.contains("Players")) return;

        this.fusion.log(Level.INFO, "Cleaning up the data.yml file!");

        final List<String> removePlayers = new ArrayList<>();

        ConfigurationSection section = data.getConfigurationSection("Players");

        if (section == null) return;

        for (final String uuid : section.getKeys(false)) {
            if (data.contains("Players." + uuid + ".tracking")) return;

            boolean hasKeys = false;

            final List<String> crates = new ArrayList<>();

            for (final Crate crate : getUsableCrates()) {
                final String fileName = crate.getFileName();

                if (!data.contains("Players." + uuid)) continue;
                if (!data.contains("Players." + uuid + "." + fileName)) continue;

                final int keys = data.getInt("Players." + uuid + "." + fileName, 0);

                if (keys <= 0) { // if keys are less than or equal to, they don't have keys.
                    crates.add(fileName);
                } else {
                    hasKeys = true;
                }
            }

            if (hasKeys) {
                crates.forEach(crate -> data.set("Players." + uuid + "." + crate, null));
            } else {
                removePlayers.add(uuid);
            }
        }

        if (!removePlayers.isEmpty()) {
            this.fusion.log(Level.INFO, "%s player's data has been marked to be removed.", removePlayers.size());

            removePlayers.forEach(uuid -> data.set("Players." + uuid, null));

            this.fusion.log(Level.INFO, "All empty player data has been removed.");
        }

        this.fusion.log(Level.INFO, "The data.yml file has been cleaned.");

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

    public void removeReward(@NotNull final Player player) {
        final UUID uuid = player.getUniqueId();

        if (this.rewards.get(uuid) != null) {
            this.allRewards.remove(this.rewards.get(uuid));

            this.rewards.get(uuid).remove();
            this.rewards.remove(uuid);
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

    private final Map<UUID, List<Integer>> slots = new HashMap<>();

    public void addSlot(@NotNull final Player player, final int rawSlot) {
        final UUID uuid = player.getUniqueId();

        if (this.slots.containsKey(uuid)) {
            final List<Integer> slots = this.slots.get(uuid);

            slots.add(rawSlot);

            this.slots.put(uuid, slots);

            return;
        }

        final List<Integer> slots = new ArrayList<>();

        slots.add(rawSlot);

        this.slots.put(uuid, slots);
    }

    public final List<Integer> getSlots(@NotNull final Player player) {
        return this.slots.get(player.getUniqueId());
    }

    public final boolean containsSlot(@NotNull final Player player) {
        return this.slots.containsKey(player.getUniqueId());
    }

    public void removeSlot(@NotNull final Player player) {
        this.slots.remove(player.getUniqueId());
    }
}