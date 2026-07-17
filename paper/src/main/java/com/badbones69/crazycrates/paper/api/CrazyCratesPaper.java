package com.badbones69.crazycrates.paper.api;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazycrates.paper.commands.CommandManager;
import com.badbones69.crazycrates.paper.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.listeners.MiscListener;
import com.badbones69.crazycrates.paper.listeners.crates.CrateInteractListener;
import com.badbones69.crazycrates.paper.listeners.crates.CrateOpenListener;
import com.badbones69.crazycrates.paper.listeners.crates.types.CosmicCrateListener;
import com.badbones69.crazycrates.paper.listeners.crates.types.MobileCrateListener;
import com.badbones69.crazycrates.paper.listeners.crates.types.QuadCrateListener;
import com.badbones69.crazycrates.paper.listeners.crates.types.WarCrateListener;
import com.badbones69.crazycrates.paper.listeners.items.PaperInteractListener;
import com.badbones69.crazycrates.paper.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.paper.managers.BukkitKeyManager;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.ButtonManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.support.MetricsWrapper;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import com.badbones69.crazycrates.paper.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.config.impl.types.config.RootKeys;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import static com.badbones69.crazycrates.paper.utils.MiscUtils.registerPermissions;

public final class CrazyCratesPaper extends CrazyCratesPlugin<CommandSender> {

    private final PaperFileManager fileManager;
    private final CrazyCrates plugin;
    private final FusionPaper fusion;

    private final long startTime;
    private final Server server;
    private final Timer timer;

    public CrazyCratesPaper(
            @NonNull final CrazyCrates plugin,
            @NonNull final FusionPaper fusion,
            @NonNull final Path path
    ) {
        super(this.fusion = fusion, path);

        this.fileManager = this.fusion.getFileManager();

        this.plugin = plugin;
        this.server = this.plugin.getServer();

        this.startTime = System.nanoTime();
        this.timer = new Timer();
    }

    private PaperSenderAdapter senderAdapter;

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private BukkitKeyManager keyManager;
    private ButtonManager buttonManager;
    private CrateManager crateManager;

    private MetricsWrapper metrics;

    @Override
    public void init() {
        super.init();

        this.fileManager.addPaperFile(this.path.resolve("data.yml"))

                .addPaperFolder(this.path.resolve("guis"))
                .addPaperFolder(this.path.resolve("crates"))

                .addFolder(this.path.resolve("schematics"), FileType.NBT)
                .addFolder(this.path.resolve("buttons"), FileType.YAML);

        if (Plugins.placeholder_api.isEnabled()) {
            new PlaceholderAPISupport().register();
        }

        this.senderAdapter = new PaperSenderAdapter();

        loadMessages();

        this.buttonManager = new ButtonManager(this);
        this.buttonManager.load();

        this.inventoryManager = new InventoryManager();
        this.keyManager = new BukkitKeyManager();

        this.crateManager = new CrateManager();

        this.userManager = new BukkitUserManager(this, this.crateManager);

        this.inventoryManager.loadButtons();

        this.crateManager.loadCustomItems();
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

        MiscUtils.janitor();
        MiscUtils.save();

        registerPermissions();

        CommandManager.load();

        final PluginManager pluginManager = this.server.getPluginManager();

        List.of(
                // Other listeners.
                new BrokeLocationsListener(),
                new EntityDamageListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new WarCrateListener(),
                new MiscListener(),

                new CrateInteractListener(),
                new CrateControlListener(),
                new CrateOpenListener(),

                new PaperInteractListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, this.plugin));

        if (this.configManager.getConfig().getProperty(RootKeys.is_metrics_enabled)) {
            this.metrics = new MetricsWrapper(4514);
            this.metrics.start();
        }

        if (this.fusion.isVerbose()) {
            final ComponentLogger logger = this.plugin.getComponentLogger();

            final Audience audience = Audience.empty();

            // Print dependency garbage
            for (final Plugins value : Plugins.values()) {
                final String name = value.getName();

                if (value.isEnabled()) {
                    logger.info(this.fusion.asComponent(audience, "<bold><gold>" + name + " <green>FOUND"));
                } else {
                    logger.info(this.fusion.asComponent(audience, "<bold><gold>" + name + " <red>NOT FOUND"));
                }
            }

            logger.info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
        }
    }

    @Override
    public void reload() {
        super.reload();

        this.fileManager.addPaperFile(this.path.resolve("data.yml"))

                .addPaperFolder(this.path.resolve("guis"))
                .addPaperFolder(this.path.resolve("crates"))

                .addFolder(this.path.resolve("schematics"), FileType.NBT)
                .addFolder(this.path.resolve("buttons"), FileType.YAML);

        if (this.metrics != null && !this.configManager.getConfig().getProperty(RootKeys.is_metrics_enabled)) {
            final Metrics scheduler = this.metrics.getMetrics();

            scheduler.shutdown();
        }
    }

    @Override
    public void disable() {
        super.disable();

        this.timer.cancel();

        this.server.getGlobalRegionScheduler().cancelTasks(this.plugin);
        this.server.getAsyncScheduler().cancelTasks(this.plugin);

        if (this.crateManager != null) {
            this.crateManager.purgeRewards();

            final HologramManager holograms = this.crateManager.getHolograms();

            if (holograms != null) {
                holograms.purge(true);
            }
        }

        MiscUtils.janitor();
    }

    public @NonNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public @NonNull PaperSenderAdapter getSenderAdapter() {
        return this.senderAdapter;
    }

    @Override
    public void loadMessages() {
        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml", this.fileManager.getDepth());

        paths.add(this.path.resolve("messages.yml")); // add to list

        this.fusion.getMessageRegistry().init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(customFile -> {
                    final String fileName = customFile.getFileName();

                    final FusionKey key = FusionKey.key(us.crazycrew.crazycrates.api.CrazyCrates.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = customFile.getConfiguration();

                    for (final Message message : Message.values()) {
                        message.addKey(action, customFile, configuration, key);
                    }
                }, () -> this.fusion.log(Level.INFO, "Path %s not found in cache.".formatted(path)));
            }
        });
    }

    @Override
    public @NonNull BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public @NonNull BukkitKeyManager getKeyManager() {
        return this.keyManager;
    }

    public @NonNull PaperFileManager getFileManager() {
        return this.fileManager;
    }

    public @NonNull ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    public @NonNull CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @NonNull MetricsWrapper getMetrics() {
        return this.metrics;
    }

    public @NonNull FusionPaper getFusion() {
        return this.fusion;
    }

    public @NonNull CrazyCrates getPlugin() {
        return this.plugin;
    }

    public @NonNull Timer getTimer() {
        return this.timer;
    }
}