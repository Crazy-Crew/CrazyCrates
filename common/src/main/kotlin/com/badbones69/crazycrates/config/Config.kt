package com.badbones69.crazycrates.config

import com.badbones69.crazycrates.files.AbstractConfig
import com.badbones69.crazycrates.files.annotations.Comment
import com.badbones69.crazycrates.files.annotations.Key
import java.nio.file.Path

class Config : AbstractConfig() {

    @Key("Settings.Prefix")
    @Comment("The base prefix for the plugin.")
    val prefix = "<dark_gray>[</dark_gray><gradient:#5e4fa2:#f79459>CrazyCrates</gradient><dark_gray]</dark_gray>: "

    @JvmField
    @Key("Settings.Toggle-Metrics")
    @Comment("Turn metrics on or off.")
    val toggleMetrics = true

    @Key("Settings.Language-File")
    @Comment("Check the locale directory for a list of available languages.")
    val languageFile = "locale-en.yml"

    @JvmField
    @Key("Settings.Verbose")
    @Comment("Turning this on is useful for debugging but gets spammy very quickly.")
    val verbose = true

    @Key("Settings.InventoryName")
    @Comment("The name of the inventory.")
    val inventoryName = "<blue><bold>Crazy</blue> <red>Crates</red></bold>"

    @Key("Settings.InventorySize")
    @Comment("The size of the inventory.")
    val inventorySize = 45

    @Key("Settings.KnockBack")
    @Comment("Whether or not crates have knock back.")
    val knockBack = true

    @Key("Settings.Physical-Accepts-Virtual-Keys")
    @Comment("Whether or not a physical crate will accept virtual keys.")
    val physicalAcceptsVirtualKeys = true

    @Key("Settings.Virtual-Accepts-Physical-Keys")
    @Comment("Whether or not virtual crates will accept physicals")
    val virtualAcceptsPhysicalKeys = true

    @Key("Settings.Give-Virtual-Keys-When-Inventory-Full")
    @Comment("Give virtual keys when inventory is full.")
    val giveVirtualKeysWhenInventoryFull = false

    @Key("Settings.Need-Key-Sound")
    @Comment("The sound for when you need a key.")
     val needKeySound = "ENTITY_VILLAGER_NO"

    @Key("Settings.QuadCrate.Timer")
    @Comment("The timer on the Quad Crate")
    val quadCrateTimer = 300

    @Key("Settings.DisabledWorlds")
    @Comment("Worlds where crates are disabled.")
    val disabledWorlds: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("world_nether")
        }
    }

    @Key("Settings.Filler.Toggle")
    @Comment("If it fills the GUI with an item.")
    val fillerToggle = false

    @Key("Settings.Filler.Item")
    @Comment("The item you wish to use.")
    val fillerItem = "BLACK_STAINED_GLASS_PANE"

    @Key("Settings.Filler.Name")
    @Comment("The name of the item.")
    val fillerName = " "

    @Key("Settings.Filler.Lore")
    @Comment("The lore of the item.")
    val fillerLore: ArrayList<String?> = object : ArrayList<String?>() {}

    @Key("Settings.GUI-Customizer")
    @Comment("Place extra items in the GUI. If you wish to not use this, remove all items and set it to GUI-Customizer: {}")
    val guiCustomizer: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("Slot:1, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:2, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:3, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:4, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:5, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:6, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:7, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:8, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:9, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:10, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:18, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:19, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:27, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:28, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:36, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:37, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:45, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:11, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:12, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:13, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:14, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:15, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:16, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:17, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:20, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:22, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:24, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:26, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:29, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:30, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:31, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:32, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:33, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:34, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ")
            add("Slot:38, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:39, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:40, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:41, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:42, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:43, Item:BLACK_STAINED_GLASS_PANE, Name: ")
            add("Slot:44, Item:BLACK_STAINED_GLASS_PANE, Name: ")
        }
    }

    fun reload(dataFolder: Path) {
        reload(dataFolder.resolve("v2/config.yml"), this::class.java)
    }
}