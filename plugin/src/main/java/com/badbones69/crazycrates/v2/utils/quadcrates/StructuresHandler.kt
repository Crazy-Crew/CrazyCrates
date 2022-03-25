package com.badbones69.crazycrates.v2.utils.quadcrates

import com.badbones69.crazycrates.getPlugin
import com.badbones69.crazycrates.v2.utils.interfaces.Structures
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import java.io.File
import java.util.Random

class StructuresHandler(structureName: String, val location: Location) : Structures {

    private val structureManager = getPlugin().server.structureManager.loadStructure(File("${getPlugin().dataFolder}/schematics/$structureName.nbt"))

    override fun pasteStructure() {
        runCatching {
            structureManager.place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, Random())
            println(structureManager.size)
            structureManager.size.toLocation(location.world).block
        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun saveStructure(location: Array<Location>) {
        runCatching {

        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun removeStructure(location: Array<Location>) {
        runCatching {

        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun getStructureLocation(): Location {
        return location
    }

    override fun getBlackList(): List<Material> {
        TODO("Not yet implemented")
    }
}