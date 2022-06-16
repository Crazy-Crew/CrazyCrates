package com.badbones69.crazycrates

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.FileManager
import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.api.enums.Messages
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager
import com.badbones69.crazycrates.commands.CCCommand
import com.badbones69.crazycrates.commands.CCTab
import com.badbones69.crazycrates.commands.KeyCommand
import com.badbones69.crazycrates.commands.KeyTab
import com.badbones69.crazycrates.controllers.*
import com.badbones69.crazycrates.cratetypes.*
import com.badbones69.crazycrates.func.color
import com.badbones69.crazycrates.func.listeners.BasicListener
import com.badbones69.crazycrates.func.registerListener
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

    private val plugin = this // Avoid using "this"

    override fun onEnable() {

        if (PaperLib.isPaper()) {
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
        } else {
            logger.warning("Detected a Spigot only server, Schematics will not load & QuadCrates will not function.")
            FileManager.getInstance().logInfo(true)
                .registerDefaultGenerateFiles("Basic.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("Classic.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("Galactic.yml", "/crates", "/crates")
                .registerCustomFilesFolder("/crates")
                .setup()
        }

        cleanData()

        CrazyManager.getInstance().loadCrates()

        registerListener(
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
            BasicListener(),
            FireworkDamageEvent(),
            this
        )

        if (PaperLib.isPaper()) registerListener(QuadCrate()) else logger.warning("Paper was not found so QuadCrates was not enabled.")

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
            if (PaperLib.isPaper()) SessionManager.endAllCrates()
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