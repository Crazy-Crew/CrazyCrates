package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.objects.QuadCrateSession;
import com.badbones69.crazycrates.commands.CCCommand;
import com.badbones69.crazycrates.commands.CCTab;
import com.badbones69.crazycrates.commands.KeyCommand;
import com.badbones69.crazycrates.commands.KeyTab;
import com.badbones69.crazycrates.controllers.BrokeLocationsControl;
import com.badbones69.crazycrates.controllers.CrateControl;
import com.badbones69.crazycrates.controllers.FireworkDamageEvent;
import com.badbones69.crazycrates.controllers.GUIMenu;
import com.badbones69.crazycrates.controllers.Preview;
import com.badbones69.crazycrates.cratetypes.CSGO;
import com.badbones69.crazycrates.cratetypes.Cosmic;
import com.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.cratetypes.QuadCrate;
import com.badbones69.crazycrates.cratetypes.QuickCrate;
import com.badbones69.crazycrates.cratetypes.Roulette;
import com.badbones69.crazycrates.cratetypes.War;
import com.badbones69.crazycrates.cratetypes.Wheel;
import com.badbones69.crazycrates.cratetypes.Wonder;
import com.badbones69.crazycrates.multisupport.Events_v1_11_R1_Down;
import com.badbones69.crazycrates.multisupport.Events_v1_12_R1_Up;
import com.badbones69.crazycrates.multisupport.ServerProtocol;
import com.badbones69.crazycrates.multisupport.Support;
import com.badbones69.crazycrates.multisupport.placeholders.MVdWPlaceholderAPISupport;
import com.badbones69.crazycrates.multisupport.placeholders.PlaceholderAPISupport;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private final FileManager fileManager = CrazyManager.getFileManager();

    private boolean isEnabled = true; // If the server is supported

    private final JavaPlugin plugin = this;
    
    @Override
    public void onEnable() {

        // Initialize the plugin variable.
        crazyManager.loadPlugin(this);

        if (!ServerProtocol.isLegacy()) {
            checkVersion();
            return;
        }

        // Crate Files
        String extensions = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "nbt" : "schematic";
        String cratesFolder = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "/Crates1.13-Up" : "/Crates1.12.2-Down";
        String schemFolder = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "/Schematics1.13-Up" : "/Schematics1.12.2-Down";

        fileManager.logInfo(true)
                .registerDefaultGenerateFiles("Basic.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Classic.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Crazy.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Galactic.yml", "/Crates", cratesFolder)
                //Schematics
                .registerDefaultGenerateFiles("classic." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("nether." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("outdoors." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("sea." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("soul." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("wooden." + extensions, "/Schematics", schemFolder)
                //Register all files inside the custom folders.
                .registerCustomFilesFolder("/Crates")
                .registerCustomFilesFolder("/Schematics")
                .setup(this);

        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }

        Messages.addMissingMessages();

        crazyManager.loadCrates();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, plugin);
        pluginManager.registerEvents(new GUIMenu(), plugin);
        pluginManager.registerEvents(new Preview(), plugin);
        pluginManager.registerEvents(new QuadCrate(), plugin);
        pluginManager.registerEvents(new War(), plugin);
        pluginManager.registerEvents(new CSGO(), plugin);
        pluginManager.registerEvents(new Wheel(), plugin);
        pluginManager.registerEvents(new Wonder(), plugin);
        pluginManager.registerEvents(new Cosmic(), plugin);
        pluginManager.registerEvents(new Roulette(), plugin);
        pluginManager.registerEvents(new QuickCrate(), plugin);
        pluginManager.registerEvents(new CrateControl(), plugin);
        pluginManager.registerEvents(new CrateOnTheGo(), plugin);

        if (ServerProtocol.isAtLeast(ServerProtocol.v1_12_R1)) {
            pluginManager.registerEvents(new Events_v1_12_R1_Up(), plugin);
        } else {
            pluginManager.registerEvents(new Events_v1_11_R1_Down(), plugin);
        }

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) {
            pluginManager.registerEvents(new BrokeLocationsControl(), plugin);
        }

        pluginManager.registerEvents(new FireworkDamageEvent(), plugin);

        if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
            new PlaceholderAPISupport().register();
        }

        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded()) {
            MVdWPlaceholderAPISupport.registerPlaceholders(plugin);
        }

        boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");

        if (Files.CONFIG.getFile().getString("Settings.Toggle-Metrics") != null) {
            if (metricsEnabled) new Metrics(plugin, 4514);
        } else {
            getLogger().warning("Metrics was automatically enabled.");
            getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml");
            getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");

            getLogger().warning("An example if confused is linked above.");

            new Metrics(plugin, 4514);
        }

        getCommand("key").setExecutor(new KeyCommand());
        getCommand("key").setTabCompleter(new KeyTab());
        getCommand("crazycrates").setExecutor(new CCCommand());
        getCommand("crazycrates").setTabCompleter(new CCTab());
    }

    private void checkVersion() {
        isEnabled = false;
        getLogger().warning("============= Crazy Crates =============");
        getLogger().warning(" ");
        getLogger().warning("Plugin Disabled: This server is running on an unsupported version and this version of Crazy Crates does not support those versions.");
        getLogger().warning("Legacy only supports 1.12.2 down to 1.8.8, It will not run on 1.13+.");
        getLogger().warning(" ");
        getLogger().warning("Support Discord: https://discord.badbones69.com");
        getLogger().warning("Version Integer: " + ServerProtocol.getCurrentProtocol());
        getLogger().warning(" ");
        getLogger().warning("============= Crazy Crates =============");

        getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void onDisable() {
        if (isEnabled) {
            QuadCrateSession.endAllCrates();
            QuickCrate.removeAllRewards();

            if (crazyManager.getHologramController() != null) {
                crazyManager.getHologramController().removeAllHolograms();
            }
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        crazyManager.setNewPlayerKeys(player);
        crazyManager.loadOfflinePlayersKeys(player);
    }
}