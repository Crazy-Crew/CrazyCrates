package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.commands.CCCommand;
import com.badbones69.crazycrates.commands.CCTab;
import com.badbones69.crazycrates.commands.KeyCommand;
import com.badbones69.crazycrates.commands.KeyTab;
import com.badbones69.crazycrates.cratetypes.CSGO;
import com.badbones69.crazycrates.cratetypes.Cosmic;
import com.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.cratetypes.QuadCrate;
import com.badbones69.crazycrates.cratetypes.QuickCrate;
import com.badbones69.crazycrates.cratetypes.Roulette;
import com.badbones69.crazycrates.cratetypes.War;
import com.badbones69.crazycrates.cratetypes.Wheel;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.FireworkDamageListener;
import com.badbones69.crazycrates.listeners.MenuListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.PreviewListener;
import com.badbones69.crazycrates.support.BinderModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class CrazyCrates extends JavaPlugin implements Listener {

    private Injector injector;

    @Inject private CCCommand ccCommand;
    @Inject private CCTab ccTab;
    @Inject private KeyCommand keyCommand;
    @Inject private KeyTab keyTab;

    @Inject private BrokeLocationsListener brokeLocationsListener;
    @Inject private CrateControlListener crateControlListener;
    @Inject private FireworkDamageListener fireworkDamageListener;
    @Inject private MenuListener menuListener;
    @Inject private MiscListener miscListener;
    @Inject private PreviewListener previewListener;

    private final FileManager fileManager = FileManager.getInstance();
    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private boolean isEnabled = false;

    @Override
    public void onEnable() {
        try {

            BinderModule module = new BinderModule(this);

            injector = module.createInjector();

            injector.injectMembers(this);

            fileManager.logInfo(true)
                    .registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                    .registerCustomFilesFolder("/crates")
                    .registerCustomFilesFolder("/schematics")
                    .setup(this);

            // Clean files if we have to.
            cleanFiles();

            // Add extra messages.
            Messages.addMissingMessages();

            String metricsPath = FileManager.Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");
            boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");

            if (metricsPath != null) {
                if (metricsEnabled) new Metrics(this, 4514);
            } else {
                getLogger().warning("Metrics was automatically enabled.");
                getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml.");
                getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");
                getLogger().warning("An example if confused is linked above.");

                new Metrics(this, 4514);
            }
        } catch (Exception e) {

            getLogger().severe(e.getMessage());

            for (StackTraceElement stack : e.getStackTrace()) {
                getLogger().severe(String.valueOf(stack));
            }

            isEnabled = false;

            return;
        }

        enable();

        isEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();

        injector = null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        crazyManager.setNewPlayerKeys(e.getPlayer());
        crazyManager.loadOfflinePlayersKeys(e.getPlayer());
    }

    public void cleanFiles() {
        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }
    }

    private void enable() {

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(menuListener, this);
        pluginManager.registerEvents(previewListener, this);
        pluginManager.registerEvents(fireworkDamageListener, this);
        pluginManager.registerEvents(crateControlListener, this);
        pluginManager.registerEvents(brokeLocationsListener, this);
        pluginManager.registerEvents(miscListener, this);

        pluginManager.registerEvents(new War(), this);
        pluginManager.registerEvents(new CSGO(), this);
        pluginManager.registerEvents(new Wheel(), this);
        pluginManager.registerEvents(new Cosmic(), this);
        pluginManager.registerEvents(new Roulette(), this);
        pluginManager.registerEvents(new QuickCrate(), this);
        pluginManager.registerEvents(new CrateOnTheGo(), this);
        pluginManager.registerEvents(new QuadCrate(), this);

        crazyManager.loadCrates();

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        getCommand("key").setExecutor(keyCommand);
        getCommand("key").setTabCompleter(keyTab);
        getCommand("crazycrates").setExecutor(ccCommand);
        getCommand("crazycrates").setTabCompleter(ccTab);
    }
}