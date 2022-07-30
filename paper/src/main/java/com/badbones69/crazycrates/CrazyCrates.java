package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.OldFileManager;
import com.badbones69.crazycrates.api.OldFileManager.Files;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.commands.CCCommand;
import com.badbones69.crazycrates.config.Config;
import com.badbones69.crazycrates.cratetypes.*;
import com.badbones69.crazycrates.files.FileManager;
import com.badbones69.crazycrates.listeners.*;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private CrazyManager crazyManager;

    private final FileManager fileManager = FileManager.INSTANCE;

    private boolean pluginEnabled = false;

    @Override
    public void onEnable() {

        if (!PaperLib.isPaper()) {
            PaperLib.suggestPaper(this);

            getLogger().warning("Disabling the plugin now...");

            pluginEnabled = false;

            getServer().getPluginManager().disablePlugin(this);
        }

        crazyManager = new CrazyManager(this);

        OldFileManager oldFileManager = new OldFileManager();

        try {

            // Set up old FileManager for now.
            oldFileManager.setup(this);

            fileManager.registerCustomFolder("/v2")
                    .setup(getDataFolder().toPath());

            new Config().reload(getDataFolder().toPath());

            // Clean files if we have to.
            //cleanFiles();

            if (new Config().toggleMetrics) new Metrics(this, 4514);

        } catch (Exception e) {

            pluginEnabled = false;

            return;
        }

        enable();

        pluginEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;

        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();
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

        pluginManager.registerEvents(new MenuListener(crazyManager), this);
        pluginManager.registerEvents(new PreviewListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(this), this);
        pluginManager.registerEvents(new CrateControlListener(this, crazyManager), this);
        pluginManager.registerEvents(new MiscListener(crazyManager), this);

        pluginManager.registerEvents(new War(this, crazyManager), this);
        pluginManager.registerEvents(new CSGO(this, crazyManager), this);
        pluginManager.registerEvents(new Wheel(this, crazyManager), this);
        pluginManager.registerEvents(new Wonder(this, crazyManager), this);
        pluginManager.registerEvents(new Cosmic(this, crazyManager), this);
        pluginManager.registerEvents(new Roulette(this, crazyManager), this);
        pluginManager.registerEvents(new QuickCrate(this, crazyManager), this);
        pluginManager.registerEvents(new CrateOnTheGo(this, crazyManager), this);
        pluginManager.registerEvents(new QuadCrate(this, crazyManager), this);

        pluginManager.registerEvents(this, this);

        // Load crates.
        // crazyManager.loadCrates();

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) new PlaceholderAPISupport().register();

        getCommand("crates").setExecutor(new CCCommand());
    }
}