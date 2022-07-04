package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.controllers.*;
import com.badbones69.crazycrates.cratetypes.*;
import com.badbones69.crazycrates.multisupport.*;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.objects.QuadCrateSession;
import com.badbones69.crazycrates.commands.CCCommand;
import com.badbones69.crazycrates.commands.CCTab;
import com.badbones69.crazycrates.commands.KeyCommand;
import com.badbones69.crazycrates.commands.KeyTab;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private final CrazyManager cc = CrazyManager.getInstance();
    private final FileManager fileManager = FileManager.getInstance();
    private boolean isEnabled = true; // If the server is supported
    
    @Override
    public void onEnable() {

        // Initialize the plugin variable.
        cc.loadPlugin();

        if (!ServerProtocol.isLegacy()) {
            checkVersion();
            return;
        }

        // Create data folder
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        fileManager.logInfo(true)
                .registerDefaultGenerateFiles("Basic.yml", "/Crates", "/Crates")
                .registerDefaultGenerateFiles("Classic.yml", "/Crates", "/Crates")
                .registerDefaultGenerateFiles("Crazy.yml", "/Crates", "/Crates")
                .registerDefaultGenerateFiles("Galactic.yml", "/Crates", "/Crates")
                //Schematics
                .registerDefaultGenerateFiles("classic.schematic", "/Schematics", "/Schematics")
                .registerDefaultGenerateFiles("nether.schematic", "/Schematics", "/Schematics")
                .registerDefaultGenerateFiles("outdoors.schematic", "/Schematics", "/Schematics")
                .registerDefaultGenerateFiles("sea.schematic", "/Schematics", "/Schematics")
                .registerDefaultGenerateFiles("soul.schematic", "/Schematics", "/Schematics")
                .registerDefaultGenerateFiles("wooden.schematic", "/Schematics", "/Schematics")
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

        cc.loadCrates();

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(this, this);
        pm.registerEvents(new GUIMenu(), this);
        pm.registerEvents(new Preview(), this);
        pm.registerEvents(new QuadCrate(), this);
        pm.registerEvents(new War(), this);
        pm.registerEvents(new CSGO(), this);
        pm.registerEvents(new Wheel(), this);
        pm.registerEvents(new Wonder(), this);
        pm.registerEvents(new Cosmic(), this);
        pm.registerEvents(new Roulette(), this);
        pm.registerEvents(new QuickCrate(), this);
        pm.registerEvents(new CrateControl(), this);
        pm.registerEvents(new CrateOnTheGo(), this);

        if (ServerProtocol.isAtLeast(ServerProtocol.v1_12_R2)) {
            pm.registerEvents(new Events_v1_12_R1_Up(), this);
        } else {
            pm.registerEvents(new Events_v1_11_R1_Down(), this);
        }

        if (!cc.getBrokeCrateLocations().isEmpty()) {
            pm.registerEvents(new BrokeLocationsControl(), this);
        }

        pm.registerEvents(new FireworkDamageEvent(), this);

        if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
            new PlaceholderAPISupport().register();
        }

        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded()) {
            MVdWPlaceholderAPISupport.registerPlaceholders(this);
        }

        new Metrics(this, 4514);

        getCommand("key").setExecutor(new KeyCommand());
        getCommand("key").setTabCompleter(new KeyTab());
        getCommand("crazycrates").setExecutor(new CCCommand());
        getCommand("crazycrates").setTabCompleter(new CCTab());
    }

    private void checkVersion() {
        isEnabled = false;
        getLogger().warning("============= Crazy Crates =============");
        getLogger().info(" ");
        getLogger().warning("Plugin Disabled: This server is running on an unsupported version and this version of Crazy Crates does not support those versions. "
                + "Legacy only supports 1.8.8, 1.12.2 - It will not run on 1.13+.....");
        getLogger().info(" ");
        getLogger().warning("Support Discord: https://discord.badbones69.com");
        getLogger().warning("Version Integer: " + ServerProtocol.getCurrentProtocol());
        getLogger().info(" ");
        getLogger().warning("============= Crazy Crates =============");

        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        if (isEnabled) {
            QuadCrateSession.endAllCrates();
            QuickCrate.removeAllRewards();

            if (cc.getHologramController() != null) {
                cc.getHologramController().removeAllHolograms();
            }
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        cc.setNewPlayerKeys(player);
        cc.loadOfflinePlayersKeys(player);
    }
}