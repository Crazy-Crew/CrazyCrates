package com.badbones69.crazycrates

import com.badbones69.crazycrates.api.CrazyManager
import com.badbones69.crazycrates.api.FileManager
import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.api.objects.QuadCrateSession
import com.badbones69.crazycrates.commands.CCCommand
import com.badbones69.crazycrates.commands.CCTab
import com.badbones69.crazycrates.commands.KeyCommand
import com.badbones69.crazycrates.commands.KeyTab
import com.badbones69.crazycrates.controllers.*
import com.badbones69.crazycrates.cratetypes.*
import com.badbones69.crazycrates.func.enums.registerPermissions
import com.badbones69.crazycrates.func.listeners.BasicListener
import com.badbones69.crazycrates.func.registerListener
import com.badbones69.crazycrates.support.libs.Support
import com.badbones69.crazycrates.support.libs.Version
import com.badbones69.crazycrates.support.placeholders.MVdWPlaceholderAPISupport
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport
import io.papermc.lib.PaperLib
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class CrazyCrates : JavaPlugin() {

    private var serverEnabled = true

    override fun onLoad() {
        if (Version.isOlder(Version.TOO_OLD)) {
            logger.warning("============= Crazy Crates =============")
            logger.info(" ")
            logger.warning("You are running Crazy Crates on a version that is not 1.18.X.")
            logger.warning("No guarantee that it will run perfectly, You have been warned.")
            logger.info(" ")
            logger.warning("Jenkins Page: https://jenkins.badbones69.com/job/Crazy-Crates-Dev/")
            logger.warning("Version Integer: " + Version.getCurrentVersion().versionInteger)
            logger.info(" ")
            logger.warning("============= Crazy Crates =============")
        }
    }

    override fun onEnable() {
        super.onEnable()

        //updateFiles("Locations.yml", "locations.yml")
        //updateFiles("Data.yml", "data.yml")
        //updateFiles("Config.yml", "config.yml")
        //updateFiles("Messages.yml", "messages.yml")

        //updateDirectory("Crates", "crates")
        //updateDirectory("Schematics", "schematics")

        if (PaperLib.isSpigot()) PaperLib.suggestPaper(this)

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

        if (!Files.LOCATIONS.file.contains("Locations")) {
            Files.LOCATIONS.file.set("Locations.Clear", null)
            Files.LOCATIONS.saveFile()
        }

        if (!Files.DATA.file.contains("Players")) {
            Files.DATA.file.set("Players.Clear", null)
            Files.DATA.saveFile()
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
            // Do paper specific stuff here.
        }

        if (CrazyManager.getInstance().brokeCrateLocations.isNotEmpty()) registerListener(BrokeLocationsControl())

        if (Support.PLACEHOLDERAPI.isPluginLoaded) PlaceholderAPISupport().register()
        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded) MVdWPlaceholderAPISupport.registerPlaceholders()

        Metrics()

        Methods.hasUpdate()

        registerPermissions(server.pluginManager)

        getCommand("key")?.setExecutor(KeyCommand())
        getCommand("key")?.tabCompleter = KeyTab()
        getCommand("crazycrates")?.setExecutor(CCCommand())
        getCommand("crazycrates")?.tabCompleter = CCTab()
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

    /**private fun updateFiles(oldFileName: String, newFileName: String) {
        val oldPath = Path.of("$dataFolder/$oldFileName")
        if (java.nio.file.Files.exists(oldPath)) {
            logger.warning("$oldFileName has been found.")
            logger.warning("Converting to $newFileName")
            val newFile = File(dataFolder, newFileName)
            try {
                java.nio.file.Files.copy(oldPath, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                java.nio.file.Files.deleteIfExists(oldPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/

    /**private fun updateDirectory(oldDirectory: String, newDirectory: String) {
        val oldPath = Path.of("$dataFolder/$oldDirectory")
        if (java.nio.file.Files.exists(oldPath)) {
            logger.warning("$oldDirectory directory has been found.")
            logger.warning("Converting $oldDirectory directory to $newDirectory")
            val newDir = File(dataFolder, newDirectory)
            try {
                java.nio.file.Files.move(oldPath, newDir.toPath(), StandardCopyOption.REPLACE_EXISTING)
                java.nio.file.Files.deleteIfExists(oldPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/
}
