package me.badbones69.crazycrates;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.objects.QuadCrateSession;
import me.badbones69.crazycrates.commands.CCCommand;
import me.badbones69.crazycrates.commands.CCTab;
import me.badbones69.crazycrates.commands.KeyCommand;
import me.badbones69.crazycrates.commands.KeyTab;
import me.badbones69.crazycrates.controllers.*;
import me.badbones69.crazycrates.cratetypes.*;
import me.badbones69.crazycrates.multisupport.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {
    
    private boolean updateChecker = false;
    private CrazyCrates cc = CrazyCrates.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    private boolean isEnabled = true;// If the server is supported
    
    @Override
    public void onEnable() {
        if (Version.getCurrentVersion().isOlder(Version.v1_8_R3) || Version.getCurrentVersion().isNewer(Version.v1_17_R1)) {
            checkVersion();
            return;
        }

        //Crate Files
        String extension = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? "nbt" : "schematic";
        String cratesFolder = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? "/Crates1.13-Up" : "/Crates1.12.2-Down";
        String schemFolder = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? "/Schematics1.13-Up" : "/Schematics1.12.2-Down";
        fileManager.logInfo(true)
        .registerDefaultGenerateFiles("Basic.yml", "/Crates", cratesFolder)
        .registerDefaultGenerateFiles("Classic.yml", "/Crates", cratesFolder)
        .registerDefaultGenerateFiles("Crazy.yml", "/Crates", cratesFolder)
        .registerDefaultGenerateFiles("Galactic.yml", "/Crates", cratesFolder)
        //Schematics
        .registerDefaultGenerateFiles("classic." + extension, "/Schematics", schemFolder)
        .registerDefaultGenerateFiles("nether." + extension, "/Schematics", schemFolder)
        .registerDefaultGenerateFiles("outdoors." + extension, "/Schematics", schemFolder)
        .registerDefaultGenerateFiles("sea." + extension, "/Schematics", schemFolder)
        .registerDefaultGenerateFiles("soul." + extension, "/Schematics", schemFolder)
        .registerDefaultGenerateFiles("wooden." + extension, "/Schematics", schemFolder)
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
        updateChecker = !Files.CONFIG.getFile().contains("Settings.Update-Checker") || Files.CONFIG.getFile().getBoolean("Settings.Update-Checker");
        //Messages.addMissingMessages(); #Does work but is disabled for now.
        cc.loadCrates();
        PluginManager pm = Bukkit.getPluginManager();
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
        if (Version.getCurrentVersion().isNewer(Version.v1_11_R1)) {
            pm.registerEvents(new Events_v1_12_R1_Up(), this);
        } else {
            pm.registerEvents(new Events_v1_11_R1_Down(), this);
        }
        if (!cc.getBrokeCrateLocations().isEmpty()) {
            pm.registerEvents(new BrokeLocationsControl(), this);
        }
        pm.registerEvents(new FireworkDamageEvent(this), this);
        if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
            new PlaceholderAPISupport(this).register();
        }
        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded()) {
            MVdWPlaceholderAPISupport.registerPlaceholders(this);
        }
        Methods.hasUpdate();
        new Metrics(this); //Starts up bStats
        getCommand("key").setExecutor(new KeyCommand());
        getCommand("key").setTabCompleter(new KeyTab());
        getCommand("crazycrates").setExecutor(new CCCommand());
        getCommand("crazycrates").setTabCompleter(new CCTab());
    }

    private void checkVersion() {
        isEnabled = false;
        getLogger().warning("============= Crazy Crates =============");
        getLogger().info(" ");
        getLogger().warning("Plugin Disabled: This server is running on an unsupported version and Crazy Crates does not support those versions. "
                + "Please check the spigot page for more information about lower Minecraft versions.");
        getLogger().info(" ");
        getLogger().warning("Support Discord: https://discord.com/invite/MCuz8JG/");
        getLogger().warning("Version Integer: " + Version.getCurrentVersion().getVersionInteger());
        getLogger().info(" ");
        getLogger().warning("============= Crazy Crates =============");
        Bukkit.getPluginManager().disablePlugin(this);
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOp() && updateChecker) {
                    Methods.hasUpdate(player);
                }
            }
        }.runTaskLaterAsynchronously(this, 40);
    }
}