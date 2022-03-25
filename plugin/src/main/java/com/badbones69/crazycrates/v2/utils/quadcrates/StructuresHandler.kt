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

            //for (x in 0 until structureManager.size.x.toInt()) {
            //    for (y in 0 until structureManager.size.y.toInt()) {
            //        for (z in 0 until structureManager.size.z.toInt()) {
            //            val block = location.block.getRelative(x, y, z)
            //            println(block.x)
            //            println(block.z)
            //        }
            //    }
            //}
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