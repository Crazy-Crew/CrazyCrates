package com.badbones69.crazycrates

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.api.enums.settings.Messages
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager
import com.badbones69.crazycrates.commands.CCCommand
import com.badbones69.crazycrates.commands.CCTab
import com.badbones69.crazycrates.commands.KeyCommand
import com.badbones69.crazycrates.commands.KeyTab
import com.badbones69.crazycrates.controllers.BrokeLocationsControl
import com.badbones69.crazycrates.controllers.CrateControl
import com.badbones69.crazycrates.controllers.FireworkDamageEvent
import com.badbones69.crazycrates.controllers.GUIMenu
import com.badbones69.crazycrates.controllers.Preview
import com.badbones69.crazycrates.controllers.events.PaperEvents
import com.badbones69.crazycrates.controllers.events.SpigotEvents
import com.badbones69.crazycrates.cratetypes.CSGO
import com.badbones69.crazycrates.cratetypes.Cosmic
import com.badbones69.crazycrates.cratetypes.CrateOnTheGo
import com.badbones69.crazycrates.cratetypes.QuadCrate
import com.badbones69.crazycrates.cratetypes.QuickCrate
import com.badbones69.crazycrates.cratetypes.Roulette
import com.badbones69.crazycrates.cratetypes.War
import com.badbones69.crazycrates.cratetypes.Wheel
import com.badbones69.crazycrates.cratetypes.Wonder
import com.badbones69.crazycrates.support.libs.Support
import com.badbones69.crazycrates.support.placeholders.MVdWPlaceholderAPISupport
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport
import io.papermc.lib.PaperLib
import org.bstats.bukkit.Metrics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class CrazyCrates : JavaPlugin(), Listener {

    private val plugin = this // Avoid using "this".

    private val crazyManager = CrazyManager.getInstance()

    private val fileManager = CrazyManager.getFileManager()

    override fun onEnable() {

        crazyManager.loadPlugin(plugin)

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
            .setup(plugin)

        // Clean files if we have to.
        cleanFiles()

        // Add missing messages.
        Messages.addMissingMessages()

        val metricsEnabled = Files.CONFIG.file.getBoolean("Settings.Toggle-Metrics")

        if (Files.CONFIG.file.getString("Settings.Toggle-Metrics") != null) {
            if (metricsEnabled) Metrics(plugin, 4514)
        } else {
            logger.warning("Metrics was automatically enabled.")
            logger.warning("Please add Toggle-Metrics: false to the top of your config.yml")
            logger.warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml")
            logger.warning("An example if confused is linked above.")

            Metrics(plugin, 4514)
        }

        // Enable the plugin.
        enable()
    }

    override fun onDisable() {
        // Disable the plugin.
        disable()
    }

    private fun enable() {

        listOf(
            GUIMenu(),
            Preview(),
            War(),
            CSGO(),
            Wheel(),
            Wonder(),
            Cosmic(),
            Roulette(),
            QuickCrate(),
            CrateControl(),
            CrateOnTheGo(),
            FireworkDamageEvent(),
            plugin
        ).onEach { loop ->
            server.pluginManager.registerEvents(loop, plugin)
        }

        // Load crates.
        crazyManager.loadCrates()

        if (PaperLib.isPaper()) {
            logger.info("Paper was found so we have enabled paper specific features!")
            listOf(
                PaperEvents(),
                QuadCrate()
            ).onEach { loop ->
                server.pluginManager.registerEvents(loop, plugin)
            }
        } else {
            // Tell them that QuadCrates is disabled.
            logger.warning("Paper was not found so QuadCrates was not enabled.")

            // Register spigot alternatives.
            logger.info("I have enabled Spigot alternatives for the plugin!")
            server.pluginManager.registerEvents(SpigotEvents(), plugin)
        }

        if (crazyManager.brokeCrateLocations.isNotEmpty()) server.pluginManager.registerEvents(BrokeLocationsControl(), plugin)

        if (Support.PLACEHOLDERAPI.isPluginLoaded) PlaceholderAPISupport().register()
        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded) MVdWPlaceholderAPISupport.registerPlaceholders()

        getCommand("key")?.setExecutor(KeyCommand())
        getCommand("key")?.tabCompleter = KeyTab()
        getCommand("crazycrates")?.setExecutor(CCCommand())
        getCommand("crazycrates")?.tabCompleter = CCTab()
    }

    private fun disable() {
        if (PaperLib.isPaper()) SessionManager.endAllCrates()

        QuickCrate.removeAllRewards()

        if (CrazyManager.getInstance().hologramController != null) CrazyManager.getInstance().hologramController.removeAllHolograms()
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent): Unit = with(e) {
        crazyManager.setNewPlayerKeys(player)
        crazyManager.loadOfflinePlayersKeys(player)
    }
}

fun cleanFiles() {
    if (!Files.LOCATIONS.file.contains("Locations")) {
        Files.LOCATIONS.file.set("Locations.Clear", null)
        Files.LOCATIONS.saveFile()
    }

    if (!Files.DATA.file.contains("Players")) {
        Files.DATA.file.set("Players.Clear", null)
        Files.DATA.saveFile()
    }
}