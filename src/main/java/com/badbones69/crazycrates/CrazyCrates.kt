package com.badbones69.crazycrates

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.api.enums.Messages
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager
import com.badbones69.crazycrates.commands.CCCommand
import com.badbones69.crazycrates.commands.CCTab
import com.badbones69.crazycrates.commands.KeyCommand
import com.badbones69.crazycrates.commands.KeyTab
import com.badbones69.crazycrates.controllers.*
import com.badbones69.crazycrates.cratetypes.*
import com.badbones69.crazycrates.support.libs.Support
import com.badbones69.crazycrates.support.placeholders.MVdWPlaceholderAPISupport
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport
import io.papermc.lib.PaperLib
import org.bstats.bukkit.Metrics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class CrazyCrates : JavaPlugin(), Listener {

    private val plugin = this // Avoid using "this".

    private val crazyManager = CrazyManager.getInstance()

    private val fileManager = CrazyManager.getFileManager()

    override fun onEnable() {

        if (PaperLib.isPaper()) {
            fileManager.logInfo(true)
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
            fileManager.logInfo(true)
                .registerDefaultGenerateFiles("Basic.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("Classic.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("Galactic.yml", "/crates", "/crates")
                .registerCustomFilesFolder("/crates")
                .setup()
        }

        // Clean files if we have to.
        cleanFiles()

        // Add missing messages.
        Messages.addMissingMessages()

        // Enable metrics.
        Metrics(plugin, 4514)

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

        if (PaperLib.isPaper()) server.pluginManager.registerEvents(QuadCrate(), plugin) else logger.warning("Paper was not found so QuadCrates was not enabled.")

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

    @EventHandler
    fun onPlayerItemPickUp(e: PlayerAttemptPickupItemEvent): Unit = with(e) {
        if (crazyManager.isDisplayReward(item)) {
            isCancelled = true
            return@with
        }

        if (crazyManager.isInOpeningList(player)) isCancelled = true
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