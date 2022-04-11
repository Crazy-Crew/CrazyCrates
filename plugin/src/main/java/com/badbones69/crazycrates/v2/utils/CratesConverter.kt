package com.badbones69.crazycrates.v2.utils

import com.badbones69.crazycrates.getPlugin

class CratesConverter {

    private fun isPluginLoaded(pluginName: String) = getPlugin().server.pluginManager.getPlugin(pluginName) != null

    fun convert(cratePlugins: CratePlugins) {
        when {
            isPluginLoaded(CratePlugins.SpecializedCrates.name) -> {

            }
            isPluginLoaded(CratePlugins.EcoCrates.name) -> {

            }
            isPluginLoaded(CratePlugins.ExcellentCrates.name) -> {

            }
        }
    }
}

enum class CratePlugins {
    SpecializedCrates,
    EcoCrates,
    ExcellentCrates
}