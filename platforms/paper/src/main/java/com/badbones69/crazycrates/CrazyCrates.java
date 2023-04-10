package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.enums.settings.Messages;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.commands.subs.CommandManager;
import com.badbones69.crazycrates.cratetypes.CSGO;
import com.badbones69.crazycrates.cratetypes.Cosmic;
import com.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.cratetypes.QuadCrate;
import com.badbones69.crazycrates.cratetypes.QuickCrate;
import com.badbones69.crazycrates.cratetypes.Roulette;
import com.badbones69.crazycrates.cratetypes.War;
import com.badbones69.crazycrates.cratetypes.Wheel;
import com.badbones69.crazycrates.cratetypes.Wonder;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.FireworkDamageListener;
import com.badbones69.crazycrates.listeners.ItemsAdderListener;
import com.badbones69.crazycrates.listeners.MenuListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.PreviewListener;
import com.badbones69.crazycrates.support.MetricsHandler;
import com.badbones69.crazycrates.support.libraries.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private static CrazyCrates plugin;

    private Starter starter;

    @Override
    public void onEnable() {
        plugin = this;

        starter = new Starter();

        starter.run();

        starter.getFileManager().setLog(true)
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
                .setup();

        // Clean files if we have to.
        cleanFiles();

        // Add extra messages.
        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

        String menu = config.getString("Settings.Enable-Crate-Menu");

        String full = config.getString("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");

        String phys = config.getString("Settings.Physical-Accepts-Physical-Keys");

        if (phys == null) {
            config.set("Settings.Physical-Accepts-Physical-Keys", true);

            Files.CONFIG.saveFile();
        }

        if (full == null) {
            config.set("Settings.Give-Virtual-Keys-When-Inventory-Full-Message", false);

            Files.CONFIG.saveFile();
        }

        if (menu == null) {
            String oldBoolean = config.getString("Settings.Disable-Crate-Menu");
            boolean switchBoolean = config.getBoolean("Settings.Disable-Crate-Menu");

            if (oldBoolean != null) {
                config.set("Settings.Enable-Crate-Menu", switchBoolean);
                config.set("Settings.Disable-Crate-Menu", null);
            } else {
                config.set("Settings.Enable-Crate-Menu", true);
            }

            Files.CONFIG.saveFile();
        }

        if (metricsEnabled) {
            MetricsHandler metricsHandler = new MetricsHandler();

            metricsHandler.start();
        }

        enable();
    }

    @Override
    public void onDisable() {
        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        if (starter.getCrazyManager().getHologramController() != null) starter.getCrazyManager().getHologramController().removeAllHolograms();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        starter.getCrazyManager().setNewPlayerKeys(e.getPlayer());
        starter.getCrazyManager().loadOfflinePlayersKeys(e.getPlayer());
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

        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new PreviewListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);
        pluginManager.registerEvents(new CrateControlListener(), this);
        pluginManager.registerEvents(new MiscListener(), this);

        pluginManager.registerEvents(new War(), this);
        pluginManager.registerEvents(new CSGO(), this);
        pluginManager.registerEvents(new Wheel(), this);
        pluginManager.registerEvents(new Wonder(), this);
        pluginManager.registerEvents(new Cosmic(), this);
        pluginManager.registerEvents(new Roulette(), this);
        pluginManager.registerEvents(new QuickCrate(), this);
        pluginManager.registerEvents(new CrateOnTheGo(), this);
        pluginManager.registerEvents(new QuadCrate(), this);

        pluginManager.registerEvents(this, this);

        if (PluginSupport.ITEMS_ADDER.isPluginEnabled()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderListener(), this);
        } else {
            starter.getCrazyManager().loadCrates();
        }

        if (!starter.getCrazyManager().getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) new PlaceholderAPISupport().register();

        CommandManager.setup();

        printHooks();
    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }

    public void printHooks() {
        for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                plugin.getLogger().info(Methods.color("&6&l" + value.name() + " &a&lFOUND"));
            } else {
                plugin.getLogger().info(Methods.color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        }
    }

    public Starter getStarter() {
        return starter;
    }
}