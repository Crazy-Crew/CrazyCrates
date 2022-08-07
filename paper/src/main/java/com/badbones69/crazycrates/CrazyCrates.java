package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.cratetypes.*;
import com.badbones69.crazycrates.listeners.*;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import io.papermc.lib.PaperLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private CrazyManager crazyManager;
    private SessionManager sessionManager;

    private FireworkDamageListener fireworkDamageListener;
    private MenuListener menuListener;
    private CrateControlListener crateControlListener;
    private ChestStateHandler chestStateHandler;

    private War war;
    private Wonder wonder;
    private Roulette roulette;
    private CSGO csgo;
    private Cosmic cosmic;
    private Wheel wheel;

    private QuickCrate quickCrate;
    private FireCracker fireCracker;

    private Methods methods;

    private boolean pluginEnabled = false;

    @Override
    public void onEnable() {

        if (!PaperLib.isPaper()) {
            PaperLib.suggestPaper(this);

            getLogger().warning("Disabling the plugin now...");

            pluginEnabled = false;

            getServer().getPluginManager().disablePlugin(this);
        }

        try {
            methods = new Methods(this, fireworkDamageListener);

            crazyManager = new CrazyManager(
                    this,
                    methods,
                    chestStateHandler,
                    crateControlListener,
                    menuListener,
                    war,
                    wonder,
                    roulette,
                    csgo,
                    cosmic,
                    wheel,
                    quickCrate,
                    fireCracker);

            // Set up old FileManager for now.
            //oldFileManager.setup(this);

        } catch (Exception e) {
            pluginEnabled = false;

            getLogger().severe(e.getMessage());

            return;
        }

        enable();

        pluginEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;

        sessionManager.endCrates();

        quickCrate.removeAllRewards();

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        crazyManager.setNewPlayerKeys(e.getPlayer());
        crazyManager.loadOfflinePlayersKeys(e.getPlayer());
    }

    public void cleanFiles() {
        //if (!Files.LOCATIONS.getFile().contains("Locations")) {
        //    Files.LOCATIONS.getFile().set("Locations.Clear", null);
        //    Files.LOCATIONS.saveFile();
        //}

        //if (!Files.DATA.getFile().contains("Players")) {
        //    Files.DATA.getFile().set("Players.Clear", null);
        //    Files.DATA.saveFile();
        //}
    }

    @SuppressWarnings("DuplicatedCode")
    private void enable() {

        PluginManager pluginManager = getServer().getPluginManager();

        chestStateHandler = new ChestStateHandler();
        sessionManager = new SessionManager();

        fireCracker = new FireCracker(this, crazyManager, methods, quickCrate);

        pluginManager.registerEvents(fireworkDamageListener = new FireworkDamageListener(), this);
        pluginManager.registerEvents(crateControlListener = new CrateControlListener(this, crazyManager, methods), this);
        pluginManager.registerEvents(menuListener = new MenuListener(crazyManager, methods), this);

        pluginManager.registerEvents(new PreviewListener(menuListener), this);
        pluginManager.registerEvents(new MiscListener(crazyManager), this);

        pluginManager.registerEvents(cosmic = new Cosmic(this, crazyManager, methods), this);pluginManager.registerEvents(csgo = new CSGO(this, crazyManager, methods), this);
        pluginManager.registerEvents(roulette = new Roulette(this, crazyManager, methods), this);
        pluginManager.registerEvents(war = new War(this, crazyManager, methods), this);
        pluginManager.registerEvents(wonder = new Wonder(this, crazyManager, methods), this);pluginManager.registerEvents(new QuadCrate(this, crazyManager, sessionManager), this);
        pluginManager.registerEvents(quickCrate = new QuickCrate(this, crazyManager, methods, crateControlListener, chestStateHandler), this);
        pluginManager.registerEvents(wheel = new Wheel(this, crazyManager, methods), this);

        pluginManager.registerEvents(new CrateOnTheGo(this, crazyManager, methods), this);


        pluginManager.registerEvents(this, this);

        // Load crates.
        crazyManager.loadCrates();

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(this, crazyManager), this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded(this)) new PlaceholderAPISupport(this, crazyManager).register();

        //Objects.requireNonNull(getCommand("crazycrates")).setExecutor("");
    }
}