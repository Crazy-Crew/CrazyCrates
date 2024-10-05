package com.badbones69.crazycrates;

import com.badbones69.crazycrates.commands.CommandManager;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.CrateOpenListener;
import com.badbones69.crazycrates.listeners.crates.types.CosmicCrateListener;
import com.badbones69.crazycrates.listeners.crates.types.MobileCrateListener;
import com.badbones69.crazycrates.listeners.crates.types.QuadCrateListener;
import com.badbones69.crazycrates.listeners.crates.types.WarCrateListener;
import com.badbones69.crazycrates.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.badbones69.crazycrates.managers.InventoryManager;
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.ryderbelserion.crazycrates.common.plugin.AbstractCratesPlugin;
import com.ryderbelserion.crazycrates.common.plugin.logger.AbstractLogger;
import com.ryderbelserion.crazycrates.common.plugin.logger.PluginLogger;
import com.badbones69.crazycrates.loader.CrazyPlugin;
import com.ryderbelserion.vital.paper.Vital;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.CrazyCratesApi;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.badbones69.crazycrates.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends AbstractCratesPlugin {

    private static CrazyCrates instance;

    private final CrazyPlugin plugin;

    private final PluginLogger logger;

    private final Server server;

    private final Timer timer;

    private final Vital vital;

    public CrazyCrates(final CrazyPlugin plugin, final Vital vital) {
        this.plugin = plugin;
        this.server = plugin.getServer();

        this.vital = vital;

        this.logger = new AbstractLogger(this.plugin.getComponentLogger());

        this.timer = new Timer();

        instance = this;
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private HeadDatabaseAPI api;

    @Override
    public void onLoad() {
        load();
    }

    @Override
    public void onEnable() { //todo() work needs to be done, just load order shit
        this.vital.getFileManager().addFile("locations.yml").addFile("data.yml").addFile("respin-gui.yml", "guis") // temporarily has to go first
                .addFile("crates.log", "logs")
                .addFile("keys.log", "logs")
                .addFolder("crates")
                .addFolder("schematics")
                .init();

        enable();

        MiscUtils.janitor();
        MiscUtils.save();

        registerPermissions();

        if (Support.head_database.isEnabled()) {
            this.api = new HeadDatabaseAPI();
        }

        this.inventoryManager = new InventoryManager();
        this.crateManager = new CrateManager();
        this.userManager = new BukkitUserManager();

        // Load holograms.
        this.crateManager.loadHolograms();

        // Load the buttons.
        this.inventoryManager.loadButtons();

        // Load the crates.
        this.crateManager.loadCrates();

        // register listeners
        registerListeners();

        // register commands
        registerCommands();

        // setup managers
        setupManagers();

        new MetricsWrapper(this, 4514).start();

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) this.logger.info("PlaceholderAPI support has been enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) {
            for (final Support value : Support.values()) {
                if (value.isEnabled()) {
                    this.logger.info("<bold><gold> {} <green>FOUND", value.getName());
                } else {
                    this.logger.info("<bold><gold> {} <red>NOT FOUND", value.getName());
                }
            }
        }
    }

    @Override
    public void onDisable() {
        disable();

        this.timer.cancel();

        if (this.crateManager != null) {
            this.crateManager.purgeRewards();

            final HologramManager holograms = this.crateManager.getHolograms();

            if (holograms != null) {
                holograms.purge(true);
            }
        }

        MiscUtils.janitor();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    protected void registerPlatformAPI(final CrazyCratesApi api) {
        this.server.getServicesManager().register(CrazyCratesApi.class, api, this.plugin, ServicePriority.Normal);
    }

    @Override
    protected void registerListeners() {
        List.of(
                // Other listeners.
                new BrokeLocationsListener(),
                new CrateControlListener(),
                new EntityDamageListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new CrateOpenListener(),
                new WarCrateListener(),
                new MiscListener()
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, this.plugin));
    }

    @Override
    protected void registerCommands() {
        CommandManager.load();
    }

    @Override
    protected void setupManagers() {

    }

    @Override
    public @NotNull String parse(final @NotNull Audience audience, @NotNull final String line, @NotNull final Map<String, String> placeholders) {
        final @NotNull Optional<UUID> uuid = audience.get(Identity.UUID);

        if (uuid.isPresent()) {
            final Player player = this.server.getPlayer(uuid.get());

            return MiscUtils.populatePlaceholders(player, line, placeholders);
        }

        return "";
    }

    @Override
    public Path getDataDirectory() {
        return this.plugin.getDataFolder().toPath();
    }

    @Override
    public PluginLogger getLogger() {
        return this.logger;
    }

    @Override
    public Collection<String> getPlayerList() {
        return this.server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        return this.server.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @ApiStatus.Internal
    public final InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @ApiStatus.Internal
    public final CrateManager getCrateManager() {
        return this.crateManager;
    }

    @ApiStatus.Internal
    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    @ApiStatus.Internal
    public final Vital getVital() {
        return this.vital;
    }

    @ApiStatus.Internal
    public final Timer getTimer() {
        return this.timer;
    }

    public CrazyPlugin getPlugin() {
        return this.plugin;
    }

    public static CrazyCrates getInstance() {
        return instance;
    }
}