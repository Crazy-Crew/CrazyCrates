package com.badbones69.crazycrates

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.FileManager
import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.api.enums.Messages
import com.badbones69.crazycrates.commands.*
import com.badbones69.crazycrates.controllers.*
import com.badbones69.crazycrates.cratetypes.*
import com.badbones69.crazycrates.func.listeners.BasicListener
import com.badbones69.crazycrates.func.registerListener
import com.badbones69.crazycrates.support.libs.Support
import com.badbones69.crazycrates.support.placeholders.MVdWPlaceholderAPISupport
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport
import com.badbones69.crazycrates.api.managers.quadcrates.sessions.SessionManager
import org.bstats.bukkit.Metrics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class CrazyCrates : JavaPlugin(), Listener {

    private val plugin = this // Avoid using "this"

    override fun onEnable() {

        if (!dataFolder.exists()) dataFolder.mkdir()

        val pluginDataDir = File("$dataFolder/data")
        if (!pluginDataDir.exists()) pluginDataDir.mkdir()

        FileManager.getInstance().logInfo(true)
            .registerDefaultGeneratedFiles("Basic.yml", "/crates", "/crates")
            .registerDefaultGeneratedFiles("Classic.yml", "/crates", "/crates")
            .registerDefaultGeneratedFiles("Crazy.yml", "/crates", "/crates")
            .registerDefaultGeneratedFiles("Galactic.yml", "/crates", "/crates")
            .registerDefaultGeneratedFiles("classic.nbt", "/schematics", "/schematics")
            .registerDefaultGeneratedFiles("nether.nbt", "/schematics", "/schematics")
            .registerDefaultGeneratedFiles("outdoors.nbt", "/schematics", "/schematics")
            .registerDefaultGeneratedFiles("sea.nbt", "/schematics", "/schematics")
            .registerDefaultGeneratedFiles("soul.nbt", "/schematics", "/schematics")
            .registerDefaultGeneratedFiles("wooden.nbt", "/schematics", "/schematics")
            .registerCustomFilesFolder("/crates")
            .registerCustomFilesFolder("/schematics")
            .setup()

        cleanData()

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
            FireworkDamageEvent(),
            this
        )

        // Add missing messages
        Messages.addMissingMessages()

        if (CrazyManager.getInstance().brokeCrateLocations.isNotEmpty()) registerListener(BrokeLocationsControl())

        if (Support.PLACEHOLDERAPI.isPluginLoaded) PlaceholderAPISupport().register()
        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded) MVdWPlaceholderAPISupport.registerPlaceholders()

        Metrics(plugin, 4514)

        Methods.hasUpdate()

        getCommand("key")?.setExecutor(KeyCommand())
        getCommand("key")?.tabCompleter = KeyTab()
        getCommand("crazycrates")?.setExecutor(CCCommand())
        getCommand("crazycrates")?.tabCompleter = CCTab()
    }

    override fun onDisable() {
        runCatching {
            SessionManager.endAllCrates()
            QuickCrate.removeAllRewards()
            if (CrazyManager.getInstance().hologramController != null) CrazyManager.getInstance().hologramController.removeAllHolograms()
        }.onFailure { logger.severe("The plugin did not start up correctly. Grab your logs file and join discord.badbones69.com") }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent): Unit = with(e) {
        CrazyManager.getInstance().setNewPlayerKeys(player)
        CrazyManager.getInstance().loadOfflinePlayersKeys(player)
    }
}

fun getPlugin() = JavaPlugin.getPlugin(CrazyCrates::class.java)

fun cleanData() {
    if (!Files.LOCATIONS.file.contains("Locations")) {
        Files.LOCATIONS.file.set("Locations.Clear", null)
        Files.LOCATIONS.saveFile()
    }

    if (!Files.DATA.file.contains("Players")) {
        Files.DATA.file.set("Players.Clear", null)
        Files.DATA.saveFile()
    }
}