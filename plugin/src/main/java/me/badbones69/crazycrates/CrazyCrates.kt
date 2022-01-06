package me.badbones69.crazycrates;

import io.papermc.lib.PaperLib
import me.badbones69.crazycrates.api.CrazyManager
import me.badbones69.crazycrates.api.FileManager
import me.badbones69.crazycrates.api.FileManager.Files
import me.badbones69.crazycrates.api.objects.QuadCrateSession
import me.badbones69.crazycrates.commands.CCCommand
import me.badbones69.crazycrates.commands.CCTab
import me.badbones69.crazycrates.commands.KeyCommand
import me.badbones69.crazycrates.commands.KeyTab
import me.badbones69.crazycrates.controllers.*
import me.badbones69.crazycrates.cratetypes.*
import me.badbones69.crazycrates.func.enums.registerPermissions
import me.badbones69.crazycrates.func.listeners.BasicListener
import me.badbones69.crazycrates.func.registerListener
import me.badbones69.crazycrates.multisupport.MVdWPlaceholderAPISupport
import me.badbones69.crazycrates.multisupport.PlaceholderAPISupport
import me.badbones69.crazycrates.multisupport.libs.Support
import me.badbones69.crazycrates.multisupport.libs.Version
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class CrazyCrates : JavaPlugin() {

    private var serverEnabled = true

    override fun onLoad() {
        if (Version.isOlder(Version.TOO_OLD)) {
            logger.warning("============= Crazy Crates =============");
            logger.info(" ")
            logger.warning("You are running Crazy Crates on a version that is not 1.18.X, QuadCrates will not work")
            logger.warning("Crazy Crates will function back to 1.13 except for QuadCrates.")
            logger.warning("You can check the Spigot Page or Jenkins to find jars for older versions.")
            logger.info(" ")
            logger.warning("Plugin Page: https://www.spigotmc.org/resources/17599/")
            logger.warning("Jenkins Page: https://jenkins.badbones69.com/job/Crazy-Crates/")
            logger.warning("Version Integer: " + Version.getCurrentVersion().versionInteger)
            logger.info(" ")
            logger.warning("============= Crazy Crates =============")
            //server.pluginManager.disablePlugin(this);
            return;
        }
    }

    override fun onEnable() {
        super.onEnable()

        PaperLib.suggestPaper(this)

        FileManager.getInstance().logInfo(true)
            .registerDefaultGenerateFiles("Basic.yml", "/crates", "/crates")
            .registerDefaultGenerateFiles("Classic.yml", "/crates", "/crates")
            .registerDefaultGenerateFiles("Crazy.yml", "/crates", "/crates")
            .registerDefaultGenerateFiles("Galactic.yml", "/crates", "/crates")
            .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
            .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
            .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
            .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
            .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
            .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
            .registerCustomFilesFolder("/crates")
            .registerCustomFilesFolder("/schematics")
            .setup()

        if (!Files.LOCATIONS.file.contains("locations")) {
            Files.LOCATIONS.file.set("locations.clear", null);
            Files.LOCATIONS.saveFile();
        }
        if (!Files.DATA.file.contains("players")) {
            Files.DATA.file.set("players.clear", null);
            Files.DATA.saveFile();
        }

        CrazyManager.getInstance().loadCrates()

        registerListener(
            GUIMenu(),
            Preview(),
            QuadCrate(),
            War(),
            CSGO(),
            Wheel(),
            Wonder(),
            Cosmic(),
            Roulette(),
            QuickCrate(),
            CrateControl(),
            CrateOnTheGo(),
            BasicListener(),
            FireworkDamageEvent()
        )

        if (PaperLib.isPaper()) {
            logger.info("Utilizing Paper Functions...")
            // Do paper specific shit here.
        }

        if (CrazyManager.getInstance().brokeCrateLocations.isNotEmpty()) registerListener(BrokeLocationsControl())

        if (Support.PLACEHOLDERAPI.isPluginLoaded) PlaceholderAPISupport().register()
        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded) MVdWPlaceholderAPISupport.registerPlaceholders()

        Metrics()

        Methods.hasUpdate()

        registerPermissions(server.pluginManager)

        getCommand("key")?.setExecutor(KeyCommand());
        getCommand("key")?.tabCompleter = KeyTab();
        getCommand("crazycrates")?.setExecutor(CCCommand());
        getCommand("crazycrates")?.tabCompleter = CCTab();
        serverEnabled = true
    }

    override fun onDisable() {
        super.onDisable()
        if (!serverEnabled) return
        QuadCrateSession.endAllCrates()
        QuickCrate.removeAllRewards()
        if (CrazyManager.getInstance().hologramController != null) CrazyManager.getInstance().hologramController.removeAllHolograms()
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent): Unit = with(e) {
        CrazyManager.getInstance().setNewPlayerKeys(player)
        CrazyManager.getInstance().loadOfflinePlayersKeys(player)
    }
}