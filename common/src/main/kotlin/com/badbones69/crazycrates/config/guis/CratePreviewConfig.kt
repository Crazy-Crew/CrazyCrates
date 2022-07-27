package com.badbones69.crazycrates.config.guis

import com.badbones69.crazycrates.files.AbstractConfig
import com.badbones69.crazycrates.files.annotations.Key
import java.nio.file.Path

class CratePreviewConfig : AbstractConfig() {

    @Key("preview-gui.buttons.back-menu-button.item")
    val backMenuButtonItem: String = "COMPASS"

    @Key("preview-gui.buttons.back-menu-button.name")
    val backMenuButtonName = "<bold><gray>!!!</gray> <red>Menu</red> <gray>!!!</gray></bold>"

    @Key("preview-gui.buttons.back-menu-button.lore")
    val backMenuButtonLore: ArrayList<String> = object : ArrayList<String>() {
        init {
            add("<gray>Return to the main menu.</gray>")
        }
    }

    @Key("preview-gui.buttons.next-page-button.item")
    val nextPageButtonItem: String = "FEATHER"

    @Key("preview-gui.buttons.next-page-button.name")
    val nextPageButtonName = "<bold><gold>Next >></gold></bold>"

    @Key("preview-gui.buttons.next-page-button.lore")
    val nextPageButtonLore: ArrayList<String> = object : ArrayList<String>() {
        init {
            add("<bold><gray>Page:</gray></bold> <blue>%page%</blue>")
        }
    }

    @Key("preview-gui.buttons.back-page-button.item")
    val backPageButtonItem: String = "FEATHER"

    @Key("preview-gui.buttons.back-page-button.name")
    val backPageButtonName = "<bold><gold><< Back</gold></bold>"

    @Key("preview-gui.buttons.back-page-button.lore")
    val backPageButtonLore: ArrayList<String> = object : ArrayList<String>() {
        init {
            add("<bold><gray>Page:</gray></bold> <blue>%page%</blue>")
        }
    }

    fun reload(dataFolder: Path) = reload(dataFolder.resolve("guis/crate-preview-gui.yml"), this::class.java)
}